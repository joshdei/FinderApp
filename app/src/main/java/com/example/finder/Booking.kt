package com.example.finder

data class Booking(
    val userid: String,
    val title: String,
    val status: String,
    val date:String,
    val religion: String,
    val town: String,
    val state: String,
    val address: String,
    val price: String,
    val preferences: String,
    val image: String,
) {
    // Secondary constructor
    constructor() : this("","","","", "", "", "", "", "","","")
}

