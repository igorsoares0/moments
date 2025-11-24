package com.example.moments.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.example.moments.data.models.MediaItem
import com.example.moments.data.video.VideoComposer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@UnstableApi
class VideoCompositionViewModel : ViewModel() {

    private val _compositionState = MutableStateFlow<VideoComposer.CompositionState>(
        VideoComposer.CompositionState.Idle
    )
    val compositionState: StateFlow<VideoComposer.CompositionState> = _compositionState.asStateFlow()

    fun createVideo(context: Context, mediaItems: List<MediaItem>, durations: List<Float>) {
        viewModelScope.launch {
            val composer = VideoComposer(context)
            composer.composeVideo(mediaItems, durations).collect { state ->
                _compositionState.value = state
            }
        }
    }

    fun resetState() {
        _compositionState.value = VideoComposer.CompositionState.Idle
    }
}
