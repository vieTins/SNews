package com.example.securescan.utils

import android.util.Log
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

class TranslationManager {
    private var translator: Translator? = null

    suspend fun translateText(text: String, sourceLanguage: String = "vi", targetLanguage: String = "en"): String {
        return withContext(Dispatchers.IO) {
            try {
                // Parse HTML content
                val doc = Jsoup.parse(text)
                
                // Translate text nodes while preserving HTML structure
                translateNode(doc.body())
                
                // Get the translated HTML
                val translatedHtml = doc.body().html()
                Log.d("TranslationManager", "Original HTML: $text")
                Log.d("TranslationManager", "Translated HTML: $translatedHtml")
                
                translatedHtml
            } catch (e: Exception) {
                e.printStackTrace()
                text
            }
        }
    }

    private suspend fun translateNode(element: Element) {
        // Create translator if not exists
        if (translator == null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage("vi")
                .setTargetLanguage("en")
                .build()
            translator = Translation.getClient(options)
            translator?.downloadModelIfNeeded()?.await()
        }

        // Process each child node
        for (node in element.childNodes()) {
            when (node) {
                is TextNode -> {
                    // Only translate non-empty text nodes
                    if (node.text().trim().isNotEmpty()) {
                        try {
                            val translatedText = translator?.translate(node.text())?.await()
                            if (translatedText != null) {
                                node.text(translatedText)
                            }
                        } catch (e: Exception) {
                            Log.e("TranslationManager", "Error translating text: ${node.text()}", e)
                        }
                    }
                }
                is Element -> {
                    // Skip translation for specific tags
                    if (!shouldSkipTranslation(node.tagName())) {
                        translateNode(node)
                    }
                }
            }
        }
    }

    private fun shouldSkipTranslation(tagName: String): Boolean {
        // Add tags that should not be translated
        return tagName in listOf("script", "style", "code", "pre")
    }

    fun close() {
        translator?.close()
        translator = null
    }
} 