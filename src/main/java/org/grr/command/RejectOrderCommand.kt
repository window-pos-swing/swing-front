package org.grr.command

import Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.order_states_ui.RejectedState
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

enum class RejectedReasonType {
    USER_CANCEL,
    STORE_REJECT,
    REFUND
}

class RejectOrderCommand(
    private val order: ReceiveOrderModel,
    private val rejectReason: String,
    private val rejectType: RejectedReasonType, // 원래 상태 (PendingState 또는 ProcessingState)
    private val rejectPanel: PosOrderStatus
) : Command {
    override fun execute() {

        if(rejectType != RejectedReasonType.USER_CANCEL){
            var result = OrderAPI().orderStatusChangeToServer(
                ServerOrderStatus.STORE_CANCEL,
                reason = rejectReason,
                orderId = order.id,
            )
            // 결과 처리
            if (!result.first) {
                JOptionPane.showMessageDialog(null, "주문접수 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
                return
            }
        }


        val rejectDate =
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")) // HH:mm뺌
        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)


        println("[DEBUG] rejectPanel : $rejectPanel")
        GlobalScope.launch {
            delay(500)
            if (rejectPanel == PosOrderStatus.WAITING) {
                SwingUtilities.invokeLater {
                    println("[DEBUG] setTab(\"접수대기\") 실행됨!")
                    OrderController.tabbedPane.setTab("접수대기")
                }
            } else {
                SwingUtilities.invokeLater {
                    println("[DEBUG] setTab(\"접수처리중\") 실행됨!")
                    OrderController.tabbedPane.setTab("접수처리중")
                }
            }
        }

//        order.changeState(RejectedState(rejectReason, rejectDate, rejectType, rejectPanel))
        println("[주문] #${order.orderNumber} 거절상태로 변경 with reason: [$rejectType] - $rejectReason at $rejectDate")

    }

}