package com.gsy.re2graphbackend.model

data class Property(
    var name: String = "",
    var columnName: String = "",
    var sqlType: String = "",
    var neo4jType: String = "",
    var isPri: Boolean = false,
    var isIgnored: Boolean = false,
)