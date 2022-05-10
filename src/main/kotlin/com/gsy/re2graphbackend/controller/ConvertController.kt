package com.gsy.re2graphbackend.controller

import com.gsy.re2graphbackend.resp.ResultCode
import com.gsy.re2graphbackend.service.ConverterService
import com.gsy.re2graphbackend.service.MySQLService
import com.gsy.re2graphbackend.service.Neo4jService
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
class ConvertController {

    @Autowired
    val mySQLService = MySQLService()
    @Autowired
    val neo4jService = Neo4jService()
    @Autowired
    val convertService = ConverterService()

    @PostMapping("/mysql/connect")
    @ResponseBody
    fun mySQLConnect(@RequestBody params: Map<String, String>): ResultCode {
        val res = with(mySQLService) {
            userName = params["userName"]?: ""
            password = params["password"]?: ""
            database = params["database"]?: ""
            connect()
        }
        return when(res) {
            0 -> ResultCode.SUCCESS
            else -> ResultCode.INTERNAL_ERROR
        }
    }

    @PostMapping("/converter/csvPath")
    @ResponseBody
    fun updateCsvPath(@RequestBody params: Map<String, String>): ResultCode {
        convertService.csvPath = params["csvPath"]?: ""
        return ResultCode.SUCCESS
    }

    @PostMapping("/neo4j/info")
    @ResponseBody
    fun updateNeo4jInfo(@RequestBody params: Map<String, String>): ResultCode {
        neo4jService.neo4jPath = params["neo4jPath"]?: ""
        neo4jService.targetDatabase = params["targetDatabase"]?: ""
        return ResultCode.SUCCESS
    }

    @GetMapping("/converter/results")
    @ResponseBody
    fun getNodesAndRelations(): Any {
        convertService.loadData()
        return mapOf(
            "code" to ResultCode.SUCCESS.code,
            "message" to ResultCode.SUCCESS.message,
            "nodes" to convertService.nodes,
            "relations" to convertService.relations,
        )
    }

    data class UpdateNodesAndRelationsParams(
        val nodes: List<NodeForUpdate> = emptyList(),
        val relations: List<RelationForUpdate> = emptyList(),
    ) {
        class NodeForUpdate {
            val isIgnored: Boolean = false
            val properties: List<PropertyForUpdate> = emptyList()
        }
        class RelationForUpdate {
            val isIgnored: Boolean = false
        }
        class PropertyForUpdate {
            val isIgnored: Boolean = false
            val name: String = ""
        }
    }

    @PostMapping("/converter/results")
    @ResponseBody
    fun updateNodesAndRelations(@RequestBody params: UpdateNodesAndRelationsParams): ResultCode {
        params.nodes.forEachIndexed{index, node ->
            convertService.nodes[index].isIgnored = node.isIgnored
            convertService.nodes[index].properties.forEachIndexed { indexP, property ->
                property.name = node.properties[indexP].name
                property.isIgnored = node.properties[indexP].isIgnored
            }
        }
        params.relations.forEachIndexed { index, relation ->
            convertService.relations[index].isIgnored = relation.isIgnored
        }
        print(convertService.nodes)
        return ResultCode.SUCCESS
    }

    @GetMapping("/converter/convert")
    @ResponseBody
    fun convert():ResultCode {
        var res: Int? = null
        runBlocking {
            launch {
                res = withTimeoutOrNull(5*60*1000) {
                    convertService.convert()
                    0
                }
            }
        }
        return when (res) {
            0 -> ResultCode.SUCCESS
            else -> ResultCode.INTERNAL_ERROR
        }
    }
}