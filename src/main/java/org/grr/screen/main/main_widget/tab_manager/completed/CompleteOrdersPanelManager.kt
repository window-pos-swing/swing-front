package org.grr.screen.main.main_widget.tab_manager.completed

import org.grr.enum.OrderReceiveType
import org.grr.enum.ServerOrderStatus
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController
import org.grr.`object`.OrderController.tabbedPane
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

    fun addOrderToCompleted(orderFrame: JPanel,order: ReceiveOrderModel) {
        completedOrdersPanel.add(orderFrame)
        completedOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        completedOrdersPanel.revalidate()
        completedOrdersPanel.repaint()

        OrderListSingleTon.orders["completedOrders"]?.add(0,order)
        OrderListSingleTon.counts["completedOrders"] = ( OrderListSingleTon.counts["completedOrders"] ?:0) + 1
        if(order.orderReceiveType == OrderReceiveType.DELIVERY.name){
            OrderListSingleTon.orders["completedDeliveryOrders"]?.add(0,order)
            OrderListSingleTon.counts["completedDeliveryOrders"] = ( OrderListSingleTon.counts["completedDeliveryOrders"] ?:0) + 1
        }else if(order.orderReceiveType == OrderReceiveType.TAKEOUT.name){
            OrderListSingleTon.orders["completedTakeOutOrders"]?.add(0,order)
            OrderListSingleTon.counts["completedTakeOutOrders"] = ( OrderListSingleTon.counts["completedTakeOutOrders"] ?:0) + 1
        }
        customTabbedPane.completedSubTabs.initializePanels()
        customTabbedPane.completedSubTabs.updateCounts()

    }

}