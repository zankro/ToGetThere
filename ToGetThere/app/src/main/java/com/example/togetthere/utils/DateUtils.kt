package com.example.togetthere.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun millisToString(millis: Long, pattern: String = "dd/MM/yyyy"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val instant = Instant.ofEpochMilli(millis)
    val zonedDateTime = instant.atZone(ZoneId.systemDefault())
    val localDate = zonedDateTime.toLocalDate()
    return localDate.format(formatter)
}

/***
 * Convert millis to date string
 * This function takes a time in milliseconds and converts it into a date string in MM/dd/yyyy format.
 * @param millis The time in milliseconds to be converted.
 * @return The date string in MM/dd/yyyy format, or an empty string if the conversion fails.***/
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

/***
 * Convert date string to millis
 * This function takes a date string in dd/MM/yyyy format, parses it into a LocalDate object, and returns the time in milliseconds. If the parsing fails, it returns null.
 * @param dateString The date string to be converted.
 * @return The time in milliseconds since the epoch, or null if the parsing fails.***/
fun convertDateToMillis(dateString: String?): Long? {
    return try {
        if (dateString.isNullOrBlank()) return null
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDate = LocalDate.parse(dateString, formatter)
        localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun stringToMillis(dateString: String): Long {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    return LocalDate.parse(dateString, formatter)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}