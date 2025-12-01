package com.example.machteacher.ui.booking

data class CartItem(
    val mentorName: String,
    val subject: String,
    val packageName: String,
    val date: String,
    val time: String,
    val duration: String,
    val price: Int,
    val mode: String,
    val notes: String = ""
)

data class BookingUiState(
    val mentorId: Long? = null,
    val mentorName: String = "",
    val subjects: List<String> = emptyList(),
    val selectedSubject: String? = null,
    val selectedPackage: String = "Sesión Individual",
    val selectedMode: String = "Online",
    val selectedDate: String = "",
    val selectedTime: String = "",
    val duration: String = "1 hora",
    val notes: String = "",
    val price: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val cart: List<CartItem> = emptyList()
)
