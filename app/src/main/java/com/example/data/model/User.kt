package com.example.data.model

enum class UserStatus(val labelEn: String, val labelBn: String) {
    ACTIVE("Active", "সক্রিয়"),
    INACTIVE("Inactive", "নিষ্ক্রিয়"),
    SUSPENDED("Suspended", "স্থগিত"),
    PENDING("Pending", "অপেক্ষমান")
}

enum class FieldProjectionMode(val titleEn: String, val titleBn: String, val avgByteSize: Int) {
    FULL_DOCUMENT("Full Document (All Fields)", "সম্পূর্ণ ডকুমেন্ট (সব ফিল্ড)", 1280),
    PROJECTED("Selective Projection (id, email, lastLoginAt)", "প্রয়োজনীয় ফিল্ড প্রজেকশন", 310)
}

enum class AppLanguage(val code: String, val displayName: String) {
    BENGALI("bn", "বাংলা"),
    ENGLISH("en", "English")
}

data class UserDocument(
    val id: String,
    val name: String,
    val email: String,
    val status: UserStatus,
    val lastLoginAt: Long, // timestamp in millis
    val createdAt: Long,
    val role: String = "member",
    val loginCount: Int = 12,
    val device: String = "Android 14 (Pixel 8)",
    val region: String = "Dhaka, Bangladesh",
    val photoUrl: String = "",
    val metadataExtra: Map<String, String> = emptyMap()
) {
    fun daysSinceLastLogin(currentTimeMillis: Long = System.currentTimeMillis()): Int {
        val diff = currentTimeMillis - lastLoginAt
        return (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(0).toInt()
    }
}
