package com.gsy.re2graphbackend.model

data class Column(
    var tableName: String = "",
    var columnName: String = "",
    var dataType: String = "",
    var columnKey: String = "",
)