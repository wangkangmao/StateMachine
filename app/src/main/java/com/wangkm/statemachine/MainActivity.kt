package com.wangkm.statemachine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wangkm.statemachine.io.TestIOActivity
import com.wangkm.statemachine.issue.IssuesMap
import com.wangkm.statemachine.resource.TestLeakActivity
import com.wangkm.statemachine.trace.TestTraceMainActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val STATUS_1 = 0x0001
    private val STATUS_2 = 0x0002
    private val STATUS_3 = 0x0004
    private val STATUS_4 = 0x0008
    private val STATUS_5 = 0x0010
    private val STATUS_6 = 0x0020
    private val STATUS_7 = 0x0040
    private val STATUS_8 = 0x0080


    private var MODE = 0
    private val MODE_A = STATUS_1 or STATUS_2 or STATUS_3
    private val MODE_B = STATUS_1 or STATUS_4 or STATUS_5 or STATUS_6
    private val MODE_C = STATUS_1 or STATUS_7 or STATUS_8


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAction()
    }

    private fun initAction() {
        test_trace.setOnClickListener {
            val intent = Intent(this@MainActivity, TestTraceMainActivity::class.java)
            startActivity(intent)
        }
        test_leak.setOnClickListener {
            val intent = Intent(this@MainActivity, TestLeakActivity::class.java)
            startActivity(intent)
        }
        test_io.setOnClickListener {
            val intent = Intent(this@MainActivity, TestIOActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        IssuesMap.clear()
    }



}