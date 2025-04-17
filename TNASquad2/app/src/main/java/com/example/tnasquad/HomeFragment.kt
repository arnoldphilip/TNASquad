package com.example.tnasquad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.tnasquad.model.Product


class HomeFragment : Fragment() {

    private lateinit var productList: ArrayList<Product>
    private lateinit var adapter: ProductAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.productRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productList = arrayListOf()
        adapter = ProductAdapter(productList)
        recyclerView.adapter = adapter

        fetchProducts()
        return view
    }

    private fun fetchProducts() {
        db.collection("products").get().addOnSuccessListener { result ->
            productList.clear()
            for (doc in result) {
                val product = doc.toObject(Product::class.java)
                productList.add(product)
            }
            adapter.notifyDataSetChanged()
        }
    }
}
