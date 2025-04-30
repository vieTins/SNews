package com.example.securescan.data.repository

import com.example.securescan.data.models.CommentPost
import com.example.securescan.data.models.LikePost
import com.example.securescan.data.models.NewsItem
import com.example.securescan.data.models.SharePost
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import android.util.Log

class NewsSocialRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val postsCollection = db.collection("posts")
    private val commentsCollection = db.collection("comments")
    private val likesCollection = db.collection("reactions")
    private val sharesCollection = db.collection("shares")

    // Hàm trợ giúp để lấy document reference từ postId (hỗ trợ cả string và int)
    private fun getPostRef(postId: String): DocumentReference {
        Log.d("NewsSocialRepository", "Accessing post with ID: $postId")
        return postsCollection.document(postId)
    }

    // Like bài đăng với xử lý tốt hơn
    fun likePost(postId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("NewsSocialRepository", "User not logged in")
            onError(Exception("User not logged in"))
            return
        }

        // Tìm bài đăng theo field "id" thay vì document ID
        postsCollection
            .whereEqualTo("id", postId)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val postDoc = snapshot.documents.firstOrNull()
                if (postDoc == null) {
                    Log.e("NewsSocialRepository", "Post with field id=$postId not found")

                    // Debug: Hiển thị toàn bộ bài viết
                    postsCollection.get().addOnSuccessListener { querySnapshot ->
                        Log.d("NewsSocialRepository", "Available posts in collection:")
                        for (doc in querySnapshot.documents) {
                            Log.d("NewsSocialRepository", "Document ID: ${doc.id}, data sample: ${doc.data?.keys}")
                        }
                        onError(Exception("Post not found"))
                    }.addOnFailureListener { e ->
                        Log.e("NewsSocialRepository", "Error listing posts", e)
                        onError(Exception("Post not found and failed to list posts: ${e.message}"))
                    }
                } else {
                    val postRef = postDoc.reference
                    processLikeUnlike(postRef, postId, currentUser.uid, onSuccess, onError)
                }
            }
            .addOnFailureListener { e ->
                Log.e("NewsSocialRepository", "Error fetching post by id", e)
                onError(e)
            }
    }


    // Xử lý logic like/unlike
    private fun processLikeUnlike(
        postRef: DocumentReference,
        postId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        likesCollection
            .whereEqualTo("postId", postId)
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    // Người dùng chưa like, thêm like
                    addLikeToPost(postRef, postId, userId, onSuccess, onError)
                } else {
                    // Đã like, thì unlike
                    val likeDoc = snapshot.documents.first()
                    removeLikeFromPost(postRef, likeDoc.reference, onSuccess, onError)
                }
            }
            .addOnFailureListener { e ->
                Log.e("NewsSocialRepository", "Error checking like status", e)
                onError(e)
            }
    }

    // Thêm like vào post
    private fun addLikeToPost(
        postRef: DocumentReference,
        postId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val like = LikePost(postId = postId, userId = userId)
        val newLikeRef = likesCollection.document()

        db.runTransaction { transaction ->
            val postSnapshot = transaction.get(postRef)

            val currentLikeCount = postSnapshot.getLong("likeCount") ?: 0
            Log.d("NewsSocialRepository", "Adding like - current count: $currentLikeCount")

            transaction.set(newLikeRef, like)
            transaction.update(postRef, "likeCount", currentLikeCount + 1)
        }.addOnSuccessListener {
            Log.d("NewsSocialRepository", "Like added successfully for postId: $postId by user: $userId")
            onSuccess()
        }.addOnFailureListener { e ->
            Log.e("NewsSocialRepository", "Failed to add like", e)
            onError(e)
        }
    }

    // Xóa like khỏi post
    private fun removeLikeFromPost(
        postRef: DocumentReference,
        likeRef: DocumentReference,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.runTransaction { transaction ->
            val postSnapshot = transaction.get(postRef)

            val currentLikeCount = postSnapshot.getLong("likeCount") ?: 0
            Log.d("NewsSocialRepository", "Removing like - current count: $currentLikeCount")

            transaction.delete(likeRef)
            transaction.update(postRef, "likeCount", maxOf(0, currentLikeCount - 1))
        }.addOnSuccessListener {
            Log.d("NewsSocialRepository", "Like removed successfully")
            onSuccess()
        }.addOnFailureListener { e ->
            Log.e("NewsSocialRepository", "Failed to remove like", e)
            onError(e)
        }
    }

    // Kiểm tra người dùng đã like post chưa
    fun checkIfUserLikedPost(postId: String, onResult: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("NewsSocialRepository", "User not logged in - cannot check like")
            onResult(false)
            return
        }

        likesCollection
            .whereEqualTo("postId", postId)
            .whereEqualTo("userId", currentUser.uid)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val isLiked = !snapshot.isEmpty
                Log.d("NewsSocialRepository", "Post $postId liked by ${currentUser.uid}: $isLiked")
                onResult(isLiked)
            }
            .addOnFailureListener { e ->
                Log.e("NewsSocialRepository", "Failed to check if liked", e)
                onResult(false)
            }
    }

    // Lấy comment cho một bài đăng
    fun getCommentForPost(
        postId: String,
        onSuccess: (List<CommentPost>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        commentsCollection
            .whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NewsSocialRepository", " Lỗi khi lấy comment của postId: $postId", error)
                    onError(error)
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(CommentPost::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("NewsSocialRepository", "️ Lỗi khi parse comment (docId: ${doc.id})", e)
                        null
                    }
                } ?: emptyList()

                Log.d("NewsSocialRepository", " Lấy được ${comments.size} comment cho postId: $postId")
                onSuccess(comments)
            }
    }

    // Thêm comment cho một bài đăng
    fun addComment(
        postId: String,
        content: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val err = Exception("Người dùng chưa đăng nhập")
            Log.e("NewsSocialRepository", " $err")
            onError(err)
            return
        }

        // Lấy thông tin người dùng từ collection users
        db.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { userDoc ->
                if (!userDoc.exists()) {
                    val err = Exception("Không tìm thấy thông tin người dùng")
                    Log.e("NewsSocialRepository", " $err")
                    onError(err)
                    return@addOnSuccessListener
                }

                val userName = userDoc.getString("name") ?: "Anonymous"
                val profilePic = userDoc.getString("profilePic")

                // Tìm bài đăng theo field "id" thay vì document ID
                postsCollection
                    .whereEqualTo("id", postId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val postDoc = snapshot.documents.firstOrNull()
                        if (postDoc == null) {
                            val err = Exception("Bài viết không tồn tại")
                            Log.e("NewsSocialRepository", " Không tìm thấy postId: $postId khi thêm comment")
                            onError(err)
                            return@addOnSuccessListener
                        }

                        val postRef = postDoc.reference
                        val comment = CommentPost(
                            postId = postId,
                            userId = currentUser.uid,
                            userName = userName,
                            content = content,
                            timestamp = System.currentTimeMillis(),
                            profilePic = profilePic
                        )

                        val newCommentRef = commentsCollection.document()

                        db.runTransaction { transaction ->
                            val freshPostSnapshot = transaction.get(postRef)
                            val currentCommentCount = freshPostSnapshot.getLong("commentCount") ?: 0

                            transaction.set(newCommentRef, comment)
                            transaction.update(postRef, "commentCount", currentCommentCount + 1)
                        }.addOnSuccessListener {
                            Log.d("NewsSocialRepository", " Thêm comment thành công cho postId: $postId")
                            onSuccess()
                        }.addOnFailureListener { e ->
                            Log.e("NewsSocialRepository", " Lỗi khi thêm comment vào postId: $postId", e)
                            onError(e)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("NewsSocialRepository", " Lỗi khi kiểm tra sự tồn tại của postId: $postId", e)
                        onError(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("NewsSocialRepository", " Lỗi khi lấy thông tin người dùng", e)
                onError(e)
            }
    }

    // Chia sẻ bài đăng
    fun sharePost(
        postId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val err = Exception("Người dùng chưa đăng nhập")
            Log.e("NewsSocialRepository", " $err")
            onError(err)
            return
        }

        // Tìm bài đăng theo field "id" thay vì document ID
        postsCollection
            .whereEqualTo("id", postId)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val postDoc = snapshot.documents.firstOrNull()
                if (postDoc == null) {
                    val err = Exception("Bài viết không tồn tại")
                    Log.e("NewsSocialRepository", " Không tìm thấy postId: $postId khi chia sẻ")
                    onError(err)
                    return@addOnSuccessListener
                }

                val postRef = postDoc.reference
                val share = SharePost(
                    postId = postId,
                    userId = currentUser.uid,
                    timestamp = System.currentTimeMillis()
                )

                val newShareRef = sharesCollection.document()

                db.runTransaction { transaction ->
                    val freshPostSnapshot = transaction.get(postRef)
                    val currentShareCount = freshPostSnapshot.getLong("shareCount") ?: 0

                    transaction.set(newShareRef, share)
                    transaction.update(postRef, "shareCount", currentShareCount + 1)
                }.addOnSuccessListener {
                    Log.d("NewsSocialRepository", "Chia sẻ postId: $postId thành công")
                    onSuccess()
                }.addOnFailureListener { e ->
                    Log.e("NewsSocialRepository", " Lỗi khi chia sẻ postId: $postId", e)
                    onError(e)
                }
            }
            .addOnFailureListener { e ->
                Log.e("NewsSocialRepository", " Lỗi khi kiểm tra postId: $postId để chia sẻ", e)
                onError(e)
            }
    }

}