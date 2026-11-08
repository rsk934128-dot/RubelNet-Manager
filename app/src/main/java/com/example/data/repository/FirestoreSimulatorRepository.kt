package com.example.data.repository

import com.example.data.model.CompositeIndexDef
import com.example.data.model.FieldProjectionMode
import com.example.data.model.FilterOperator
import com.example.data.model.GenkitFlowItem
import com.example.data.model.GenkitLogEntry
import com.example.data.model.OptimizationReport
import com.example.data.model.QueryFilter
import com.example.data.model.SecurityRuleTest
import com.example.data.model.UserDocument
import com.example.data.model.UserStatus
import com.example.data.model.FirestoreUsageStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FirestoreSimulatorRepository {

    private val _users = MutableStateFlow<List<UserDocument>>(emptyList())
    val users: StateFlow<List<UserDocument>> = _users.asStateFlow()

    private val _adminStatusDocCount = MutableStateFlow(0)
    val adminStatusDocCount: StateFlow<Int> = _adminStatusDocCount.asStateFlow()

    private val _genkitLogs = MutableStateFlow<List<GenkitLogEntry>>(emptyList())
    val genkitLogs: StateFlow<List<GenkitLogEntry>> = _genkitLogs.asStateFlow()

    init {
        seedInitialUsers()
        updateAggregatedCount()
    }

    private fun seedInitialUsers() {
        val now = System.currentTimeMillis()
        val oneDay = 86_400_000L

        val initialList = mutableListOf<UserDocument>()

        val userTemplates = listOf(
            Triple("Rubel Ahmed", "rubel@rubelnet.com", 14),
            Triple("Tanvir Hossain", "tanvir.h@gmail.com", 9),
            Triple("Nusrat Jahan", "nusrat.j@yahoo.com", 2),
            Triple("Shafiqul Islam", "shafiq@rubelnet.com", 21),
            Triple("Fatima Akter", "fatima.akter@outlook.com", 1),
            Triple("Kamrul Hasan", "kamrul.hasan@gmail.com", 11),
            Triple("Sadia Sultana", "sadia.s@gmail.com", 5),
            Triple("Arifur Rahman", "arif.r@rubelnet.com", 30),
            Triple("Mehedi Hasan", "mehedi.h@gmail.com", 8),
            Triple("Jannatul Ferdous", "jannat.f@gmail.com", 4),
            Triple("Mohammad Ali", "m.ali@technet.bd", 18),
            Triple("Rashedul Karim", "rashed@rubelnet.com", 3),
            Triple("Farzana Yeasmin", "farzana.y@gmail.com", 12),
            Triple("Mahmudul Haque", "mahmud@gmail.com", 45),
            Triple("Tasnim Tabassum", "tasnim.t@gmail.com", 7),
            Triple("Zubair Ahmed", "zubair.a@rubelnet.com", 16),
            Triple("Shahriar Kabir", "shahriar@gmail.com", 0),
            Triple("Nazmul Huda", "nazmul.h@yahoo.com", 25),
            Triple("Sabrina Rahman", "sabrina.r@gmail.com", 6),
            Triple("Abdur Rahim", "abdur.r@rubelnet.com", 15),
            Triple("Tariqul Islam", "tariq@gmail.com", 2),
            Triple("Salma Begum", "salma.b@gmail.com", 80),
            Triple("Ashraful Alam", "ashraf@gmail.com", 10),
            Triple("Fahim Montasir", "fahim.m@rubelnet.com", 13),
            Triple("Sumaiya Khatun", "sumaiya.k@gmail.com", 1),
            Triple("Emran Hossain", "emran.h@gmail.com", 22),
            Triple("Anika Tabassum", "anika.t@gmail.com", 35),
            Triple("Habibur Rahman", "habib@rubelnet.com", 4),
            Triple("Rezaul Karim", "rezaul.k@gmail.com", 17),
            Triple("Sharmin Sultana", "sharmin.s@gmail.com", 8),
            Triple("Moniruzzaman", "monir@rubelnet.com", 19),
            Triple("Farhana Islam", "farhana.i@gmail.com", 0),
            Triple("Delowar Hossain", "delowar@gmail.com", 40),
            Triple("Asma Ul Husna", "asma@gmail.com", 6),
            Triple("Kazi Nazrul", "kazi.n@rubelnet.com", 12),
            Triple("Rokeya Begum", "rokeya.b@gmail.com", 28),
            Triple("Shakib Al Hasan", "shakib@cricket.bd", 3),
            Triple("Tamim Iqbal", "tamim@cricket.bd", 14),
            Triple("Mushfiqur Rahim", "mushfiq@cricket.bd", 5),
            Triple("Mashrafe Mortaza", "mash@leader.bd", 50)
        )

        userTemplates.forEachIndexed { index, (name, email, daysAgo) ->
            val loginTime = now - (daysAgo * oneDay) + ((index * 137_000L) % oneDay)
            val createdDays = daysAgo + 60 + index * 5
            val createdTime = now - (createdDays * oneDay)

            val status = when {
                daysAgo > 60 -> UserStatus.INACTIVE
                index % 11 == 0 -> UserStatus.SUSPENDED
                else -> UserStatus.ACTIVE
            }

            val role = if (index % 7 == 0) "admin" else if (index % 4 == 0) "manager" else "member"

            initialList.add(
                UserDocument(
                    id = "usr_${UUID.randomUUID().toString().substring(0, 8)}",
                    name = name,
                    email = email,
                    status = status,
                    lastLoginAt = loginTime,
                    createdAt = createdTime,
                    role = role,
                    loginCount = (100 - daysAgo).coerceAtLeast(1),
                    device = when (index % 4) {
                        0 -> "Android 14 (Pixel 8)"
                        1 -> "Android 13 (Samsung S23)"
                        2 -> "Chrome 124 (Windows 11)"
                        else -> "iOS 17 (iPhone 15 Pro)"
                    },
                    region = if (index % 3 == 0) "Dhaka, BD" else if (index % 3 == 1) "Chittagong, BD" else "Sylhet, BD",
                    metadataExtra = mapOf(
                        "plan" to if (index % 5 == 0) "premium" else "standard",
                        "newsletter" to (index % 2 == 0).toString(),
                        "storageUsedMb" to "${(index * 42) % 500}"
                    )
                )
            )
        }

        _users.value = initialList
    }

    private fun updateAggregatedCount() {
        val now = System.currentTimeMillis()
        val cutoff = now - (7 * 86_400_000L)
        val count = _users.value.count { it.status == UserStatus.ACTIVE && it.lastLoginAt < cutoff }
        _adminStatusDocCount.value = count
    }

    fun queryInactiveUsers(
        daysThreshold: Int,
        statusFilter: UserStatus?,
        pageIndex: Int,
        pageSize: Int,
        searchQuery: String = ""
    ): Pair<List<UserDocument>, Int> {
        val now = System.currentTimeMillis()
        val cutoffTimestamp = now - (daysThreshold * 86_400_000L)

        val filtered = _users.value.filter { user ->
            val matchesThreshold = user.lastLoginAt < cutoffTimestamp
            val matchesStatus = if (statusFilter != null) user.status == statusFilter else true
            val matchesSearch = if (searchQuery.isNotBlank()) {
                user.name.contains(searchQuery, ignoreCase = true) ||
                        user.email.contains(searchQuery, ignoreCase = true) ||
                        user.id.contains(searchQuery, ignoreCase = true)
            } else true

            matchesThreshold && matchesStatus && matchesSearch
        }.sortedByDescending { it.lastLoginAt } // Matches orderBy('lastLoginAt', 'desc') for pagination

        val totalMatches = filtered.size
        val fromIndex = (pageIndex * pageSize).coerceAtMost(totalMatches)
        val toIndex = (fromIndex + pageSize).coerceAtMost(totalMatches)

        val page = if (fromIndex < toIndex) filtered.subList(fromIndex, toIndex) else emptyList()
        return Pair(page, totalMatches)
    }

    fun markUsersAsInactive(userIds: Set<String>) {
        _users.value = _users.value.map { user ->
            if (userIds.contains(user.id)) {
                user.copy(status = UserStatus.INACTIVE)
            } else {
                user
            }
        }
        updateAggregatedCount()
    }

    fun markUsersAsActive(userIds: Set<String>) {
        val now = System.currentTimeMillis()
        _users.value = _users.value.map { user ->
            if (userIds.contains(user.id)) {
                user.copy(status = UserStatus.ACTIVE, lastLoginAt = now)
            } else {
                user
            }
        }
        updateAggregatedCount()
    }

    fun sendReengagementNotification(userIds: Set<String>): Int {
        // Simulated re-engagement email/push trigger
        return userIds.size
    }

    fun deleteUsers(userIds: Set<String>) {
        _users.value = _users.value.filterNot { userIds.contains(it.id) }
        updateAggregatedCount()
    }

    fun addUser(name: String, email: String, status: UserStatus, daysAgo: Int, role: String = "member"): UserDocument {
        val now = System.currentTimeMillis()
        val oneDay = 86_400_000L
        val newUser = UserDocument(
            id = "usr_${UUID.randomUUID().toString().substring(0, 8)}",
            name = name,
            email = email,
            status = status,
            lastLoginAt = now - (daysAgo * oneDay),
            createdAt = now - ((daysAgo + 30) * oneDay),
            role = role,
            loginCount = 1,
            device = "Android 14 (Mobile)",
            region = "Dhaka, BD"
        )
        _users.value = listOf(newUser) + _users.value
        updateAggregatedCount()
        return newUser
    }

    fun analyzeQuery(filters: List<QueryFilter>, collection: String = "users"): OptimizationReport {
        val rangeFilters = filters.filter { it.operator.isRange }
        val equalityFilters = filters.filter { it.operator == FilterOperator.EQUAL }

        val hasRange = rangeFilters.isNotEmpty()
        val hasEquality = equalityFilters.isNotEmpty()

        // Firestore rule: Range on one field + Equality on another requires Composite Index!
        val needsComposite = hasRange && hasEquality && (rangeFilters.any { rf -> equalityFilters.any { ef -> ef.field != rf.field } })

        val indexFields = mutableListOf<Pair<String, String>>()
        equalityFilters.forEach {
            indexFields.add(Pair(it.field, "ASCENDING"))
        }
        rangeFilters.forEach {
            indexFields.add(Pair(it.field, "ASCENDING"))
        }

        val compIndex = if (needsComposite) {
            CompositeIndexDef(
                collectionGroup = collection,
                fields = indexFields
            )
        } else null

        val reasonsBn = mutableListOf<String>()
        val reasonsEn = mutableListOf<String>()

        if (needsComposite) {
            reasonsBn.add("Firestore-এ যখন আপনি একটি রেঞ্জ ফিল্টার (<, >, <=, >=) এবং একটি ইকুয়ালিটি ফিল্টার (==) একসাথে ভিন্ন ফিল্ডে ব্যবহার করেন, তখন কম্পোজিট ইনডেক্স (Composite Index) তৈরি করা বাধ্যতামূলক।")
            reasonsBn.add("এই কুয়েরিতে '${equalityFilters.joinToString { it.field }}' এর উপর ইকুয়ালিটি এবং '${rangeFilters.joinToString { it.field }}' এর উপর রেঞ্জ ফিল্টার রয়েছে।")
            reasonsEn.add("In Firestore, combining a range filter (<, >, <=, >=) on one field and equality filter (==) on another requires a Composite Index.")
            reasonsEn.add("Fields requiring composite index: ${indexFields.joinToString { "${it.first} (${it.second})" }}")
        } else {
            reasonsBn.add("একক ফিল্ডের কুয়েরি বা সাধারণ ইকুয়ালিটি ফিল্টারের জন্য ফায়ারবেসের অটোমেটিক সিঙ্গেল-ফিল্ড ইনডেক্সই যথেষ্ট।")
            reasonsEn.add("Single-field queries or simple filters can utilize Firestore's automatic single-field indexes.")
        }

        val rules = """
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      // শুধুমাত্র অনুমোদিত ব্যবহারকারী ও অ্যাডমিন অ্যাক্সেস
      allow read: if request.auth != null && (request.auth.uid == userId || request.auth.token.role == 'admin');
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    match /adminStatus/{docId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.token.role == 'admin';
    }
  }
}
""".trimIndent()

        return OptimizationReport(
            queryDescription = "users.where(${filters.joinToString(" and ") { "${it.field} ${it.operator.symbol} ${it.value}" }})",
            hasRangeFilter = hasRange,
            hasEqualityFilter = hasEquality,
            needsCompositeIndex = needsComposite,
            compositeIndex = compIndex,
            reasonsBn = reasonsBn,
            reasonsEn = reasonsEn,
            paginationAdviceBn = "বড় ডাটাবেসের ক্ষেত্রে limit() ও startAfter() সহ কার্সার-বেসড পেজিনেশন ব্যান্ডউইথ ও মেমরি কমায়।",
            paginationAdviceEn = "Cursor-based pagination with limit() and startAfter() prevents high bandwidth and out-of-memory overhead.",
            fieldProjectionSavingsPct = 76,
            aggregationAdviceBn = "যদি শুধু ইনঅ্যাক্টিভ ইউজারের মোট সংখ্যা প্রয়োজন হয়, তবে adminStatus/inactiveUsersCount এগ্রিগেশন ডকুমেন্ট ব্যবহার করলে ৫০,০০০ রিডের পরিবর্তে মাত্র ১টি রিড খরচ হবে!",
            aggregationAdviceEn = "If only total count is needed, an aggregation document (adminStatus/inactiveUsersCount) costs 1 read instead of 50,000 document reads!",
            rulesSnippet = rules
        )
    }

    suspend fun executeGenkitInactiveUsersFlow(
        daysThreshold: Int = 7,
        pageSize: Int = 50,
        shouldFail: Boolean = false
    ): Pair<Boolean, Int> {
        val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
        fun log(level: String, msg: String) {
            val list = _genkitLogs.value.toMutableList()
            list.add(GenkitLogEntry(timeFormat.format(Date()), level, msg))
            _genkitLogs.value = list
        }

        _genkitLogs.value = emptyList()
        log("INFO", "[RubelNet Genkit] Initializing inactive users workflow...")
        delay(300)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val cutoff = System.currentTimeMillis() - (daysThreshold * 86_400_000L)
        log("INFO", "[RubelNet Genkit] Calculating cutoff: $daysThreshold days ago (${dateFormat.format(Date(cutoff))})")
        delay(250)

        log("INFO", "[RubelNet Genkit] Building compound query: collection('users').where('lastLoginAt', '<', cutoffTimestamp).where('status', '==', 'active').orderBy('lastLoginAt', 'desc').limit($pageSize)")
        delay(400)

        if (shouldFail) {
            log("ERROR", "[RubelNet Genkit] Error fetching inactive users: FirebaseError: FAILED_PRECONDITION: The query requires an index: https://console.firebase.google.com/v1/r/project/rubelnet/firestore/indexes?create_composite=...")
            log("WARN", "[RubelNet Genkit] Catching error in try-catch block as recommended in section 7 of optimization guide.")
            log("INFO", "[RubelNet Genkit] Safe fallback: Returning cached aggregation count.")
            return Pair(false, 0)
        }

        val (page, total) = queryInactiveUsers(daysThreshold, UserStatus.ACTIVE, 0, pageSize)
        log("SUCCESS", "[RubelNet Genkit] Found ${page.size} inactive users on this page (Total matching in DB: $total)")
        delay(200)

        log("INFO", "[RubelNet Genkit] Applying selective field projection: mapped {id, email, lastLoginAt}")
        delay(150)

        log("INFO", "[RubelNet Genkit] Syncing aggregation counter to 'adminStatus/inactiveUsersCount' = $total")
        _adminStatusDocCount.value = total
        delay(150)

        log("SUCCESS", "[RubelNet Genkit] Workflow execution completed successfully in 147ms.")
        return Pair(true, page.size)
    }

    fun getSecurityRuleTests(): List<SecurityRuleTest> {
        return listOf(
            SecurityRuleTest(
                id = "t1",
                descriptionBn = "লগইন করা ব্যবহারকারী নিজের প্রোফাইল রিড করতে পারবেন",
                descriptionEn = "Authenticated user reading their own document",
                requestAuthId = "usr_001",
                userRole = "member",
                userStatus = "active",
                targetPath = "/users/usr_001",
                operation = "read",
                shouldAllow = true,
                reasonBn = "request.auth.uid == userId শর্ত পূরণ হয়েছে",
                reasonEn = "request.auth.uid matches document userId"
            ),
            SecurityRuleTest(
                id = "t2",
                descriptionBn = "লগইন করা ব্যবহারকারী অন্যের প্রোফাইল পড়তে পারবেন না",
                descriptionEn = "Member user reading another user's document",
                requestAuthId = "usr_002",
                userRole = "member",
                userStatus = "active",
                targetPath = "/users/usr_001",
                operation = "read",
                shouldAllow = false,
                reasonBn = "সাধারণ ব্যবহারকারীর ক্ষেত্রে uid মিলছে না এবং অ্যাডমিন রোল নেই",
                reasonEn = "UID does not match target document and user lacks admin role"
            ),
            SecurityRuleTest(
                id = "t3",
                descriptionBn = "অ্যাডমিন যেকোনো ইউজারের ডাটা পড়তে ও অডিট করতে পারবেন",
                descriptionEn = "Admin reading any user document for inactive audit",
                requestAuthId = "admin_999",
                userRole = "admin",
                userStatus = "active",
                targetPath = "/users/usr_001",
                operation = "read",
                shouldAllow = true,
                reasonBn = "request.auth.token.role == 'admin' শর্ত অনুমোদিত",
                reasonEn = "Admin custom claim grants access to all user profiles"
            ),
            SecurityRuleTest(
                id = "t4",
                descriptionBn = "লগইন ছাড়া (Anonymous / Unauthenticated) অ্যাক্সেস নিষিদ্ধ",
                descriptionEn = "Unauthenticated client querying users collection",
                requestAuthId = null,
                userRole = null,
                userStatus = "unknown",
                targetPath = "/users/usr_001",
                operation = "read",
                shouldAllow = false,
                reasonBn = "request.auth == null হওয়ায় রিড রিজেক্ট হয়েছে",
                reasonEn = "Blocked because request.auth is null"
            ),
            SecurityRuleTest(
                id = "t5",
                descriptionBn = "অ্যাডমিন স্ট্যাটাস এগ্রিগেশন কাউন্টার রিড",
                descriptionEn = "Authenticated user reading adminStatus/inactiveUsersCount",
                requestAuthId = "usr_003",
                userRole = "member",
                userStatus = "active",
                targetPath = "/adminStatus/inactiveUsersCount",
                operation = "read",
                shouldAllow = true,
                reasonBn = "adminStatus পড়তে শুধু লগইন থাকলেই যথেষ্ট",
                reasonEn = "adminStatus allows read to any authenticated user"
            )
        )
    }

    fun getGenkitPredefinedFlows(): List<GenkitFlowItem> {
        return listOf(
            GenkitFlowItem(
                id = "audit_inactive",
                name = "auditInactiveUsersFlow",
                descriptionBn = "গত ৭ দিনে লগইন না করা সক্রিয় ইউজারদের খুঁজে বের করা ও রিপোর্ট জেনারেট করা",
                descriptionEn = "Queries active users with lastLoginAt < 7 days and updates audit cache",
                codeSnippet = """
// RubelNet Genkit Flow: auditInactiveUsers
export const auditInactiveUsersFlow = onFlow(
  {
    name: 'auditInactiveUsersFlow',
    inputSchema: z.object({ daysThreshold: z.number().default(7) }),
    outputSchema: z.object({ inactiveCount: z.number(), users: z.array(z.any()) }),
  },
  async ({ daysThreshold }) => {
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - daysThreshold);
    const cutoffTimestamp = Timestamp.fromDate(cutoffDate);

    const q = query(
      collection(db, 'users'),
      where('lastLoginAt', '<', cutoffTimestamp),
      where('status', '==', 'active'),
      orderBy('lastLoginAt', 'desc'),
      limit(50)
    );

    const snapshot = await getDocs(q);
    const inactiveUsers = snapshot.docs.map(doc => {
      const data = doc.data();
      return { id: doc.id, email: data.email, lastLoginAt: data.lastLoginAt };
    });

    console.log(`[RubelNet Genkit] Found ${'$'}{inactiveUsers.length} inactive users`);
    return { inactiveCount: inactiveUsers.length, users: inactiveUsers };
  }
);
""".trimIndent(),
                defaultParams = mapOf("daysThreshold" to "7", "pageSize" to "50")
            ),
            GenkitFlowItem(
                id = "sync_aggregation",
                name = "syncAggregationCounterFlow",
                descriptionBn = "সার্ভার-সাইডে ক্রন জবের মাধ্যমে inactiveUsersCount কাউন্টার আপডেট রাখা",
                descriptionEn = "Scheduled Cloud Function to aggregate inactive count into adminStatus document",
                codeSnippet = """
export const syncAggregationCounterFlow = onSchedule('every 1 hours', async () => {
  const sevenDaysAgo = new Date(Date.now() - 7 * 86400000);
  const q = query(
    collection(db, 'users'),
    where('lastLoginAt', '<', Timestamp.fromDate(sevenDaysAgo)),
    where('status', '==', 'active')
  );
  
  // Server-side aggregate query
  const countSnapshot = await getCountFromServer(q);
  const total = countSnapshot.data().count;

  await setDoc(doc(db, 'adminStatus', 'inactiveUsersCount'), {
    count: total,
    updatedAt: FieldValue.serverTimestamp()
  }, { merge: true });

  console.log(`[RubelNet Genkit] Aggregation synced: ${'$'}{total} users`);
});
""".trimIndent(),
                defaultParams = mapOf("cron" to "every 1 hours")
            )
        )
    }

    fun getUsageStats(): FirestoreUsageStats {
        val totalDocs = _users.value.size
        val cutoff = System.currentTimeMillis() - (7 * 86_400_000L)
        val inactiveCount = _users.value.count { it.status == UserStatus.ACTIVE && it.lastLoginAt < cutoff }
        val activeCount = _users.value.count { it.status == UserStatus.ACTIVE }

        return FirestoreUsageStats(
            totalDocuments = totalDocs,
            activeUsersCount = activeCount,
            inactiveUsersCount = inactiveCount,
            avgLatencyMs = 38.4,
            p95LatencyMs = 62.8,
            minLatencyMs = 14.5,
            maxLatencyMs = 96.0,
            cacheHitRatePct = 94.6,
            readsToday = (1200L + totalDocs * 12L),
            writesToday = 142L,
            bandwidthSavedKb = (totalDocs * 0.95 * 76.0),
            totalQueriesExecuted = 168
        )
    }

    suspend fun benchmarkQueryLatency(samples: Int = 5): Pair<Double, List<Double>> {
        val results = mutableListOf<Double>()
        for (i in 0 until samples) {
            val start = System.nanoTime()
            val cutoff = System.currentTimeMillis() - (7 * 86_400_000L)
            _users.value.filter { it.status == UserStatus.ACTIVE && it.lastLoginAt < cutoff }
            delay(15L + (i * 4L % 18))
            val durationMs = (System.nanoTime() - start) / 1_000_000.0
            results.add(String.format(Locale.US, "%.1f", durationMs).toDouble())
        }
        val avg = results.average()
        return Pair(String.format(Locale.US, "%.1f", avg).toDouble(), results)
    }
}
