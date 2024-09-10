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
        background = Color.WHITE  // 전체보기 탭의 배경색을 유지
    }
    private val pendingOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE  // 접수대기 탭의 배경색을 유지
    }
    private val processingOrdersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color.WHITE  // 접수진행 탭의 배경색을 유지
    }

    init {
        // 탭 설정 및 UI 유지
        addTab("전체보기", JScrollPane(allOrdersPanel).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        })

        addTab("접수대기", JScrollPane(pendingOrdersPanel).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        })

        addTab("접수진행", JScrollPane(processingOrdersPanel).apply {
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

    fun getProcessingOrderComponents(): Array<Component> {
        return processingOrdersPanel.components
    }

    //============ 주문 처리 ============
    // 주문 프레임 추가
    fun addOrderToPending(orderFrame: JPanel) {
        pendingOrdersPanel.add(orderFrame)
        pendingOrdersPanel.revalidate()
        pendingOrdersPanel.repaint()
    }

    fun addOrderToProcessing(orderFrame: JPanel) {
        processingOrdersPanel.add(orderFrame)
        processingOrdersPanel.revalidate()
        processingOrdersPanel.repaint()
    }

    fun addOrderToAllOrders(orderFrame: JPanel) {
        allOrdersPanel.add(orderFrame)
        allOrdersPanel.revalidate()
        allOrdersPanel.repaint()
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
        }
    }

    // 주문 프레임 삭제 및 업데이트
    fun updateOrderInAllOrders(order: Order) {
        val frameToUpdate = allOrdersPanel.components
            .filterIsInstance<JPanel>()
            .find { it.getClientProperty("orderNumber") == order.orderNumber }

        frameToUpdate?.let {
            // 주문 프레임을 삭제하지 않고 UI만 업데이트
            val updatedUI = order.getUI()
            it.removeAll()  // 기존 UI 요소를 모두 제거
            it.add(updatedUI)  // 새로운 UI 요소를 추가
            it.revalidate()  // UI 갱신
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