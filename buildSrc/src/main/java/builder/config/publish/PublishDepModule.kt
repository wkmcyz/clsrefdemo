package builder.config.publish

/**
 * build 的时候依赖的模块配置
 * @author chaochaowu @ Zebra Inc.
 * @since 12-23-2021
 */
class PublishDepModule {
    /**
     * module 名称，例如 "zebraui"
     */
    var module: String = ""

    /**
     * 发布的时候要使用的依赖版本。
     */
    var version:String = ""

    /**
     * 当前项目里对本 module 的依赖是否使用远端的 aar。
     * 如果是 true 的话，表示依赖远端的 aar；
     * 如果是 false 的话，表示依赖本地的 project.
     */
    var useAar: Boolean = false
}
