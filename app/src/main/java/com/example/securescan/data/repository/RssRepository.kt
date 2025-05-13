package com.example.securescan.data.repository

import android.util.Log
import com.example.securescan.data.models.RssNewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL

class RssRepository {
    private val hackerNewsUrl = "https://feeds.feedburner.com/TheHackersNews"

    fun getHackerNews(): Flow<List<RssNewsItem>> = flow {
        try {
            val url = URL(hackerNewsUrl)
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(url.openStream(), "UTF-8")

            val newsItems = mutableListOf<RssNewsItem>()
            var eventType = parser.eventType
            var currentItem: MutableMap<String, String>? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "item" -> currentItem = mutableMapOf()
                            "title", "description", "link", "pubDate", "author", "guid" -> {
                                if (currentItem != null) {
                                    currentItem[parser.name] = parser.nextText()
                                }
                            }
                            "enclosure" -> {
                                if (currentItem != null) {
                                    currentItem["imageUrl"] = parser.getAttributeValue(null, "url")
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "item" && currentItem != null) {
                            newsItems.add(
                                RssNewsItem(
                                    title = currentItem["title"] ?: "",
                                    description = currentItem["description"] ?: "",
                                    link = currentItem["link"] ?: "",
                                    pubDate = currentItem["pubDate"] ?: "",
                                    author = currentItem["author"] ?: "",
                                    imageUrl = currentItem["imageUrl"],
                                    guid = currentItem["guid"] ?: ""
                                )
                            )
                            currentItem = null
                        }
                    }
                }
                eventType = parser.next()
            }
            emit(newsItems)
        } catch (e: Exception) {
            Log.e("RssRepository", "Error fetching RSS feed", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
} 