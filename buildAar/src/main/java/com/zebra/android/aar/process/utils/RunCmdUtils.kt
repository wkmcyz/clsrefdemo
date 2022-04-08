package com.zebra.android.aar.process.utils

import utils.Extensions
import java.io.File

object RunCmdUtils {

    fun deleteZipEntry(zipPath: String, filePath: String, dir: File) {
        runCmd("zip -d $zipPath $filePath", dir)
    }

    fun runCmd(cmd: String, dir: File) {
        try {
            println("cmd = $cmd")
            val msg = Extensions.runCommand(cmd, dir)
            println("msg = ${msg}")
        } catch (t: Throwable) {
            println(t)
        }
    }
}
