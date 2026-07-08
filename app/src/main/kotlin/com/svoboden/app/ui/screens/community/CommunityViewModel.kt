package com.svoboden.app.ui.screens.community

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CommunityPost(
    val id: Int,
    val author: String,
    val time: String,
    val title: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val tag: String,
    val tagColor: Color,
    val hasImage: Boolean = false
)

@HiltViewModel
class CommunityViewModel @Inject constructor() : ViewModel() {
    private val _posts = MutableStateFlow(
        listOf(
            CommunityPost(
                id = 1,
                author = "Джеймс Д.",
                time = "2 часа назад",
                title = "Как я выжил в первую неделю",
                content = "Первые 3 дня были самыми трудными. Я постоянно тянулся к телефону, чтобы отвлечься от тяги. Что мне больше всего помогло, так это техника дыхания 4-7-8, когда наступала волна...",
                likes = 124,
                comments = 18,
                tag = "ВЕХА",
                tagColor = Color(0xFF68D391),
                hasImage = true
            ),
            CommunityPost(
                id = 2,
                author = "Мария К.",
                time = "5 часов назад",
                title = "Советы по управлению стрессом",
                content = "Стресс часто является самым большим триггером для рецидива. Я начала вести 5-минутный утренний дневник, где записываю три вещи, за которые я благодарна. Это звучит просто, но это меняет весь мой настрой...",
                likes = 89,
                comments = 7,
                tag = "СОВЕТЫ",
                tagColor = Color(0xFFF6AD55)
            ),
            CommunityPost(
                id = 3,
                author = "Сэм С.",
                time = "Вчера",
                title = "Достиг 30-дневного рубежа!",
                content = "Чувствую себя яснее и энергичнее, чем когда-либо. Путь не был линейным, но он того стоит.",
                likes = 256,
                comments = 42,
                tag = "ВЕХА",
                tagColor = Color(0xFF68D391)
            )
        )
    )
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Все истории")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    val filteredPosts: StateFlow<List<CommunityPost>> = kotlinx.coroutines.flow.combine(_posts, _selectedFilter) { posts, filter ->
        if (filter == "Все истории") posts
        else posts.filter { it.tag.equals(filter, ignoreCase = true) || (filter == "Вехи" && it.tag == "ВЕХА") || (filter == "Снятие стресса" && it.tag == "СОВЕТЫ") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun toggleLike(postId: Int) {
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(likes = it.likes + 1) else it
        }
    }

    fun shareProgress(author: String, title: String, content: String) {
        val newPost = CommunityPost(
            id = (_posts.value.maxOfOrNull { it.id } ?: 0) + 1,
            author = author,
            time = "Только что",
            title = title,
            content = content,
            likes = 0,
            comments = 0,
            tag = "ВЕХА",
            tagColor = Color(0xFF68D391)
        )
        _posts.value = listOf(newPost) + _posts.value
    }
}
