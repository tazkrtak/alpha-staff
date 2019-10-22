package com.tazkrtak.staff.models

import com.tazkrtak.staff.App

interface Result {
    var messageId: Int?
    var details: String
    var isSuccess: Boolean

    val message
        get() = """
        ${App.appContext!!.getString(messageId!!)}
        $details
    """.trim().trimIndent()
}