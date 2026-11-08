package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage

data class TabItem(
    val titleBn: String,
    val titleEn: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)

@Composable
fun NavigationTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    language: AppLanguage,
    inactiveUsersCount: Int
) {
    val tabs = listOf(
        TabItem("ইউজার অডিট", "Users Audit", Icons.Default.Group, "tab_users_audit"),
        TabItem("অপ্টিমাইজার", "Optimizer", Icons.Default.Bolt, "tab_optimizer"),
        TabItem("জেনকিট ফ্লো", "Genkit Flows", Icons.Default.RocketLaunch, "tab_genkit"),
        TabItem("সিকিউরিটি", "Security Rules", Icons.Default.Security, "tab_security"),
        TabItem("এগ্রিগেশন", "Aggregation", Icons.Default.Analytics, "tab_aggregation"),
        TabItem("PWABuilder", "PWABuilder", Icons.Default.Language, "tab_pwa_builder")
    )

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.testTag("main_tab_row")
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) tab.titleBn else tab.titleEn,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (index == 0 && inactiveUsersCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text(
                                    text = "$inactiveUsersCount",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.titleEn
                    )
                },
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}
