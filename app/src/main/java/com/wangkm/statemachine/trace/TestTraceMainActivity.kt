/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wangkm.statemachine.trace

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.tencent.matrix.AppActiveMatrixDelegate
import com.tencent.matrix.Matrix
import com.tencent.matrix.listeners.IAppForeground
import com.tencent.matrix.plugin.Plugin
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.constants.Constants
import com.tencent.matrix.trace.core.UIThreadMonitor
import com.tencent.matrix.trace.tracer.SignalAnrTracer
import com.tencent.matrix.trace.view.FrameDecorator
import com.tencent.matrix.util.MatrixLog
import com.wangkm.statemachine.R
import com.wangkm.statemachine.issue.IssueFilter
import kotlinx.android.synthetic.main.test_trace.*

class TestTraceMainActivity : Activity(), IAppForeground {
    var decorator: FrameDecorator? = null
    private var frameIntervalMs = 0f
    private var maxFps = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_trace)
        initView()
        initAction()
        initData()
    }

    private fun initData() {
        this.frameIntervalMs = 1f * UIThreadMonitor.getMonitor()
            .frameIntervalNanos / Constants.TIME_MILLIS_TO_NANO
        this.maxFps = Math.round(1000f / frameIntervalMs).toFloat()
    }

    private fun initAction() {

        /**
         * 界面卡顿、动画、耗时等监控
         */
        test_fps.setOnClickListener {
            val intent = Intent(this, TestFpsActivity::class.java)
            startActivity(intent)
        }

        /**
         * 页面进入耗时
         */
        test_enter.setOnClickListener {
            val intent = Intent(this, TestEnterActivity::class.java)
            startActivity(intent)
        }

        /**
         * ANR 监控
         */

        test_anr.setOnClickListener {
            A()
        }

        test_anr_signal_handler.setOnClickListener {
            try {
                Thread.sleep(20000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        test_print_trace.setOnClickListener {
            SignalAnrTracer.printTrace()
        }

    }

    private fun initView() {
        IssueFilter.setCurrentFilter(IssueFilter.ISSUE_TRACE)
        val plugin: Plugin =
            Matrix.with().getPluginByClass(
                TracePlugin::class.java
            )
        if (!plugin.isPluginStarted) {
            MatrixLog.i(
                TAG,
                "plugin-trace start"
            )
            plugin.start()
        }
        decorator = FrameDecorator.getInstance(this)
        if (!canDrawOverlays()) {
            requestWindowPermission()
        } else {
            decorator?.show()
        }
        AppActiveMatrixDelegate.INSTANCE.addListener(this)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        MatrixLog.i(
            TAG,
            "requestCode:%s resultCode:%s",
            requestCode,
            resultCode
        )
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (canDrawOverlays()) {
                decorator!!.show()
            } else {
                Toast.makeText(
                    this,
                    "fail to request ACTION_MANAGE_OVERLAY_PERMISSION",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val plugin: Plugin =
            Matrix.with().getPluginByClass(
                TracePlugin::class.java
            )
        if (plugin.isPluginStarted) {
            MatrixLog.i(
                TAG,
                "plugin-trace stop"
            )
            plugin.stop()
        }
        if (canDrawOverlays()) {
            decorator!!.dismiss()
        }
        AppActiveMatrixDelegate.INSTANCE.removeListener(this)
    }

    private fun canDrawOverlays(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun requestWindowPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, PERMISSION_REQUEST_CODE)
    }


    fun testJankiess(view: View?) {
        A()
    }



    private fun A() {
        B()
        H()
        L()
        SystemClock.sleep(800)
    }

    private fun B() {
        C()
        G()
        SystemClock.sleep(200)
    }

    private fun C() {
        D()
        E()
        F()
        SystemClock.sleep(100)
    }

    private fun D() {
        SystemClock.sleep(20)
    }

    private fun E() {
        SystemClock.sleep(20)
    }

    private fun F() {
        SystemClock.sleep(20)
    }

    private fun G() {
        SystemClock.sleep(20)
    }

    private fun H() {
        SystemClock.sleep(20)
        I()
        J()
        K()
    }

    private fun I() {
        SystemClock.sleep(20)
    }

    private fun J() {
        SystemClock.sleep(6)
    }

    private fun K() {
        SystemClock.sleep(10)
    }

    private fun L() {
        SystemClock.sleep(10000)
    }

    private var isStop = false
    fun stopAppMethodBeat(view: View?) {
        val appMethodBeat =
            Matrix.with().getPluginByClass(
                TracePlugin::class.java
            ).appMethodBeat
        if (isStop) {
            Toast.makeText(this, "start AppMethodBeat", Toast.LENGTH_LONG).show()
            appMethodBeat.onStart()
        } else {
            Toast.makeText(this, "stop AppMethodBeat", Toast.LENGTH_LONG).show()
            appMethodBeat.onStop()
        }
        isStop = !isStop
    }

    fun evilMethod5(`is`: Boolean) {
        try {
            if (`is`) {
                Thread.sleep(800)
                throw AssertionArrayException()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Log.i("", "")
        }
        return
    }

    internal inner class AssertionArrayException : Exception()

    fun testInnerSleep() {
        SystemClock.sleep(6000)
    }

    private fun tryHeavyMethod() {
        Debug.getMemoryInfo(Debug.MemoryInfo())
    }

    override fun onForeground(isForeground: Boolean) {
        if (!canDrawOverlays()) {
            return
        }
        if (!isForeground) {
            decorator!!.dismiss()
        } else {
            decorator!!.show()
        }
    }

    companion object {
        private const val TAG = "Matrix.TestTraceMainActivity"
        private const val PERMISSION_REQUEST_CODE = 0x02
    }
}