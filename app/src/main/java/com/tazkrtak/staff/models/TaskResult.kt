package com.tazkrtak.staff.models

data class TaskResult(
    override var messageId: Int? = null,
    override var details: String = "",
    override var isSuccess: Boolean = false ,
    override var extra: Map<String, Any> = mapOf()
) : Result