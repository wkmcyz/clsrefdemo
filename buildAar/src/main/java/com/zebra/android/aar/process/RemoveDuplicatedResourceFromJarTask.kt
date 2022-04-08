package com.zebra.android.aar.process

import com.zebra.android.aar.process.utils.RunCmdUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.jar.JarFile

abstract class RemoveDuplicatedResourceFromJarTask : DefaultTask() {

    companion object {
        const val TASK_NAME_REMOVE_DUPLICATED_RESOURCE = "removeDuplicatedResource"
        private const val NAME_FOLDER_RES = "res"
        private const val NAME_CLASSES_DOT_JAR = "classes.jar"
        private const val NAME_AAR_UNZIP_FILE = "aar_unzip"
        private const val SUFFIX_TMP = ".tmp"
        private const val NAME_BUNDLE_RELEASE_AAR = "bundleReleaseAar"
        private const val SUFFIX_AAR = "aar"

        fun createTaskRegister(): TaskRegister {
            return object : TaskRegister {
                override fun apply(project: Project) {

                    project.afterEvaluate {

                        val taskRemoveDuplicatedResource = this.tasks.register(
                            TASK_NAME_REMOVE_DUPLICATED_RESOURCE,
                            RemoveDuplicatedResourceFromJarTask::class.java
                        ).get()

                        project.tasks.findByName(NAME_BUNDLE_RELEASE_AAR)?.let { sourceTask ->
                            project.logger.log(LogLevel.DEBUG, "find the task ${sourceTask.name}, ${sourceTask.outputs}")
                            val outputJar = sourceTask.outputs.files.find {
                                it.absolutePath.endsWith(SUFFIX_AAR)
                            }
                            taskRemoveDuplicatedResource.outputFilePath = outputJar?.absolutePath
                            sourceTask.finalizedBy(taskRemoveDuplicatedResource)
                        }
                    }
                }
            }
        }
    }

    @Input
    var outputFilePath: String? = null

    @ExperimentalStdlibApi
    @TaskAction
    fun taskAction() {

        project.logger.log(LogLevel.DEBUG, "===== AarProcessorTask executed, $outputFilePath")
        val outputZipPath = outputFilePath ?: return
        perform(outputZipPath)
    }

    private fun perform(outputZipPath: String?) {
        outputZipPath ?: return

        val originFile = File(outputZipPath)
        val originName = originFile.name
        val originFilePathWithoutName = outputZipPath.substring(0, outputZipPath.length - originName.length)
        val tmpFile = File(originFile.absolutePath + SUFFIX_TMP)
        tmpFile.delete()
        originFile.renameTo(tmpFile)
        val aarUnzipFileName = NAME_AAR_UNZIP_FILE
        val aarUnzipFile = File(originFilePathWithoutName, aarUnzipFileName)
        aarUnzipFile.delete()

        RunCmdUtils.runCmd("unzip $tmpFile -d ${aarUnzipFile.absolutePath}", dir = project.rootDir)

        project.logger.log(LogLevel.DEBUG, "aarUnzipFile = $aarUnzipFile")
        val resDirNames = (File(aarUnzipFile, NAME_FOLDER_RES).listFiles() ?: emptyArray())
            .map {
                it.name
            }

        val classesDotJarFile = File(aarUnzipFile, NAME_CLASSES_DOT_JAR)
        val classesDotJar = JarFile(classesDotJarFile)
        classesDotJar.stream().filter { jarEntry ->
            resDirNames.find { jarEntry.name.startsWith(it) } != null
        }.map { it.name }.forEach {
            RunCmdUtils.deleteZipEntry(classesDotJarFile.absolutePath, it, dir = project.rootDir)
        }

        RunCmdUtils.runCmd("zip -r ${originFile.absolutePath} .", dir = aarUnzipFile)
    }

}
