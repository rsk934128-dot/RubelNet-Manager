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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.GenkitLogEntry
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import com.example.ui.viewmodel.RubelNetManagerViewModel

@Composable
fun GenkitFlowsScreen(
    viewModel: RubelNetManagerViewModel
) {
    val language by viewModel.language.collectAsState()
    val isRunning by viewModel.isFlowRunning.collectAsState()
    val simulateError by viewModel.simulateError.collectAsState()
    val logs by viewModel.genkitLogs.collectAsState()
    val flows by viewModel.genkitFlows.collectAsState()
    val daysThreshold by viewModel.daysThreshold.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
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
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = FirebaseAmber,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "RubelNet Genkit ফ্লো স্টুডিও" else "RubelNet Genkit Studio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Surface(
                            color = FirebaseAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Genkit AI / Cloud Flow",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FirebaseAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "সার্ভার-সাইডে Genkit Flow চালিয়ে ইনঅ্যাক্টিভ ইউজার খোঁজা, ডাটা ফিল্টার করা ও এগ্রিগেশন কাউন্টার সিঙ্ক করার প্রসেস টেস্ট করুন।"
                        else
                            "Test server-side Genkit Flows for querying inactive users, running background audits, and syncing aggregation counters.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Action Panel & Runner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "auditInactiveUsersFlow",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "ইনপুট: daysThreshold = $daysThreshold দিন"
                                else
                                    "Params: daysThreshold = $daysThreshold days",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.runGenkitFlow("audit_inactive") },
                            enabled = !isRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("run_genkit_flow_button")
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Running...", color = Color.Black, fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "ফ্লো রান করুন" else "Run Flow",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Error Simulation Switch (Prompt Section 7: Try-Catch testing)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.BugReport,
                                    contentDescription = null,
                                    tint = if (simulateError) StatusInactive else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "এরর সিমুলেট (Error Simulation)" else "Simulate Missing Index Error",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "Try-catch ব্লক টেস্ট করার জন্য" else "Demonstrates try-catch graceful handling",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = simulateError,
                                onCheckedChange = { viewModel.toggleSimulateError() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = StatusInactive,
                                    checkedTrackColor = StatusInactive.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.testTag("simulate_error_switch")
                            )
                        }
                    }
                }
            }
        }

        // Live Console Log Window
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RubelNet Console Logs",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (logs.isNotEmpty()) {
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${logs.size} entries",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        if (logs.isEmpty()) {
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "ফ্লো রান করার পর এখানে লাইভ কনসোল লগ দৃশ্যমান হবে..."
                                else
                                    "Click 'Run Flow' above to see live execution logs...",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                logs.forEach { entry ->
                                    ConsoleLogLine(entry)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Source Code Definition for auditInactiveUsersFlow
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "Genkit Flow সোর্স কোড:" else "Genkit Flow Source Code:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                CodeBlockView(
                    code = flows.firstOrNull()?.codeSnippet ?: "",
                    languageLabel = "TypeScript Genkit"
                )
            }
        }
    }
}

@Composable
fun ConsoleLogLine(entry: GenkitLogEntry) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = entry.timestamp,
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            color = when (entry.level) {
                "SUCCESS" -> StatusActive.copy(alpha = 0.25f)
                "ERROR" -> StatusInactive.copy(alpha = 0.25f)
                "WARN" -> FirebaseAmber.copy(alpha = 0.25f)
                else -> Color(0xFF38BDF8).copy(alpha = 0.2f)
            },
            shape = RoundedCornerShape(3.dp)
        ) {
            Text(
                text = entry.level,
                color = when (entry.level) {
                    "SUCCESS" -> StatusActive
                    "ERROR" -> StatusInactive
                    "WARN" -> FirebaseAmber
                    else -> Color(0xFF38BDF8)
                },
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = entry.message,
            color = when (entry.level) {
                "ERROR" -> Color(0xFFFCA5A5)
                "SUCCESS" -> Color(0xFFA7F3D0)
                "WARN" -> Color(0xFFFDE68A)
                else -> Color(0xFFE2E8F0)
            },
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
