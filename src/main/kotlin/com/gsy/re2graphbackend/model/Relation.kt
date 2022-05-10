package com.gsy.re2graphbackend.model

import com.gsy.re2graphbackend.imodel.Extractable
import java.util.*

data class Relation (
    var name: String = "",
    var startNode: String = "",
    var endNode: String = "",
    var isMul2Mul: Boolean = false,
    var constraint: Constraint,
    var secondConstraint: Constraint? = null,
    override var isIgnored: Boolean = false,
): Extractable {
    override fun getSQL(): String {
        return if (isMul2Mul) {
            secondConstraint?.let { secondConstraint ->
                val tempStart = "t" + UUID.randomUUID().toString().replace("-", "")
                val tempEnd = "t" + UUID.randomUUID().toString().replace("-", "")
                val tempI = "t" + UUID.randomUUID().toString().replace("-", "")
                val tempColumn = "c" + UUID.randomUUID().toString().replace("-", "")
                return """
                    select $tempStart.$tempColumn, $tempEnd.$tempColumn, '$name' as ':TYPE'
                    from ${constraint.tableName} 
                    join 
                        ( select (@i := @i+1) DIV 1 as $tempColumn, ${constraint.getAbsoluteRefColumnName()} 
                            from ${constraint.referencedTableName} force index(PRIMARY), 
                                (select (@i := 0)) $tempI ) $tempStart
                        on $tempStart.${constraint.referencedColumnName} = ${constraint.getAbsoluteColumnName()}
                    join 
                        ( select (@ii := @ii+1) DIV 1 as $tempColumn, ${secondConstraint.getAbsoluteRefColumnName()} 
                            from ${secondConstraint.referencedTableName} force index(PRIMARY), 
                                (select (@ii := 0)) $tempI ) $tempEnd
                        on $tempEnd.${secondConstraint.referencedColumnName} = ${secondConstraint.getAbsoluteColumnName()}
                """.trimIndent()
            }
            return """"""
        } else {
            val tempI = "t" + UUID.randomUUID().toString().replace("-", "")
            val tempColumn = "c" + UUID.randomUUID().toString().replace("-", "")
            """
                select (@i := @i+1) DIV 1 as $tempColumn, ${constraint.columnName}, '$name' as ':TYPE'
                from ${constraint.tableName} force index(PRIMARY), (select (@i := 0)) $tempI
            """.trimIndent()
        }
    }

    override fun getFileName(): String {
        return "${name}_rel"
    }

    override fun getHeader(): Array<String> {
        return arrayOf(":START_ID($startNode)",":END_ID($endNode)",":TYPE")
    }
}