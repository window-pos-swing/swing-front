package org.grr.screen.main.main_widget.order_states_ui

import org.grr.`object`.OrderController
import OrderRejectCancelDialog
import org.grr.command.AcceptOrderCommand
import org.grr.command.RejectOrderCommand
import org.grr.command.RejectedReasonType
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.model.OrderState
import org.grr.model.ReceiveOrderModel
import org.grr.model.SettingModel
import org.grr.screen.main.main_widget.dialog.TimeSelectDialog.CookTimeDialog
import org.grr.screen.main.main_widget.dialog.TimeSelectDialog.DeliveryTimeDialog
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import org.grr.`object`.OverlayManager
import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import javax.swing.*

class PendingState(
    private val parentFrame: JFrame,
    private val cardPanel: JPanel,
) : OrderState {
    private lateinit var overlayManager: OverlayManager

    override fun handle(order: ReceiveOrderModel) {
        println("PendingState")
    }

    override fun getUI(order: ReceiveOrderModel): JPanel {
        return BaseOrderPanel(order).apply {
            val buttonPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                background = Color.WHITE

                // 싱글톤 방식으로 OverlayManager 가져오기
                if (!::overlayManager.isInitialized) {
                    overlayManager = OverlayManager // 전역적으로 초기화된 OverlayManager 사용
                }
                add(createPrintButton())
                add(Box.createRigidArea(Dimension(15, 0)))

                add(createRejectButton(order, overlayManager))
                add(Box.createRigidArea(Dimension(15, 0)))
                add(createAcceptButton(order, overlayManager))

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

    private fun createRejectButton(order: ReceiveOrderModel, overlayManager: OverlayManager): JButton {
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
                    parentFrame,
                    cardPanel = cardPanel,
                    "주문 거절 사유 선택",
                    "주문 거절 사유를 선택해 주세요.",
                    "주문 거절",
                    onReject = { rejectReason ->
                        val rejectOrderCommand = RejectOrderCommand(order, rejectReason, RejectedReasonType.STORE_REJECT, PosOrderStatus.WAITING)
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

    private fun createAcceptButton(order: ReceiveOrderModel, overlayManager: OverlayManager): JButton {
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
                //delivery & takeOut
                //어떤 다이얼로그를 띄워줘야할까 판별하는 부분
                println("order.orderReceiveType : ${order.orderReceiveType}")
                println("[PendingState] order.parentFrame : ${order.parentFrame}")
                println("[PendingState] order.cardPanel : ${order.cardPanel}")
                statusOfDialog(order.orderReceiveType , overlayManager, order)
            }
        }
    }

    //어떤 다이얼로그를 띄워줘야할까 판별하는 부분
    private fun statusOfDialog(takeType: String, overlayManager: OverlayManager, order: ReceiveOrderModel) {
        val allOff = !SettingModel.cookingTimeControl && !SettingModel.deliveryTimeControl
        val allOn = SettingModel.cookingTimeControl && SettingModel.deliveryTimeControl
        val deliveryDialogType = when {
            SettingModel.cookingTimeControl && !SettingModel.deliveryTimeControl-> "CookONDeliveryOFF"
            SettingModel.deliveryTimeControl && !SettingModel.cookingTimeControl -> "DeliveryONCookOFF"
            allOff-> "AllOFF"
            allOn -> "AllON"
            else -> ""
        }

        val takeOutDialogType = when {
            SettingModel.cookingTimeControl -> "CookON"
            !SettingModel.cookingTimeControl -> "CookOFF"
            else -> ""
        }

        when (takeType) {
            // ===============[포장 주문 처리] ======================
            OrderReceiveType.TAKEOUT.name -> {
                when(takeOutDialogType) {
                    "CookOFF" -> {
                        //요리시간 다이얼로그만 띄워줌
                        println("[$takeType] CookOFF ...")
                        overlayManager.addOverlayPanel()
                        val dialog = CookTimeDialog(
                            parent = parentFrame ,
                            cardPanel = cardPanel,
                            order = order,
                            orderController = OrderController,
                            overlayManager = overlayManager,
                            takeType = takeType
                        )
                        dialog.addWindowListener(object : java.awt.event.WindowAdapter() {
                            override fun windowClosed(e: java.awt.event.WindowEvent?) {
                                dialog.dispose()
                            }
                        })
                        dialog.isVisible = true
                    }
                    "CookON" -> {
                        println("[$takeType] CookON ...")
                        AcceptOrderCommand(
                            parent = parentFrame,
                            cardPanel = cardPanel,
                            order = order,
                            orderController = OrderController,
                            takeType = takeType,
                            cookTime = SettingModel.cookingTime
                        ).execute() // 주문 상태 변경
                    }
                }


            }
            // ===============[배달 주문 처리] ======================
            OrderReceiveType.DELIVERY.name -> {
                when (deliveryDialogType) {
                    "CookONDeliveryOFF" -> {
                        println("[$takeType] CookONDeliveryOFF ...")
                        overlayManager.addOverlayPanel()
                        val dialog = DeliveryTimeDialog(
                            parent = parentFrame ,
                            cardPanel = cardPanel,
                            order = order,
                            orderController = OrderController,
                            SettingModel.cookingTime ,
                            overlayManager = overlayManager
                        )
                        dialog.addWindowListener(object : java.awt.event.WindowAdapter() {
                            override fun windowClosed(e: java.awt.event.WindowEvent?) {
                                dialog.dispose()
                                overlayManager.removeOverlayPanel()
                            }
                        })
                        dialog.isVisible = true

                    }

                    "DeliveryONCookOFF" -> {
                        println("[$takeType] DeliveryONCookOFF ...")
                        overlayManager.addOverlayPanel()
                        val dialog = CookTimeDialog(
                            parent = parentFrame ,
                            cardPanel = cardPanel,
                            order = order,
                            orderController = OrderController,
                            overlayManager = overlayManager,
                            takeType = takeType
                        )
                        dialog.addWindowListener(object : java.awt.event.WindowAdapter() {
                            override fun windowClosed(e: java.awt.event.WindowEvent?) {
                                dialog.dispose()
                            }
                        })
                        dialog.isVisible = true
                    }

                    "AllON" -> {
                        println("[$takeType] AllON ...")
                        order.deliveryTime = SettingModel.deliveryTime
                        AcceptOrderCommand(
                            parent = parentFrame,
                            cardPanel = cardPanel,
                            order = order,
                            orderController = OrderController,
                            takeType = takeType,
                            cookTime = SettingModel.cookingTime
                        ).execute() // 주문 상태 변경

                    }

                    "AllOFF" -> {
                        println("[$takeType] AllOFF ...")
                        overlayManager.addOverlayPanel()
                        val dialog = CookTimeDialog(
                            parent = parentFrame ,
                            cardPanel = cardPanel,
                            order = order,
                            orderController = OrderController,
                            overlayManager = overlayManager,
                            takeType = takeType
                        )
                        dialog.addWindowListener(object : java.awt.event.WindowAdapter() {
                            override fun windowClosed(e: java.awt.event.WindowEvent?) {
                                dialog.dispose()
                                //overlayManager.removeOverlayPanel() // DeliveryTimeDialog에서 overlayManager를 관리하므로 여기서 제거하지 않음
                            }
                        })
                        dialog.isVisible = true
                    }

                }
            }
        }
    }
}