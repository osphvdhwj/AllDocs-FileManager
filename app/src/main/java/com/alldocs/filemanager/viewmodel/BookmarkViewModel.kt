package com.alldocs.filemanager.viewmodel

import androidx.lifecycle.ViewModel
import com.alldocs.filemanager.model.Bookmark
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.UUID

class BookmarkViewModel : ViewModel() {
    
    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()
    
    init {
        loadDefaultBookmarks()
    }
    
    private fun loadDefaultBookmarks() {
        val defaults = listOf(
            Bookmark(
                id = UUID.randomUUID().toString(),
                name = "Downloads",
                path = "/storage/emulated/0/Download",
                order = 0
            ),
            Bookmark(
                id = UUID.randomUUID().toString(),
                name = "Documents",
                path = "/storage/emulated/0/Documents",
                order = 1
            ),
            Bookmark(
                id = UUID.randomUUID().toString(),
                name = "Pictures",
                path = "/storage/emulated/0/Pictures",
                order = 2
            ),
            Bookmark(
                id = UUID.randomUUID().toString(),
                name = "DCIM",
                path = "/storage/emulated/0/DCIM",
                order = 3
            )
        )
        _bookmarks.value = defaults
    }
    
    fun addBookmark(name: String, file: File) {
        val bookmark = Bookmark(
            id = UUID.randomUUID().toString(),
            name = name,
            path = file.absolutePath,
            order = _bookmarks.value.size
        )
        _bookmarks.value = _bookmarks.value + bookmark
    }
    
    fun removeBookmark(bookmarkId: String) {
        _bookmarks.value = _bookmarks.value.filter { it.id != bookmarkId }
    }
    
    fun updateBookmark(bookmarkId: String, newName: String) {
        _bookmarks.value = _bookmarks.value.map { bookmark ->
            if (bookmark.id == bookmarkId) {
                bookmark.copy(name = newName)
            } else bookmark
        }
    }
    
    fun reorderBookmarks(fromIndex: Int, toIndex: Int) {
        val mutableList = _bookmarks.value.toMutableList()
        val item = mutableList.removeAt(fromIndex)
        mutableList.add(toIndex, item)
        _bookmarks.value = mutableList.mapIndexed { index, bookmark ->
            bookmark.copy(order = index)
        }
    }
}