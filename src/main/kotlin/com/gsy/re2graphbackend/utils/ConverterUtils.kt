package com.gsy.re2graphbackend.utils

class ConverterUtils {

    companion object TypeConverter {
        fun convert(s: String): String {
            return when (s) {
                "varchar" -> "string"
                else -> s
            }
        }
    }
}