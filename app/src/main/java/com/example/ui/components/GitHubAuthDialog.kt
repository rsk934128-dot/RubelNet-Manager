package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.GitHubProfile
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import com.example.ui.viewmodel.RubelNetManagerViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GitHubAuthDialog(
    viewModel: RubelNetManagerViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val gitHubUser by viewModel.gitHubUser.collectAsState()
    val isLoading by viewModel.isGitHubLoading.collectAsState()

    var customUsername by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }
    var customEmail by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF24292F), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GH",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "GitHub অথেন্টিফিকেশন" else "GitHub Authentication",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Firebase OAuth Provider (github.com)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = if (gitHubUser != null) StatusActive.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (gitHubUser != null) "LOGGED IN" else "NOT CONNECTED",
                        color = if (gitHubUser != null) StatusActive else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // User is authenticated
                if (gitHubUser != null) {
                    val user = gitHubUser!!
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusActive.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(Color(0xFF24292F), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.login.take(2).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = FirebaseAmber.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = user.role.uppercase(),
                                                color = FirebaseAmber,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "@${user.login}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = user.email,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // GitHub Stats row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Repositories", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${user.publicRepos}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Followers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${user.followers}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Auth Provider", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("github.com", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusActive)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Granted OAuth Scopes
                            Text(
                                text = if (language == AppLanguage.BENGALI) "অনুমোদিত স্কোপসমূহ (Granted Scopes):" else "Granted OAuth Scopes:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                user.scopes.forEach { sc ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = sc,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Sign Out / Switch account
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.signOutGitHub() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusInactive),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (language == AppLanguage.BENGALI) "লগআউট" else "Sign Out", fontSize = 12.sp)
                                }

                                FilledTonalButton(
                                    onClick = { showCustomInput = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (language == AppLanguage.BENGALI) "অ্যাকাউন্ট পরিবর্তন" else "Switch User", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    // Not authenticated view
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "আপনার GitHub অ্যাকাউন্ট দিয়ে লগইন করে RubelNet Firebase Manager এ অ্যাডমিন অ্যাক্সেস পান।"
                                else
                                    "Authenticate with GitHub to gain authorized admin access in RubelNet Firebase Manager.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Big Primary Sign In With GitHub Button
                            Button(
                                onClick = {
                                    viewModel.signInWithGitHubAccount("rsk934128-dot", "RSK (rsk934128-dot)", "fs2217732@gmail.com", "Owner & Admin")
                                },
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292F)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("github_login_button")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Authenticating...", color = Color.White)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("GH", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "@rsk934128-dot দিয়ে লগইন করুন" else "Sign in as @rsk934128-dot",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Switcher / Custom Login section
                if (showCustomInput || gitHubUser == null) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "অন্য GitHub অ্যাকাউন্টে দ্রুত লগইন:" else "Quick GitHub Account Switcher:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.signInWithGitHubAccount("rsk934128-dot", "RSK (rsk934128-dot)", "fs2217732@gmail.com", "Owner & Admin")
                                    showCustomInput = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("@rsk934128-dot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.signInWithGitHubAccount("rubelnet", "Rubel Ahmed", "rubel@rubelnet.com", "admin")
                                    showCustomInput = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("@rubelnet", fontSize = 11.sp)
                            }
                        }

                        // Custom Handle input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customUsername,
                                onValueChange = { customUsername = it },
                                placeholder = { Text("GitHub Handle e.g. octocat", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (customUsername.isNotBlank()) {
                                        val u = customUsername.trim().replace("@", "")
                                        viewModel.signInWithGitHubAccount(u, u.replaceFirstChar { it.uppercase() }, "$u@github.com", "member")
                                        showCustomInput = false
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (language == AppLanguage.BENGALI) "লগইন" else "Login", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Firebase OAuth Provider Setup Code
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ফায়ারবেস অ্যান্ড্রয়েড OAuthProvider কোড:" else "Firebase Android OAuth Code:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    val oauthCode = """
// Firebase Auth GitHub Provider Setup
val provider = OAuthProvider.newBuilder("github.com").apply {
    scopes = listOf("user:email", "read:user")
    addCustomParameter("allow_signup", "true")
}

FirebaseAuth.getInstance()
    .startActivityForSignInWithProvider(activity, provider.build())
    .addOnSuccessListener { authResult ->
        val user = authResult.user
        val credential = authResult.credential as? OAuthCredential
        val githubToken = credential?.accessToken
        Log.d("RubelNet", "Logged in: ${'$'}{user?.displayName} (@${'$'}{user?.email})")
    }
    .addOnFailureListener { e ->
        Log.e("RubelNet", "GitHub Auth Error", e)
    }
""".trimIndent()

                    CodeBlockView(
                        code = oauthCode,
                        languageLabel = "Kotlin (Android SDK)"
                    )
                }

                // OAuth Configuration Info
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "GitHub Developer Settings Callback URL:" else "GitHub OAuth Callback URL:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "https://rubelnet-mgr.firebaseapp.com/__/auth/handler",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (language == AppLanguage.BENGALI) "ঠিক আছে" else "Close")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
