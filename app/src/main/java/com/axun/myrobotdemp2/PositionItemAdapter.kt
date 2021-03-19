package com.axun.myrobotdemp2

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 *@packageName com.axun.myrobotdemp2
 *@author kzcai
 *@date 3/10/21
 */
class PositionItemAdapter:BaseQuickAdapter<RobotPosition,BaseViewHolder>(R.layout.activity_robot_navigation) {
    override fun convert(helper: BaseViewHolder, item: RobotPosition?) {
        item?.apply {
            val info = "${helper.adapterPosition+1}(x:${x} y:${y} z:${z})"
            helper.setText(R.id.tv_position,info)
        }
    }
}