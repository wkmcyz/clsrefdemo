package builder.config.publish

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import utils.Global
import java.io.File

/**
 * 发布构建 ： 用来对一个模块进行 publish 的构建
 *  发布构建的时候使用的版本信息获取工具类。
 * @author chaochaowu @ Zebra Inc.
 * @since 12-24-2021
 */
object PublishModuleConfigResolver {

    /**
     * 发布构建里 模块的 "发布版本号"
     * 表示某模块如果要进行 publish 的话,应该使用的版本号.
     */
    @JvmStatic
    private val publishConfigFileRelativePath: File
        get() = File(Global.rootDir, "zsdk/buildConfig/publishModuleConfig.json")

    /**
     * 获取当前要设置的   发布信息
     */
    @JvmStatic
    fun getPublishInfo(): LinkedHashMap<String, String> {
        val map = resolvePublishInfo(publishConfigFileRelativePath)
        val linkedHashMap = LinkedHashMap<String, String>()
        map.forEach { module, version ->
            linkedHashMap[module] = version
        }
        return linkedHashMap
    }

    private val gson = Gson()

    private fun resolvePublishInfo(file: File): Map<String, String> {
        // 从文件中读取
        val configText = file.readText()
        val gson = Gson()
        val map = gson.fromJson<HashMap<String, String>>(
            configText,
            object : TypeToken<HashMap<String, String>>() {}.type
        )
        return map
    }
}
