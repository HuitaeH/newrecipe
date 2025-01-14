package Adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.recipee.R
import com.example.recipee.RecipeDetailFragment
import datas.Recipe
import com.example.recipee.databinding.ItemRecipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private val recipes = mutableListOf<Recipe>()


    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                // Fragment Transaction으로 상세 화면으로 이동
                val fragment = RecipeDetailFragment()
                val transaction = (itemView.context as AppCompatActivity)
                    .supportFragmentManager.beginTransaction()

                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        fun bind(recipe: Recipe) {
            with(binding) {
                recipeTitle.text = recipe.title
                recipeBadge.text = recipe.badge
                recipeTime.text = "${recipe.cookingTime} min"
                authorName.text = recipe.authorName

                // 좋아요 버튼 상태 설정
                likeButton.setImageResource(
                    if (recipe.isLiked) R.drawable.like
                    else R.drawable.like
                )

                // 좋아요 수 표시
                likeCount.text = recipe.likeCount.toString()

                // 좋아요 버튼 클릭 리스너
                likeButton.setOnClickListener {
                    recipe.isLiked = !recipe.isLiked
                    recipe.likeCount += if (recipe.isLiked) 1 else -1
                    notifyItemChanged(adapterPosition)
                }
                // Glide를 통해 이미지 로드 등


                // 이미지 로딩은 Glide 또는 Coil 라이브러리 사용 필요
                // Glide.with(itemView).load(recipe.imageUrl).into(recipeImage)
                // Glide.with(itemView).load(recipe.authorImageUrl).into(profileImage)

                bookmarkButton.setImageResource(
                    if (recipe.isBookmarked) R.drawable.ic_bookmarked
                    else R.drawable.ic_bookmark
                )

                bookmarkButton.setOnClickListener {
                    recipe.isBookmarked = !recipe.isBookmarked
                    notifyItemChanged(adapterPosition)

                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
                        userRef.get().addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Get the current list of bookmarked posts or initialize an empty list
                                val bookmarkedList = document.get("bookmarked") as? MutableList<String> ?: mutableListOf()

                                if (recipe.isBookmarked) {
                                    // Add the post ID to the bookmarked list
                                    if (!bookmarkedList.contains(recipe.postId)) {
                                        bookmarkedList.add(recipe.postId)
                                    }
                                } else {
                                    // Remove the post ID from the bookmarked list
                                    bookmarkedList.remove(recipe.postId)
                                }

                                // Update the user's bookmarked list in Firestore
                                userRef.update("bookmarked", bookmarkedList)
                                    .addOnSuccessListener {
                                        // Optional: Notify the user of success
                                        Toast.makeText(context, "Bookmark updated successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        // Optional: Handle the error
                                        Toast.makeText(context, "Failed to update bookmark: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }.addOnFailureListener { e ->
                            // Optional: Handle the error in fetching user profile
                            Toast.makeText(context, "Failed to fetch user data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount() = recipes.size

    fun submitList(newRecipes: List<Recipe>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }
}