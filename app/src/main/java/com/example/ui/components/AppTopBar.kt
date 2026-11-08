package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.GitHubProfile
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.StatusActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    language: AppLanguage,
    gitHubUser: GitHubProfile?,
    onToggleLanguage: () -> Unit,
    onGitHubClick: () -> Unit,
    onAddUserClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = FirebaseAmber,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RN",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "RubelNet Manager" else "RubelNet Manager",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Status dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(StatusActive, CircleShape)
                            )
                        }
                        Text(
                            text = if (language == AppLanguage.BENGALI) "ফায়ারবেস ও জেনকিট অপ্টিমাইজার" else "Firestore & Genkit Optimizer",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            actions = {
                // GitHub Auth Button
                AssistChip(
                    onClick = onGitHubClick,
                    label = {
                        Text(
                            text = if (gitHubUser != null) "@${gitHubUser.login}" else "GitHub",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(0xFF24292F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("GH", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Black)
                        }
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (gitHubUser != null) Color(0xFF24292F) else MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (gitHubUser != null) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("github_auth_top_button")
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Language Toggle Button
                AssistChip(
                    onClick = onToggleLanguage,
                    label = {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "BN" else "EN",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language Switch",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.testTag("language_toggle_button")
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Add User Icon
                FilledTonalIconButton(
                    onClick = onAddUserClick,
                    modifier = Modifier.testTag("add_user_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Test User",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}
