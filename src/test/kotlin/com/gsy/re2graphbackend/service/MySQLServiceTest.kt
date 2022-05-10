package com.gsy.re2graphbackend.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MySQLServiceTest {

    private val mySQLService = MySQLService("root", "153426", "test")

    @Test
    fun connect() {
        with(mySQLService) {
            connect()
        }
    }

    @Test
    fun getDatabaseSchema() {
        print(
            with(mySQLService) {
                connect()
                getDatabaseSchema()
            }
        )
    }

    @Test
    fun getDatabaseKey() {
        print(
            with(mySQLService) {
                connect()
                getDatabaseKey()
            }
        )
    }

    @Test
    fun exportData() {
        with(mySQLService) {
            connect()
        }
    }
}