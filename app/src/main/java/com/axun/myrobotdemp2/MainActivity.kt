package com.axun.myrobotdemp2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.axun.myrobotdemp2.PermissionRequest.permissions
import com.csjbot.coshandler.listener.OnAuthenticationListener
import com.csjbot.coshandler.listener.OnConnectListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object{
        val API_KEY = "dce59084-b53a-481d-8958-89141286c7f9"
        val USER_SECRET = "2301BD47A9F61886CB574BC4B88BC2B5"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        RobotSdk.instance.init(this, API_KEY, USER_SECRET)
        initView()
    }

    private fun requestPermission() {

        if (PermissionRequest.lacksPermissions(this, permissions)) {
            PermissionRequest.requestAll(this, object : PermissionRequest.OnPermissionCallback {
                override fun onPermissionSuccess() {

                }

                override fun onPermissionFailed() {

                }
            })
        } else {

        }
    }

    private var hasPermission = false
    private fun initView(){
        ed_address.setText("127.0.0.1")
        ed_port.setText("60002")
        request()
        btn_init.setOnClickListener {
           request()
        }

        btn_go.setOnClickListener {
            startActivity(Intent(this@MainActivity, RobotNavigationActivity::class.java))
        }

//        ed_address.setText("192.168.3.15")


        btn_link.setOnClickListener {
            if (!hasPermission){
                showToast("请先获取授权")
                return@setOnClickListener
            }
            initSdk()
        }
    }

    private fun request(){
        RobotSdk.instance.authentication(object : OnAuthenticationListener {
            override fun success() {
                showToast("授权成功")
                runOnUiThread {
                    tv_sdk_status.text = "授权成功"
                    hasPermission = true
                    runOnUiThread {
                        initSdk()
                    }
                }
            }

            override fun error() {
                showToast("授权失败")
                tv_sdk_status.text = "授权失败"
            }

        })
    }

    private fun initSdk(){
        RobotSdk.instance.startRobot(ip = ed_address.text.toString(),port = ed_port.text.toString().toInt(),listener = object : OnConnectListener {
            override fun success() {
                runOnUiThread {
                    showToast("连接成功")
                    tv_sdk_status.text = tv_sdk_status.text.toString() + "连接成功"
                    btn_go.visibility = View.VISIBLE
                    startActivity(Intent(this@MainActivity, RobotNavigationActivity::class.java))
                }
            }

            override fun faild() {
                runOnUiThread {
                    showToast("连接失败")
                    tv_sdk_status.text = tv_sdk_status.text.toString() + "连接失败"
                }
            }

            override fun timeout() {
               runOnUiThread {
                   showToast("连接超时")
                   tv_sdk_status.text = tv_sdk_status.text.toString() + "连接超时"
               }
            }

            override fun disconnect() {
                runOnUiThread {
                    showToast("断开连接")
                    tv_sdk_status.text = tv_sdk_status.text.toString() + "断开连接"
                }
            }

        })
    }
}