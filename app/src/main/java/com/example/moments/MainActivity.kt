package com.example.moments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import com.example.moments.data.models.MediaItem
import com.example.moments.data.models.Template
import com.example.moments.ui.screens.ChooseMediasScreen
import com.example.moments.ui.screens.HomeScreen
import com.example.moments.ui.screens.PreviewScreen
import com.example.moments.ui.screens.ViewTemplateScreen
import com.example.moments.ui.theme.MomentsTheme

enum class Screen {
    HOME, VIEW_TEMPLATE, CHOOSE_MEDIAS, PREVIEW
}

@UnstableApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomentsTheme {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var selectedTemplate by remember { mutableStateOf<Template?>(null) }
                var selectedMedias by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
                var videoUri by remember { mutableStateOf<Uri?>(null) }

                when (currentScreen) {
                    Screen.HOME -> {
                        HomeScreen(
                            onTemplateClick = { template ->
                                selectedTemplate = template
                                currentScreen = Screen.VIEW_TEMPLATE
                            }
                        )
                    }

                    Screen.VIEW_TEMPLATE -> {
                        selectedTemplate?.let { template ->
                            ViewTemplateScreen(
                                template = template,
                                onClose = { currentScreen = Screen.HOME },
                                onOpenClick = { currentScreen = Screen.CHOOSE_MEDIAS }
                            )
                        }
                    }

                    Screen.CHOOSE_MEDIAS -> {
                        selectedTemplate?.let { template ->
                            ChooseMediasScreen(
                                template = template,
                                onClose = { currentScreen = Screen.VIEW_TEMPLATE },
                                onContinue = { medias ->
                                    selectedMedias = medias
                                },
                                onVideoCreated = { uri ->
                                    videoUri = uri
                                    currentScreen = Screen.PREVIEW
                                }
                            )
                        }
                    }

                    Screen.PREVIEW -> {
                        selectedTemplate?.let { template ->
                            videoUri?.let { uri ->
                                PreviewScreen(
                                    videoUri = uri,
                                    template = template,
                                    selectedMedias = selectedMedias,
                                    onClose = { currentScreen = Screen.HOME },
                                    onShare = {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "video/mp4"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        startActivity(Intent.createChooser(intent, "Share video"))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}