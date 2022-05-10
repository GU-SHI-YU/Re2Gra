package com.gsy.re2graphbackend.model

import com.gsy.re2graphbackend.imodel.Extractable
import com.gsy.re2graphbackend.utils.ConverterUtils
import java.util.*


data class Node(
    var name: String = "",
    var tableName: String = "",
    var properties: List<Property>,
    override var isIgnored: Boolean = false,
): Extractable {
    override fun getSQL(): String {
        val tempI = "t" + UUID.randomUUID().toString().replace("-", "")
        val tempColumn = "c" + UUID.randomUUID().toString().replace("-", "")
        return """
        select (@i := @i+1) DIV 1 as $tempColumn, 
            ${properties.filter { !it.isIgnored }.joinToString { it.columnName }}, 
            '$name' as ':LABEL'
        from $tableName force index(PRIMARY), ( select @i := 0 ) $tempI
    """.trimIndent()
    }

    override fun getFileName(): String {
        return "${name}_node"
    }

    override fun getHeader(): Array<String> {
        return arrayOf(":ID($name)") +
                properties.filter { !it.isIgnored }
                    .map { "${it.columnName}:${it.neo4jType}" }
                    .toTypedArray() +
                arrayOf(":LABEL")
    }
}