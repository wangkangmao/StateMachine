package com.wangkm.statemachine.trace

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.tencent.matrix.util.MatrixLog
import com.wangkm.statemachine.App

class TestStartUpActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        sendBroadcast(new Intent(this, StartUpBroadcastReceiver.class));
        startService(Intent(this, StartUpService::class.java))
        //        callByContentResolver();
    }

    fun callByContentResolver() {
        val contentResolver = App.getContext().contentResolver
        val uri =
            Uri.parse("content://sample.tencent.matrix.trace.StartUpContentProvider/user")
        val bundle = contentResolver.call(uri, "method", null, null)
        val returnCall = bundle!!.getString("returnCall")
        MatrixLog.i(TAG, "[callByContentResolver] returnCall:%s", returnCall)
    }

    companion object {
        private const val TAG = "TestStartUpActivity"
    }
}