package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.FieldProjectionMode
import com.example.data.model.UserDocument
import com.example.data.model.UserStatus
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusInactive
import com.example.ui.theme.StatusPending
import com.example.ui.viewmodel.RubelNetManagerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UsersAuditScreen(
    viewModel: RubelNetManagerViewModel
) {
    val language by viewModel.language.collectAsState()
    val daysThreshold by viewModel.daysThreshold.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val projectionMode by viewModel.projectionMode.collectAsState()
    val selectedUserIds by viewModel.selectedUserIds.collectAsState()
    val pageSize by viewModel.pageSize.collectAsState()
    val pagedResult by viewModel.pagedUsers.collectAsState()

    var showUserDetailsSheet by remember { mutableStateOf<UserDocument?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Query Banner & Explanation
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(FirebaseAmber, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "টার্গেট ফায়ারস্টোর কুয়েরি" else "Target Firestore Query",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "${pagedResult.totalCount} জন ম্যাচিং" else "${pagedResult.totalCount} Matching",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual query code block
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "users.where('lastLoginAt', '<', ${daysThreshold}d_ago)\n     .where('status', '==', '${statusFilter?.name?.lowercase() ?: "active"}')",
                                color = Color(0xFF38BDF8),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "গত $daysThreshold দিনের মধ্যে লগইন করেনি এবং স্ট্যাটাস '${statusFilter?.labelBn ?: "সকল"}' এমন ইউজার ফিল্টার করা হচ্ছে।"
                            else
                                "Filtering users who haven't logged in within the last $daysThreshold days with status '${statusFilter?.labelEn ?: "All"}'.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Controls: Threshold & Status Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "নিষ্ক্রিয়তার সময়সীমা (Inactivity Threshold):" else "Inactivity Threshold:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(7, 14, 30, 60).forEach { days ->
                            FilterChip(
                                selected = daysThreshold == days,
                                onClick = { viewModel.setDaysThreshold(days) },
                                label = {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) "> $days দিন" else "> $days days",
                                        fontWeight = if (daysThreshold == days) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (daysThreshold == days) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FirebaseAmber.copy(alpha = 0.25f),
                                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.testTag("threshold_${days}_chip")
                            )
                        }
                    }
                }
            }

            // Status Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ব্যবহারকারীর স্ট্যাটাস (User Status):" else "User Status:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = statusFilter == null,
                            onClick = { viewModel.setStatusFilter(null) },
                            label = { Text(if (language == AppLanguage.BENGALI) "সকল" else "All") }
                        )
                        UserStatus.values().forEach { st ->
                            FilterChip(
                                selected = statusFilter == st,
                                onClick = { viewModel.setStatusFilter(st) },
                                label = {
                                    Text(if (language == AppLanguage.BENGALI) st.labelBn else st.labelEn)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (st) {
                                        UserStatus.ACTIVE -> StatusActive.copy(alpha = 0.25f)
                                        UserStatus.INACTIVE -> StatusInactive.copy(alpha = 0.25f)
                                        UserStatus.PENDING -> StatusPending.copy(alpha = 0.25f)
                                        UserStatus.SUSPENDED -> Color.Gray.copy(alpha = 0.25f)
                                    }
                                )
                            )
                        }
                    }
                }
            }

            // Field Projection & Data Optimization Toggle
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (projectionMode == FieldProjectionMode.PROJECTED)
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DataSaverOn,
                                contentDescription = "Field Projection",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "ফিল্ড প্রজেকশন অপ্টিমাইজেশন" else "Selective Field Projection",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (language == AppLanguage.BENGALI)
                                        if (projectionMode == FieldProjectionMode.PROJECTED)
                                            "সক্রিয়: শুধু id, email, lastLoginAt আনা হচ্ছে (~৭৬% মেমরি সেভ)"
                                        else "বন্ধ: সম্পূর্ণ ডকুমেন্ট ফেচ হচ্ছে (${pagedResult.estimatedPayloadBytes} bytes)"
                                    else
                                        if (projectionMode == FieldProjectionMode.PROJECTED)
                                            "Active: mapped {id, email, lastLoginAt} (~76% memory saved)"
                                        else "Disabled: full document fetching (${pagedResult.estimatedPayloadBytes} bytes)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = projectionMode == FieldProjectionMode.PROJECTED,
                            onCheckedChange = { isChecked ->
                                viewModel.setProjectionMode(
                                    if (isChecked) FieldProjectionMode.PROJECTED else FieldProjectionMode.FULL_DOCUMENT
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.testTag("projection_switch")
                        )
                    }
                }
            }

            // Search and Multi-select row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ইউজার খুঁজুন (নাম, ইমেইল)..." else "Search user...",
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        } else null,
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_user_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Select All / Deselect button
                    FilledTonalButton(
                        onClick = { viewModel.selectAllCurrentPage() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("select_all_button")
                    ) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "পেজ সিলেক্ট" else "Select Page",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Users List
            if (pagedResult.users.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = StatusActive,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "কোনো ইনঅ্যাক্টিভ ইউজার পাওয়া যায়নি!"
                                else
                                    "No inactive users found for this query!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "সব ইউজার সম্প্রতি লগইন করেছেন অথবা ফিল্টারের মান পরিবর্তন করুন।"
                                else
                                    "All users are active or try adjusting threshold days.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(pagedResult.users, key = { it.id }) { user ->
                    val isSelected = selectedUserIds.contains(user.id)
                    UserItemCard(
                        user = user,
                        language = language,
                        isSelected = isSelected,
                        projectionMode = projectionMode,
                        onToggleSelect = { viewModel.toggleUserSelection(user.id) },
                        onClick = { showUserDetailsSheet = user }
                    )
                }
            }

            // Pagination Controls Footer
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Previous button
                        OutlinedButton(
                            onClick = { viewModel.prevPage() },
                            enabled = pagedResult.currentPage > 0,
                            modifier = Modifier.testTag("prev_page_button")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == AppLanguage.BENGALI) "পূর্ববর্তী" else "Prev", fontSize = 12.sp)
                        }

                        // Page info
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "পেজ ${pagedResult.currentPage + 1} / ${pagedResult.totalPages}"
                                else
                                    "Page ${pagedResult.currentPage + 1} of ${pagedResult.totalPages}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "প্রতি পেজে $pageSize জন"
                                else
                                    "Limit: $pageSize docs",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Next button
                        OutlinedButton(
                            onClick = { viewModel.nextPage() },
                            enabled = pagedResult.currentPage < pagedResult.totalPages - 1,
                            modifier = Modifier.testTag("next_page_button")
                        ) {
                            Text(if (language == AppLanguage.BENGALI) "পরবর্তী" else "Next", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Floating Batch Action Bar
        AnimatedVisibility(
            visible = selectedUserIds.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "${selectedUserIds.size}",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "নির্বাচিত" else "Selected",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Mark Inactive
                        IconButton(
                            onClick = { viewModel.markSelectedAsInactive() },
                            modifier = Modifier.testTag("batch_mark_inactive_button")
                        ) {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = "Mark Inactive",
                                tint = StatusInactive
                            )
                        }

                        // Send Re-engagement Email
                        IconButton(
                            onClick = { viewModel.triggerReengagementForSelected() },
                            modifier = Modifier.testTag("batch_reengage_button")
                        ) {
                            Icon(
                                Icons.Default.Mail,
                                contentDescription = "Send Re-engagement",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Mark Active
                        IconButton(
                            onClick = { viewModel.markSelectedAsActive() },
                            modifier = Modifier.testTag("batch_mark_active_button")
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Mark Active",
                                tint = StatusActive
                            )
                        }

                        // Clear
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Selection")
                        }
                    }
                }
            }
        }
    }

    // User Details Modal Bottom Sheet
    if (showUserDetailsSheet != null) {
        val user = showUserDetailsSheet!!
        ModalBottomSheet(
            onDismissRequest = { showUserDetailsSheet = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            UserDetailsBottomSheetContent(
                user = user,
                language = language,
                onDismiss = { showUserDetailsSheet = null }
            )
        }
    }
}

