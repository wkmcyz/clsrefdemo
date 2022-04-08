package utils

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


object Extensions {

    private const val DEBUG_LOG = false

    /**
     * 阻塞执行命令。
     *
     * [returnOutput] : true ，会返回命令的输出； false : 返回 null
     */
    fun runCommand(cmd: List<String>, workingDir: File = Global.rootDir, returnOutput: Boolean = true): String? {
        try {
            val parts = cmd
            if (DEBUG_LOG) {
                println("exec : ${parts.joinToString(" ")}")
            }
            val procBuilder = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)

            val newLineCharacter = System.getProperty("line.separator")
            val proc = procBuilder.start()
            val brStdOut = BufferedReader(InputStreamReader(proc.inputStream))
            val brStdErr = BufferedReader(InputStreamReader(proc.errorStream))
            val outputBuilder = StringBuilder()
            val errorBuilder = StringBuilder()
            // read proc output
            while (proc.isAlive) {
                while (true) {
                    val line = brStdOut.readLine() ?: break
                    if (DEBUG_LOG) {
                        println(line)
                    }
                    outputBuilder.append(line + newLineCharacter)
                }
                while (true) {
                    val line = brStdErr.readLine() ?: break
                    if (DEBUG_LOG) {
                        println(line)
                    }
                    errorBuilder.append(line + newLineCharacter)
                }
                proc.waitFor(100, TimeUnit.MILLISECONDS)
            }
            while (true) {
                val line = brStdOut.readLine() ?: break
                if (DEBUG_LOG) {
                    println(line)
                }
                outputBuilder.append(line + newLineCharacter)
            }
            while (true) {
                val line = brStdErr.readLine() ?: break
                if (DEBUG_LOG) {
                    println(line)
                }
                errorBuilder.append(line + newLineCharacter)
            }
            // output
            val output = "$outputBuilder"
            val error = "$errorBuilder"
            val exitValue = proc.exitValue()
            if (exitValue != 0) {
                println("===============ERROR LOG START==============")
                error?.let {
                    println(it)
                }
                println("===============ERROR LOG END==============")
                throw IllegalStateException("exit code of `$cmd` is not zero!")
            }
            return if (returnOutput) output.trim() else null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 将 [cmd] 按照空格拆分成多个命令参数进行执行
     */
    fun runCommand(cmd: String, workingDir: File = Global.rootDir, returnOutput: Boolean = true): String? {
        val parts = cmd.split("\\s".toRegex())
        return runCommand(parts, workingDir = workingDir, returnOutput = returnOutput)
    }

}