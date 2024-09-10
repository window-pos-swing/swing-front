package org.example.view.dialog

import javax.swing.*
import java.awt.*

class CookingTimeDialog(
    parent: JFrame?,
    private val onCookingTimeSelected: (Int) -> Unit  // 조리 시간 선택 콜백
) : JDialog(parent, "예상 조리시간 선택", true) {

    private var cookingTimeValue = 15  // 기본 조리 시간 15분으로 설정
    private val cookingTimeLabel = JLabel("$cookingTimeValue 분", JLabel.CENTER).apply {
        font = Font("Arial", Font.BOLD, 24)
    }

    init {
        layout = BorderLayout()
        size = Dimension(300, 200)
        setLocationRelativeTo(parent)

        // 상단 패널 (타이틀 및 닫기 버튼)
        val topPanel = JPanel().apply {
            layout = BorderLayout()
            add(JLabel("예상 조리시간 선택", JLabel.CENTER).apply {
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

            add(JLabel("조리 예상시간을 설정해주세요", JLabel.CENTER), constraints)

            // 시간 감소 버튼
            val minusButton = JButton("-").apply {
                addActionListener {
                    if (cookingTimeValue > 5) cookingTimeValue -= 5  // 5분 단위 감소
                    cookingTimeLabel.text = "$cookingTimeValue 분"
                }
            }

            // 시간 증가 버튼
            val plusButton = JButton("+").apply {
                addActionListener {
                    cookingTimeValue += 5  // 5분 단위 증가
                    cookingTimeLabel.text = "$cookingTimeValue 분"
                }
            }

            // 버튼 및 라벨 추가
            val timePanel = JPanel().apply {
                layout = GridLayout(1, 3, 10, 10)
                add(minusButton)
                add(cookingTimeLabel)
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
                    onCookingTimeSelected(cookingTimeValue)  // 조리 시간 선택 콜백 호출
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
