package com.example.machteacher.model

import java.text.SimpleDateFormat
import java.util.Locale

fun ddMMyyyyToIso(d: String): String {
    val inF = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val outF = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return outF.format(inF.parse(d)!!)
}

fun durationLabelToMinutes(label: String) = when (label) {
    "1 hora" -> 60
    "1.5 horas" -> 90
    "2 horas" -> 120
    "3 horas" -> 180
    else -> 60
}
