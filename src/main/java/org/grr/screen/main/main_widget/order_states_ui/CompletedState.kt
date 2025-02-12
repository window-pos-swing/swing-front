package org.grr.screen.main.main_widget.order_states_ui

import ReceiptPrinter
import org.grr.model.OrderState
import org.grr.model.ReceiveOrderModel
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.SwingConstants

class CompletedState : OrderState {
    override fun handle(order: ReceiveOrderModel) {
        // Completed 상태 처리 로직
    }

    override fun getUI(order: ReceiveOrderModel): JPanel {
        return BaseOrderPanel(order).apply {
            // headerPanel의 오른쪽에 버튼 추가
            val buttonPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)  // 수평 박스 레이아웃 설정
                background = Color.WHITE

                // 프린터 버튼
                add(
                    FillRoundedButton(
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
                        // 인쇄 기능 추가
                        // ✅ 프린트 출력
                        var receiptPrinter = ReceiptPrinter()
                        receiptPrinter.ForCustomersOrderSheet(order)
                        receiptPrinter.ForBurialOrderSheet(order)
                    }
                })

                // 버튼 사이에 간격을 추가
                add(Box.createRigidArea(Dimension(15, 0)))

                // 접수하기 버튼
                add(
                    FillRoundedButton(
                    text = "결제완료",
                    borderColor = MyColor.PINK,
                    backgroundColor = MyColor.PINK,
                    textColor = MyColor.DARK_RED,
                    borderRadius = 20,
                    borderWidth = 1,
                    textAlignment = SwingConstants.CENTER,
                    padding = Insets(10, 20, 10, 20),
                    buttonSize = Dimension(130, 50),
                    customFont = MyFont.Bold(22f)
                ))
            }

            // headerPanel의 오른쪽에 버튼 패널 추가
            val headerPanel = components.find { it is JPanel && it.layout is BoxLayout } as JPanel?  // headerPanel 찾기
            headerPanel?.add(Box.createHorizontalGlue())  // 오른쪽 정렬을 위해 추가
            headerPanel?.add(buttonPanel)  // 버튼 패널을 headerPanel의 오른쪽에 추가
        }
    }
}
