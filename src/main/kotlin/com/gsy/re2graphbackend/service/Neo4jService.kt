package com.gsy.re2graphbackend.service

import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
class Neo4jService(
    var neo4jPath: String = "",
    var targetDatabase: String = "",
    var nodes: List<String> = emptyList(),
    var relations: List<String> = emptyList(),
) {
    fun import(csvPath: String) {
        val commands = mutableListOf(
            "cmd",
            "/c",
            "$neo4jPath/neo4j-admin",
            "import",
            "--force",
            "--database=$targetDatabase",
        )
        commands += nodes.map { node->
            "--nodes=$csvPath/${node}_node.header.csv,$csvPath/${node}_node.csv"
        }.toMutableList()
        commands += relations.map { relation->
            "--relationships=$csvPath/${relation}_rel.header.csv,$csvPath/${relation}_rel.csv"
        }.toMutableList()
        val pb = ProcessBuilder(commands)
        println(pb.command().joinToString(separator = "\n") { it })
        with(pb.start()) {
            val br = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            while (isAlive) {
                while (br.ready()) {
                    println(br.readLine())
                }
            }
        }
    }
}