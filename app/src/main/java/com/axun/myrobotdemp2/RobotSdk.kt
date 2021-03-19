package com.axun.myrobotdemp2

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import com.csjbot.coshandler.core.CsjRobot
import com.csjbot.coshandler.listener.*
import com.iflytek.thridparty.s


/**
 *@packageName com.axun.library.robot
 *@author kzcai
 *@date 3/10/21
 */
class RobotSdk private constructor() {

    companion object {
        val instance: RobotSdk by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RobotSdk()
        }
    }

    private var context: Context? = null

    //API key
    private var apiKey = ""

    //User secret
    private var userSecret = ""

    //设置apiKey信息
    fun init(context: Context, apiKey: String, userSecret: String) {
        this.context = context
        this.apiKey = apiKey
        this.userSecret = userSecret
        continuNaviListener = ContinuNaviListener(handler)
    }

    /**
     * @Description 联网授权
     * @Param
     * @return
     **/
    fun authentication(listener: OnAuthenticationListener) {
        if (apiKey.isEmpty()||userSecret.isEmpty()){
            throw Exception("apiKey or userSecret is empty")
        }
        CsjRobot.authentication(context, apiKey, userSecret, listener)
    }

    fun startRobot(
        ip: String = "127.0.0.1",
        port: Int = 60002,
        type: CsjRobot.RobotType = CsjRobot.RobotType.ALICE_NEW,
        listener: OnConnectListener
    ) {
        // 是否启用语音模块(默认开启)  #####在init之前调用
        CsjRobot.enableAsr(true);
// 是否启用人脸识别模块
        CsjRobot.enableFace(true);
// 是否启用导航模块
        CsjRobot.enableSlam(true);
// 设置通信的地址，默认是192.168.99.101， 60002
        CsjRobot.setIpAndrPort(ip, port);
/*
* 设置机器人类型(在init之前调用)
* 如果设置了ALICE，则为第八代机器人的协议
*/
        CsjRobot.setRobotType(type);
/*
* 初始化SDK
*/
        CsjRobot.getInstance().init(context)

        CsjRobot.getInstance().registerConnectListener(listener)

    }

    /**
     * @Description 保存地图
     * @Param
     * @return
     **/
    fun saveMap(mapName: String = "map.map"){
        CsjRobot.getInstance().action.saveMap(mapName)
    }

    /**
     * @Description 加载地图
     * @Param
     * @return
     **/
    fun loadMap(){
        CsjRobot.getInstance().action.loadMap()
    }

    //地图恢复状态
    fun queryMapState(listener: OnMapStateListener){
        CsjRobot.getInstance().action.getMapState(listener)
    }

    /**
     * @Description 查询机器人电池状态
     * @Param
     * @return
     **/
    fun queryRobotBattery(listener: OnRobotStateListener){
        CsjRobot.getInstance().setOnRobotStateBatteryListener(listener)
    }


    fun getCurrentPosition(listener: (position: RobotPosition) -> Unit){
        CsjRobot.getInstance().action.getPosition{
            try {
                val jsonObject = JSON.parseObject(it)
                val rotation: String = jsonObject.getString("rotation")
                val x: String = jsonObject.getString("x")
                val y: String = jsonObject.getString("y")
                val z: String = jsonObject.getString("z")
                val robotPose = RobotPosition()
                robotPose.x = x.toFloat()
                robotPose.y = y.toFloat()
                robotPose.z = z.toFloat()
                robotPose.rotation = rotation.toFloat()
                listener.invoke(robotPose)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * @Description 导航
     * @Param
     * @return
     **/
    fun navi(position: RobotPosition, listener: OnNaviListener? = null){
        val json = JSON.toJSONString(position)
        isInNavi = true
        CsjRobot.getInstance().action.navi(json, object : OnNaviListener {
            override fun moveResult(p0: String?) {
                listener?.moveResult(p0)
                isInNavi = false
            }

            override fun messageSendResult(p0: String?) {
                listener?.messageSendResult(p0)
            }

            override fun cancelResult(p0: String?) {
                listener?.cancelResult(p0)
                isInNavi = false
            }

            override fun goHome() {
                listener?.goHome()
            }

        })
    }

    fun cancelNavi(listener: OnNaviListener){
        CsjRobot.getInstance().action.cancelNavi(listener)
        isInNavi = false
    }

    /**
     * @Description 连续导航
     * @Param
     * @return
     **/
    private var positions:List<RobotPosition> = ArrayList()
    private var currentPositionIndex = 0
    private var isInNavi = false


    fun continuousNavi(positions: List<RobotPosition>){
        isInNavi = true
        if (positions.isNullOrEmpty()){
            context?.showToast("传入的坐标点列表为空")
            return
        }
        this.positions = positions

        val position  = positions[currentPositionIndex]
        navi(position, continuNaviListener)
    }


    private var inOrder = true

    private var handler =object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                1->{
                    val position = msg.obj as RobotPosition
                    navi(position, continuNaviListener)

                }
            }
        }
    }

    private lateinit var continuNaviListener : ContinuNaviListener




    fun moveForward(){
        CsjRobot.getInstance().action.moveForward()
    }

    fun moveLeft(){
        CsjRobot.getInstance().action.moveLeft()
    }

    fun moveRight(){
        CsjRobot.getInstance().action.moveRight()
    }

    fun moveBack(){
        CsjRobot.getInstance().action.moveBack()
    }


    inner class ContinuNaviListener(private val handler: Handler):OnNaviListener{
        override fun moveResult(p0: String?) {
            if (!isInNavi){
                return
            }
            if (currentPositionIndex<positions.size ){
//                inOrder = true
                currentPositionIndex++

            }else{
//                inOrder = false
//                currentPositionIndex--
//                if (currentPositionIndex<=0){
//                    inOrder = true
//                }
                currentPositionIndex = 0
            }

            val position  = positions[currentPositionIndex]
            val message = Message()
            message.what = 1
            message.obj= position
            handler.sendMessage(message)


            val msg = "已到达，下个点${currentPositionIndex+1}${JSON.toJSONString(position)}"
            Log.d("RobotSdkLog",msg)
//            if (BuildConfig.DEBUG){
//                context?.showToast(msg)
//            }
        }

        override fun messageSendResult(p0: String?) {
            Log.d("RobotSdkLog","")
        }

        override fun cancelResult(p0: String?) {

        }

        override fun goHome() {

        }

    }

}

