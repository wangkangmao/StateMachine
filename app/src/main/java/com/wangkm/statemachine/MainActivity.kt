package com.wangkm.statemachine

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
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
        checkBox1.setOnCheckedChangeListener(MyOnCheckedChangeListener())
        checkBox2.setOnCheckedChangeListener(MyOnCheckedChangeListener())
        checkBox3.setOnCheckedChangeListener(MyOnCheckedChangeListener())
        checkBox4.setOnCheckedChangeListener(MyOnCheckedChangeListener())
        checkBox5.setOnCheckedChangeListener(MyOnCheckedChangeListener())
        checkBox6.setOnCheckedChangeListener(MyOnCheckedChangeListener())
        checkBox7.setOnCheckedChangeListener(MyOnCheckedChangeListener())
        checkBox8.setOnCheckedChangeListener(MyOnCheckedChangeListener())
    }


    inner class MyOnCheckedChangeListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            when (buttonView.id) {
                R.id.checkBox1 -> {
                    if(isChecked){
                        MODE = MODE or STATUS_1
                        textView.text = MODE.toString()
                    }else{
                        MODE = MODE xor STATUS_1
                        textView.text = MODE.toString()
                    }
                }
                R.id.checkBox2 -> {
                    if(isChecked){
                        MODE = MODE or STATUS_2
                        textView.text = MODE.toString()
                    }else{
                        MODE = MODE xor STATUS_2
                        textView.text = MODE.toString()
                    }
                }
                R.id.checkBox3 -> {
                    if(isChecked){
                        MODE = MODE or STATUS_3
                        textView.text = MODE.toString()
                    }else{
                        MODE = MODE xor STATUS_3
                        textView.text = MODE.toString()
                    }
                }
                R.id.checkBox4 -> {
                    if(isChecked){
                        MODE = MODE or STATUS_4
                        textView.text = MODE.toString()
                    }else{
                        MODE = MODE xor STATUS_4
                        textView.text = MODE.toString()
                    }
                }
                R.id.checkBox5 -> {
                }
                R.id.checkBox6 -> {
                }
                R.id.checkBox7 -> {
                }
                R.id.checkBox8 -> {
                }
            }
        }

    }
}