package com.gsy.re2graphbackend.model

data class Constraint(
    var constraintName: String = "",
    var tableName: String = "",
    var columnName: String = "",
    var referencedTableName: String = "",
    var referencedColumnName: String = "",
) {
    fun getAbsoluteColumnName(): String {
        return "$tableName.$columnName"
    }

    fun getAbsoluteRefColumnName(): String {
        return "$referencedTableName.$referencedColumnName"
    }
}