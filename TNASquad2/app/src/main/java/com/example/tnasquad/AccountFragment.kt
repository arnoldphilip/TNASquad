package com.example.tnasquad

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.account, container, false)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            showAccountDetails(view, currentUser)
        } else {
            showLoginSignup(view)
        }

        return view
    }

    private fun showAccountDetails(view: View, user: FirebaseUser) {
        val nameTextView: TextView = view.findViewById(R.id.tvUserName)
        val emailTextView: TextView = view.findViewById(R.id.tvUserEmail)
        val loginButton: Button = view.findViewById(R.id.btnLoginSignup)
        val logoutButton: Button = view.findViewById(R.id.btnLogout)

        loginButton.visibility = View.GONE
        logoutButton.visibility = View.VISIBLE

        val uid = user.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "User"
                    val email = document.getString("email") ?: user.email
                    nameTextView.text = "Welcome, $name"
                    emailTextView.text = "Email: $email"
                } else {
                    nameTextView.text = "Welcome, User"
                    emailTextView.text = "Email: ${user.email}"
                }
            }
            .addOnFailureListener {
                nameTextView.text = "Welcome, User"
                emailTextView.text = "Email: ${user.email}"
            }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            // Reload fragment smoothly
            parentFragmentManager.beginTransaction()
                .replace(id, AccountFragment()) // replaces this fragment with a fresh instance
                .commit()
        }
    }

    private fun showLoginSignup(view: View) {
        val loginButton: Button = view.findViewById(R.id.btnLoginSignup)
        val logoutButton: Button = view.findViewById(R.id.btnLogout)
        val nameTextView: TextView = view.findViewById(R.id.tvUserName)
        val emailTextView: TextView = view.findViewById(R.id.tvUserEmail)

        loginButton.visibility = View.VISIBLE
        logoutButton.visibility = View.GONE
        nameTextView.text = "Not logged in"
        emailTextView.text = ""

        loginButton.setOnClickListener {
            val intent = Intent(activity, login::class.java)
            startActivity(intent)
        }
    }
}
