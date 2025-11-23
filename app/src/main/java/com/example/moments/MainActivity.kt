package com.example.moments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.moments.data.models.Template
import com.example.moments.ui.screens.ChooseMediasScreen
import com.example.moments.ui.screens.HomeScreen
import com.example.moments.ui.screens.ViewTemplateScreen
import com.example.moments.ui.theme.MomentsTheme

enum class Screen {
    HOME, VIEW_TEMPLATE, CHOOSE_MEDIAS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomentsTheme {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var selectedTemplate by remember { mutableStateOf<Template?>(null) }

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
                                onContinue = { selectedMedias ->
                                    // TODO: Navigate to preview screen
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}