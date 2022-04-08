package utils

/**
 * TODO 简要描述
 * @author chaochaowu @ Zebra Inc.
 * @since 08-02-2021
 */
object Git {

    val currentCommitHash: String
        get() {
            val output = Extensions.runCommand("git rev-parse HEAD", Global.rootDir)
            return output ?: throw IllegalStateException("can not get git commit hash ")
        }

    val currentCommitHashShort: String
        @Suppress("MagicNumber")
        get() = currentCommitHash.substring(0, 7)


    val currentGitBranch: String
        get() {
            val output = utils.Extensions.runCommand("git name-rev --name-only HEAD", Global.rootDir)
            return output ?: throw IllegalStateException("can not get git branch ")
        }
}
