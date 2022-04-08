package com.zebra.android.gradle

import com.zebra.android.aar.process.ParseDependencyTask
import com.zebra.android.aar.process.RemoveDuplicatedResourceFromJarTask
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class AarProcessorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        RemoveDuplicatedResourceFromJarTask.createTaskRegister().apply(project)
        ParseDependencyTask.createTaskRegister().apply(project)
    }

}
