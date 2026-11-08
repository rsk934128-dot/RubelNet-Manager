package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.model.AppLanguage
import com.example.ui.components.AddUserDialog
import com.example.ui.components.AppTopBar
import com.example.ui.components.GitHubAuthDialog
import com.example.ui.components.NavigationTabBar
import com.example.ui.screens.AggregationMetricsScreen
import com.example.ui.screens.GenkitFlowsScreen
import com.example.ui.screens.PwaBuilderScreen
import com.example.ui.screens.QueryOptimizerScreen
import com.example.ui.screens.SecurityRulesScreen
import com.example.ui.screens.UsersAuditScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RubelNetManagerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RubelNetManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RubelNetManagerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RubelNetManagerApp(viewModel: RubelNetManagerViewModel) {
    val language by viewModel.language.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val aggregatedCount by viewModel.aggregatedInactiveCount.collectAsState()
    val gitHubUser by viewModel.gitHubUser.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showGitHubDialog by remember { mutableStateOf(false) }

    // Handle back button: if not on Users tab (0), go to Users tab
    BackHandler(enabled = selectedTab != 0) {
        viewModel.setSelectedTab(0)
    }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        topBar = {
            Column {
                AppTopBar(
                    language = language,
                    gitHubUser = gitHubUser,
                    onToggleLanguage = {
                        val nextLang = if (language == AppLanguage.BENGALI) AppLanguage.ENGLISH else AppLanguage.BENGALI
                        viewModel.setLanguage(nextLang)
                    },
                    onGitHubClick = { showGitHubDialog = true },
                    onAddUserClick = { showAddUserDialog = true }
                )
                NavigationTabBar(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.setSelectedTab(it) },
                    language = language,
                    inactiveUsersCount = aggregatedCount
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> UsersAuditScreen(viewModel = viewModel)
                1 -> QueryOptimizerScreen(viewModel = viewModel)
                2 -> GenkitFlowsScreen(viewModel = viewModel)
                3 -> SecurityRulesScreen(viewModel = viewModel)
                4 -> AggregationMetricsScreen(viewModel = viewModel)
                5 -> PwaBuilderScreen(viewModel = viewModel)
            }
        }
    }

    if (showAddUserDialog) {
        AddUserDialog(
            language = language,
            onDismiss = { showAddUserDialog = false },
            onConfirm = { name, email, status, daysAgo, role ->
                viewModel.addNewUser(name, email, status, daysAgo, role)
                showAddUserDialog = false
            }
        )
    }

    if (showGitHubDialog) {
        GitHubAuthDialog(
            viewModel = viewModel,
            onDismiss = { showGitHubDialog = false }
        )
    }
}
