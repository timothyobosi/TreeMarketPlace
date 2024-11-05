package com.code.treeMarketplaceApp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddTreeSpeciesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var imagePreview: ImageView
    private  var selectedImageUri: Uri? = null// Use nullable type and initialize to null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tree_species)

        db = FirebaseFirestore.getInstance()

        val nameField: EditText = findViewById(R.id.nameField)
        val descriptionField: EditText = findViewById(R.id.descriptionField)
        imagePreview = findViewById(R.id.imagePreview)
        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        val addButton: Button = findViewById(R.id.addButton)

        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        addButton.setOnClickListener {
            val name = nameField.text.toString()
            val description = descriptionField.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri!!, name, description)
            } else {
                Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imagePreview.setImageURI(selectedImageUri)
        }
    }

    private fun uploadImageToFirebase(uri: Uri, name: String, description: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    addTreeSpecies(name, description, imageUrl)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTreeSpecies(name: String, description: String, imageUrl: String) {
        val treeSpecies = TreeSpecies(name, description, imageUrl)
        db.collection("treeSpecies")
            .add(treeSpecies)
            .addOnSuccessListener {
                Toast.makeText(this, "Tree species added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add species: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}
