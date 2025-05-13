package com.example.securescan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.data.models.RssNewsItem
import com.example.securescan.data.repository.RssRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RssNewsViewModel : ViewModel() {
    private val repository = RssRepository()
    private val _newsItems = MutableStateFlow<List<RssNewsItem>>(emptyList())
    val newsItems: StateFlow<List<RssNewsItem>> = _newsItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedNews = MutableStateFlow<RssNewsItem?>(null)
    val selectedNews: StateFlow<RssNewsItem?> = _selectedNews

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getHackerNews()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { items ->
                    _newsItems.value = items
                    _isLoading.value = false
                }
        }
    }

    fun setSelectedNews(newsItem: RssNewsItem) {
        _selectedNews.value = newsItem
    }

    fun getNewsByGuid(guid: String): RssNewsItem? {
        return newsItems.value.find { it.guid == guid }
    }
} 