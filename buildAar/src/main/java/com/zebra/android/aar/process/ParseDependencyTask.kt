package com.zebra.android.aar.process

import com.zebra.android.aar.process.utils.ProjectDependencyDataHandler
import com.zebra.android.aar.process.utils.RootProjectExtraUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ParseDependencyTask : DefaultTask() {

    companion object {

        private const val TASK_NAME = "parseDependencyTask"

        fun createTaskRegister(): TaskRegister {
            return object : TaskRegister {
                override fun apply(project: Project) {
                    registerTaskOnce(project)
                    parseDependencies(project)
                }
            }
        }

        private fun parseDependencies(project: Project) {
            project.gradle.taskGraph.addTaskExecutionGraphListener {
                project.logger.log(LogLevel.DEBUG, "taskGraph populated for $project")
                project.configurations.forEach { configuration ->
                    configuration.allDependencies.forEach { dependency ->
                        if (dependency is DefaultProjectDependency) {
                            project.logger.log(LogLevel.DEBUG, "find DefaultProjectDependency $dependency Configure project")
                            val group = dependency.group // ape-zebraenglish-android-component
                            val name = dependency.name // eg: log, commonData
                            val version = dependency.version // "unspecified"
                            if (project.name != name) {
                                RootProjectExtraUtils.saveProjectDependency(
                                    project,
                                    project.name,
                                    name
                                )
                            }
                        }
                    }
                }
            }
        }

        @Synchronized
        private fun registerTaskOnce(project: Project) {
            if (project.rootProject.tasks.findByName(TASK_NAME) == null) {
                project.rootProject.tasks.register(
                    TASK_NAME, ParseDependencyTask::class.java
                )
            }
        }
    }

    @ExperimentalStdlibApi
    @TaskAction
    fun taskAction() {
        project.logger.log(LogLevel.DEBUG, "ParseDependencyTask taskAction")

        val map = RootProjectExtraUtils.getProjectDependency(project)
        map.forEach {
            project.logger.log(LogLevel.DEBUG, "project ${it.key} values: ${it.value.keys().toList().joinToString()}")
        }
        val projectOrder = ProjectDependencyDataHandler(project.logger).handle(map)
        val parent = File(project.rootProject.buildDir.absolutePath)
        if (!parent.exists()) {
            parent.mkdirs()
        }
        val file = File(parent, "project-order.txt")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        file.printWriter().use { out ->
            projectOrder.forEach {
                out.println(it)
            }
        }
        project.logger.log(LogLevel.DEBUG, "file.absolutePath = ${file.absolutePath}")
    }
}
