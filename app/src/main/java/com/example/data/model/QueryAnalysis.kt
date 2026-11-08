package com.example.data.model

enum class FilterOperator(val symbol: String, val isRange: Boolean) {
    EQUAL("==", false),
    LESS_THAN("<", true),
    LESS_THAN_OR_EQUAL("<=", true),
    GREATER_THAN(">", true),
    GREATER_THAN_OR_EQUAL(">=", true),
    NOT_EQUAL("!=", true),
    ARRAY_CONTAINS("array-contains", false),
    IN("in", false)
}

data class QueryFilter(
    val field: String,
    val operator: FilterOperator,
    val value: String
)

data class CompositeIndexDef(
    val collectionGroup: String,
    val queryScope: String = "COLLECTION",
    val fields: List<Pair<String, String>> // field name to "ASCENDING" / "DESCENDING"
) {
    fun toCliCommand(): String {
        val fieldArgs = fields.joinToString(" ") { "--field-config field-path=${it.first},order=${it.second.lowercase()}" }
        return "firebase firestore:indexes:create --collection-group=$collectionGroup $fieldArgs"
    }

    fun toJsonSnippet(): String {
        val fieldsJson = fields.joinToString(",\n        ") {
            """{ "fieldPath": "${it.first}", "order": "${it.second}" }"""
        }
        return """
{
  "indexes": [
    {
      "collectionGroup": "$collectionGroup",
      "queryScope": "COLLECTION",
      "fields": [
        $fieldsJson
      ]
    }
  ]
}
""".trimIndent()
    }
}

data class OptimizationReport(
    val queryDescription: String,
    val hasRangeFilter: Boolean,
    val hasEqualityFilter: Boolean,
    val needsCompositeIndex: Boolean,
    val compositeIndex: CompositeIndexDef?,
    val reasonsBn: List<String>,
    val reasonsEn: List<String>,
    val paginationAdviceBn: String,
    val paginationAdviceEn: String,
    val fieldProjectionSavingsPct: Int,
    val aggregationAdviceBn: String,
    val aggregationAdviceEn: String,
    val rulesSnippet: String
)
