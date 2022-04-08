/*
 *
 *  * Copyright 2017 fenbi.com. All rights reserved.
 *  * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

package commonutil.gradleutil

import org.gradle.api.Project


/**
 * 用于 gradle 的一些逻辑判断处理
 * @author chaochaowu @ Zebra Inc.
 * @since 01-19-2022
 */
object ZebraGradleUtil {

    @JvmStatic
    fun isReleaseBuild(project: Project): Boolean {
        val isRelease = project.gradle.startParameter.taskNames.any { taskName ->
            taskName.contains("Release") || taskName.contains("uploadAppHub")
        }
        return isRelease
    }

    /**
     * 获取开启本次 build 的 project 的名称。
     * 例如: 执行 "./gradlew :main:clean"的话，会返回 "main".
     * 如果包含多个命令，例如 "./gradlew :main:clean :main2:assembleRelease" ,则会返回第一个命令的 project 名称，也就是 "main"
     */
    @JvmStatic
    private fun retrieveBuildEntryProjectName(project: Project): String? {
        return project.gradle.startParameter.taskNames.firstOrNull {
            it.startsWith(":")
        }?.split(":")?.firstOrNull()
    }

    /**
     * 解析出来本次编译要使用的 forkLib 的类型。
     */
    @JvmStatic
    fun retrieveBuildType(project: Project): String {
        val projectName = retrieveBuildEntryProjectName(project)
        val buildType= when (projectName) {
            "main" -> "main"
            "mainHD" -> "HD"
            "mainPotter" -> "potter"
            else -> "main"
        }
        //println("zch1 projectName:$projectName, buildType:$buildType")
        return buildType
    }
}
