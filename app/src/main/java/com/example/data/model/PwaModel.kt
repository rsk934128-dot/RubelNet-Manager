package com.example.data.model

data class PwaManifest(
    val name: String = "RubelNet Firebase Manager",
    val shortName: String = "RubelNet",
    val description: String = "Firestore Query Optimizer & Inactive User Management Console",
    val startUrl: String = "/",
    val display: String = "standalone", // standalone, fullscreen, minimal-ui
    val themeColor: String = "#FF8F00",
    val backgroundColor: String = "#0F172A",
    val orientation: String = "portrait",
    val icons: List<PwaIcon> = listOf(
        PwaIcon("/icons/icon-192.png", "192x192", "image/png", "any"),
        PwaIcon("/icons/icon-512.png", "512x512", "image/png", "maskable")
    )
) {
    fun toJson(): String {
        val iconsJson = icons.joinToString(",\n    ") {
            """{ "src": "${it.src}", "sizes": "${it.sizes}", "type": "${it.type}", "purpose": "${it.purpose}" }"""
        }
        return """
{
  "name": "$name",
  "short_name": "$shortName",
  "description": "$description",
  "start_url": "$startUrl",
  "display": "$display",
  "orientation": "$orientation",
  "theme_color": "$themeColor",
  "background_color": "$backgroundColor",
  "icons": [
    $iconsJson
  ]
}
""".trimIndent()
    }
}

data class PwaIcon(
    val src: String,
    val sizes: String,
    val type: String,
    val purpose: String
)

data class PwaAuditCheck(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val detailBn: String,
    val detailEn: String,
    val isPassed: Boolean,
    val weight: Int
)

data class TwaPackageConfig(
    val host: String = "rubelnet.com",
    val packageName: String = "com.aistudio.rubelnet.mgr",
    val appName: String = "RubelNet Manager",
    val sha256Fingerprint: String = "14:6D:E2:83:5A:F3:99:A2:4B:91:80:C2:21:40:53:C9:83:B2:3F:88:51:7E:6D:E1:92:44:B2:77:81:9A:F0:7C"
) {
    fun toAssetLinksJson(): String {
        return """
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "$packageName",
      "sha256_cert_fingerprints": [
        "$sha256Fingerprint"
      ]
    }
  }
]
""".trimIndent()
    }

    fun toBubblewrapCommand(): String {
        return "bubblewrap init --manifest=https://$host/manifest.json\nbubblewrap build"
    }
}
