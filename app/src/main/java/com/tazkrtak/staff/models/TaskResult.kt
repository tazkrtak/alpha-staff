package com.tazkrtak.staff.models

import com.tazkrtak.staff.App

data class TaskResult(
    var messageId: Int,
    var details: String = "",
    var isSuccess: Boolean = false
) {
    val message
        get() = """
        ${App.appContext!!.getString(messageId)}
        $details
    """.trim().trimIndent()
}