package com.code.treeMarketplaceApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TreeSpeciesAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var addSpeciesButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize RecyclerView and Firestore
        recyclerView = findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        db = FirebaseFirestore.getInstance()

        // Set up adapter (initially with empty mutable list)
        adapter = TreeSpeciesAdapter(mutableListOf())
        recyclerView.adapter = adapter

        // Set up FloatingActionButton to open AddTreeSpeciesActivity
        addSpeciesButton = findViewById(R.id.addSpeciesButton)
        addSpeciesButton.setOnClickListener {
            startActivity(Intent(this, AddTreeSpeciesActivity::class.java))
        }

        // Fetch and display data from Firestore
        fetchTreeSpeciesData()

        // BottomNavigation setup
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_marketplace -> {
                    startActivity(Intent(this, MarketplaceActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Fetch Tree Species data from Firestore and update the adapter
    private fun fetchTreeSpeciesData() {
        db.collection("treeSpecies")
            .get()
            .addOnSuccessListener { documents ->
                val treeSpeciesList = documents.map { doc ->
                    TreeSpecies(
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                }
                adapter.updateData(treeSpeciesList)
            }
            .addOnFailureListener { exception ->
                Log.w("HomeActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Failed to load species.", Toast.LENGTH_SHORT).show()
            }
    }



    // Add a new Tree Species to Firestore
    private fun addTreeSpecies(name: String, description: String, imageUrl: String) {
        val treeSpecies = TreeSpecies(name, description, imageUrl)
        db.collection("treeSpecies")
            .add(treeSpecies)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Tree species added with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                fetchTreeSpeciesData() // Refresh the list
            }
            .addOnFailureListener { e ->
                Log.w("HomeActivity", "Error adding document", e)
                Toast.makeText(this, "Failed to add species: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
