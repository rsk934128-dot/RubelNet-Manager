package com.example.data.model

data class FirestoreUsageStats(
    val totalDocuments: Int = 40,
    val activeUsersCount: Int = 26,
    val inactiveUsersCount: Int = 14,
    val avgLatencyMs: Double = 38.4,
    val p95LatencyMs: Double = 64.0,
    val minLatencyMs: Double = 16.2,
    val maxLatencyMs: Double = 112.0,
    val cacheHitRatePct: Double = 94.6,
    val readsToday: Long = 1480,
    val writesToday: Long = 192,
    val bandwidthSavedKb: Double = 2840.0,
    val totalQueriesExecuted: Int = 142,
    val lastBenchmarkTime: Long = System.currentTimeMillis(),
    val latencyHistoryMs: List<Double> = listOf(42.0, 36.5, 31.0, 48.2, 34.0, 38.4, 29.8, 41.2, 35.0, 38.4)
)
