package com.gsy.re2graphbackend.service

import com.gsy.re2graphbackend.imodel.Extractable
import com.gsy.re2graphbackend.model.Column
import com.gsy.re2graphbackend.model.Constraint
import org.apache.commons.dbutils.BasicRowProcessor
import org.apache.commons.dbutils.GenerousBeanProcessor
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.handlers.BeanListHandler
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

@Service
class MySQLService(
    var userName: String = "",
    var password:String = "",
    var database: String = "",
) {

    private var conn: Connection? = null

    fun connect(): Int {
        val prop = Properties()
        with(prop) {
            put("user", userName)
            put("password", password)
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/${database}", prop)
        } catch (e: SQLException) {
            println("SQL Exception")
            e.printStackTrace()
            return -1
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
        return 0
    }

    fun getDatabaseSchema(): MutableList<Column>? {
        val sql = """
                select TABLE_NAME, COLUMN_NAME, DATA_TYPE, COLUMN_KEY
                from information_schema.columns
                where table_schema = '$database'
           """
        val runner = QueryRunner()
        val handler = BeanListHandler(Column::class.java, BasicRowProcessor(GenerousBeanProcessor()))
        return runner.query(conn, sql, handler)
    }

    fun getDatabaseKey() : MutableList<Constraint>? {
        val sql = """
                select CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
                from information_schema.key_column_usage
                where constraint_schema = '$database' and referenced_table_schema = '$database'
            """
        val runner = QueryRunner()
        val handler = BeanListHandler(Constraint::class.java, BasicRowProcessor(GenerousBeanProcessor()))
        return runner.query(conn, sql, handler)
    }

    fun exportData(e: Extractable): ResultSet {
        if (conn == null) {
            connect()
        }
        val sql = e.getSQL()
        println(sql)
        return conn!!.createStatement().executeQuery(sql)
    }
}