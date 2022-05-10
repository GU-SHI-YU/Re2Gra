package com.gsy.re2graphbackend.service

import com.gsy.re2graphbackend.model.*
import com.gsy.re2graphbackend.utils.CSVUtils
import com.gsy.re2graphbackend.utils.ConverterUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConverterService {

    @Autowired
    var mySQLService = MySQLService()
    @Autowired
    var neo4jService = Neo4jService()
    var csvPath = ""

    private var tables: List<Table> = emptyList()
    var nodes: List<Node> = emptyList()
    var relations: List<Relation> = emptyList()

    fun loadData() {
        initTables()
        initNodes()
        initRelations()
    }

    private fun initTables() {
        val temp = mutableListOf<Table>()
        with(mySQLService) {
            connect()
            val columns = getDatabaseSchema()
            val constraints = getDatabaseKey()
            val tableName2Columns = columns?.groupBy { it.tableName }
            val tableName2Constraints = constraints?.groupBy { it.tableName }
            tableName2Columns?.forEach{ (tableName, tableColumns) ->
                val tableConstrains = tableName2Constraints?.get(tableName)
                temp.add(Table(tableName, tableColumns, tableConstrains))
            }
        }
        tables = temp
    }

    private fun initNodes() {
        nodes = tables.filter { !it.getIsRelationTable() }.mapNotNull { table ->
            table.columns?.let { columns ->
                Node(
                    table.name,
                    table.name,
                    columns.map { column ->
                        Property(
                            column.columnName,
                            column.columnName,
                            column.dataType,
                            ConverterUtils.convert(column.dataType.lowercase()),
                            column.columnKey == "PRI"
                        ).also { property ->
                            // TODO: 这里虽然很帅但是这里性能很差
                            property.isIgnored = table.foreignKeys
                                ?.map { it.columnName }
                                ?.contains(column.columnName) == true
                        }
                    }
                )
            }
        }
    }

    private fun initRelations() {
        val temp = mutableListOf<Relation>()
        tables.forEach { table ->
            if (table.getIsRelationTable()) {
                val source = table.foreignKeys?.get(0)?: Constraint()
                val target = table.foreignKeys?.get(1)?: Constraint()
                temp += Relation(
                    table.name,
                    source.referencedTableName,
                    target.referencedTableName,
                    true,
                    source,
                    target,
                )
            } else {
                table.foreignKeys?.forEach { fk ->
                    temp += Relation(fk.constraintName, fk.tableName, fk.referencedTableName, false, fk)
                }
            }
        }
        relations = temp
    }

    fun printTables() {
        println(tables.size)
        println(tables)
    }

    fun printNodes() {
        println(nodes.size)
        println(nodes)
    }

    fun printRelations() {
        println(relations.size)
        println(relations)
    }

    fun exportData() {
        (nodes + relations).filter { !it.isIgnored }.forEach { e ->
            CSVUtils.exportString(e.getHeader(), "$csvPath/${e.getFileName()}.header.csv")
            CSVUtils.exportResultSet(mySQLService.exportData(e), "$csvPath/${e.getFileName()}.csv")
        }
    }

    fun convert() {
        exportData()
        with(neo4jService) {
            nodes = this@ConverterService.nodes.map { it.name }
            relations = this@ConverterService.relations.map { it.name }
            import(csvPath)
        }
    }
}