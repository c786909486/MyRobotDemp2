package com.axun.myrobotdemp2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.alibaba.fastjson.JSON
import com.csjbot.coshandler.listener.OnNaviListener
import kotlinx.android.synthetic.main.activity_robot_navigation.*

/**
 *@packageName com.axun.myrobotdemp2
 *@author kzcai
 *@date 3/10/21
 */
class RobotNavigationActivity:AppCompatActivity() {

    private var adapter = PositionItemAdapter()
    private var positions:MutableList<RobotPosition> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_navigation)
        initView()
        val positionsStr = SPUtils.getStringSp(this,"positions")
        if (!positionsStr.isNullOrEmpty()){
            positions = JSON.parseArray(positionsStr,RobotPosition::class.java)
            adapter.setNewData(positions)
        }
    }

    private fun initView(){

        RobotSdk.instance.loadMap()
        rv_positions.layoutManager = LinearLayoutManager(this)
        rv_positions.adapter = adapter
        adapter.setNewData(positions)

        btn_left.setOnClickListener {
            RobotSdk.instance.moveLeft()
        }

        btn_forward.setOnClickListener {
            RobotSdk.instance.moveForward()
        }

        btn_right.setOnClickListener {
            RobotSdk.instance.moveRight()
        }

        btn_back.setOnClickListener {
            RobotSdk.instance.moveBack()
        }

        btn_go_home.setOnClickListener {
            RobotSdk.instance.goHome(object :OnNaviListener{
                override fun moveResult(p0: String?) {

                }

                override fun messageSendResult(p0: String?) {

                }

                override fun cancelResult(p0: String?) {

                }

                override fun goHome() {
                    runOnUiThread {
                        showToast("θΏεεη΅ζ‘©")
                    }
                }

            })
        }

//        btn_save_map.setOnClickListener {
//            RobotSdk.instance.saveMap()
//        }
//
//        btn_load_map.setOnClickListener {
//            RobotSdk.instance.loadMap()
//        }

        btn_get_position.setOnClickListener {

            RobotSdk.instance.getCurrentPosition{
                runOnUiThread {
                    showToast(JSON.toJSONString(it))
                    if (!positions.contains(it)){
                        positions.add(it)
                        adapter.setNewData(positions)
                        SPUtils.putStringSp(this,"positions",JSON.toJSONString(positions))
                    }else{
                        showToast("ε½εεζ ε·²θ?°ε½")
                    }
                }
            }
        }

        btn_stop_navi.setOnClickListener {
            RobotSdk.instance.cancelNavi(object :OnNaviListener{
                override fun moveResult(p0: String?) {

                }

                override fun messageSendResult(p0: String?) {

                }

                override fun cancelResult(p0: String?) {
                    runOnUiThread {
                        showToast("εζ­’ε·‘θͺοΌ${p0}")
                    }
                }

                override fun goHome() {

                }

            })
        }

        adapter.setOnItemClickListener { _, view, position ->
            val item = adapter.getItem(position)
            RobotSdk.instance.navi(item!!,object :OnNaviListener{
                override fun moveResult(p0: String?) {
                    runOnUiThread { showToast("η§»ε¨ε?ζ${p0}") }
                }

                override fun messageSendResult(p0: String?) {

                }

                override fun cancelResult(p0: String?) {

                }

                override fun goHome() {
                    showToast("θΏεεη΅ζ‘©")
                }

            })
        }

        adapter.setOnItemLongClickListener { _, view, position ->
            adapter.remove(position)
            SPUtils.putStringSp(this,"positions",JSON.toJSONString(positions))
            showToast("ε·²ε ι€θ―₯εζ ηΉ")
            true
        }


        btn_loop.setOnClickListener {
            if (positions.isNotEmpty()&&positions.size>=3){
                RobotSdk.instance.continuousNavi(positions)
            }else{
                showToast("θ―·ιι3δΈͺηΊ§δ»₯δΈεζ ηΉ")
            }
        }
    }
}