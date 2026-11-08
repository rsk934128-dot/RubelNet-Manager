package com.example.data.model

enum class FlowStatus {
    IDLE,
    RUNNING,
    SUCCESS,
    FAILED
}

data class GenkitLogEntry(
    val timestamp: String,
    val level: String, // INFO, WARN, ERROR, SUCCESS
    val message: String
)

data class GenkitFlowItem(
    val id: String,
    val name: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val codeSnippet: String,
    val defaultParams: Map<String, String>,
    var lastStatus: FlowStatus = FlowStatus.IDLE,
    var lastRunTime: Long = 0,
    var lastDurationMs: Long = 0,
    var recordsProcessed: Int = 0
)

data class SecurityRuleTest(
    val id: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val requestAuthId: String?,
    val userRole: String?,
    val userStatus: String,
    val targetPath: String,
    val operation: String, // "read", "write", "list"
    val shouldAllow: Boolean,
    val reasonBn: String,
    val reasonEn: String
)
