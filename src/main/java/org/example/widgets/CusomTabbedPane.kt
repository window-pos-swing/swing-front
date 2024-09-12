package org.example

import org.example.model.Order
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.plaf.basic.BasicTabbedPaneUI

class CustomTabbedPane : JTabbedPane() {
    private val allOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
    }
    private val pendingOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
    }
    private val processingOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE
    }
    private val completedOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE  // 접수완료 탭의 배경색
    }
    private val rejectedOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE  // 주문거절 탭의 배경색
    }

    init {
        // 기존 탭 설정
        addTab("전체보기 0", JScrollPane(allOrdersPanel).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        })

        addTab("접수대기 0", JScrollPane(pendingOrdersPanel).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        })

        addTab("접수진행 0", JScrollPane(processingOrdersPanel).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        })

        // 새로 추가된 탭
        addTab("접수완료 0", JScrollPane(completedOrdersPanel).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        })

        addTab("주문거절 0", JScrollPane(rejectedOrdersPanel).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        })

        // 탭 스타일 및 배경 유지
        setUI(object : BasicTabbedPaneUI() {
            override fun installDefaults() {
                super.installDefaults()
                tabInsets = java.awt.Insets(15, 60, 15, 60)
                tabAreaInsets = java.awt.Insets(0, 0, 0, 0)
                selectedTabPadInsets = java.awt.Insets(0, 0, 0, 0)
                contentBorderInsets = java.awt.Insets(0, 0, 0, 0)
            }

            override fun paintTabBackground(
                g: java.awt.Graphics?,
                tabPlacement: Int,
                tabIndex: Int,
                x: Int,
                y: Int,
                w: Int,
                h: Int,
                isSelected: Boolean
            ) {
                if (isSelected) {
                    g?.color = Color.WHITE
                    g?.fillRect(x, y, w, h)
                } else {
                    g?.color = Color.LIGHT_GRAY
                    g?.fillRect(x, y, w, h)
                }
            }

            override fun paintTabBorder(
                g: java.awt.Graphics?,
                tabPlacement: Int,
                tabIndex: Int,
                x: Int,
                y: Int,
                w: Int,
                h: Int,
                isSelected: Boolean
            ) {
                g?.color = Color.GRAY
                if (isSelected) {
                    g?.drawLine(x, y, x, y + h)
                    g?.drawLine(x, y, x + w, y)
                    g?.drawLine(x + w, y, x + w, y + h)
                } else {
                    g?.drawRect(x, y, w, h)
                }
            }

            override fun paintFocusIndicator(
                g: java.awt.Graphics?,
                tabPlacement: Int,
                rects: Array<out java.awt.Rectangle>?,
                tabIndex: Int,
                iconRect: java.awt.Rectangle?,
                textRect: java.awt.Rectangle?,
                isSelected: Boolean
            ) {
                // 포커스 표시 비활성화
            }

            override fun paintContentBorder(
                g: java.awt.Graphics?,
                tabPlacement: Int,
                selectedIndex: Int
            ) {
                g?.color = Color.GRAY
                val tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight)
                val contentX = 0
                val contentY = tabAreaHeight
                val contentWidth = this@CustomTabbedPane.width - 1
                val contentHeight = this@CustomTabbedPane.height - tabAreaHeight - 1
                g?.drawRect(contentX, contentY, contentWidth, contentHeight)
                val totalTabWidth = rects.take(tabCount).sumOf { it.width }
                if (totalTabWidth < contentWidth) {
                    g?.drawLine(totalTabWidth, tabAreaHeight, contentWidth, tabAreaHeight)
                }
            }
        })

        // 선택된 탭의 색상 업데이트
        addChangeListener { e: ChangeEvent? ->
            for (i in 0 until tabCount) {
                if (i == selectedIndex) {
                    setForegroundAt(i, Color(255, 102, 0))
                } else {
                    setForegroundAt(i, Color.BLACK)
                }
            }
        }

        setForegroundAt(selectedIndex, Color(255, 102, 0))
        setPreferredSize(Dimension(1200, 70))
    }


    // 탭 타이틀 업데이트 메서드
    private fun updateTabTitle(tabIndex: Int, tabName: String, count: Int) {
        setTitleAt(tabIndex, "$tabName $count")
    }

    // 주문 처리
    fun addOrderToPending(orderFrame: JPanel) {
        pendingOrdersPanel.add(orderFrame)
        pendingOrdersPanel.revalidate()
        pendingOrdersPanel.repaint()
        updateTabTitle(1, "접수대기", pendingOrdersPanel.componentCount)
    }

    fun addOrderToProcessing(orderFrame: JPanel) {
        processingOrdersPanel.add(orderFrame)
        processingOrdersPanel.revalidate()
        processingOrdersPanel.repaint()
        updateTabTitle(2, "접수진행", processingOrdersPanel.componentCount)
    }

    fun addOrderToCompleted(orderFrame: JPanel) {
        completedOrdersPanel.add(orderFrame)
        completedOrdersPanel.revalidate()
        completedOrdersPanel.repaint()
        updateTabTitle(3, "접수완료", completedOrdersPanel.componentCount)
    }

    fun addOrderToRejected(orderFrame: JPanel) {
        rejectedOrdersPanel.add(orderFrame)
        rejectedOrdersPanel.revalidate()
        rejectedOrdersPanel.repaint()
        updateTabTitle(4, "주문거절", rejectedOrdersPanel.componentCount)
    }

    fun addOrderToAllOrders(orderFrame: JPanel) {
        allOrdersPanel.add(orderFrame)
        allOrdersPanel.revalidate()
        allOrdersPanel.repaint()
        updateTabTitle(0, "전체보기", allOrdersPanel.componentCount)
    }

    // 주문 프레임 삭제 및 업데이트
    fun removeOrderFromPending(order: Order) {
        val frameToRemove = pendingOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToRemove?.let {
            pendingOrdersPanel.remove(it)
            pendingOrdersPanel.revalidate()
            pendingOrdersPanel.repaint()
            updateTabTitle(1, "접수대기", pendingOrdersPanel.componentCount)
        }
    }

    fun removeOrderFromProcessing(order: Order) {
        val frameToRemove = processingOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToRemove?.let {
            processingOrdersPanel.remove(it)
            processingOrdersPanel.revalidate()
            processingOrdersPanel.repaint()
            updateTabTitle(2, "접수진행", processingOrdersPanel.componentCount)
        }
    }

    // 주문 프레임 삭제 및 업데이트
    fun updateOrderInAllOrders(order: Order) {
        val frameToUpdate = allOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToUpdate?.let {
            val updatedUI = order.getUI()
            it.removeAll()
            it.add(updatedUI)
            it.revalidate()
            it.repaint()
        }
    }

    private fun createOrderFrame(order: Order): JPanel {
        return order.getUI().apply {
            maximumSize = Dimension(size.width, 200)
            preferredSize = Dimension(size.width, 200)
            putClientProperty("orderNumber", order.orderNumber)
        }
    }
}
