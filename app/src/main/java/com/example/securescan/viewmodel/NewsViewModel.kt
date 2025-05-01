package com.example.securescan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.models.CommentPost
import com.example.securescan.data.models.NewsItem
import com.example.securescan.data.repository.NewsRepository
import com.example.securescan.data.repository.NewsSocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository = NewsRepository() ,
    private val socialRepository: NewsSocialRepository = NewsSocialRepository()

) : ViewModel() {

    // UI state
    private val _searchQuery = MutableStateFlow("")

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userLikes = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val userLikes: StateFlow<Map<String, Boolean>> = _userLikes

    private val _currentNewsItem = MutableStateFlow<NewsItem?>(null)
    val currentNewsItem: StateFlow<NewsItem?> = _currentNewsItem

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _currentComments = MutableStateFlow<List<CommentPost>>(emptyList())
    val currentComments: StateFlow<List<CommentPost>> = _currentComments

    //  News theo tìm kiếm
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredNews: StateFlow<List<NewsItem>> = _searchQuery
        .flatMapLatest { query -> newsRepository.searchNews(query) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    //  3 tin nổi bật
    val featuredNews: StateFlow<List<NewsItem>> = newsRepository
        .getFeaturedNews()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    //  Tất cả tin tức
    val allNews: StateFlow<List<NewsItem>> = newsRepository
        .getAllNews()

        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    //  Theo ID
    fun getNewsById(id: String): Flow<NewsItem?> {
        return newsRepository.getNewsById(id)
    }

    //  Tương tác UI
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun startSearching() {
        _isSearching.value = true
    }

    fun closeSearch() {
        _isSearching.value = false
        _searchQuery.value = ""
    }

    fun toggleLike(postId: String) {
        Log.d("NewsViewModel", "toggleLike called for post $postId")
        viewModelScope.launch {
            try {
                Log.d("NewsViewModel", "Starting like process for post $postId")
                socialRepository.likePost(
                    postId = postId,
                    onSuccess = {
                        Log.d("NewsViewModel", "Like operation successful for post $postId")
                        checkUserLikedPost(postId)
                        // Cập nhật số lượng like
                        viewModelScope.launch {
                            Log.d("NewsViewModel", "Updating news item for post $postId")
                            getNewsById(postId).collect { newsItem ->
                                if (newsItem != null) {
                                    Log.d("NewsViewModel", "Updated news item: $newsItem")
                                    _currentNewsItem.value = newsItem
                                }
                            }
                        }
                    },
                    onError = { e ->
                        Log.e("NewsViewModel", "Error in like operation for post $postId", e)
                        _error.value = e.message
                    }
                )
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Exception in toggleLike for post $postId", e)
                _error.value = e.message
            }
        }
    }

    fun checkUserLikedPost(postId: String) {
        Log.d("NewsViewModel", "checkUserLikedPost called for post $postId")
        socialRepository.checkIfUserLikedPost(postId) { isLiked ->
            Log.d("NewsViewModel", "User like status for post $postId: $isLiked")
            _userLikes.value = _userLikes.value.toMutableMap().apply {
                put(postId, isLiked)
            }
        }
    }
    fun loadComments(postId: String) {
        socialRepository.getCommentForPost(
            postId = postId,
            onSuccess = { commentList ->
                _currentComments.value = commentList
            },
            onError = { e ->
                _error.value = e.message
            }
        )
    }
    fun addComment(postId: String, content: String, onComplete: (Boolean) -> Unit) {
        if (content.isBlank()) {
            _error.value = "Bình luận không thể trống"
            onComplete(false)
            return
        }

        socialRepository.addComment(
            postId = postId,
            content = content,
            onSuccess = {
                onComplete(true)
            },
            onError = { e ->
                _error.value = e.message
                onComplete(false)
            }
        )
    }
    fun sharePost(postId: String, onComplete: (Boolean) -> Unit) {
        socialRepository.sharePost(
            postId = postId,
            onSuccess = {
                onComplete(true)
            },
            onError = { e ->
                _error.value = e.message
                onComplete(false)
            }
        )
    }

    private val notificationViewModel = NotificationViewModel()


}