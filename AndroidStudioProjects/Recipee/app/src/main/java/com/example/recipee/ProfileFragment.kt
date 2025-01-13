package com.example.recipee

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class ProfileFragment : Fragment(R.layout.profile_activity) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            // Example: Set the profile picture URL and other info
            val profilePictureUrl = "https://example.com/profile.jpg" // Get from image upload
            val username = "JohnDoe" // Get from input field or user info
            val points = 100
            val badges = listOf("Beginner", "Expert") // This can be determined based on points
            val posts = listOf("post1", "post2") // Can be fetched from Firestore
            val likes = 50
            val lastLogin = FieldValue.serverTimestamp() // Automatically set the timestamp for last login

            // Store or update user profile data in Firestore
            saveUserProfile(userId, username, profilePictureUrl, points, badges, posts, likes, lastLogin)
        } else {
            Toast.makeText(requireContext(), "User not signed in!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserProfile(
        userId: String,
        username: String,
        profilePictureUrl: String,
        points: Int,
        badges: List<String>,
        posts: List<String>,
        likes: Int,
        lastLogin: Any
    ) {
        // Create a map of user data
        val userData = hashMapOf(
            "username" to username,
            "profilePictureUrl" to profilePictureUrl,
            "points" to points,
            "badges" to badges,
            "posts" to posts,
            "likes" to likes,
            "lastLogin" to lastLogin,
            "postHistory" to posts // This can also be a history of all posts
        )

        // Save data under the user's unique ID in the 'profile' collection
        db.collection("profile").document(userId) // Change collection name here
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "User profile added or updated for user: $userId")
                Toast.makeText(requireContext(), "User profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating user profile", e)
                Toast.makeText(requireContext(), "Failed to update user profile.", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to upload a profile picture to Firebase Storage and return the URL
    private fun uploadProfilePicture(imageUri: Uri, callback: (String?) -> Unit) {
        val profilePicturesRef = storageRef.child("profile_pictures/${UUID.randomUUID()}")
        profilePicturesRef.putFile(imageUri)
            .addOnSuccessListener {
                profilePicturesRef.downloadUrl.addOnSuccessListener { uri ->
                    // Return the image URL after successful upload via callback
                    callback(uri.toString())
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error uploading profile picture", it)
                callback(null)
            }
    }
}
