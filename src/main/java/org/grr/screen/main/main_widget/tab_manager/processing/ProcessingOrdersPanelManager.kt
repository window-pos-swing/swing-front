package org.grr.screen.main.main_widget.tab_manager.processing

import org.grr.enum.OrderReceiveType
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController
import org.grr.`object`.OrderListSingleTon
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

class ProcessingOrdersPanelManager(
    var customTabbedPane: CustomTabbedPane
) {
    // 패널 초기화
    private val processingOrdersPanel: JPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = java.awt.Color.WHITE
    }

    // 패널 반환 메서드
    fun getPanel(): JPanel = processingOrdersPanel

    fun addOrderToProcessing(orderFrame: JPanel, typeOrderFrame : JPanel ,order: ReceiveOrderModel) {
        orderFrame.maximumSize = Dimension(Int.MAX_VALUE, orderFrame.preferredSize.height)
        processingOrdersPanel.add(orderFrame)
        processingOrdersPanel.add(Box.createRigidArea(Dimension(0, 30)))
        processingOrdersPanel.revalidate()
        processingOrdersPanel.repaint()
        OrderListSingleTon.orders["processingOrders"]?.add(order)
        customTabbedPane.updateTabTitle(2, "접수처리중", OrderListSingleTon.counts["processingOrders"] ?: 0)
        customTabbedPane.updateTabTitle(1, "접수대기", OrderListSingleTon.counts["pendingOrders"] ?: 0)
        if(order.orderReceiveType == OrderReceiveType.DELIVERY.name){
            OrderListSingleTon.orders["processingDeliveryOrders"]?.add(order)
        }else if(order.orderReceiveType == OrderReceiveType.TAKEOUT.name){
            OrderListSingleTon.orders["processingTakeOutOrders"]?.add(order)
        }
        OrderController.tabbedPane.processingSubTabs.initializePanels()
    }

    fun removeOrderFromProcessing(order: ReceiveOrderModel) {
        val frameToRemove = processingOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToRemove?.let {
            processingOrdersPanel.remove(it)
            processingOrdersPanel.revalidate()
            processingOrdersPanel.repaint()
            customTabbedPane.updateTabTitle(2, "접수처리중", OrderListSingleTon.counts["processingOrders"] ?: 0)
        }

        if(order.orderReceiveType == OrderReceiveType.DELIVERY.name){
            val deliveryOrdersPanelRemove = customTabbedPane.processingSubTabs.deliveryOrdersPanel.components
                .filterIsInstance<JPanel>()
                .find { it.getClientProperty("orderNumber") == order.orderNumber }

            deliveryOrdersPanelRemove?.let{
                OrderListSingleTon.counts["processingDeliveryOrders"] = (OrderListSingleTon.counts["processingDeliveryOrders"] ?: 0) - 1
                customTabbedPane.processingSubTabs.deliveryOrdersPanel.remove(it)
                customTabbedPane.processingSubTabs.deliveryOrdersPanel.revalidate()
                customTabbedPane.processingSubTabs.deliveryOrdersPanel.repaint()
            }

        }else if(order.orderReceiveType == OrderReceiveType.TAKEOUT.name){
            val takeOutOrdersPanelRemove = customTabbedPane.processingSubTabs.takeoutOrdersPanel.components
                .filterIsInstance<JPanel>()
                .find { it.getClientProperty("orderNumber") == order.orderNumber }

            takeOutOrdersPanelRemove?.let{
                OrderListSingleTon.counts["processingTakeOutOrders"] = (OrderListSingleTon.counts["processingTakeOutOrders"] ?: 0) - 1
                customTabbedPane.processingSubTabs.takeoutOrdersPanel.remove(it)
                customTabbedPane.processingSubTabs.takeoutOrdersPanel.revalidate()
                customTabbedPane.processingSubTabs.takeoutOrdersPanel.repaint()
            }
        }
        customTabbedPane.processingSubTabs.updateCounts()
    }

}