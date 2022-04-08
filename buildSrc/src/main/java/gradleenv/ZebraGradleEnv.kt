/*
 *
 *  * Copyright 2017 fenbi.com. All rights reserved.
 *  * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

package gradleenv

import org.gradle.api.Project

/**
 * 一次构建过程中的 环境变量（全局变量）
 * @author chaochaowu @ Zebra Inc.
 * @since 01-19-2022
 */
object ZebraGradleEnv {

    /**
     * 是否是 google play 的打包
     */
    @JvmStatic
    var isGooglePlayBuild = false
        get() = field || isCretaBuild

    /**
     * 是否是印度版的打包
     */
    @JvmStatic
    var isCretaBuild = false

    @JvmStatic
    fun initGooglePlay(project: Project) {
        isGooglePlayBuild = if (project.hasProperty("googleplay")) {
            project.property("googleplay") == "true"
        } else {
            false
        }
    }
}
