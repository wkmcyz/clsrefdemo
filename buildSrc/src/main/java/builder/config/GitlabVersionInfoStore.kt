/*
 *
 *  * Copyright 2017 fenbi.com. All rights reserved.
 *  * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package builder.config


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.charset.Charset

/**
 * 使用一个 gitlab 仓库来模拟数据的存储。
 * gitlab 仓库为 https://gitlab.zhenguanyu.com/wuchaochao/ape-sdk-aar-version-storage
 * 本地目录为 publishAarProject/ape-sdk-aar-version-storage.
 *
 * 远端仓库的 master 分支的 /latest 保存了最新的 commitId
 * 远端仓库的 master 分支的 /data/xxx 保存了每个 commit 对应的 module:version 信息。
 *
 * @author wkmcyz@163.com.
 * @since 03-09-2022
 */
object GitlabVersionInfoStore : IVersionInfoStore {

    private val client = OkHttpClient()
    private val gson = Gson()

    private const val GITLAB_REPO_RAW = "https://gitlab.zhenguanyu.com/wuchaochao/ape-sdk-aar-version-storage/raw"
    private const val BRANCH = "master"
    private const val GITLAB_REPO_RAW_PREFIX = "$GITLAB_REPO_RAW/$BRANCH"

    private const val COMMIT_VERSION_INFO_DIR_PATH = "data/"

    override fun getModuleVersionInfoByCommit(commitId: String): Map<String, String> {
        val request = Request.Builder()
            .url("$GITLAB_REPO_RAW_PREFIX/$COMMIT_VERSION_INFO_DIR_PATH/${commitId}")
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body
            val msg = body?.bytes()?.toString(Charset.forName("UTF-8")) ?: ""
            body?.close()
            return gson.fromJson<HashMap<String, String>>(
                msg,
                object : TypeToken<HashMap<String, String>>() {}.type
            )
        } else {
            return emptyMap()
        }
    }

}
