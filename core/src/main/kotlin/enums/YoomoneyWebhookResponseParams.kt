package enums

enum class YoomoneyWebhookResponseParams(val value: String) {
    NOTIFICATION_TYPE("notification_type"),
    OPERATION_ID("operation_id"),
    AMOUNT("amount"),
    CURRENCY("currency"),
    DATETIME("datetime"),
    SENDER("sender"),
    CODEPRO("codepro"),
    LABEL("label"),
    SHA1_HASH("sha1_hash")
}