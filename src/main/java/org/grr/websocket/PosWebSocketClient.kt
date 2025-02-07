package org.grr.websocket

import org.grr.command.RejectOrderCommand
import org.grr.command.RejectedReasonType
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.`object`.OrderController
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities


class PosWebSocketClient(
    serverUri: URI,
    val parentFrame: JFrame,  // 부모 프레임
    val cardPanel: JPanel,    // 카드 패널
) : WebSocketClient(serverUri) {

    lateinit var tabbedPane: CustomTabbedPane

    init {
        tabbedPane = CustomTabbedPane(parentFrame)
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("WebSocket 연결 성공!")
    }

    override fun onMessage(message: String?) {
        println("WebSocket 메시지 수신: $message")
        val orderData = ReceiveOrderModel.fromJson(
            json = message!!,
            parentFrame = parentFrame,
            cardPanel = cardPanel
        )
        try {
            if (orderData.posOrderStatusType == ServerOrderStatus.USER_CANCEL.name) {
                println("유저가 주문을 취소하였습니다.")
                val rejectOrderCommand = RejectOrderCommand(orderData, "고객 거절", RejectedReasonType.USER_CANCEL, PosOrderStatus.USER_CANCEL)
                rejectOrderCommand.execute()
                return
            }

            if (orderData.posOrderStatusType != ServerOrderStatus.REQUEST.name ) return

            // 키 결정
            val key = when (orderData.orderReceiveType) {
                OrderReceiveType.DELIVERY.name -> "pendingDeliveryOrders"
                OrderReceiveType.TAKEOUT.name -> "pendingTakeOutOrders"
                else -> "pendingOrders"
            }

            // 싱글톤에 추가
            synchronized(OrderListSingleTon.orders) {

                val allOrders = OrderListSingleTon.orders["allOrders"]
                val pendingOrders = OrderListSingleTon.orders["pendingOrders"]

                OrderListSingleTon.orders[key]?.add(0, orderData) // 리스트 맨 앞에 추가
                println("[웹소켓 BEFORE]")
                println("OrderListSingleTon.counts pendingDeliveryOrders ${OrderListSingleTon.counts["pendingDeliveryOrders"]}")

                // ✅ counts 값을 UI 업데이트 전에 먼저 갱신
                OrderListSingleTon.counts["allOrders"] = (OrderListSingleTon.counts["allOrders"] ?: 0) + 1
                OrderListSingleTon.counts["pendingOrders"] = (OrderListSingleTon.counts["pendingOrders"] ?: 0) + 1
                OrderListSingleTon.counts[key] = (OrderListSingleTon.counts[key] ?: 0) + 1

                println("[웹소켓 AFTER]")
                println("OrderListSingleTon.counts pendingDeliveryOrders ${OrderListSingleTon.counts["pendingDeliveryOrders"]}")

                allOrders?.add(0, orderData)
                pendingOrders?.add(0, orderData)

                // ✅ UI에 주문 추가 후 데이터 일관성을 유지
                SwingUtilities.invokeLater {
                    OrderController.addNewOrder(orderData)
                }

                // ✅ 마지막 주문 삭제 로직
                if (allOrders != null && allOrders.size > OrderListSingleTon.PAGE_SIZE) {
                    println("[DEBUG] allOrders before removal: ${allOrders.map { it.orderNumber }}")
                    OrderController.removeAllOrder(allOrders.last())
                    allOrders.removeLast()
                }

                if (pendingOrders != null && pendingOrders.size > OrderListSingleTon.PAGE_SIZE) {
                    println("[DEBUG] pendingOrders before removal: ${pendingOrders.map { it.orderNumber }}")
                    OrderController.tabbedPane.removeOrderFromPending(pendingOrders.last() , true)
                    pendingOrders.removeLast()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {

        }
    }


    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("WebSocket 연결 종료: $reason")
    }

    override fun onError(ex: Exception?) {
        println("WebSocket 오류 발생: ${ex?.message}")
        ex?.printStackTrace()
    }
}