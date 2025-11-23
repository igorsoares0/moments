package com.example.moments.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(
    selectedIndex: Int = 0,
    onHomeClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onProjectsClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onHomeClick) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = if (selectedIndex == 0) Color(0xFF7C3AED) else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(
            onClick = onAddClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = Color(0xFF7C3AED),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(onClick = onProjectsClick) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Projects",
                tint = if (selectedIndex == 2) Color(0xFF7C3AED) else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
