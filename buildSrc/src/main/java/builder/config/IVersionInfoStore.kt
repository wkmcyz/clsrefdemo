/*
 *
 *  * Copyright 2017 fenbi.com. All rights reserved.
 *  * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package builder.config


/**
 * 用来获取 aar 版本相关的信息
 * @author wkmcyz@163.com.
 * @since 03-09-2022
 */
interface IVersionInfoStore {

    /**
     * 获取指定的 commitId 所对应的 模块:版本 信息。
     * 表示该 commitId 上，这些模块的 aar 是已经发布了的。
     *
     * @param commitId String
     * @return Map<String, String>
     */
    fun getModuleVersionInfoByCommit(commitId: String): Map<String, String>

}
