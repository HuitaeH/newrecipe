package com.example.recipee

import Adapter.RecipeAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import datas.Recipe
import java.util.*
import datas.UserProfile

class ProfileFragment : Fragment(R.layout.profile_activity) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private val bookmarkedRecipes = listOf( // 더미 데이터로 북마크된 레시피 정의
        Recipe(
            id = 1,
            title = "Creamy Garlic Pasta",
            cookingTime = 20,
            category = "vegan",
            authorName = "Minji",
            likeCount = 8,
            imageResId = R.drawable.garlicpasta,
            profileImageResId = R.drawable.profile2
        ),
        Recipe(
            id = 2,
            title = "Spiced Fried Chicken",
            cookingTime = 30,
            category = "diet",
            authorName = "Huitae",
            likeCount = 15,
            imageResId = R.drawable.chicken,
            profileImageResId = R.drawable.profile1
        )
    )
    private lateinit var bookmarksAdapter: RecipeAdapter // 어댑터 선언

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.bookmarkedRecipesRecyclerView)
        bookmarksAdapter = RecipeAdapter()
        recyclerView.apply {
            adapter = bookmarksAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // View Bookmarks 버튼 클릭 이벤트
        val viewBookmarksButton = view.findViewById<Button>(R.id.viewBookmarksButton)
        viewBookmarksButton.setOnClickListener {
            showBookmarksDialog() // 다이얼로그 호출
        }

        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            fetchAndDisplayUserProfile(userId, view)

            val updateButton = view.findViewById<Button>(R.id.updateProfilePictureButton)
            updateButton.setOnClickListener {
                openFileChooser()
            }
        } else {
            Toast.makeText(requireContext(), "User not signed in!", Toast.LENGTH_SHORT).show()
        }



    }

    private fun displayBookmarkedRecipes() {
        // 북마크된 레시피를 RecyclerView에 표시
        bookmarksAdapter.submitList(bookmarkedRecipes)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.bookmarkedRecipesRecyclerView)
        recyclerView?.visibility = View.VISIBLE // RecyclerView를 보이도록 설정
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
                        lastLogin = document.getTimestamp("lastLogin") ?: Timestamp.now()
                    )

                    view.findViewById<TextView>(R.id.username).text = userProfile.username
                    // "Points"는 첫 번째 TextView에 고정
                    view.findViewById<TextView>(R.id.points_label).text = "Points"

                    // points 값은 두 번째 TextView에 설정
                    view.findViewById<TextView>(R.id.points_value).text = userProfile.points.toString()
                    // "Badge"는 첫 번째 TextView에 고정
                    view.findViewById<TextView>(R.id.badges_label).text = "Badge"

                    // badge 값은 두 번째 TextView에 설정
                    view.findViewById<TextView>(R.id.badges_value).text = userProfile.badge

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

    private fun showBookmarksDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bookmarks, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // RecyclerView 설정
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.dialogRecyclerView)
        recyclerView.apply {
            adapter = RecipeAdapter().apply { submitList(bookmarkedRecipes) } // 북마크된 레시피 연결
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 닫기 버튼 클릭 이벤트
        val closeButton = dialogView.findViewById<Button>(R.id.closeDialogButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // 다이얼로그 띄우기
        dialog.show()
    }

}
