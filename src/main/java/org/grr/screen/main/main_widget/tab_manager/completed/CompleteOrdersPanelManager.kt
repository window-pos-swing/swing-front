package org.grr.screen.main.main_widget.tab_manager.completed

import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

class CompleteOrdersPanelManager(
    var customTabbedPane: CustomTabbedPane
) {

    // 패널 초기화
    private val completedOrdersPanel: JPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = java.awt.Color.WHITE
    }

    // 패널 반환 메서드
    fun getPanel(): JPanel = completedOrdersPanel

    fun initCompletedOrders(orders: List<ReceiveOrderModel>){
        orders.forEach { order ->
            val orderFrame = customTabbedPane.createOrderFrame(order, forProcessing = false)
            orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
            completedOrdersPanel.add(orderFrame)
            completedOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        }
    }

    fun addOrderToCompleted(orderFrame: JPanel) {
        completedOrdersPanel.add(orderFrame)
        completedOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        completedOrdersPanel.revalidate()
        completedOrdersPanel.repaint()
        customTabbedPane.updateTabTitle(3, "접수완료", OrderListSingleTon.counts["completedOrders"] ?: 0)
        customTabbedPane.updateTabTitle(2, "접수처리중", OrderListSingleTon.counts["processingOrders"] ?: 0)
    }

}