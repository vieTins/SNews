package com.example.securescan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.models.NewsItem
import com.example.securescan.data.repository.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class NewsViewModel(
    private val newsRepository: NewsRepository = NewsRepository()
) : ViewModel() {

    // UI state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // 🔍 News theo tìm kiếm
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredNews: StateFlow<List<NewsItem>> = _searchQuery
        .flatMapLatest { query -> newsRepository.searchNews(query) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // 🌟 3 tin nổi bật
    val featuredNews: StateFlow<List<NewsItem>> = newsRepository
        .getFeaturedNews()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // 📋 Tất cả tin tức
    val allNews: StateFlow<List<NewsItem>> = newsRepository
        .getAllNews()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // 🔍 Theo ID
    fun getNewsById(id: Int): Flow<NewsItem?> {
        return newsRepository.getNewsById(id)
    }

    // 📌 Tương tác UI
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
}