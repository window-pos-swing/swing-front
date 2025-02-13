package org.grr.command

import Command
import kotlinx.coroutines.*
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.OrderFilter
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.FormManager
import org.grr.`object`.OrderController
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class CompletedOrderCommand(
    private val order: ReceiveOrderModel,
) : Command {
    override fun execute() {
        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)
        val status = if (order.orderReceiveType == OrderReceiveType.DELIVERY) ServerOrderStatus.DELIVERY_COMPLETE else ServerOrderStatus.PICKUP_COMPLETE
        val result = OrderAPI().orderStatusChangeToServer(
            status,
            orderId = order.id,
        )
        // 결과 처리
        if (!result.first) {
            JOptionPane.showMessageDialog(null, "주문접수 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            return
        }else{
            GlobalScope.launch {
                delay(1000)
                // ✅ UI 변경을 **메인(UI) 스레드에서 즉시 실행**
                SwingUtilities.invokeLater {
                    println("[DEBUG] setTab(\"접수처리중\") 실행됨!")
                    if(OrderListSingleTon.currentTab == "전체보기"){
//                        OrderController.tabbedPane.setTab("접수완료")
//                        OrderController.tabbedPane.setTab("접수처리중")
                        OrderController.tabbedPane.setTab("전체보기")
                    }else{
//                        OrderController.tabbedPane.setTab("접수완료")
                        OrderController.tabbedPane.setTab("접수처리중")
                    }

                }
            }
            println("[주문] #${order.orderNumber} 주문완료 상태로 변경")
        }
    }

}