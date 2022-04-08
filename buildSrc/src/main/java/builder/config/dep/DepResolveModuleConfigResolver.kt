package builder.config.dep

import builder.config.GitlabVersionInfoStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import utils.Extensions
import utils.Global
import java.io.File

/**
 * 从 [configFileRelativePath] 里解析出来
 * - 当前依赖的 模块 aar 版本信息。
 * - 当前这些模块使用 project 依赖还是 aar 依赖。
 * @author chaochaowu @ Zebra Inc.
 * @since 12-24-2021
 */
object DepResolveModuleConfigResolver {

    @JvmStatic
    val configFileRelativePath = "zsdk/buildConfig/depModuleConfig.json"

    private val enabledConfigFile: File
        get() = File(Global.rootDir, "zsdk/buildConfig/enableOptionalAarBuild")

    private var moduleConfigs: List<DepModuleConfig>? = null

    /**
     * zsdk 仓库, 用来进行发布 aar 的分支.
     */
    const val TARGET_BRANCH = "develop"

    val versionMap: LinkedHashMap<String, String> by lazy {
        val commit = resolveTargetCommit()
        val map = GitlabVersionInfoStore.getModuleVersionInfoByCommit(commit)
        val m = LinkedHashMap<String, String>()
        map.forEach { s, s2 ->
            m.put(s, s2)
        }
        println(m)
        m
    }

    @JvmStatic
    val enabled: Boolean
        get() = enabledConfigFile.exists() && enabledConfigFile.readText().trim().equals("true")

    /**
     * 获取当前 commit 和 zsdk 目标分支最近的祖先节点
     */
    private fun resolveTargetCommit(): String {
        val zsdk = File(Global.rootDir, "zsdk")
        Extensions.runCommand("git fetch", workingDir = zsdk)
        val cmt = Extensions.runCommand("git merge-base origin/$TARGET_BRANCH HEAD", workingDir = zsdk) ?: ""
        return cmt
    }

    /**
     *   获取 <module , version> 的映射关系。
     *   这部分信息在远端，不在本地。
     */
    @JvmStatic
    fun getModuleVersionMapFromRootDir(rootDir: File): LinkedHashMap<String, String> {
        return versionMap
    }

    /**
     * 从 path 解析出来。 <module, 是否使用 aar> 的映射关系。
     */
    @JvmStatic
    fun getModuleUseAarMapFromRootDir(rootDir: File): LinkedHashMap<String, Boolean> {
        val list = resolveArtifactsFromModuleConfig(File(rootDir, configFileRelativePath))
        val map = LinkedHashMap<String, Boolean>()
        list.forEach {
            map[it.module] = it.useAar
        }
        return map
    }

    private val gson = Gson()

    private fun resolveArtifactsFromModuleConfig(file: File): List<DepModuleConfig> {
        // 从文件中读取
        val configText = file.readText()
        val gson = Gson()
        val list = gson.fromJson<List<DepModuleConfig>>(
            configText,
            object : TypeToken<List<DepModuleConfig>>() {}.type
        )
        moduleConfigs = list
        return list
    }

}
