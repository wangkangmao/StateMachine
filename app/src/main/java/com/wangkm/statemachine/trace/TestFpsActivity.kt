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
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.widget.ListView
import android.widget.MediaController
import com.tencent.matrix.Matrix
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.constants.Constants
import com.tencent.matrix.trace.core.UIThreadMonitor
import com.tencent.matrix.trace.listeners.IDoFrameListener
import com.tencent.matrix.util.MatrixLog
import com.wangkm.statemachine.R
import com.wangkm.statemachine.issue.IssueFilter
import kotlinx.android.synthetic.main.test_fps_layout.*
import java.io.IOException
import java.util.concurrent.Executor


/**
 * Created by caichongyang on 2017/11/14.
 */
class TestFpsActivity : Activity() {
    private var mListView: ListView? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var frameIntervalMs = 0f
    private var maxFps = 0f

    companion object {
        private const val TAG = "YxTestFpsActivity"
        private val sHandlerThread = HandlerThread("test")

        init {
            sHandlerThread.start()
        }
    }

    private fun initData() {
        this.frameIntervalMs = 1f * UIThreadMonitor.getMonitor()
            .frameIntervalNanos / Constants.TIME_MILLIS_TO_NANO
        this.maxFps = Math.round(1000f / frameIntervalMs).toFloat()
    }

    private val count = 0
    private var time = System.currentTimeMillis()
    private val mDoFrameListener: IDoFrameListener =
        object : IDoFrameListener(object : Executor {
            var handler =
                Handler(sHandlerThread.looper)

            override fun execute(command: Runnable) {
                handler.post(command)
            } }) {
            override fun doFrameAsync(
                focusedActivity: String,
                startNs: Long,
                endNs: Long,
                dropFrame: Int,
                isVsyncFrame: Boolean,
                intendedFrameTimeNs: Long,
                inputCostNs: Long,
                animationCostNs: Long,
                traversalCostNs: Long) {
                super.doFrameAsync(focusedActivity,
                    startNs,
                    endNs,
                    dropFrame,
                    isVsyncFrame,
                    intendedFrameTimeNs,
                    inputCostNs,
                    animationCostNs,
                    traversalCostNs)
                MatrixLog.i(TAG,
                    "[doFrameAsync]" + " costMs=" + (endNs - intendedFrameTimeNs) / Constants.TIME_MILLIS_TO_NANO + " dropFrame=" + dropFrame +
                            " isVsyncFrame=" + isVsyncFrame +
                            " offsetVsync=" + (startNs - intendedFrameTimeNs) / Constants.TIME_MILLIS_TO_NANO +
                            " maxFps=    " + maxFps +
                            "          [%s:%s:%s]",
                    inputCostNs / Constants.TIME_MILLIS_TO_NANO,
                    animationCostNs / Constants.TIME_MILLIS_TO_NANO,
                    traversalCostNs / Constants.TIME_MILLIS_TO_NANO)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_fps_layout)
        IssueFilter.setCurrentFilter(IssueFilter.ISSUE_TRACE)
        Matrix.with().getPluginByClass(TracePlugin::class.java)
            .frameTracer.onStartTrace()
        Matrix.with().getPluginByClass(TracePlugin::class.java)
            .frameTracer.addListener(mDoFrameListener)
        time = System.currentTimeMillis()
        initListView()
        initVideoView()
//        getVideoFps()
        initData()
    }

    private fun getVideoFps() {
        val extractor = MediaExtractor()
        var frameRate = 24
        val videoPath1 = "sdcard/1.mp4"
        val videoPath2 = "sdcard/2.mp4"
        val videoPath3 = "sdcard/3.mp4"
        val videoPath4 = "sdcard/4.mp4"
        try {
            extractor.setDataSource(videoPath4)
            val numTracks =  extractor.trackCount
            for (i in 0 until numTracks) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime!!.startsWith("video/")) {
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE)
                        Log.d("myframeRate","frameRate=   $frameRate")
                    }
                }
            }

        }catch (e:IOException){
            e.printStackTrace()
        }finally {
            extractor.release()
        }

    }

    private fun initVideoView() {
        val controller = MediaController(this) //实例化控制器
        val videoPath1 = "sdcard/1.mp4"
        videoView.setVideoPath(videoPath1)

        /**
         * 将控制器和播放器进行互相关联
         */
        controller.setMediaPlayer(videoView)
        videoView.setMediaController(controller)
    }

    private fun initListView() {
//        mListView = findViewById<View>(R.id.list_view) as ListView
//        val data = arrayOfNulls<String>(200)
//        for (i in 0..199) {
//            data[i] = "MatrixTrace:$i"
//        }
//        mListView!!.setOnTouchListener { view, motionEvent ->
//    //            MatrixLog.i(TAG, "onTouch=$motionEvent")
//            SystemClock.sleep(80)
//            false
//        }
//        mListView!!.adapter = object :
//            ArrayAdapter<Any?>(this, android.R.layout.simple_list_item_1, data) {
//            var random = Random()
//            override fun getView(
//                position: Int,
//                convertView: View?,
//                parent: ViewGroup
//            ): View {
//    //                mainHandler.post(new Runnable() {
//    //                    @Override
//    //                    public void run() {
//    //                        int rand = random.nextInt(10);
//    //                        SystemClock.sleep(rand * 4);
//    //                    }
//    //                });
//                return super.getView(position, convertView, parent)
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MatrixLog.i(
            TAG,
            "[onDestroy] count:" + count + " time:" + (System.currentTimeMillis() - time) + ""
        )
        Matrix.with().getPluginByClass(TracePlugin::class.java)
            .frameTracer.removeListener(mDoFrameListener)
        Matrix.with().getPluginByClass(TracePlugin::class.java)
            .frameTracer.onCloseTrace()
    }
}