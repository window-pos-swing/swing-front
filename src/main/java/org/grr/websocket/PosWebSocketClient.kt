package org.grr.websocket

import okhttp3.internal.notifyAll
import org.grr.enum.BusinessStatus
import org.grr.enum.OrderReceiveType
import org.grr.`object`.OrderController
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.model.SettingModel
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.swing.JFrame
import javax.swing.JPanel


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

        try {
            val orderData = ReceiveOrderModel.fromJson(
                json = message!!,
                parentFrame = parentFrame,
                cardPanel = cardPanel
            )
            if (orderData.posOrderStatusType == ServerOrderStatus.USER_CANCEL.name) {
                println("유저가 주문을 취소하였습니다.")
                OrderController.moveOrderToReject(orderData)
                return
            }

            if (orderData.posOrderStatusType != ServerOrderStatus.REQUEST.name && orderData.posOrderStatusType != ServerOrderStatus.USER_CANCEL.name) return

            // 키 결정
            val key = when (orderData.orderReceiveType) {
                OrderReceiveType.DELIVERY.name -> "pendingDeliveryOrders"
                OrderReceiveType.TAKEOUT.name -> "pendingTakeOutOrders"
                else -> "pendingOrders"
            }

            // 싱글톤에 추가
            synchronized(OrderListSingleTon.orders) {
                OrderListSingleTon.orders[key]?.add(0, orderData) // 리스트 맨 앞에 추가
                OrderListSingleTon.counts[key] = (OrderListSingleTon.counts[key] ?: 0) + 1
                OrderListSingleTon.counts["allOrders"] = (OrderListSingleTon.counts["allOrders"] ?: 0) + 1
                OrderListSingleTon.orders["allOrders"]?.add(0, orderData)
                OrderListSingleTon.counts["pendingOrders"] = (OrderListSingleTon.counts["pendingOrders"] ?: 0) + 1
                OrderListSingleTon.orders["pendingOrders"]?.add(0, orderData)
//                OrderListSingleTon.counts["rejectUserOrders"] = (OrderListSingleTon.counts["rejectUserOrders"] ?: 0) + 1
//                OrderListSingleTon.orders["rejectUserOrders"]?.add(0, orderData)
                // OrderController에 추가
            }
            OrderController.addNewOrder(orderData)
        } catch (e: Exception) {
            e.printStackTrace()
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