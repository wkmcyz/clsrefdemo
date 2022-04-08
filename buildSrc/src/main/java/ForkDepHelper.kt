import bean.AddDepData
import bean.DepData
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate

/**
 * @author zhangconghui @ Zebra Inc.
 * @since 01-19-2022
 */
object ForkDepHelper {

    private val forkModNames = setOf(
        "curryResources",
        "forkMainResources",
        "forkCurryLib",
        "forkMainLib"
    )

    @JvmStatic
    fun addForkModules(project: Project) {
        project.rootProject.subprojects {
            afterEvaluate {
                if (forkModNames.contains(name)) {
                    return@afterEvaluate
                }
                if (name == "forkMainCommonRes") {
                    return@afterEvaluate
                }
                val forkBuildType: String by rootProject.extra

                addForkMods(forkBuildType, "implementation")
                addForkMods(forkBuildType, "api")
            }
        }
    }

    private fun Project.addForkMods(buildType: String, configurationName: String) {
        configurations.findByName(configurationName)?.let { impl ->
            impl.dependencies.toList().forEach { d ->
                when (d.name) {
                    "commonResources" -> {
                        when (buildType) {
                            "curry" -> dependencies.add(configurationName, project(":curryResources"))
                            "main", "HD", "potter" -> dependencies.add(configurationName, project(":forkMainResources"))
                        }
                    }
                    "zebraui" -> {
                        when (buildType) {
                            "curry" -> dependencies.add(configurationName, project(":forkCurryLib"))
                            "main", "HD", "potter" -> dependencies.add(configurationName, project(":forkMainLib"))
                        }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun addDepsWithFork(project: Project) {
        val forkBuildType: String by project.rootProject.extra

        /**
         * key: 特定端，如: 国内版/印度版/国际版
         * value: 需要给哪个模块添加哪些依赖
         */
        val forkDepsMap: Map<String, List<AddDepData>> = mapOf(
            "curry" to listOf(
                AddDepData(
                    modName = "bizHomeHD",
                    deps = listOf(
                        DepData(
                            isProject = true,
                            addName = "bizHome"
                        )
                    )
                )
            ),
            "main" to listOf(
                AddDepData(
                    modName = "bizHomeHD",
                    deps = listOf(
                        DepData(
                            isProject = true,
                            addName = "bizHomeMain"
                        )
                    )
                )
            )
        )

        project.rootProject.subprojects {
            afterEvaluate {
                forkDepsMap[forkBuildType]?.let { deps ->
                    addDepVersion(deps)
                }
            }
        }
    }

    private fun Project.addDepVersion(addDeps: List<AddDepData>) {
        val deps = addDeps.filter { it.modName == name }.flatMap { it.deps }
        if (deps.isEmpty()) {
            return
        }

        deps.forEach { dep ->
            if (dep.isProject) {
                println("zch1 projectDir=${project(":${dep.addName}").projectDir.absolutePath}")

                dependencies.add(
                    "implementation",
                    project(":${dep.addName}")
                )
            } else {
                dependencies.add(
                    "implementation",
                    "${dep.group}:${dep.artifact}:${dep.version}"
                )
            }
        }
    }

    @JvmStatic
    fun replaceDepsWithFork(project: Project) {
        project.rootProject.subprojects {
            afterEvaluate {
                val versions: Map<String, String> by rootProject.extra
                val forkBuildType: String by rootProject.extra

                /**
                 * key: 特定端，如: 国内版/印度版/国际版
                 * value: 需要替换版本的依赖
                 */
                val forkDepsMap: Map<String, List<DepData>> = mapOf(
                    "curry" to listOf(
                        DepData(
                            group = "com.yuantiku.android.common",
                            artifact = "ytk-common-frog",
                            version = versions["ytkCommonFrogGooglePlay"].orEmpty()
                        ),
                        DepData(
                            group = "com.zebra.android.component",
                            artifact = "lib_cocos_game_so",
                            version = versions["cocosGameCurry"].orEmpty()
                        ),
                        DepData(
                            group = "com.zebra.android.component",
                            artifact = "network-diagnose",
                            version = versions["networkDiagnoseCurry"].orEmpty()
                        )
                    )
                )

                forkDepsMap[forkBuildType]?.let { deps ->
                    replaceDepVersion(deps)
                }
            }
        }
    }

    private fun Project.replaceDepVersion(deps: List<DepData>) {
        if (deps.isEmpty()) {
            return
        }
        configurations.all {
            resolutionStrategy.dependencySubstitution {
                deps.forEach { dep ->
                    if (dep.isProject) {
                        substitute(project(":${dep.fromName}"))
                            .with(project(":${dep.toName}"))
                    } else {
                        substitute(module("${dep.group}:${dep.artifact}"))
                            .with(module("${dep.group}:${dep.artifact}:${dep.version}"))
                    }
                }
            }
        }
    }

    @JvmStatic
    fun deleteDepsWithFork(project: Project) {
        project.rootProject.subprojects {
            afterEvaluate {
                val versions: Map<String, String> by rootProject.extra
                val forkBuildType: String by rootProject.extra

                /**
                 * key: 特定端，如: 国内版/印度版/国际版
                 * value: 需要删除的依赖
                 */
                val forkDepsMap: Map<String, List<DepData>> = mapOf(
                    "curry" to listOf(
                        DepData(
                            group = "com.tencent.bugly",
                            artifact = "nativecrashreport",
                            version = versions["nativeCrashReport"].orEmpty()
                        ),
                        DepData(
                            group = "com.tencent.bugly",
                            artifact = "crashreport_upgrade",
                            version = versions["crashReportUpgrade"].orEmpty()
                        ),
                        DepData(
                            group = "com.tencent.bugly",
                            artifact = "crashreport",
                            version = versions["crashReport"].orEmpty()
                        )
                    )
                )

                forkDepsMap[forkBuildType]?.let { deps ->
                    deleteDepVersion(deps)
                }
            }
        }
    }

    private fun Project.deleteDepVersion(deps: List<DepData>) {
        if (deps.isEmpty()) {
            return
        }

        val versions: Map<String, String> by rootProject.extra
        configurations.all {
            resolutionStrategy.dependencySubstitution {
                deps.forEach { dep ->
                    if (dep.isProject) {
                        //substitute project with kotlin-stdlib
                    } else {
                        substitute(module("${dep.group}:${dep.artifact}"))
                            .with(module("org.jetbrains.kotlin:kotlin-stdlib:${versions["kotlin_version"]}"))
                    }
                }
            }
        }
    }
}
