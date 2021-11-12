package com.wangkm.statemachine

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author: created by wangkm
 * @time: 2021/11/10 11:11
 * @desc：
 * @email: 1240413544@qq.com
 */

object NetWorkUtils{


    fun networkTest(){
        var lost = String()
        var delay = String()
        var p: Process? = null
        try {
            p = Runtime.getRuntime().exec("ping 203.107.52.140")
        } catch (e: IOException) {
            Log.d("networkTest","error:" + e.message)
            e.printStackTrace()
        }
        val buf = BufferedReader(
            InputStreamReader(
                p!!.inputStream
            )
        )
        var str = String()
        try {
            while (buf.readLine().also({ str = it }) != null) {
                println(str)
                if (str.contains("packet loss")) {
                    val i = str.indexOf("received")
                    val j = str.indexOf("%")
                    Log.d("networkTest","丢包率:" + str.substring(i + 10, j + 1))
                    // System.out.println("丢包率:"+str.substring(j-3, j+1));
                    lost = str.substring(i + 10, j + 1)
                }
                if (str.contains("avg")) {
                    val i = str.indexOf("/", 20)
                    val j = str.indexOf(".", i)
                    Log.d("networkTest","延迟:" + str.substring(i + 1, j))
                    delay = str.substring(i + 1, j)
                    delay = delay + "ms"
                }
            }
        } catch (e: Exception) {
        }
        println(lost)

    }
}