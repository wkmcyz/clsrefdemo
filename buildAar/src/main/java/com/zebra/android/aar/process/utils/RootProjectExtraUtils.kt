package com.zebra.android.aar.process.utils

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.util.concurrent.ConcurrentHashMap

object RootProjectExtraUtils {

    private const val KEY_DEPENDENCIES_MAP = "DEPENDENCIES_MAP"

    @Synchronized
    fun saveProjectDependency(
        project: Project,
        projectName: String,
        projectDependencyName: String
    ) {
        // println("project = [${project}], projectName = [${projectName}], projectDependencyName = [${projectDependencyName}]")
        val rootProject = project.rootProject
        val map = initOrGetMap(rootProject)
        val projectDependency = map.getOrDefault(projectName, ConcurrentHashMap())
        projectDependency[projectDependencyName] = 1
        map[projectName] = projectDependency
    }

    @Synchronized
    fun getProjectDependency(project: Project): ConcurrentHashMap<String, ConcurrentHashMap<String, Int>> {
        // println("project = [${project}]")
        return initOrGetMap(project)
    }

    @Synchronized
    private fun initOrGetMap(project: Project): ConcurrentHashMap<String, ConcurrentHashMap<String, Int>> {
        val rootProject = project.rootProject
        return if (rootProject.extra.has(KEY_DEPENDENCIES_MAP)) {
            @Suppress("UNCHECKED_CAST")
            rootProject.extra.get(KEY_DEPENDENCIES_MAP) as ConcurrentHashMap<String, ConcurrentHashMap<String, Int>>
        } else {
            ConcurrentHashMap<String, ConcurrentHashMap<String, Int>>().apply {
                project.rootProject.extra.set(KEY_DEPENDENCIES_MAP, this)
            }
        }
    }
}
