package com.tazkrtak.staff.models

data class TaskResult(
    override var messageId: Int? = null,
    override var details: String = "",
    override var isSuccess: Boolean = false
) : Result