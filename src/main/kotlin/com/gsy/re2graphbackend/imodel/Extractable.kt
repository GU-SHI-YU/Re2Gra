package com.gsy.re2graphbackend.imodel

interface Extractable {
    var isIgnored: Boolean

    fun getSQL(): String
    fun getFileName(): String
    fun getHeader(): Array<String>
}