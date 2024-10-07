package org.example.view.states


import org.example.CustomTabbedPane
import org.example.MyFont
import org.example.command.RejectOrderCommand
import org.example.model.Order
import org.example.model.OrderState
import org.example.screen.main.main_widget.dialog.EstimatedTimeDialog
import org.example.style.MyColor
import org.example.view.components.BaseOrderPanel
import org.example.widgets.FillRoundedButton
import java.awt.*
import javax.swing.*

class CompletedState : OrderState {
    override fun handle(order: Order) {
        // Completed 상태 처리 로직
    }

    override fun getUI(order: Order): JPanel {
        return BaseOrderPanel(order).apply {
            // headerPanel의 오른쪽에 버튼 추가
            val buttonPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.RIGHT, 15, 0)  // FlowLayout을 사용하여 버튼 크기 조정 가능하게 설정
                background = Color.WHITE  // 배경색 설정

                border = BorderFactory.createEmptyBorder(15,0,0,0)

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
                    iconPath = "/print_icon.png",
                    buttonSize = Dimension(50, 50),
                    iconWidth = 45,
                    iconHeight = 45
                ).apply {
                    addActionListener {
                        // 인쇄 기능 추가
                        println("프린터 버튼 클릭")
                    }
                })

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
