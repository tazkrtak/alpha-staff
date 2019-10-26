package com.tazkrtak.staff.models

import com.tazkrtak.staff.App

interface Result {
    var messageId: Int?
    var details: String
    var isSuccess: Boolean
    var extra: Map<String, Any>
    val message
        get() = """
        ${App.appContext!!.getString(messageId!!)}
        $details
    """.trim().trimIndent()
}