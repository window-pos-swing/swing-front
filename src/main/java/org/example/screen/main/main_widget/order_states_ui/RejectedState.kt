package org.example.view.states


import org.example.MyFont
import org.example.model.Order
import org.example.model.OrderState
import org.example.style.MyColor
import org.example.view.components.BaseOrderPanel
import org.example.widgets.AutoScalingLabel
import org.example.widgets.CHRoundedPanel
import org.example.widgets.FillRoundedButton
import java.awt.*
import javax.swing.*

class RejectedState(
    val rejectReason: String,
    val rejectDate: String,
    val originState: OrderState  // 거절된 원래 상태 (PendingState 또는 ProcessingState)
) : OrderState {

    override fun handle(order: Order) {
        // 거절된 상태에 대한 추가 처리 로직이 필요할 경우 여기에 작성
    }

    override fun getUI(order: Order): JPanel {
        return BaseOrderPanel(order).apply {
            layout = BorderLayout()  // 전체 레이아웃을 BorderLayout으로 설정
            background = Color.WHITE  // 전체 배경색을 하얀색으로 설정
            isOpaque = true  // BaseOrderPanel을 불투명하게 설정

            // headerPanel에 "고객접수거절" 버튼 추가
            val buttonPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.RIGHT, 0, 0)  // 오른쪽 정렬
                background = Color.WHITE  // 배경색을 하얀색으로 설정
                isOpaque = true  // 불투명하게 설정
                border = BorderFactory.createEmptyBorder(15, 0, 0, 0)

                // "고객접수거절" 버튼
                add(
                    FillRoundedButton(
                        text = "고객접수거절",
                        borderColor = MyColor.PINK,
                        backgroundColor = MyColor.PINK,
                        textColor = MyColor.DARK_RED,
                        borderRadius = 20,
                        borderWidth = 1,
                        textAlignment = SwingConstants.CENTER,
                        padding = Insets(10, 20, 10, 20),
                        buttonSize = Dimension(200, 50),
                        customFont = MyFont.Bold(18f)
                    )
                )
            }

            val headerPanel = getHeaderPanel()
            headerPanel.add(Box.createHorizontalGlue())  // 오른쪽 정렬을 위해 공간 추가
            headerPanel.add(buttonPanel)  // 버튼 패널을 headerPanel의 오른쪽에 추가
            headerPanel.isOpaque = true  // 불투명하게 설정
            headerPanel.background = Color.WHITE  // 배경색 설정
            add(headerPanel, BorderLayout.NORTH)

            // 메인 contentPanel 구성
            val contentPanel = JPanel().apply {
                layout = BorderLayout()
                isOpaque = true  // 패널을 불투명하게 설정
                background = Color.WHITE  // 배경색을 하얀색으로 설정
                border = BorderFactory.createEmptyBorder(15, 0, 0, 0)

                // 왼쪽 패널
                val leftPanel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    isOpaque = false
                    border = BorderFactory.createEmptyBorder(0, 0, 0, 20)
                    add(getAddressPanel())  // 주소 정보
                    add(Box.createRigidArea(Dimension(0, 15)))  // 간격 추가
                    add(getMenuDetailPanel())  // 메뉴 세부 정보
                    add(Box.createRigidArea(Dimension(0, 5)))  // 간격 추가
                    add(getFooterPanel())  // 수저/포크, 요청사항
                }

                // 오른쪽 패널 (GridBagLayout 사용)
                val rightPanel = CHRoundedPanel(30,30).apply {
                    layout = GridBagLayout()  // 그리드 레이아웃 설정
                    preferredSize = Dimension(255, 234)  // rightPanel 크기 설정
                    isOpaque = true  // 패널을 불투명하게 설정
                    background = Color.WHITE  // 배경색을 하얀색으로 설정
                    foreground = MyColor.PINK
                    border = BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 15, 0),  // 상하좌우 15px 마진
                        BorderFactory.createLineBorder(MyColor.PINK, 1, true)  // 기존 핑크색 테두리
                    )

                    // GridBagConstraints 설정
                    val gbc = GridBagConstraints().apply {
                        gridx = 0  // 첫 번째 열
                        weightx = 1.0  // 가로 방향으로 크기 동일하게 설정
                        fill = GridBagConstraints.BOTH  // 가로, 세로로 꽉 차도록 설정
                    }

                    // 1. 거절일시 박스
                    val rejectDatePanel = JPanel().apply {
                        layout = GridBagLayout()  // 그리드 레이아웃 사용
                        isOpaque = true  // 패널을 불투명하게 설정
                        background = Color.WHITE  // 배경색을 하얀색으로 설정

                        // 하단에만 1px 핑크색 테두리와 좌우에 10px 패딩 추가
                        border = BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(0, 10, 0, 10),  // 좌우에 10px 패딩
                            BorderFactory.createMatteBorder(0, 0, 1, 0, MyColor.PINK)  // 하단에 1px 테두리
                        )

                        // GridBagConstraints 설정
                        val gbcDatePanel = GridBagConstraints().apply {
                            gridx = 0
                            weightx = 1.0  // 가로 방향으로 꽉 차도록 설정
                            fill = GridBagConstraints.HORIZONTAL  // 가로로 확장 가능하도록 설정
                            insets = Insets(5, 0, 5, 0)  // 컴포넌트 간 간격 설정
                        }

                        // "거절일시" 텍스트 (왼쪽 정렬)
                        gbcDatePanel.anchor = GridBagConstraints.WEST  // 왼쪽 정렬
                        add(JLabel("거절일시").apply {
                            font = MyFont.Medium(20f)
                            foreground = MyColor.GREY600  // 텍스트 색상 설정
                        }, gbcDatePanel.apply { gridy = 0 })  // 첫 번째 행에 추가

                        // 실제 거절일시 (가운데 정렬)
                        gbcDatePanel.anchor = GridBagConstraints.CENTER  // 가운데 정렬
                        add(JLabel("${rejectDate}").apply {
                            font = MyFont.Bold(28f)
                            foreground = MyColor.GREY900  // 실제 거절일시 텍스트 색상
                            horizontalAlignment = SwingConstants.CENTER  // 수평 가운데 정렬
                        }, gbcDatePanel.apply { gridy = 1 })  // 두 번째 행에 추가
                    }

                    // 2. 거절이유 박스
                    val rejectReasonPanel = JPanel().apply {
                        layout = GridBagLayout()  // 그리드 레이아웃 사용
                        isOpaque = true  // 패널을 불투명하게 설정
                        background = Color.WHITE  // 배경을 하얀색으로 설정

                        // GridBagConstraints 설정
                        val gbcReasonPanel = GridBagConstraints().apply {
                            gridx = 0
                            weightx = 1.0  // 가로 방향으로 꽉 차도록 설정
                            fill = GridBagConstraints.HORIZONTAL  // 가로로 확장 가능하도록 설정
                            insets = Insets(5, 10, 5, 10)  // 컴포넌트 간 간격 설정
                        }

                        // "거절이유" 텍스트 (왼쪽 정렬)
                        gbcReasonPanel.anchor = GridBagConstraints.WEST  // 왼쪽 정렬
                        add(JLabel("거절이유").apply {
                            font = MyFont.Medium(20f)
                            foreground = MyColor.GREY600  // 텍스트 색상 설정
                        }, gbcReasonPanel.apply { gridy = 0 })  // 첫 번째 행에 추가

                        // 실제 거절이유 (가운데 정렬)
                        val rejectReasonLabel = AutoScalingLabel(rejectReason).apply {
                            foreground = MyColor.GREY900  // 텍스트 색상 설정
                            horizontalAlignment = SwingConstants.CENTER  // 수평 가운데 정렬
                        }
                        gbcReasonPanel.anchor = GridBagConstraints.CENTER  // 가운데 정렬
                        add(rejectReasonLabel, gbcReasonPanel.apply { gridy = 1 })  // 두 번째 행에 추가
                    }

                    // rightPanel에 두 개의 박스를 추가 (반반씩 차지하도록)
                    add(rejectDatePanel, gbc.apply { gridy = 0; weighty = 0.5 })  // 상단 절반을 차지하는 거절일시 박스
                    add(rejectReasonPanel, gbc.apply { gridy = 1; weighty = 0.5 })  // 하단 절반을 차지하는 거절이유 박스
                }

                //rightPanel에 하단 마진을 주기 위해 감싼 레이아웃
                val wrapperPanel = JPanel().apply {
                    layout = BorderLayout()
                    isOpaque = false
                    border = BorderFactory.createEmptyBorder(0, 0, 15, 0)  // 하단 마진 설정

                    add(rightPanel, BorderLayout.CENTER)  // rightPanel을 중앙에 추가
                }

                // contentPanel에 좌우 패널 배치
                add(leftPanel, BorderLayout.CENTER)
                add(wrapperPanel, BorderLayout.EAST)
            }

            // contentPanel을 CENTER에 배치
            add(contentPanel, BorderLayout.CENTER)
        }
    }

}
