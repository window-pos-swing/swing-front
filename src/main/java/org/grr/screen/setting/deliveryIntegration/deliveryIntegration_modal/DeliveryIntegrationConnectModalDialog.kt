package org.grr.screen.setting.deliveryIntegration.deliveryIntegration_modal

import CustomRoundedDialog
import org.grr.style.MyColor
import org.grr.util.MyFont
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.*

class DeliveryIntegrationConnectModalDialog(
    parent: JFrame,
    title: String,
    deliveryIntegration: String,
    callback: ((String) -> Unit)? = null
) : CustomRoundedDialog(parent, title, 650, 650, null ,callback) {
    init {
        setSize(650, 650)
        setLocationRelativeTo(parent)
        background = Color.WHITE

        // 선택된 배달대행사 표시 패널
        val selectedAgencyPanel = JLabel(deliveryIntegration).apply {
            font = MyFont.Bold(26f)
            foreground = MyColor.LIGHT_BLUE
            horizontalAlignment = SwingConstants.CENTER
            border = BorderFactory.createEmptyBorder(40, 20, 20, 0)
        }

        // 설명 문구
        val descriptionLabel = JLabel("아이디와 인증번호를 입력해주세요").apply {
            font = MyFont.Bold(26f)
            foreground = Color.BLACK
            horizontalAlignment = SwingConstants.CENTER
            border = BorderFactory.createEmptyBorder(0, 20, 40, 0)
        }

        // 입력 필드 패널
        val inputPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE

            // 로그인 아이디 입력 필드
            val idField = JTextField("아이디 입력").apply {
                preferredSize = Dimension(570, 95)
                background = MyColor.LIGHT_GREY
                font = MyFont.Bold(26f)
                foreground = Color.GRAY // 기본 텍스트 색상
                border = BorderFactory.createEmptyBorder(20, 20, 20, 20) // 내부 여백
                maximumSize = Dimension(570, 95) // 고정 크기

                addFocusListener(object : FocusListener {
                    override fun focusGained(e: FocusEvent) {
                        if (text == "아이디 입력") {
                            text = "" // 필드 클릭 시 기본 텍스트 삭제
                            foreground = Color.BLACK // 글씨 색상 변경
                        }
                    }

                    override fun focusLost(e: FocusEvent) {
                        if (text.isEmpty()) {
                            text = "아이디 입력" // 필드가 비어 있으면 기본 텍스트 복구
                            foreground = Color.GRAY // 글씨 색상 회색으로 변경
                        }
                    }
                })
            }

            // 인증번호 입력 필드
            val authCodeField = JTextField("인증번호 입력").apply {
                preferredSize = Dimension(570, 95)
                background = MyColor.LIGHT_GREY
                font = MyFont.Bold(26f)
                foreground = Color.GRAY // 기본 텍스트 색상
                border = BorderFactory.createEmptyBorder(20, 20, 20, 20) // 내부 여백
                maximumSize = Dimension(570, 95) // 고정 크기

                addFocusListener(object : FocusListener {
                    override fun focusGained(e: FocusEvent) {
                        if (text == "인증번호 입력") {
                            text = "" // 필드 클릭 시 기본 텍스트 삭제
                            foreground = Color.BLACK // 글씨 색상 변경
                        }
                    }

                    override fun focusLost(e: FocusEvent) {
                        if (text.isEmpty()) {
                            text = "인증번호 입력" // 필드가 비어 있으면 기본 텍스트 복구
                            foreground = Color.GRAY // 글씨 색상 회색으로 변경
                        }
                    }
                })
            }
            // 필드 추가
            add(idField)
            add(Box.createVerticalStrut(30)) // 필드 간 간격
            add(authCodeField)
        }

        // 버튼 패널
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 0, 0, 0)

            val confirmButton = JButton("연결 요청").apply {
                preferredSize = Dimension(300, 62)
                maximumSize = Dimension(300, 62) // 고정 크기
                font = MyFont.Bold(26f)
                background = Color.WHITE
                foreground = Color.RED
                border = BorderFactory.createLineBorder(Color.RED, 1)
                isFocusPainted = false
                addActionListener {
                    dispose()
                    /*
                        연결 요청처리 해야함
                    */
                    callback?.invoke(deliveryIntegration)
                }
            }
            add(confirmButton)
        }

        // 상단 영역 (배달대행사 이름과 설명)
        val topPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            add(selectedAgencyPanel)
            add(descriptionLabel)
        }

        // 메인 패널
        val mainPanel = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(10, 20, 10, 20) // 내부 여백
            add(topPanel, BorderLayout.NORTH) // 상단 영역 추가
            add(inputPanel, BorderLayout.CENTER) // 입력 필드 추가
            add(buttonPanel, BorderLayout.SOUTH) // 버튼 추가
        }

        contentPane.add(mainPanel, BorderLayout.CENTER) // 테이블 추가
    }
}