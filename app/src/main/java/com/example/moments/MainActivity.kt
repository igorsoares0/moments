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
import com.example.moments.ui.screens.HomeScreen
import com.example.moments.ui.screens.ViewTemplateScreen
import com.example.moments.ui.theme.MomentsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomentsTheme {
                var selectedTemplate by remember { mutableStateOf<Template?>(null) }

                if (selectedTemplate != null) {
                    ViewTemplateScreen(
                        template = selectedTemplate!!,
                        onClose = { selectedTemplate = null },
                        onOpenClick = {
                            // TODO: Navigate to choose media screen
                        }
                    )
                } else {
                    HomeScreen(
                        onTemplateClick = { template ->
                            selectedTemplate = template
                        }
                    )
                }
            }
        }
    }
}