package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.FilterOperator
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.FirebaseNavy
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import com.example.ui.viewmodel.RubelNetManagerViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QueryOptimizerScreen(
    viewModel: RubelNetManagerViewModel
) {
    val language by viewModel.language.collectAsState()
    val customFilters by viewModel.customFilters.collectAsState()
    val report by viewModel.optimizationReport.collectAsState()

    var newField by remember { mutableStateOf("") }
    var selectedOp by remember { mutableStateOf(FilterOperator.EQUAL) }
    var newValue by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Query Inspector Status
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (report.needsCompositeIndex)
                        FirebaseAmber.copy(alpha = 0.15f)
                    else
                        StatusActive.copy(alpha = 0.15f)
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
                                imageVector = if (report.needsCompositeIndex) Icons.Default.Warning else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (report.needsCompositeIndex) FirebaseAmber else StatusActive,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BENGALI)
                                        if (report.needsCompositeIndex) "কম্পোজিট ইনডেক্স প্রয়োজন (Required)" else "সাধারণ ইনডেক্স যথেষ্ট (Optimal)"
                                    else
                                        if (report.needsCompositeIndex) "Composite Index Required" else "Single Field Index Sufficient",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (report.needsCompositeIndex) MaterialTheme.colorScheme.error else StatusActive
                                )
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "ফায়ারস্টোর কোয়েরি ইনডেক্সিং অডিট" else "Firestore Query Indexing Audit",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = if (report.needsCompositeIndex) Color(0xFFFEF3C7) else Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (report.needsCompositeIndex) "INDEX NEEDED" else "NO EXTRA INDEX",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (report.needsCompositeIndex) Color(0xFFB45309) else Color(0xFF065F46),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Explanations
                    val reasons = if (language == AppLanguage.BENGALI) report.reasonsBn else report.reasonsEn
                    reasons.forEach { reason ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text(text = "• ", fontWeight = FontWeight.Bold, color = FirebaseAmber)
                            Text(
                                text = reason,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Active Compound Filters Visualizer
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "বর্তমান কম্পাউন্ড কুয়েরি ফিল্টার:" else "Active Query Filters:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    customFilters.forEachIndexed { index, filter ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = filter.field,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = filter.operator.symbol,
                                    color = Color(0xFFF59E0B),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = filter.value,
                                    color = Color(0xFFA7F3D0),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            if (customFilters.size > 1) {
                                IconButton(
                                    onClick = { viewModel.removeCustomFilter(index) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Remove filter",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Composite Index Configuration (When needed)
        if (report.needsCompositeIndex && report.compositeIndex != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "১. ইনডেক্স তৈরির কনফিগারেশন (Index Setup)" else "1. Index Setup Command & JSON",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "ফায়ারবেস কনসোলে অথবা CLI ব্যবহার করে এই ইনডেক্সটি ডিপ্লয় করুন:"
                        else
                            "Deploy this index via Firebase CLI or firestore.indexes.json:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    CodeBlockView(
                        code = report.compositeIndex!!.toCliCommand(),
                        languageLabel = "Firebase CLI Command"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    CodeBlockView(
                        code = report.compositeIndex!!.toJsonSnippet(),
                        languageLabel = "firestore.indexes.json"
                    )
                }
            }
        }

        // Section 2: Pagination & Limiting Implementation
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "২. পেজিনেশন ও লিমিটিং (Pagination & Limiting)" else "2. Pagination & Limiting",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = if (language == AppLanguage.BENGALI) report.paginationAdviceBn else report.paginationAdviceEn,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val paginationCode = """
// TypeScript Genkit / Cloud Function Pagination
const PAGE_SIZE = 50;

async function getInactiveUsersPaginated(lastDoc = null) {
  let q = query(
    collection(db, 'users'),
    where('lastLoginAt', '<', cutoffTimestamp),
    where('status', '==', 'active'),
    orderBy('lastLoginAt', 'desc'), // ইনডেক্সের সাথে মিলিয়ে অর্ডারিং
    limit(PAGE_SIZE)
  );

  if (lastDoc) {
    q = query(q, startAfter(lastDoc));
  }

  const snapshot = await getDocs(q);
  const inactiveUsers = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  console.log(`[RubelNet Genkit] Found ${'$'}{inactiveUsers.length} inactive users on this page`);

  return {
    users: inactiveUsers,
    lastDoc: snapshot.docs[snapshot.docs.length - 1]
  };
}
""".trimIndent()

                CodeBlockView(
                    code = paginationCode,
                    languageLabel = "TypeScript / Genkit Flow"
                )
            }
        }

        // Section 3: Field Projection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "৩. ফিল্ড প্রজেকশন অপ্টিমাইজেশন (Field Projection)" else "3. Field Projection",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = if (language == AppLanguage.BENGALI)
                        "সম্পূর্ণ ডকুমেন্ট ফেচ না করে doc.data() থেকে শুধু প্রয়োজনীয় ফিল্ডগুলো এক্সট্রাক্ট করলে মেমরি ও ক্লায়েন্ট ব্যান্ডউইথ বহুগুণে সাশ্রয় হয়।"
                    else
                        "Mapping only required fields from doc.data() drastically reduces client-side memory overhead.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val projectionCode = """
const inactiveUsers = snapshot.docs.map(doc => {
  const data = doc.data();
  return {
    id: doc.id,
    email: data.email,       // শুধু email ও lastLoginAt দরকার হলে
    lastLoginAt: data.lastLoginAt
  };
});
""".trimIndent()

                CodeBlockView(
                    code = projectionCode,
                    languageLabel = "Field Projection"
                )
            }
        }

        // Section 4: Error Handling & Try-Catch
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "৪. ত্রুটি হ্যান্ডেলিং (Error Handling)" else "4. Error Handling & Resilience",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                val errorHandlingCode = """
try {
  const snapshot = await getDocs(inactiveUsersQuery);
  const inactiveUsers = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  console.log(`[RubelNet Genkit] Found ${'$'}{inactiveUsers.length} inactive users`);
} catch (error) {
  console.error("[RubelNet Genkit] Error fetching inactive users:", error);
  // ইনডেক্স অনুপস্থিতি বা নেটওয়ার্ক এরর হ্যান্ডেল করুন
}
""".trimIndent()

                CodeBlockView(
                    code = errorHandlingCode,
                    languageLabel = "Try-Catch Resilience"
                )
            }
        }
    }
}
