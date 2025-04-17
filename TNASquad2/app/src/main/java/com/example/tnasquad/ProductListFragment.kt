package com.example.tnasquad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnasquad.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class ProductListFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var productList: ArrayList<Product>
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_product_list_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.productRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        productList = arrayListOf()
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        fetchProducts(view)
    }

    private fun fetchProducts(root: View) {
        val spinner = root.findViewById<ProgressBar>(R.id.loading)
        val emptyTv = root.findViewById<TextView>(R.id.emptyState)

        spinner.visibility = View.VISIBLE
        emptyTv.visibility = View.GONE

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (doc in result) {
                    val product = doc.toObject(Product::class.java)
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
                spinner.visibility = View.GONE
                emptyTv.visibility = if (productList.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener { e ->
                spinner.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show()
            }
    }
}