@Composable
fun UserItemCard(
    user: UserDocument,
    language: AppLanguage,
    isSelected: Boolean,
    projectionMode: FieldProjectionMode,
    onToggleSelect: () -> Unit,
    onClick: () -> Unit
) {
    val daysAgo = user.daysSinceLastLogin()
    val dateFormat = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("user_card_${user.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                modifier = Modifier.testTag("checkbox_${user.id}")
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Avatar initial circle
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = when (user.status) {
                            UserStatus.ACTIVE -> StatusActive.copy(alpha = 0.2f)
                            UserStatus.INACTIVE -> StatusInactive.copy(alpha = 0.2f)
                            UserStatus.PENDING -> StatusPending.copy(alpha = 0.2f)
                            UserStatus.SUSPENDED -> Color.Gray.copy(alpha = 0.2f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = when (user.status) {
                        UserStatus.ACTIVE -> StatusActive
                        UserStatus.INACTIVE -> StatusInactive
                        UserStatus.PENDING -> StatusPending
                        UserStatus.SUSPENDED -> Color.Gray
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    // Status badge
                    Surface(
                        color = when (user.status) {
                            UserStatus.ACTIVE -> StatusActive.copy(alpha = 0.15f)
                            UserStatus.INACTIVE -> StatusInactive.copy(alpha = 0.15f)
                            UserStatus.PENDING -> StatusPending.copy(alpha = 0.15f)
                            UserStatus.SUSPENDED -> Color.Gray.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) user.status.labelBn else user.status.labelEn,
                            color = when (user.status) {
                                UserStatus.ACTIVE -> StatusActive
                                UserStatus.INACTIVE -> StatusInactive
                                UserStatus.PENDING -> StatusPending
                                UserStatus.SUSPENDED -> Color.Gray
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = user.email,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (language == AppLanguage.BENGALI)
                            "শেষ লগইন: $daysAgo দিন আগে"
                        else
                            "Last login: $daysAgo days ago",
                        fontSize = 11.sp,
                        color = if (daysAgo >= 7) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (daysAgo >= 7) FontWeight.SemiBold else FontWeight.Normal
                    )

                    if (projectionMode == FieldProjectionMode.FULL_DOCUMENT) {
                        Text(
                            text = user.device.take(16),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Projected",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserDetailsBottomSheetContent(
    user: UserDocument,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: ${user.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            }
            Surface(
                color = when (user.status) {
                    UserStatus.ACTIVE -> StatusActive.copy(alpha = 0.2f)
                    UserStatus.INACTIVE -> StatusInactive.copy(alpha = 0.2f)
                    UserStatus.PENDING -> StatusPending.copy(alpha = 0.2f)
                    UserStatus.SUSPENDED -> Color.Gray.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.BENGALI) user.status.labelBn else user.status.labelEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = when (user.status) {
                        UserStatus.ACTIVE -> StatusActive
                        UserStatus.INACTIVE -> StatusInactive
                        UserStatus.PENDING -> StatusPending
                        UserStatus.SUSPENDED -> Color.Gray
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Document Properties
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow("Email", user.email)
                DetailRow(
                    if (language == AppLanguage.BENGALI) "শেষ লগইন (lastLoginAt)" else "lastLoginAt",
                    "${dateFormat.format(Date(user.lastLoginAt))} (${user.daysSinceLastLogin()} days ago)"
                )
                DetailRow(
                    if (language == AppLanguage.BENGALI) "রেজিস্ট্রেশন তারিখ" else "Created At",
                    dateFormat.format(Date(user.createdAt))
                )
                DetailRow("Role", user.role)
                DetailRow("Device", user.device)
                DetailRow("Region", user.region)
                DetailRow("Login Count", "${user.loginCount}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(if (language == AppLanguage.BENGALI) "বন্ধ করুন" else "Close")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
