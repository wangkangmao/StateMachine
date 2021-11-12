package com.wangkm.statemachine

import android.app.Application
import android.content.Context
import android.content.Intent
import com.tencent.matrix.Matrix
import com.tencent.matrix.batterycanary.BatteryMonitorPlugin
import com.tencent.matrix.iocanary.IOCanaryPlugin
import com.tencent.matrix.iocanary.config.IOConfig
import com.tencent.matrix.resource.ResourcePlugin
import com.tencent.matrix.resource.config.ResourceConfig
import com.tencent.matrix.resource.config.ResourceConfig.DumpMode
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.config.TraceConfig
import com.tencent.matrix.util.MatrixLog
import com.tencent.sqlitelint.SQLiteLint
import com.tencent.sqlitelint.SQLiteLintPlugin
import com.tencent.sqlitelint.config.SQLiteLintConfig
import com.wangkm.statemachine.battery.BatteryCanaryInitHelper
import com.wangkm.statemachine.resource.ManualDumpActivity
import java.io.File


/**
 * @author: created by wangkm
 * @time: 2021/11/11 12:39
 * @desc：
 * @email: 1240413544@qq.com
 */

class App:Application() {

    val TAG  = "MyApp"

    private val sContext: Context? = null


    override fun onCreate() {
        super.onCreate()

        // 监控模块开关
        val dynamicConfig = DynamicConfigImplDemo()
        // 构建matrix模块
        val builder = Matrix.Builder(this)
        // 构建性能追踪器
        val tracePlugin = configureTracePlugin(dynamicConfig)
        builder.plugin(tracePlugin)

        // 初始化Matrix
        Matrix.init(builder.build())
        // 启动插件
        tracePlugin!!.start()

        MatrixLog.i(TAG, "Matrix configurations done.")


    }



    private fun configureTracePlugin(dynamicConfig: DynamicConfigImplDemo): TracePlugin? {
        val fpsEnable: Boolean = dynamicConfig.isFPSEnable
        val traceEnable: Boolean = dynamicConfig.isTraceEnable
        val signalAnrTraceEnable: Boolean = dynamicConfig.isSignalAnrTraceEnable
        val traceFileDir =
            File(applicationContext.filesDir, "matrix_trace")
        if (!traceFileDir.exists()) {
            if (traceFileDir.mkdirs()) {
                MatrixLog.i(TAG, "failed to create traceFileDir")
            }
        }
        val anrTraceFile = File(
            traceFileDir,
            "anr_trace"
        ) // path : /data/user/0/sample.tencent.matrix/files/matrix_trace/anr_trace
        val printTraceFile = File(
            traceFileDir,
            "print_trace"
        ) // path : /data/user/0/sample.tencent.matrix/files/matrix_trace/print_trace
        val traceConfig = TraceConfig.Builder()
            .dynamicConfig(dynamicConfig)
            .enableFPS(fpsEnable)
            .enableEvilMethodTrace(traceEnable)
            .enableAnrTrace(traceEnable)
            .enableStartup(traceEnable)
            .enableIdleHandlerTrace(traceEnable) // Introduced in Matrix 2.0
            .enableMainThreadPriorityTrace(true) // Introduced in Matrix 2.0
            .enableSignalAnrTrace(signalAnrTraceEnable) // Introduced in Matrix 2.0
            .anrTracePath(anrTraceFile.absolutePath)
            .printTracePath(printTraceFile.absolutePath)
            .splashActivities("sample.tencent.matrix.SplashActivity;")
            .isDebug(true)
            .isDevEnv(false)
            .build()

        //Another way to use SignalAnrTracer separately
        //useSignalAnrTraceAlone(anrTraceFile.getAbsolutePath(), printTraceFile.getAbsolutePath());
        return TracePlugin(traceConfig)
    }


    private fun configureResourcePlugin(dynamicConfig: DynamicConfigImplDemo): ResourcePlugin? {
        val intent = Intent()
        val mode = DumpMode.MANUAL_DUMP
        MatrixLog.i(TAG, "Dump Activity Leak Mode=%s", mode)
        intent.setClassName(this.packageName, "com.tencent.mm.ui.matrix.ManualDumpActivity")
        val resourceConfig = ResourceConfig.Builder()
            .dynamicConfig(dynamicConfig)
            .setAutoDumpHprofMode(mode)
            .setManualDumpTargetActivity(ManualDumpActivity::class.java.getName())
            .build()
        ResourcePlugin.activityLeakFixer(this)
        return ResourcePlugin(resourceConfig)
    }

    private fun configureIOCanaryPlugin(dynamicConfig: DynamicConfigImplDemo): IOCanaryPlugin? {
        return IOCanaryPlugin(
            IOConfig.Builder()
                .dynamicConfig(dynamicConfig)
                .build()
        )
    }

    private fun configureSQLiteLintPlugin(): SQLiteLintPlugin? {
        val sqlLiteConfig: SQLiteLintConfig

        /*
         * HOOK模式下，SQLiteLint会自己去获取所有已执行的sql语句及其耗时(by hooking sqlite3_profile)
         * @see 而另一个模式：SQLiteLint.SqlExecutionCallbackMode.CUSTOM_NOTIFY , 则需要调用 {@link SQLiteLint#notifySqlExecution(String, String, int)}来通知
         * SQLiteLint 需要分析的、已执行的sql语句及其耗时
         * @see TestSQLiteLintActivity#doTest()
         */
        // sqlLiteConfig = new SQLiteLintConfig(SQLiteLint.SqlExecutionCallbackMode.HOOK);
        sqlLiteConfig = SQLiteLintConfig(SQLiteLint.SqlExecutionCallbackMode.CUSTOM_NOTIFY)
        return SQLiteLintPlugin(sqlLiteConfig)
    }

    private fun configureBatteryCanary(): BatteryMonitorPlugin? {
        // Configuration of battery plugin is really complicated.
        // See it in BatteryCanaryInitHelper.
        return BatteryCanaryInitHelper.createMonitor()
    }

    fun getContext(): Context? {
        return sContext
    }

}