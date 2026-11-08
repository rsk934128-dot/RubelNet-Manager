package com.example.data.model

data class GitHubProfile(
    val uid: String,
    val login: String, // username e.g. "rubelnet"
    val name: String,
    val email: String,
    val avatarUrl: String,
    val bio: String = "Full-stack Firebase & Android Developer",
    val publicRepos: Int = 24,
    val followers: Int = 142,
    val role: String = "admin",
    val scopes: List<String> = listOf("user:email", "read:user", "repo:status"),
    val authenticatedAt: Long = System.currentTimeMillis()
)

data class GitHubOAuthConfig(
    val clientId: String = "Ov23lizRnbL94XpQ1",
    val redirectUri: String = "https://rubelnet-mgr.firebaseapp.com/__/auth/handler",
    val authorizationUrl: String = "https://github.com/login/oauth/authorize",
    val tokenUrl: String = "https://github.com/login/oauth/access_token",
    val scopes: String = "read:user user:email"
)
