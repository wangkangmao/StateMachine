package com.wangkm.statemachine

import android.content.Context
import com.tencent.matrix.plugin.DefaultPluginListener
import com.tencent.matrix.report.Issue
import com.tencent.matrix.util.MatrixLog

/**
 * @author: created by wangkm
 * @time: 2021/11/11 12:38
 * @desc：
 * @email: 1240413544@qq.com
 */
class TestPluginListener(context: Context?) : DefaultPluginListener(context) {



    override fun onReportIssue(issue: Issue) {
        super.onReportIssue(issue)
        MatrixLog.e(TAG, issue.toString())

        //add your code to process data
    }

    companion object {
        const val TAG = "Matrix.TestPluginListener"
    }
}