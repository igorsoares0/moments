package com.example.moments.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moments.data.models.Template
import com.example.moments.data.models.TemplateCategory
import com.example.moments.ui.components.BottomNavBar
import com.example.moments.ui.components.TemplateCard

@Composable
fun HomeScreen() {
    // Mock data for templates
    val featureTemplates = listOf(
        Template(1, "Feature 1", "", 7, 72, TemplateCategory.FEATURE),
        Template(2, "Feature 2", "", 7, 72, TemplateCategory.FEATURE)
    )

    val newTemplates = listOf(
        Template(3, "New 1", "", 7, 72, TemplateCategory.NEW),
        Template(4, "New 2", "", 7, 72, TemplateCategory.NEW),
        Template(5, "New 3", "", 7, 72, TemplateCategory.NEW)
    )

    val mostViewedTemplates = listOf(
        Template(6, "Most Viewed 1", "", 7, 72, TemplateCategory.MOST_VIEWED),
        Template(7, "Most Viewed 2", "", 7, 72, TemplateCategory.MOST_VIEWED),
        Template(8, "Most Viewed 3", "", 7, 72, TemplateCategory.MOST_VIEWED)
    )

    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        bottomBar = {
            BottomNavBar(selectedIndex = 0)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "TRY PLUS",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "Our Templates",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Feature Section
                item {
                    Text(
                        text = "feature",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featureTemplates) { template ->
                            TemplateCard(template = template)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // New Section
                item {
                    Text(
                        text = "New",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(newTemplates) { template ->
                            TemplateCard(template = template)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Most Viewed Section
                item {
                    Text(
                        text = "Most viewed",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(mostViewedTemplates) { template ->
                            TemplateCard(template = template)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
