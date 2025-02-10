package org.grr.command

import Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.grr.api.OrderAPI
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController
import org.grr.`object`.OrderController.tabbedPane
import org.grr.`object`.OrderListSingleTon
import java.awt.CardLayout
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

        if (rejectType != RejectedReasonType.USER_CANCEL) {
            var result = OrderAPI().orderStatusChangeToServer(
                ServerOrderStatus.STORE_CANCEL,
                reason = rejectReason,
                orderId = order.id,
            )
            // 결과 처리
            if (!result.first) {
                JOptionPane.showMessageDialog(null, "주문접수 실패: ${result.second}", "오류", JOptionPane.ERROR_MESSAGE)
                return
            } else {
                GlobalScope.launch {
                    delay(1000)
                    if (rejectPanel == PosOrderStatus.WAITING) {
                        SwingUtilities.invokeLater {
                            println("[DEBUG] setTab(\"접수대기\") 실행됨!")
                            if(OrderListSingleTon.currentPendingSubTabName == "전체보기"){
                                if(OrderListSingleTon.currentTab == "전체보기"){
//                                    OrderController.tabbedPane.setTab("접수대기")
//                                    OrderController.tabbedPane.setTab("주문거절")
                                    OrderController.tabbedPane.setTab("전체보기")
                                }else{
//                                    OrderController.tabbedPane.setTab("주문거절")
                                    OrderController.tabbedPane.setTab("접수대기")
                                }

                            } else if(OrderListSingleTon.currentPendingSubTabName == "배달") {
                                val cardLayout = OrderController.tabbedPane.cardPanel?.layout as CardLayout
                                OrderController.tabbedPane.pendingSubTabs.selectButton(tabbedPane.pendingSubTabs.deliveryButton)
                                tabbedPane.pendingSubTabs.showTab("배달")
                                OrderController.tabbedPane.cardPanel!!.add(tabbedPane.pendingSubTabs, "접수대기 하위탭")
                                cardLayout.show(OrderController.tabbedPane.cardPanel, "접수대기 하위탭")
                            } else {
                                val cardLayout = OrderController.tabbedPane.cardPanel?.layout as CardLayout
                                OrderController.tabbedPane.pendingSubTabs.selectButton(tabbedPane.pendingSubTabs.deliveryButton)
                                tabbedPane.pendingSubTabs.showTab("포장")
                                OrderController.tabbedPane.cardPanel!!.add(tabbedPane.pendingSubTabs, "접수대기 하위탭")
                                cardLayout.show(OrderController.tabbedPane.cardPanel, "접수대기 하위탭")
                            }
                        }
                    } else {
                        SwingUtilities.invokeLater {
                            println("[DEBUG] setTab(\"접수처리중\") 실행됨!")
                            if(OrderListSingleTon.currentProcessingSubTabName == "전체보기"){
                                if(OrderListSingleTon.currentTab == "전체보기"){
//                                    OrderController.tabbedPane.setTab("접수처리중")
//                                    OrderController.tabbedPane.setTab("주문거절")
                                    OrderController.tabbedPane.setTab("전체보기")
                                }else{
//                                    OrderController.tabbedPane.setTab("주문거절")
                                    OrderController.tabbedPane.setTab("접수처리중")
                                }

                            } else if(OrderListSingleTon.currentProcessingSubTabName == "배달") {
                                val cardLayout = OrderController.tabbedPane.cardPanel?.layout as CardLayout
                                OrderController.tabbedPane.processingSubTabs.selectButton(tabbedPane.processingSubTabs.deliveryButton)
                                tabbedPane.processingSubTabs.showTab("배달")
                                OrderController.tabbedPane.cardPanel!!.add(tabbedPane.processingSubTabs, "접수대기 하위탭")
                                cardLayout.show(OrderController.tabbedPane.cardPanel, "접수대기 하위탭")
                            } else {
                                val cardLayout = OrderController.tabbedPane.cardPanel?.layout as CardLayout
                                OrderController.tabbedPane.processingSubTabs.selectButton(tabbedPane.processingSubTabs.takeoutButton)
                                tabbedPane.processingSubTabs.showTab("포장")
                                OrderController.tabbedPane.cardPanel!!.add(tabbedPane.processingSubTabs, "접수대기 하위탭")
                                cardLayout.show(OrderController.tabbedPane.cardPanel, "접수대기 하위탭")
                            }

                        }
                    }
                }
            }
        }else{
            //USER CANCEL
            GlobalScope.launch {
                delay(1000)
                SwingUtilities.invokeLater {
//                    tabbedPane.setTab("접수대기")
                    tabbedPane.updateTabAppearance("주문거절")
                    val cardLayout = OrderController.tabbedPane.cardPanel?.layout as CardLayout
                    OrderController.tabbedPane.rejectedSubTabs.showTab("고객취소")
                    OrderController.tabbedPane.rejectedSubTabs.selectButton(OrderController.tabbedPane.rejectedSubTabs.customerCancelButton)
                    OrderController.tabbedPane.cardPanel!!.add(OrderController.tabbedPane.rejectedSubTabs, "주문거절 하위탭")
                    cardLayout.show(OrderController.tabbedPane.cardPanel, "주문거절 하위탭")
                    OrderController.tabbedPane.rejectedSubTabs.initializePanels()
                    OrderController.tabbedPane.updateTabTitle(
                        4, "주문거절", (
                                OrderListSingleTon.counts["rejectStoreOrders"]
                                    ?: 0) + (OrderListSingleTon.counts["rejectUserOrders"]
                            ?: 0) + (OrderListSingleTon.counts["rejectRefundOrders"] ?: 0)
                    )
                }
            }

        }


        val rejectDate =
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")) // 년도 / 월 / 일
        // 주문 상태를 RejectedState로 변경 (거절 사유와 원래 상태 포함)

        println("[DEBUG] rejectPanel : $rejectPanel")

//        order.changeState(RejectedState(rejectReason, rejectDate, rejectType, rejectPanel))
        println("[주문] #${order.orderNumber} 거절상태로 변경 with reason: [$rejectType] - $rejectReason at $rejectDate")

    }

}