package com.zebra.android.aar.process

import org.gradle.api.Project

interface TaskRegister {
    fun apply(project: Project)
}
