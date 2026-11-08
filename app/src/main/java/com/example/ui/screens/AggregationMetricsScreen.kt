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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import java.util.Locale
import com.example.ui.components.CodeBlockView
import com.example.ui.components.FirestoreAggregateStatsView
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import com.example.ui.viewmodel.RubelNetManagerViewModel

@Composable
fun AggregationMetricsScreen(
    viewModel: RubelNetManagerViewModel
) {
    val language by viewModel.language.collectAsState()
    val aggregatedCount by viewModel.aggregatedInactiveCount.collectAsState()
    val firestoreStats by viewModel.firestoreStats.collectAsState()
    val isBenchmarking by viewModel.isBenchmarkingLatency.collectAsState()

    var userCountK by remember { mutableFloatStateOf(50f) } // in thousands (10k to 500k)
    var dailyChecks by remember { mutableFloatStateOf(50f) } // checks per day

    val rawReadsPerDay = (userCountK * 1000 * dailyChecks).toLong()
    val aggregatedReadsPerDay = (24 + dailyChecks).toLong() // 24 hourly cron runs + 1 read per check
    val rawMonthlyCostUsd = (rawReadsPerDay * 30.0 / 1_000_000.0) * 0.06 // $0.06 per 100k reads approx
    val aggMonthlyCostUsd = (aggregatedReadsPerDay * 30.0 / 1_000_000.0) * 0.06
    val savingsUsd = (rawMonthlyCostUsd - aggMonthlyCostUsd).coerceAtLeast(0.0)

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
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = FirebaseAmber,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ডাটা মডেলিং ও এগ্রিগেশন" else "Data Modeling & Aggregation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Surface(
                            color = StatusActive.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "99.9% Savings",
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
                            "যদি শুধু ইনঅ্যাক্টিভ ইউজারের সংখ্যা জানা প্রয়োজন হয়, তবে প্রতিবার সম্পূর্ণ কালেকশন কুয়েরি না করে adminStatus ডকুমেন্টে এগ্রিগেশন ক্যাশ সংরক্ষণ করাই সর্বোত্তম প্র্যাকটিস।"
                        else
                            "If only the count of inactive users is needed, maintaining an aggregation document in 'adminStatus/inactiveUsersCount' saves massive read costs.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Aggregate Firestore Usage Statistics & Average Query Latency Composable
        item {
            FirestoreAggregateStatsView(
                stats = firestoreStats,
                language = language,
                isBenchmarking = isBenchmarking,
                onRunBenchmark = { viewModel.runLatencyBenchmark() }
            )
        }

        // Live Aggregation Stat Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "adminStatus/inactiveUsersCount" else "adminStatus/inactiveUsersCount",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "সিঙ্ক করা ইনঅ্যাক্টিভ ইউজার সংখ্যা" else "Cached Inactive Count",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = FirebaseAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "$aggregatedCount",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = FirebaseAmber,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Interactive Cost & Read Calculator
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ফায়ারস্টোর রিড ও বিলিং সেভিংস ক্যালকুলেটর" else "Firestore Reads & Cost Savings Calculator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slider 1: Total Users
                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "মোট ব্যবহারকারী সংখ্যা: ${(userCountK * 1000).toInt()} জন"
                        else
                            "Total Users in Collection: ${(userCountK * 1000).toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = userCountK,
                        onValueChange = { userCountK = it },
                        valueRange = 5f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = FirebaseAmber,
                            activeTrackColor = FirebaseAmber
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider 2: Daily Dashboard Views
                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "দৈনিক ড্যাশবোর্ড চেক: ${dailyChecks.toInt()} বার"
                        else
                            "Daily Dashboard Inquiries: ${dailyChecks.toInt()} times",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = dailyChecks,
                        onValueChange = { dailyChecks = it },
                        valueRange = 5f..150f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondary,
                            activeTrackColor = MaterialTheme.colorScheme.secondary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Comparison Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Raw query card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StatusInactive.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "সম্পূর্ণ কালেকশন ফেচ" else "Raw Document Scan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusInactive
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${rawReadsPerDay / 1000}k",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = StatusInactive
                                )
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "রিড/দিন (~$${String.format(Locale.US, "%.1f", rawMonthlyCostUsd)}/মাস)" else "reads/day (~$${String.format(Locale.US, "%.1f", rawMonthlyCostUsd)}/mo)",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Aggregated card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StatusActive.copy(alpha = 0.1f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "এগ্রিগেশন ডকুমেন্ট" else "Aggregated Document",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusActive
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$aggregatedReadsPerDay",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = StatusActive
                                )
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "রিড/দিন (~$0.01/মাস)" else "reads/day (~$0.01/mo)",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Savings banner
                    Surface(
                        color = StatusActive.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = StatusActive,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "মাসিক সাশ্রয়: প্রায় $${String.format(Locale.US, "%.2f", savingsUsd)} ডলার এবং ৯৯.৯% লোড রিডাকশন!"
                                else
                                    "Monthly Savings: approx $${String.format(Locale.US, "%.2f", savingsUsd)} USD with 99.9% read reduction!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusActive
                            )
                        }
                    }
                }
            }
        }

        // Section: Aggregation Cloud Function / Cron snippet
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "এগ্রিগেশন ক্রন জব কোড (Cloud Functions / Genkit):" else "Aggregation Scheduled Function:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                val aggregationSnippet = """
// Firebase Cloud Function v2 Scheduled Aggregation
export const syncInactiveUsersCounter = onSchedule('every 1 hours', async (event) => {
  const sevenDaysAgo = new Date();
  sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

  const q = query(
    collection(db, 'users'),
    where('lastLoginAt', '<', Timestamp.fromDate(sevenDaysAgo)),
    where('status', '==', 'active')
  );

  // Firestore Server Aggregation Count (1 read)
  const countSnapshot = await getCountFromServer(q);
  const totalInactive = countSnapshot.data().count;

  // Store in dedicated admin document
  await setDoc(doc(db, 'adminStatus', 'inactiveUsersCount'), {
    count: totalInactive,
    updatedAt: FieldValue.serverTimestamp()
  }, { merge: true });

  console.log(`[RubelNet Genkit] Inactive users count synced: ${'$'}{totalInactive}`);
});
""".trimIndent()

                CodeBlockView(
                    code = aggregationSnippet,
                    languageLabel = "Cloud Function v2 (Scheduled)"
                )
            }
        }
    }
}
