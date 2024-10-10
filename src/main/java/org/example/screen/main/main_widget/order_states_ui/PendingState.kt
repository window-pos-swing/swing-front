package org.example.view.states

import OrderController
import OrderRejectCancelDialog
import org.example.CustomTabbedPane
import org.example.MyFont
import org.example.command.RejectOrderCommand
import org.example.command.RejectedReasonType
import org.example.model.Order
import org.example.model.OrderState
import org.example.screen.main.main_widget.dialog.EstimatedTimeDialog
import org.example.style.MyColor
import org.example.view.components.BaseOrderPanel
import org.example.widgets.FillRoundedButton
import org.example.widgets.IconRoundBorder
import org.example.widgets.OverlayManager
import javax.swing.*
import java.awt.*

class PendingState(private val parentFrame: JFrame? = null, private val cardPanel: JPanel? = null) : OrderState {
    override fun handle(order: Order) {
        println("PendingState")
    }

    override fun getUI(order: Order): JPanel {
        return BaseOrderPanel(order).apply {
            val buttonPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                background = Color.WHITE

                val overlayManager = cardPanel?.let { OverlayManager(parentFrame ?: SwingUtilities.getWindowAncestor(this@apply) as? JFrame ?: JFrame(), it) }

                add(createPrintButton())
                add(Box.createRigidArea(Dimension(15, 0)))
                if (overlayManager != null) {
                    add(createRejectButton(order, overlayManager))
                    add(Box.createRigidArea(Dimension(15, 0)))
                    add(createAcceptButton(order, overlayManager))
                }
            }

            val headerPanel = components.find { it is JPanel && it.layout is BoxLayout } as JPanel?
            headerPanel?.add(Box.createHorizontalGlue())
            headerPanel?.add(buttonPanel)
        }
    }

    private fun createPrintButton(): JButton {
        return FillRoundedButton(
            text = "",
            borderColor = Color(230, 230, 230),
            backgroundColor = Color(230, 230, 230),
            textColor = Color.BLACK,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            iconPath = "/print_icon_main.png",
            buttonSize = Dimension(50, 50),
            iconWidth = 45,
            iconHeight = 45
        ).apply {
            addActionListener {
                println("프린터 버튼 클릭")
            }
        }
    }

    private fun createRejectButton(order: Order, overlayManager: OverlayManager): JButton {
        return FillRoundedButton(
            text = "주문거절",
            borderColor = MyColor.GREY300,
            backgroundColor = MyColor.GREY300,
            textColor = Color.BLACK,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(130, 50),
            customFont = MyFont.Bold(20f)
        ).apply {
            addActionListener {
                overlayManager.addOverlayPanel()
                val dialog = OrderRejectCancelDialog(
                    parentFrame ?: JFrame(),
                    cardPanel = cardPanel!!,
                    "주문 거절 사유 선택",
                    "주문 거절 사유를 선택해 주세요.",
                    "주문 거절",
                    onReject = { rejectReason ->
                        val rejectOrderCommand = RejectOrderCommand(order, rejectReason, RejectedReasonType.STORE_REJECT)
                        rejectOrderCommand.execute()
                    }
                )
                dialog.addWindowListener(object : java.awt.event.WindowAdapter() {
                    override fun windowClosed(e: java.awt.event.WindowEvent?) {
                        overlayManager.removeOverlayPanel()
                        dialog.dispose()
                    }
                })
                dialog.isVisible = true
            }
        }
    }

    private fun createAcceptButton(order: Order, overlayManager: OverlayManager): JButton {
        return FillRoundedButton(
            text = "접수하기",
            borderColor = MyColor.DARK_NAVY,
            backgroundColor = MyColor.DARK_NAVY,
            textColor = Color.WHITE,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(10, 20, 10, 20),
            buttonSize = Dimension(130, 50),
            customFont = MyFont.Bold(20f)
        ).apply {
            addActionListener {
                overlayManager.addOverlayPanel()
                val dialog = EstimatedTimeDialog(
                    parent = parentFrame ?: JFrame(),
                    cardPanel = cardPanel!!,
                    title = "예상 시간 선택",
                    order = order,
                    orderController = OrderController(CustomTabbedPane(parentFrame ?: JFrame()))
                )
                dialog.addWindowListener(object : java.awt.event.WindowAdapter() {
                    override fun windowClosed(e: java.awt.event.WindowEvent?) {
                        dialog.dispose()
                        overlayManager.removeOverlayPanel()

                    }
                })
                dialog.isVisible = true
            }
        }
    }
}