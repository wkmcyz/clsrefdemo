package com.zebra.android.aar.process.utils

import org.gradle.api.logging.LogLevel
import java.util.concurrent.ConcurrentHashMap
import org.gradle.api.logging.Logger

class ProjectDependencyDataHandler(
    private val logger: Logger
) {

    fun handle(map: ConcurrentHashMap<String, ConcurrentHashMap<String, Int>>): List<String> {
        val parseResult = parseData(map)
        return topologicalSort(parseResult)
    }

    private fun parseData(map: ConcurrentHashMap<String, ConcurrentHashMap<String, Int>>): ParseResult {
        val dependencies = HashMap<String, Set<String>>()
        map.map {
            dependencies[it.key] = it.value.keys
        }

        val totalProjectList = dependencies.flatMap { it.value + it.key }.toSet().toList()
        val projectNameToIndexMap = totalProjectList.mapIndexed { index: Int, s: String ->
            s to index
        }.toMap()

        val prerequisites = dependencies.flatMap {
            val projectIndex = projectNameToIndexMap[it.key] ?: -1
            val intArray = it.value.map { projectNameToIndexMap.getOrDefault(it, -1) }
                .filter { it != -1 }
                .toIntArray()

            if (projectIndex != -1) {
                intArray.map { depenIndex ->
                    IntArray(2).apply {
                        this[0] = projectIndex
                        this[1] = depenIndex
                    }
                }
            } else {
                listOf(IntArray(0))
            }
        }.toTypedArray()

        return ParseResult(
            totalProjectList,
            prerequisites
        )
    }


    private fun topologicalSort(parseResult: ParseResult): List<String> {
        val totalProjectList = parseResult.totalProjectList
        val prerequisites = parseResult.prerequisites

        val result = TopologicalSort().findOrder(
            totalProjectList.size,
            prerequisites
        )

        logger.log(LogLevel.DEBUG, "totalProjectList.joinToString() = ${totalProjectList.joinToString()}")
        logger.log(LogLevel.DEBUG, "prerequisites.contentDeepToString() = ${prerequisites.contentDeepToString()}")
        logger.log(LogLevel.DEBUG, "result.joinToString() = ${result.joinToString()}")

        val projectOrder = result
            .map { totalProjectList.getOrNull(it) ?: "" }
            .filter { it.isNotEmpty() }
        logger.log(LogLevel.DEBUG, "ordered = ${projectOrder}")

        StringBuilder().apply {
            append("digraph pic {\n")
            prerequisites.forEach {
                append("${totalProjectList[it[0]]} -> ${totalProjectList[it[1]]}\n")
            }
            append("}")
        }.toString().let {
            logger.log(LogLevel.INFO, "it = ${it}")
        }

        return projectOrder
    }
}

data class ParseResult(
    val totalProjectList: List<String>,
    val prerequisites: Array<IntArray>
)
