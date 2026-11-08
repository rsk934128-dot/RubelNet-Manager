package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.FirestoreUsageStats
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FirestoreAggregateStatsView(
    stats: FirestoreUsageStats,
    language: AppLanguage,
    isBenchmarking: Boolean = false,
    onRunBenchmark: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .testTag("firestore_aggregate_stats_view")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title & Benchmark Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(FirebaseAmber.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = FirebaseAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "ফায়ারস্টোর সার্বিক ব্যবহার ও লেটেন্সি"
                                else
                                    "Firestore Aggregate Usage & Latency",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Live heartbeat dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(StatusActive, CircleShape)
                            )
                        }
                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "সর্বশেষ বেঞ্চমার্ক: ${timeFormat.format(Date(stats.lastBenchmarkTime))}"
                            else
                                "Last benchmark: ${timeFormat.format(Date(stats.lastBenchmarkTime))}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Benchmark Trigger Button
                FilledTonalButton(
                    onClick = onRunBenchmark,
                    enabled = !isBenchmarking,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = FirebaseAmber.copy(alpha = 0.18f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("benchmark_latency_button")
                ) {
                    if (isBenchmarking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = FirebaseAmber,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "পরিমাপ হচ্ছে..." else "Testing...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = FirebaseAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "লেটেন্সি টেস্ট" else "Test Latency",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary 2 Big Metric Cards (Total Documents & Average Latency)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Document Count Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_total_documents")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "মোট ডকুমেন্ট" else "Total Documents",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${stats.totalDocuments}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "${stats.activeUsersCount} সক্রিয় • ${stats.inactiveUsersCount} নিষ্ক্রিয়"
                            else
                                "${stats.activeUsersCount} active • ${stats.inactiveUsersCount} inactive",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Average Query Latency Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusActive.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusActive.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_average_latency")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "গড় কুয়েরি লেটেন্সি" else "Average Latency",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusActive
                            )
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = StatusActive,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${stats.avgLatencyMs}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = StatusActive
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "ms",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusActive,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "P95: ${stats.p95LatencyMs}ms (Min: ${stats.minLatencyMs}ms)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Stats Row: Reads Today, Cache Hit Rate, Bandwidth Saved
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Reads Today
                Column {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "দৈনিক রিড অপারেশন" else "Reads Today",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${stats.readsToday}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Cache Hit Ratio
                Column {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ক্যাশ হিট অনুপাত" else "Cache Hit Rate",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${stats.cacheHitRatePct}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusActive
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Cached,
                            contentDescription = null,
                            tint = StatusActive,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Bandwidth Saved
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ডাটা সাশ্রয়" else "Bandwidth Saved",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", stats.bandwidthSavedKb / 1024.0)} MB",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Latency History Sparkline & Distribution
            Text(
                text = if (language == AppLanguage.BENGALI)
                    "সাম্প্রতিক কুয়েরি এক্সিকিউশন লেটেন্সি হিস্ট্রি:"
                else
                    "Recent Query Latency Samples (ms):",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val maxVal = stats.latencyHistoryMs.maxOrNull() ?: 100.0
                stats.latencyHistoryMs.forEachIndexed { idx, value ->
                    val ratio = (value / maxVal.coerceAtLeast(1.0)).toFloat().coerceIn(0.15f, 1f)
                    val barHeight by animateFloatAsState(targetValue = ratio, animationSpec = tween(400))

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "${value.toInt()}",
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((32 * barHeight).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    when {
                                        value <= 40.0 -> StatusActive
                                        value <= 70.0 -> FirebaseAmber
                                        else -> StatusInactive
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Query Benchmark Comparison Matrix
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "লেটেন্সি ও পারফরম্যান্স তুলনা (Query Strategy Comparison):"
                        else
                            "Firestore Strategy Latency Benchmarks:",
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )

                    LatencyComparisonRow(
                        label = if (language == AppLanguage.BENGALI) "এগ্রিগেশন ডকুমেন্ট (adminStatus)" else "Aggregation Document (adminStatus)",
                        latency = "~18ms",
                        cost = "1 Read",
                        status = "Ultra-Fast",
                        statusColor = StatusActive
                    )

                    LatencyComparisonRow(
                        label = if (language == AppLanguage.BENGALI) "কম্পোজিট ইনডেক্স কুয়েরি (where lastLogin < 7d)" else "Indexed Query (where lastLogin < 7d)",
                        latency = "${stats.avgLatencyMs}ms",
                        cost = "50 Reads (Page)",
                        status = "Optimal",
                        statusColor = FirebaseAmber
                    )

                    LatencyComparisonRow(
                        label = if (language == AppLanguage.BENGALI) "ইনডেক্স ছাড়া ফুল কালেকশন স্ক্যান" else "Unindexed Full Collection Scan",
                        latency = "~420ms+",
                        cost = "50,000 Reads",
                        status = "Slow / Inefficient",
                        statusColor = StatusInactive
                    )
                }
            }
        }
    }
}

@Composable
private fun LatencyComparisonRow(
    label: String,
    latency: String,
    cost: String,
    status: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE2E8F0)
            )
            Text(
                text = "Cost: $cost",
                fontSize = 9.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = latency,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                color = statusColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
