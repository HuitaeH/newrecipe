package com.example.recipee

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.Timestamp
import android.widget.TextView
import android.widget.ImageView
import android.widget.Button
import com.bumptech.glide.Glide
import java.util.*
import datas.UserProfile

class ProfileFragment : Fragment(R.layout.profile_activity) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            fetchAndDisplayUserProfile(userId, view)

            // Set up the profile picture update button
            val updateButton = view.findViewById<Button>(R.id.updateProfilePictureButton)
            updateButton.setOnClickListener {
                openFileChooser()
            }
        } else {
            Toast.makeText(requireContext(), "User not signed in!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAndDisplayUserProfile(userId: String, view: View) {
        db.collection("profile").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userProfile = UserProfile(
                        userId = userId,
                        username = document.getString("username") ?: "Unknown User",
                        profilePictureUrl = document.getString("profilePictureUrl") ?: "",
                        points = document.getLong("points")?.toInt() ?: 0,
                        posts = (document.get("posts") as? List<String>) ?: emptyList(),
                        likes = document.getLong("likes")?.toInt() ?: 0,
                        lastLogin = document.getTimestamp("lastLogin") ?: Timestamp.now()
                    )

                    view.findViewById<TextView>(R.id.username).text = userProfile.username
                    view.findViewById<TextView>(R.id.points).text = "Points: ${userProfile.points}"
                    view.findViewById<TextView>(R.id.likes).text = "Likes: ${userProfile.likes}"
                    view.findViewById<TextView>(R.id.badges).text = "Badge: ${userProfile.badge}"

                    // Load profile picture
                    val imageView = view.findViewById<ImageView>(R.id.profilePicture)
                    Glide.with(requireContext())
                        .load(userProfile.profilePictureUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(imageView)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching profile data", e)
                Toast.makeText(requireContext(), "Failed to fetch profile data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            uploadProfilePicture(selectedImageUri!!) { imageUrl ->
                if (imageUrl != null) {
                    updateProfilePictureInFirestore(imageUrl)
                } else {
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadProfilePicture(imageUri: Uri, callback: (String?) -> Unit) {
        val profilePicturesRef = storageRef.child("profile_pictures/${UUID.randomUUID()}")
        profilePicturesRef.putFile(imageUri)
            .addOnSuccessListener {
                profilePicturesRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error uploading profile picture", e)
                callback(null)
            }
    }

    private fun updateProfilePictureInFirestore(imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("profile").document(userId)
            .update("profilePictureUrl", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating profile picture in Firestore", e)
                Toast.makeText(requireContext(), "Failed to update profile picture.", Toast.LENGTH_SHORT).show()
            }
    }
}
