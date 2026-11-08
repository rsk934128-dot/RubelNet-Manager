package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppLanguage
import com.example.data.model.FieldProjectionMode
import com.example.data.model.FilterOperator
import com.example.data.model.GenkitFlowItem
import com.example.data.model.GenkitLogEntry
import com.example.data.model.OptimizationReport
import com.example.data.model.QueryFilter
import com.example.data.model.SecurityRuleTest
import com.example.data.model.UserDocument
import com.example.data.model.UserStatus
import com.example.data.repository.FirestoreSimulatorRepository
import com.example.data.model.PwaAuditCheck
import com.example.data.model.PwaManifest
import com.example.data.model.TwaPackageConfig
import com.example.data.model.GitHubOAuthConfig
import com.example.data.model.GitHubProfile
import com.example.data.model.FirestoreUsageStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RubelNetManagerViewModel(
    private val repository: FirestoreSimulatorRepository = FirestoreSimulatorRepository()
) : ViewModel() {

    // Language state
    private val _language = MutableStateFlow(AppLanguage.BENGALI)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Tab state (0: Users Audit, 1: Query Optimizer, 2: Genkit Flows, 3: Security Rules, 4: Aggregation & Cost)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Inactive Users Query State
    private val _daysThreshold = MutableStateFlow(7)
    val daysThreshold: StateFlow<Int> = _daysThreshold.asStateFlow()

    private val _statusFilter = MutableStateFlow<UserStatus?>(UserStatus.ACTIVE)
    val statusFilter: StateFlow<UserStatus?> = _statusFilter.asStateFlow()

    private val _pageIndex = MutableStateFlow(0)
    val pageIndex: StateFlow<Int> = _pageIndex.asStateFlow()

    private val _pageSize = MutableStateFlow(10)
    val pageSize: StateFlow<Int> = _pageSize.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _projectionMode = MutableStateFlow(FieldProjectionMode.FULL_DOCUMENT)
    val projectionMode: StateFlow<FieldProjectionMode> = _projectionMode.asStateFlow()

    private val _selectedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedUserIds: StateFlow<Set<String>> = _selectedUserIds.asStateFlow()

    // Aggregated counter from repository
    val aggregatedInactiveCount = repository.adminStatusDocCount

    // Query builder filters for Optimizer Tab
    private val _customFilters = MutableStateFlow<List<QueryFilter>>(
        listOf(
            QueryFilter("lastLoginAt", FilterOperator.LESS_THAN, "cutoffTimestamp (7d)"),
            QueryFilter("status", FilterOperator.EQUAL, "'active'")
        )
    )
    val customFilters: StateFlow<List<QueryFilter>> = _customFilters.asStateFlow()

    // Optimization report
    private val _optimizationReport = MutableStateFlow<OptimizationReport>(
        repository.analyzeQuery(_customFilters.value)
    )
    val optimizationReport: StateFlow<OptimizationReport> = _optimizationReport.asStateFlow()

    // Genkit Flows state
    val genkitLogs: StateFlow<List<GenkitLogEntry>> = repository.genkitLogs
    val genkitFlows = MutableStateFlow<List<GenkitFlowItem>>(repository.getGenkitPredefinedFlows())

    private val _isFlowRunning = MutableStateFlow(false)
    val isFlowRunning: StateFlow<Boolean> = _isFlowRunning.asStateFlow()

    private val _simulateError = MutableStateFlow(false)
    val simulateError: StateFlow<Boolean> = _simulateError.asStateFlow()

    // Security Rules tests
    val securityRuleTests = MutableStateFlow<List<SecurityRuleTest>>(repository.getSecurityRuleTests())

    // PWA Builder & TWA state
    private val _pwaManifest = MutableStateFlow(PwaManifest())
    val pwaManifest: StateFlow<PwaManifest> = _pwaManifest.asStateFlow()

    private val _twaConfig = MutableStateFlow(TwaPackageConfig())
    val twaConfig: StateFlow<TwaPackageConfig> = _twaConfig.asStateFlow()

    val pwaAuditChecks = MutableStateFlow(
        listOf(
            PwaAuditCheck("c1", "Web App Manifest সনাক্ত হয়েছে", "Web App Manifest Detected", "valid manifest.json loaded with name, start_url, and icons", "valid manifest.json loaded with name, start_url, and icons", true, 20),
            PwaAuditCheck("c2", "সার্ভিস ওয়ার্কার ও অফলাইন ক্যাশিং", "Service Worker & Offline Fallback", "Cache-First strategy caches Firestore schema and UI assets", "Cache-First strategy caches Firestore schema and UI assets", true, 25),
            PwaAuditCheck("c3", "HTTPS সিকিউর ট্রান্সপোর্ট", "HTTPS Secure Transport", "Served over encrypted TLS/HTTPS protocol", "Served over encrypted TLS/HTTPS protocol", true, 15),
            PwaAuditCheck("c4", "মাস্কেবল আইকন (512x512)", "Maskable Icons (512x512)", "Adaptive icon safe zone supported for Android launchers", "Adaptive icon safe zone supported for Android launchers", true, 15),
            PwaAuditCheck("c5", "ডিজিটাল অ্যাসেট লিংকস (TWA)", "Digital Asset Links (assetlinks.json)", "Verified SHA-256 fingerprint for full-screen chrome removal", "Verified SHA-256 fingerprint for full-screen chrome removal", true, 15),
            PwaAuditCheck("c6", "পুশ নোটিফিকেশন সাপোর্ট", "Push Notifications (WebPush / FCM)", "Background re-engagement messaging enabled", "Background re-engagement messaging enabled", true, 10)
        )
    )

    // GitHub Auth State
    private val _gitHubUser = MutableStateFlow<GitHubProfile?>(
        GitHubProfile(
            uid = "gh_934128",
            login = "rsk934128-dot",
            name = "RSK (rsk934128-dot)",
            email = "fs2217732@gmail.com",
            avatarUrl = "https://github.com/rsk934128-dot.png",
            role = "Owner & Admin",
            publicRepos = 32,
            followers = 215,
            bio = "RubelNet Lead Firebase Developer & Project Owner"
        )
    )
    val gitHubUser: StateFlow<GitHubProfile?> = _gitHubUser.asStateFlow()

    private val _isGitHubLoading = MutableStateFlow(false)
    val isGitHubLoading: StateFlow<Boolean> = _isGitHubLoading.asStateFlow()

    val gitHubConfig = MutableStateFlow(GitHubOAuthConfig())

    // Aggregate Usage Statistics state
    private val _firestoreStats = MutableStateFlow(repository.getUsageStats())
    val firestoreStats: StateFlow<FirestoreUsageStats> = _firestoreStats.asStateFlow()

    private val _isBenchmarkingLatency = MutableStateFlow(false)
    val isBenchmarkingLatency: StateFlow<Boolean> = _isBenchmarkingLatency.asStateFlow()

    // Feedback message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Current page results
    data class PageResult(
        val users: List<UserDocument>,
        val totalCount: Int,
        val totalPages: Int,
        val currentPage: Int,
        val estimatedPayloadBytes: Int
    )

    data class QueryParams(
        val days: Int,
        val status: UserStatus?,
        val page: Int,
        val size: Int,
        val search: String,
        val projMode: FieldProjectionMode
    )

    private val queryParams = combine(
        combine(_daysThreshold, _statusFilter, _pageIndex) { days, status, page ->
            Triple(days, status, page)
        },
        combine(_pageSize, _searchQuery, _projectionMode) { size, search, projMode ->
            Triple(size, search, projMode)
        }
    ) { (days, status, page), (size, search, projMode) ->
        QueryParams(days, status, page, size, search, projMode)
    }

    val pagedUsers: StateFlow<PageResult> = combine(
        repository.users,
        queryParams
    ) { _, params ->
        val (list, total) = repository.queryInactiveUsers(params.days, params.status, params.page, params.size, params.search)
        val pages = if (total == 0) 1 else (total + params.size - 1) / params.size
        val safePage = params.page.coerceIn(0, (pages - 1).coerceAtLeast(0))
        val bytes = list.size * params.projMode.avgByteSize
        PageResult(
            users = list,
            totalCount = total,
            totalPages = pages,
            currentPage = safePage,
            estimatedPayloadBytes = bytes
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PageResult(emptyList(), 0, 1, 0, 0)
    )

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setDaysThreshold(days: Int) {
        _daysThreshold.value = days
        _pageIndex.value = 0
        updateCustomFilters()
    }

    fun setStatusFilter(status: UserStatus?) {
        _statusFilter.value = status
        _pageIndex.value = 0
    }

    fun setPageSize(size: Int) {
        _pageSize.value = size
        _pageIndex.value = 0
    }

    fun nextPage() {
        val totalPages = pagedUsers.value.totalPages
        if (_pageIndex.value < totalPages - 1) {
            _pageIndex.value += 1
        }
    }

    fun prevPage() {
        if (_pageIndex.value > 0) {
            _pageIndex.value -= 1
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _pageIndex.value = 0
    }

    fun setProjectionMode(mode: FieldProjectionMode) {
        _projectionMode.value = mode
        val msg = if (_language.value == AppLanguage.BENGALI) {
            if (mode == FieldProjectionMode.PROJECTED) "ফিল্ড প্রজেকশন চালু: ~৭৬% মেমরি ও ডেটা সাশ্রয়" else "সম্পূর্ণ ডকুমেন্ট মোড চালু"
        } else {
            if (mode == FieldProjectionMode.PROJECTED) "Field projection enabled: ~76% memory saved" else "Full document mode enabled"
        }
        _userMessage.value = msg
    }

    fun toggleUserSelection(userId: String) {
        val current = _selectedUserIds.value.toMutableSet()
        if (current.contains(userId)) {
            current.remove(userId)
        } else {
            current.add(userId)
        }
        _selectedUserIds.value = current
    }

    fun selectAllCurrentPage() {
        val pageIds = pagedUsers.value.users.map { it.id }.toSet()
        val current = _selectedUserIds.value.toMutableSet()
        if (current.containsAll(pageIds)) {
            current.removeAll(pageIds)
        } else {
            current.addAll(pageIds)
        }
        _selectedUserIds.value = current
    }

    fun clearSelection() {
        _selectedUserIds.value = emptySet()
    }

    fun markSelectedAsInactive() {
        val selected = _selectedUserIds.value
        if (selected.isEmpty()) return
        repository.markUsersAsInactive(selected)
        val msg = if (_language.value == AppLanguage.BENGALI) {
            "${selected.size} জন ব্যবহারকারীকে 'নিষ্ক্রিয়' (Inactive) করা হয়েছে"
        } else {
            "Marked ${selected.size} users as Inactive"
        }
        _userMessage.value = msg
        clearSelection()
    }

    fun markSelectedAsActive() {
        val selected = _selectedUserIds.value
        if (selected.isEmpty()) return
        repository.markUsersAsActive(selected)
        val msg = if (_language.value == AppLanguage.BENGALI) {
            "${selected.size} জন ব্যবহারকারীকে 'সক্রিয়' (Active) করা হয়েছে"
        } else {
            "Marked ${selected.size} users as Active"
        }
        _userMessage.value = msg
        clearSelection()
    }

    fun triggerReengagementForSelected() {
        val selected = _selectedUserIds.value
        if (selected.isEmpty()) return
        val count = repository.sendReengagementNotification(selected)
        val msg = if (_language.value == AppLanguage.BENGALI) {
            "$count টি রি-এনগেজমেন্ট ইমেইল ও নোটিফিকেশন পাঠানো হয়েছে!"
        } else {
            "Sent $count re-engagement notifications successfully!"
        }
        _userMessage.value = msg
        clearSelection()
    }

    fun deleteSelectedUsers() {
        val selected = _selectedUserIds.value
        if (selected.isEmpty()) return
        repository.deleteUsers(selected)
        val msg = if (_language.value == AppLanguage.BENGALI) {
            "${selected.size} জন ব্যবহারকারী মুছে ফেলা হয়েছে"
        } else {
            "Deleted ${selected.size} users"
        }
        _userMessage.value = msg
        clearSelection()
    }

    fun addNewUser(name: String, email: String, status: UserStatus, daysAgo: Int, role: String) {
        val user = repository.addUser(name, email, status, daysAgo, role)
        val msg = if (_language.value == AppLanguage.BENGALI) {
            "নতুন ইউজার '${user.name}' তৈরি করা হয়েছে"
        } else {
            "Created user '${user.name}'"
        }
        _userMessage.value = msg
    }

    fun addCustomFilter(field: String, op: FilterOperator, value: String) {
        val list = _customFilters.value.toMutableList()
        list.add(QueryFilter(field, op, value))
        _customFilters.value = list
        _optimizationReport.value = repository.analyzeQuery(list)
    }

    fun removeCustomFilter(index: Int) {
        val list = _customFilters.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _customFilters.value = list
            _optimizationReport.value = repository.analyzeQuery(list)
        }
    }

    private fun updateCustomFilters() {
        _customFilters.value = listOf(
            QueryFilter("lastLoginAt", FilterOperator.LESS_THAN, "cutoffTimestamp (${_daysThreshold.value}d)"),
            QueryFilter("status", FilterOperator.EQUAL, "'${_statusFilter.value?.name?.lowercase() ?: "active"}'")
        )
        _optimizationReport.value = repository.analyzeQuery(_customFilters.value)
    }

    fun toggleSimulateError() {
        _simulateError.value = !_simulateError.value
    }

    fun runGenkitFlow(flowId: String) {
        if (_isFlowRunning.value) return
        _isFlowRunning.value = true
        viewModelScope.launch {
            val (success, count) = repository.executeGenkitInactiveUsersFlow(
                daysThreshold = _daysThreshold.value,
                pageSize = _pageSize.value,
                shouldFail = _simulateError.value
            )
            _isFlowRunning.value = false
            val msg = if (_language.value == AppLanguage.BENGALI) {
                if (success) "জেনকিট ফ্লো সম্পন্ন: $count জন ইউজার অডিট হয়েছে" else "ফ্লো ত্রুটি সিমুলেশন সফল: try-catch দ্বারা সুরক্ষিত"
            } else {
                if (success) "Genkit flow completed: $count users audited" else "Flow error simulation handled via try-catch"
            }
            _userMessage.value = msg
        }
    }

    fun updatePwaManifest(name: String, shortName: String, themeColor: String, display: String, startUrl: String) {
        _pwaManifest.value = _pwaManifest.value.copy(
            name = name,
            shortName = shortName,
            themeColor = themeColor,
            display = display,
            startUrl = startUrl
        )
        val msg = if (_language.value == AppLanguage.BENGALI) "ম্যানিফেস্ট আপডেট সম্পন্ন!" else "Manifest updated!"
        _userMessage.value = msg
    }

    fun signInWithGitHubAccount(username: String, name: String, email: String, role: String = "admin") {
        _isGitHubLoading.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(600)
            _gitHubUser.value = GitHubProfile(
                uid = "gh_${System.currentTimeMillis() % 1000000}",
                login = username.lowercase().replace(" ", "").replace("@", ""),
                name = name,
                email = email,
                avatarUrl = "https://github.com/${username.lowercase().replace("@", "")}.png",
                role = role
            )
            _isGitHubLoading.value = false
            val msg = if (_language.value == AppLanguage.BENGALI)
                "GitHub অ্যাকাউন্ট @${username} সফলভাবে অথেন্টিকেট হয়েছে!"
            else
                "Successfully authenticated with GitHub as @$username!"
            _userMessage.value = msg
        }
    }

    fun signOutGitHub() {
        _gitHubUser.value = null
        val msg = if (_language.value == AppLanguage.BENGALI)
            "GitHub থেকে সফলভাবে সাইন আউট করা হয়েছে"
        else
            "Signed out from GitHub successfully"
        _userMessage.value = msg
    }

    fun runLatencyBenchmark() {
        if (_isBenchmarkingLatency.value) return
        _isBenchmarkingLatency.value = true
        viewModelScope.launch {
            val (avgLatency, history) = repository.benchmarkQueryLatency(samples = 6)
            val current = repository.getUsageStats()
            _firestoreStats.value = current.copy(
                avgLatencyMs = avgLatency,
                latencyHistoryMs = history,
                lastBenchmarkTime = System.currentTimeMillis()
            )
            _isBenchmarkingLatency.value = false
            val msg = if (_language.value == AppLanguage.BENGALI)
                "কুয়েরি লেটেন্সি বেঞ্চমার্ক সম্পন্ন: গড় ${avgLatency}ms"
            else
                "Latency benchmark completed: avg ${avgLatency}ms"
            _userMessage.value = msg
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
