package com.lokavo.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateFormatter {
    private var formatDate = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    private var simpleDate = SimpleDateFormat("dd MMM yyyy HH.mm", Locale.getDefault())

    fun getCurrentDate(): String = formatDate.format(Date())

    fun getRelativeTime(dateValue: String): String? {
        val date = formatDate.parse(dateValue)
        return date?.let { getRelativeTime(it) }
    }

    private fun getRelativeTime(date: Date): String {
        val now = Date()
        val diffInMillies = now.time - date.time
        val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillies)
        val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies)
        val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillies)

        return when {
            diffInSeconds < 60 -> {
                "$diffInSeconds detik yang lalu"
            }

            diffInMinutes < 60 -> {
                "$diffInMinutes menit yang lalu"
            }

            diffInHours < 24 -> {
                "$diffInHours jam yang lalu"
            }

            else -> {
                simpleDate.format(date)
            }
        }
    }
}
