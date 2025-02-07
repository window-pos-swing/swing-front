package org.grr.command

import Command
import org.grr.`object`.OrderController
import kotlinx.coroutines.*
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.OrderFilter
import org.grr.model.OrderState
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController.tabbedPane
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import org.grr.screen.main.main_widget.order_states_ui.RejectedState
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

class AcceptOrderCommand(
    private val parent: JFrame,
    private val cardPanel: JPanel,
    private val order: ReceiveOrderModel,
    private val orderController: OrderController,
    private val takeType: String,
    private val cookTime: Int = 0,
) : Command {
    override fun execute() {
        GlobalScope.launch {
            val deliveryTime = if (takeType == "takeOut") 0 else order.deliveryTime
            val sendCookTime = if (cookTime == 0) order.cookTime else cookTime

            // ✅ 주문을 "수락됨" 상태로 변경
            if (!changeOrderStatusToAccepted(deliveryTime, sendCookTime)) {
                return@launch
            }

            delay(500)
            // ✅ UI 변경을 **메인(UI) 스레드에서 즉시 실행**
            SwingUtilities.invokeLater {
                println("[DEBUG] setTab(\"접수대기\") 실행됨!")
                OrderController.tabbedPane.setTab("접수대기")
            }

            delay(5000) // 🚀 5초 후 조리중으로 변경

            if (order.state is RejectedState || order.state is CompletedState || order.isPickupWait) {
                return@launch
            }

            changeOrderStatusToCooking(deliveryTime, sendCookTime)

        }
    }



    private suspend fun changeOrderStatusToAccepted(deliveryTime: Int, cookTime: Int): Boolean {
        val result = OrderAPI().orderStatusChangeToServer(
            ServerOrderStatus.ACCEPT,
            orderId = order.id,
            estimatedCookingTime = deliveryTime,
            estimatedArrivalTime = cookTime
        )

        if (!result.first) {
            SwingUtilities.invokeLater {
                JOptionPane.showMessageDialog(null, "주문접수 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            }
            return false
        }

        return true
    }


    private suspend fun changeOrderStatusToCooking(deliveryTime: Int, cookTime: Int) {
        val result = OrderAPI().orderStatusChangeToServer(
            ServerOrderStatus.COOKING,
            orderId = order.id,
            estimatedCookingTime = deliveryTime,
            estimatedArrivalTime = cookTime
        )

        if (!result.first) {
            SwingUtilities.invokeLater {
                JOptionPane.showMessageDialog(null, "조리 상태 전환 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
            }
        }
    }

}
