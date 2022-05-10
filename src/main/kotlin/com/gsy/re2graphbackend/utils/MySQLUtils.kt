package com.gsy.re2graphbackend.utils

import java.sql.ResultSet
import java.sql.ResultSetMetaData

class MySQLUtils {

    companion object ResultSetPrinter{

        fun printResultSet(rs: ResultSet){
            val metadata = rs.metaData
            val columnCount = metadata.columnCount
            val columnMaxLengths = IntArray(columnCount){10}
            val results = ArrayList<Array<String>>()
            while (rs.next()) {
                val columnStr = Array(columnCount){""}
                for ( i in 0 until columnCount) {
                    columnStr[i] = rs.getString(i + 1)
                    columnMaxLengths[i] = columnMaxLengths[i].coerceAtLeast(columnStr[i].length)
                }
                results.add(columnStr)
            }
            printSep(columnMaxLengths)
            printColumnName(metadata, columnMaxLengths)
            printSep(columnMaxLengths)
            val iterator = results.iterator()
            while (iterator.hasNext()) {
                val columnStr = iterator.next()
                for (i in 0 until columnCount) {
                    System.out.printf("|%" + columnMaxLengths[i] +"s", columnStr[i])
                }
                println("|")
            }
            printSep(columnMaxLengths)
        }

        private fun printSep(columnMaxLengths: IntArray) {
            for (columnMaxLength in columnMaxLengths) {
                print("+")
                for (i in 1..columnMaxLength) {
                    print("-")
                }
            }
            println("+")
        }

        private fun printColumnName(metaData: ResultSetMetaData, columnMaxLengths: IntArray) {
            val columnCount = metaData.columnCount
            for (i in 0 until columnCount) {
                System.out.printf("|%" + columnMaxLengths[i] + "s", metaData.getColumnName(i + 1))
            }
            println("|")
        }
    }
}