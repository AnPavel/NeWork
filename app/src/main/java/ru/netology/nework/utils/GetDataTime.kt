package ru.netology.nework.utils

import java.text.SimpleDateFormat
import java.util.*

class GetDataTime {
    val dateFormat: String? = SimpleDateFormat("dd MMM yyyy, HH:mm:ss z", Locale.ENGLISH).format(Date())
}
