package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.UserStatus

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddUserDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, status: UserStatus, daysAgo: Int, role: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(UserStatus.ACTIVE) }
    var daysAgo by remember { mutableIntStateOf(10) }
    var role by remember { mutableStateOf("member") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.BENGALI) "নতুন টেস্ট ইউজার যোগ করুন" else "Add Test User",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == AppLanguage.BENGALI) "ব্যবহারকারীর নাম" else "Full Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_user_name_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_user_email_input")
                )

                Text(
                    text = if (language == AppLanguage.BENGALI) "শেষ লগইন (কত দিন আগে?):" else "Last Login (Days Ago):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(2, 5, 8, 14, 30, 90).forEach { d ->
                        FilterChip(
                            selected = daysAgo == d,
                            onClick = { daysAgo = d },
                            label = { Text("$d d") }
                        )
                    }
                }

                Text(
                    text = if (language == AppLanguage.BENGALI) "স্ট্যাটাস (Status):" else "Status:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    UserStatus.values().forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(if (language == AppLanguage.BENGALI) st.labelBn else st.labelEn) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (name.isBlank()) "Test User ${System.currentTimeMillis() % 1000}" else name.trim()
                    val finalEmail = if (email.isBlank()) "user_${System.currentTimeMillis() % 1000}@rubelnet.com" else email.trim()
                    onConfirm(finalName, finalEmail, status, daysAgo, role)
                },
                modifier = Modifier.testTag("confirm_add_user_button")
            ) {
                Text(if (language == AppLanguage.BENGALI) "যোগ করুন" else "Create")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.BENGALI) "বাতিল" else "Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
