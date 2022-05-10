package com.gsy.re2graphbackend.model

data class Table(
    var name: String = "",
    var columns: List<Column>? = null,
    var foreignKeys: List<Constraint>? = null,
) {
    private var isRelationTable: Boolean? = null

    fun getIsRelationTable(): Boolean{
        isRelationTable?.let {
            return it
        }
        if (foreignKeys?.size != 2) {
            isRelationTable = false
            return isRelationTable as Boolean
        }
        val columnsIsForeignKey = mutableSetOf<String>()
        foreignKeys?.forEach{ fk ->
            columnsIsForeignKey.add(fk.columnName)
        }
        isRelationTable = (columnsIsForeignKey.size == columns?.size)
        return isRelationTable as Boolean
    }
}