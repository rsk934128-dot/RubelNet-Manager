package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import com.example.data.model.PwaAuditCheck
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import com.example.ui.viewmodel.RubelNetManagerViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PwaBuilderScreen(
    viewModel: RubelNetManagerViewModel
) {
    val language by viewModel.language.collectAsState()
    val manifest by viewModel.pwaManifest.collectAsState()
    val twaConfig by viewModel.twaConfig.collectAsState()
    val auditChecks by viewModel.pwaAuditChecks.collectAsState()

    var editAppName by remember { mutableStateOf(manifest.name) }
    var editShortName by remember { mutableStateOf(manifest.shortName) }
    var editStartUrl by remember { mutableStateOf(manifest.startUrl) }
    var editThemeColor by remember { mutableStateOf(manifest.themeColor) }
    var editDisplay by remember { mutableStateOf(manifest.display) }
    var editHost by remember { mutableStateOf(twaConfig.host) }

    val totalScore = auditChecks.filter { it.isPassed }.sumOf { it.weight }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header: PWABuilder & TWA
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
                                imageVector = Icons.Default.InstallMobile,
                                contentDescription = null,
                                tint = FirebaseAmber,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "PWABuilder & TWA স্টুডিও" else "PWABuilder & TWA Studio",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "PWA কে অ্যান্ড্রয়েড APK / AAB প্যাকেজে রূপান্তর" else "Convert Web App to Android APK/AAB",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = StatusActive.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Play Store Ready",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusActive,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "PWABuilder এর মাধ্যমে আপনার ওয়েব অ্যাপ্লিকেশন বা ফায়ারবেস ড্যাশবোর্ডকে Google Play Store এর জন্য Trusted Web Activity (TWA) ভিত্তিক অ্যান্ড্রয়েড অ্যাপ হিসেবে প্যাকেজ ও পাবলিশ করুন।"
                        else
                            "Package and deploy your Progressive Web App or Firebase web console into a high-performance Android Trusted Web Activity (TWA) using PWABuilder and Bubblewrap.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // PWA Store Readiness Score Card
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = StatusActive, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "PWA কোয়ালিটি ও স্টোর স্কোর" else "PWA Store Readiness Score",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "সব শর্ত পূরণ হয়েছে! অ্যাপটি সরাসরি Google Play Store এ পাবলিশ করার উপযোগী।"
                            else
                                "All criteria met! The app is fully qualified for Google Play Store listing via TWA.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = StatusActive.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "$totalScore",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = StatusActive
                            )
                            Text(
                                text = "/ 100",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Checklist Items
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "PWABuilder রিকোয়ারমেন্টস চেকলিস্ট:" else "PWABuilder Checklist:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                auditChecks.forEach { check ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(StatusActive.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = StatusActive, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) check.titleBn else check.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (language == AppLanguage.BENGALI) check.detailBn else check.detailEn,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = StatusActive.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "+${check.weight}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusActive,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Manifest.json Generator & Customizer
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = FirebaseAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ওয়েব অ্যাপ ম্যানিফেস্ট এডিটর" else "Web App Manifest Editor",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.updatePwaManifest(
                                    name = editAppName,
                                    shortName = editShortName,
                                    themeColor = editThemeColor,
                                    display = editDisplay,
                                    startUrl = editStartUrl
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber),
                            modifier = Modifier.testTag("save_manifest_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "সংরক্ষণ" else "Save",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editAppName,
                        onValueChange = { editAppName = it },
                        label = { Text("App Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editShortName,
                            onValueChange = { editShortName = it },
                            label = { Text("Short Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editThemeColor,
                            onValueChange = { editThemeColor = it },
                            label = { Text("Theme Color") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == AppLanguage.BENGALI) "ডিসপ্লে মোড (Display):" else "Display Mode:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("standalone", "fullscreen", "minimal-ui").forEach { disp ->
                            FilterChip(
                                selected = editDisplay == disp,
                                onClick = { editDisplay = disp },
                                label = { Text(disp, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Live Generated manifest.json Code Block
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "manifest.webmanifest (or manifest.json):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                CodeBlockView(
                    code = manifest.toJson(),
                    languageLabel = "JSON"
                )
            }
        }

        // Digital Asset Links (.well-known/assetlinks.json)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "ডিজিটাল অ্যাসেট লিংকস (.well-known/assetlinks.json):" else "Digital Asset Links (assetlinks.json):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    text = if (language == AppLanguage.BENGALI)
                        "TWA অ্যাপে অ্যান্ড্রয়েড ক্রোম ইউআরএল বার মুক্ত ফুল-স্ক্রিন চালানোর জন্য আপনার ডোমেইনের /.well-known/assetlinks.json পাথে এই ফাইলটি রাখুন:"
                    else
                        "Upload this file to https://${twaConfig.host}/.well-known/assetlinks.json to verify ownership and remove the URL browser bar:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                CodeBlockView(
                    code = twaConfig.toAssetLinksJson(),
                    languageLabel = "assetlinks.json"
                )
            }
        }

        // PWABuilder / Bubblewrap CLI Command
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "PWABuilder / Bubblewrap প্যাকেজিং কমান্ড:" else "Bubblewrap & PWABuilder Build Command:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                CodeBlockView(
                    code = """
# Install PWABuilder CLI (Bubblewrap)
npm i -g @bubblewrap/cli

# Initialize TWA Android package from Manifest
bubblewrap init --manifest=https://${twaConfig.host}/manifest.json

# Build signed Android App Bundle (.aab) for Google Play
bubblewrap build
""".trimIndent(),
                    languageLabel = "Bash / Terminal"
                )
            }
        }

        // Service Worker offline persistence recipe
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "ফায়ারবেস অফলাইন সার্ভিস ওয়ার্কার (service-worker.js):" else "Firebase PWA Service Worker:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                val swCode = """
// service-worker.js for RubelNet PWA
const CACHE_NAME = 'rubelnet-pwa-v1';
const ASSETS = [
  '/',
  '/index.html',
  '/manifest.json',
  '/icons/icon-192.png',
  '/icons/icon-512.png'
];

self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(CACHE_NAME).then(c => c.addAll(ASSETS)));
});

self.addEventListener('fetch', (e) => {
  // Stale-while-revalidate strategy for UI & offline Firestore
  e.respondWith(
    caches.match(e.request).then(cached => {
      const network = fetch(e.request).then(res => {
        const copy = res.clone();
        caches.open(CACHE_NAME).then(c => c.put(e.request, copy));
        return res;
      }).catch(() => cached);
      return cached || network;
    })
  );
});
""".trimIndent()

                CodeBlockView(
                    code = swCode,
                    languageLabel = "JavaScript (SW)"
                )
            }
        }
    }
}
