package com.tazkrtak.staff.models

data class AuthResult(
    override var messageId: Int? = null,
    override var details: String = "",
    override var isSuccess: Boolean = false,
    override var extra: Map<String, Any> = mapOf(),
    var target: Target? = null
) : Result {
    enum class Target { ID, PASSWORD }
}