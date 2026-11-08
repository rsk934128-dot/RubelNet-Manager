package com.example

import com.example.data.model.FilterOperator
import com.example.data.model.GitHubProfile
import com.example.data.model.PwaManifest
import com.example.data.model.QueryFilter
import com.example.data.model.TwaPackageConfig
import com.example.data.model.UserStatus
import com.example.data.repository.FirestoreSimulatorRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_inactive_users_query_filters() {
        val repository = FirestoreSimulatorRepository()
        val (users, total) = repository.queryInactiveUsers(
            daysThreshold = 7,
            statusFilter = UserStatus.ACTIVE,
            pageIndex = 0,
            pageSize = 50
        )
        assertTrue("Total inactive matching should be > 0", total > 0)
        assertTrue("Page items should not be empty", users.isNotEmpty())

        val cutoff = System.currentTimeMillis() - (7 * 86_400_000L)
        users.forEach { user ->
            assertEquals(UserStatus.ACTIVE, user.status)
            assertTrue("lastLoginAt must be older than 7 days", user.lastLoginAt < cutoff)
        }
    }

    @Test
    fun test_composite_index_detection() {
        val repository = FirestoreSimulatorRepository()
        val filters = listOf(
            QueryFilter("lastLoginAt", FilterOperator.LESS_THAN, "cutoffTimestamp"),
            QueryFilter("status", FilterOperator.EQUAL, "'active'")
        )
        val report = repository.analyzeQuery(filters, "users")
        assertTrue("Requires composite index", report.needsCompositeIndex)
        assertEquals("users", report.compositeIndex?.collectionGroup)
    }

    @Test
    fun test_pwa_manifest_and_assetlinks() {
        val manifest = PwaManifest(name = "RubelNet PWA", shortName = "RubelNet")
        val json = manifest.toJson()
        assertTrue("Manifest json contains short_name", json.contains("\"short_name\": \"RubelNet\""))
        assertTrue("Manifest json contains standalone", json.contains("\"display\": \"standalone\""))

        val twaConfig = TwaPackageConfig(packageName = "com.aistudio.rubelnet.mgr")
        val assetLinks = twaConfig.toAssetLinksJson()
        assertTrue("AssetLinks contains package name", assetLinks.contains("com.aistudio.rubelnet.mgr"))
        assertTrue("AssetLinks contains handle_all_urls", assetLinks.contains("delegate_permission/common.handle_all_urls"))
    }

    @Test
    fun test_github_auth_profile() {
        val profile = GitHubProfile(
            uid = "gh_934128",
            login = "rsk934128-dot",
            name = "RSK (rsk934128-dot)",
            email = "fs2217732@gmail.com",
            avatarUrl = "https://github.com/rsk934128-dot.png",
            role = "Owner & Admin"
        )
        assertEquals("rsk934128-dot", profile.login)
        assertEquals("Owner & Admin", profile.role)
        assertEquals("fs2217732@gmail.com", profile.email)
        assertTrue("Contains user:email scope", profile.scopes.contains("user:email"))
        assertNotNull(profile.uid)
    }

    @Test
    fun test_firestore_aggregate_usage_statistics() {
        val repository = FirestoreSimulatorRepository()
        val stats = repository.getUsageStats()
        assertTrue("Total document count should be greater than zero", stats.totalDocuments > 0)
        assertTrue("Average query latency should be greater than zero", stats.avgLatencyMs > 0.0)
        assertTrue("Average query latency should be below 100ms with index", stats.avgLatencyMs < 100.0)
        assertTrue("P95 latency should be greater than or equal to average latency", stats.p95LatencyMs >= stats.avgLatencyMs)
        assertTrue("Cache hit rate should be above 90%", stats.cacheHitRatePct > 90.0)
        assertTrue("Active users count should be positive", stats.activeUsersCount > 0)
    }
}
