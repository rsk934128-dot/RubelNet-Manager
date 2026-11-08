package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.SecurityRuleTest
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import com.example.ui.viewmodel.RubelNetManagerViewModel

@Composable
fun SecurityRulesScreen(
    viewModel: RubelNetManagerViewModel
) {
    val language by viewModel.language.collectAsState()
    val tests by viewModel.securityRuleTests.collectAsState()
    val report by viewModel.optimizationReport.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = StatusActive,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ফায়ারস্টোর সিকিউরিটি রুলস" else "Firestore Security Rules",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Surface(
                            color = StatusActive.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Zero-Trust v2",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusActive,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "সঠিক সিকিউরিটি রুলস অননুমোদিত রিড প্রতিরোধ করে এবং বিলিং বৃদ্ধি রক্ষা করে। রুলস অডিট ও পলিসি সিমুলেশন:"
                        else
                            "Robust security rules prevent unauthorized scans and safeguard from accidental runaway billing. Rules audit & policy tests:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section: Active Rules Code
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "প্রস্তাবিত firestore.rules:" else "Recommended firestore.rules:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                CodeBlockView(
                    code = report.rulesSnippet,
                    languageLabel = "firestore.rules (v2)"
                )
            }
        }

        // Section: Rule Evaluation Tests
        item {
            Text(
                text = if (language == AppLanguage.BENGALI) "রুলস সিমুলেশন ও অ্যাক্সেস পলিসি যাচাই:" else "Security Rules Policy Simulator:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        items(tests, key = { it.id }) { test ->
            SecurityTestCard(test = test, language = language)
        }
    }
}

@Composable
fun SecurityTestCard(test: SecurityRuleTest, language: AppLanguage) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = if (test.shouldAllow) StatusActive.copy(alpha = 0.2f) else StatusInactive.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (test.shouldAllow) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (test.shouldAllow) StatusActive else StatusInactive,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) test.descriptionBn else test.descriptionEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Surface(
                    color = if (test.shouldAllow) StatusActive.copy(alpha = 0.15f) else StatusInactive.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (test.shouldAllow) "ALLOW" else "DENY",
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = if (test.shouldAllow) StatusActive else StatusInactive,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Simulated request context details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Auth: ${test.requestAuthId ?: "null (anonymous)"} [${test.userRole ?: "none"}]",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${test.operation.uppercase()} ${test.targetPath}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (language == AppLanguage.BENGALI) "কারণ: ${test.reasonBn}" else "Reason: ${test.reasonEn}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
