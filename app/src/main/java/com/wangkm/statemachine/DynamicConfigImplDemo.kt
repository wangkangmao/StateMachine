package com.wangkm.statemachine

import com.tencent.mrs.plugin.IDynamicConfig

/**
 * @author: created by wangkm
 * @time: 2021/11/11 12:38
 * @desc：
 * @email: 1240413544@qq.com
 */
class DynamicConfigImplDemo : IDynamicConfig {
    val isFPSEnable: Boolean
        get() = true

    val isTraceEnable: Boolean
        get() = true

    val isMatrixEnable: Boolean
        get() = true

    val isDumpHprof: Boolean
        get() = false

    val isSignalAnrTraceEnable: Boolean
        get() = true


    override fun get(key: String, defStr: String): String {
        return null.toString()
    }

    override fun get(key: String, defInt: Int): Int {
        return 0
    }

    override fun get(key: String, defLong: Long): Long {
        return 0
    }

    override fun get(key: String, defBool: Boolean): Boolean {
        return false
    }

    override fun get(key: String, defFloat: Float): Float {
        return 0F
    }
}