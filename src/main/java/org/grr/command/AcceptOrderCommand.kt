package org.grr.command

import Command
import org.grr.`object`.OrderController
import kotlinx.coroutines.*
import org.grr.api.OrderAPI
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
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
        CoroutineScope(Dispatchers.IO).launch {
            // 배달포장 상태에 따라 시간 설정
            val deliveryTime = if (takeType == "takeOut") 0 else order.deliveryTime
            val sendCookTime = if (cookTime == 0) order.cookTime else cookTime

            // 첫 번째 서버 호출
            val result = OrderAPI().orderStatusChangeToServer(
                ServerOrderStatus.ACCEPT,
                orderId = order.orderId,
                estimatedCookingTime = deliveryTime,
                estimatedArrivalTime = sendCookTime
            )

            if (!result.first) {
                SwingUtilities.invokeLater {
                    JOptionPane.showMessageDialog(null, "주문접수 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
                }
                return@launch
            }

            // 상태 변경 및 컨트롤러 업데이트
//            SwingUtilities.invokeLater {
                order.changeState(ProcessingState(sendCookTime + deliveryTime, parent, cardPanel))
                orderController.onOrderStateChanged(order)

                println("===========================================================================")
                println("[AcceptOrderCommand] #${order.orderNumber} 접수처리중으로 상태 변경 with total time: ${sendCookTime + deliveryTime} minutes")
                println("[CookTime] $sendCookTime")
                println("[DeliveryTime] $deliveryTime")
                println("===========================================================================")
//            }

//            //TODO(5초 뒤 조리중으로 변경)
//            delay(5000)

            // 두 번째 서버 호출
//            val result2 = OrderAPI().orderStatusChangeToServer(
//                ServerOrderStatus.COOKING,
//                orderId = order.orderId,
//                estimatedCookingTime = deliveryTime,
//                estimatedArrivalTime = sendCookTime
//            )
//
//            if (!result2.first) {
//                SwingUtilities.invokeLater {
//                    JOptionPane.showMessageDialog(null, "주문접수 실패: ${result2.second}", "오류", JOptionPane.ERROR_MESSAGE)
//                }
//                return@launch
//            }

        }
    }
}
