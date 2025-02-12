package org.grr.command

import Command
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import javax.swing.JOptionPane

class CookedCommand(
    private val order: ReceiveOrderModel,
) : Command {
    override fun execute() {
        val result = OrderAPI().orderStatusChangeToServer(
            ServerOrderStatus.COOKED,
            orderId = order.id,
        )
        // 결과 처리
        if (!result.first) {
            JOptionPane.showMessageDialog(null, "조리완료 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            return
        }
        if (order.orderReceiveType == OrderReceiveType.DELIVERY){
            order.isPickupWait = true
        }else{
            order.isPickupCompleted = true
        }
        OrderController.tabbedPane.processingSubTabs.initializePanels()
        println("[주문] #${order.orderNumber} 조리완료 상태 변경")
    }
}