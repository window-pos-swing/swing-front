package org.example.view.dialog

import kotlin.Unit;

import javax.swing.*
import java.awt.*

class DeliveryTimeDialog(
        parent: JFrame?,
        private val cookingTime: Int,  // 조리 시간
        private val onDeliveryTimeSelected: (Int) -> Unit  // 배달 시간 선택 콜백
) : JDialog(parent, "배달 예상시간 선택", true) {

    private var deliveryTimeValue = 45  // 기본 배달 시간 45분으로 설정
    private val deliveryTimeLabel = JLabel("$deliveryTimeValue 분", JLabel.CENTER).apply {
        font = Font("Arial", Font.BOLD, 24)
    }

    init {
        layout = BorderLayout()
        size = Dimension(300, 200)
        setLocationRelativeTo(parent)

        // 상단 패널 (타이틀 및 닫기 버튼)
        val topPanel = JPanel().apply {
            layout = BorderLayout()
            add(JLabel("배달 예상시간 선택", JLabel.CENTER).apply {
                font = Font("Arial", Font.BOLD, 16)
            }, BorderLayout.CENTER)
            add(JButton("X").apply {
                addActionListener { dispose() }  // X 버튼 클릭 시 다이얼로그 닫기
            }, BorderLayout.EAST)
        }

        // 중앙 패널 (시간 설정)
        val centerPanel = JPanel().apply {
            layout = GridBagLayout()
            val constraints = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                gridx = 0
                gridy = 0
                gridwidth = 3
            }

            add(JLabel("배달 예상시간을 설정해주세요", JLabel.CENTER), constraints)

            // 시간 감소 버튼
            val minusButton = JButton("-").apply {
                addActionListener {
                    if (deliveryTimeValue > 5) deliveryTimeValue -= 5  // 5분 단위 감소
                    deliveryTimeLabel.text = "$deliveryTimeValue 분"
                }
            }

            // 시간 증가 버튼
            val plusButton = JButton("+").apply {
                addActionListener {
                    deliveryTimeValue += 5  // 5분 단위 증가
                    deliveryTimeLabel.text = "$deliveryTimeValue 분"
                }
            }

            // 버튼 및 라벨 추가
            val timePanel = JPanel().apply {
                layout = GridLayout(1, 3, 10, 10)
                add(minusButton)
                add(deliveryTimeLabel)
                add(plusButton)
            }

            constraints.gridy = 1
            add(timePanel, constraints)
        }

        // 하단 패널 (시간 접수 버튼)
        val bottomPanel = JPanel().apply {
            layout = FlowLayout()
            add(JButton("시간 접수").apply {
                background = Color(255, 153, 51)
                foreground = Color.WHITE
                isOpaque = true
                border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
                addActionListener {
                    onDeliveryTimeSelected(deliveryTimeValue)  // 배달 시간 선택 콜백 호출
                    dispose()  // 다이얼로그 닫기
                }
            })
        }

        // 다이얼로그에 패널 추가
        add(topPanel, BorderLayout.NORTH)
        add(centerPanel, BorderLayout.CENTER)
        add(bottomPanel, BorderLayout.SOUTH)
    }
}
