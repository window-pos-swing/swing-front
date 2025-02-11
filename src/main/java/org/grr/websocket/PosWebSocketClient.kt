package org.grr.websocket

import ReceiptPrinter
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

            // ✅ 프린트 출력
            var receiptPrinter = ReceiptPrinter()
            receiptPrinter.ForCustomersOrderSheet(orderData)
            receiptPrinter.ForBurialOrderSheet(orderData)

            // ✅ UI 업데이트
            SwingUtilities.invokeLater {
                OrderController.addNewOrder()
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