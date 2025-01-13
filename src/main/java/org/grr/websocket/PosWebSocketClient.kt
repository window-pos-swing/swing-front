package org.grr.websocket

import org.grr.`object`.OrderController
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.swing.JFrame
import javax.swing.JPanel


class PosWebSocketClient(
    private val orderController: OrderController,
    serverUri: URI,
    val parentFrame: JFrame,  // 부모 프레임
    val cardPanel: JPanel,    // 카드 패널
) : WebSocketClient(serverUri) {

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
            if(orderData.posOrderStatusType != ServerOrderStatus.REQUEST.name) return
            // 싱글톤 저장
            OrderListSingleTon.addOrder(orderData)
            // OrderController에 추가
            orderController.addOrder(orderData)
            println("현재 저장된 주문 수: ${OrderListSingleTon.getOrders().size}")

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