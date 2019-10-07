package com.tazkrtak.staff.models

data class Bus(
    val id: String? = null,
    val number: Int? = null,
    val startStation: String? = null,
    val endStation: String? = null,
    val ticketsPrices: ArrayList<Double>? = arrayListOf()
)