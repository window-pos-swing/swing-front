package org.grr.screen.setting.headerPanel

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.grr.api.LogoutToServer
import org.grr.`object`.Storage
import org.grr.screen.login.LoginForm
import org.grr.style.MyColor
import org.grr.util.MyFont
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*

// 세팅에서 가장 상단 패널
class HeaderPanelForm : JPanel() {
    init {
        layout = BorderLayout()
        background = MyColor.DARK_NAVY
        preferredSize = Dimension(1440, 80)  // 패널 크기 강제 설정
        maximumSize = Dimension(Int.MAX_VALUE, 80)  // 최대 크기도 설정

        val titleLabel = JLabel("꼬르륵 식당 점주님").apply {
            font = MyFont.Bold(44f)
            foreground = Color.WHITE
        }

        val storeIcon = ImageIcon(javaClass.getResource("/store.png"))
        val storeLabel = JLabel(storeIcon).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 10)  // 아이콘과 텍스트 사이 여백 추가
        }

        val titlePanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
            background = MyColor.DARK_NAVY
            add(storeLabel, BorderLayout.WEST)
            add(titleLabel, BorderLayout.CENTER)
        }

        val logoutButton = JButton("로그아웃").apply {
            preferredSize = Dimension(150, 50)
            background = MyColor.GREY100
            font = MyFont.Bold(24f)
            isOpaque = true
            /*
                로그아웃을 진행하는 구문
            */
            addActionListener {
                // 로그아웃 요청
                val logoutToServer = LogoutToServer()
                GlobalScope.launch {

                    delay(500) // 0.5초 대기

                    val (isSuccess, message) = logoutToServer.logoutToServer()

                    delay(500) // 0.5초 대기

                    SwingUtilities.invokeLater {
                        if (isSuccess) {
//                            토큰 삭제
                            Storage.deleteToken()
//                            저장된 로그인 정보 삭제
                            Storage.clearLoginInfo()

                            val loginForm = LoginForm()
                            loginForm.isVisible = true

                            val parentWindow = SwingUtilities.getWindowAncestor(this@apply)
                            parentWindow?.dispose()
                        } else {
                            JOptionPane.showMessageDialog(this@apply, message, "오류", JOptionPane.ERROR_MESSAGE)
                        }
                    }
                }
            }
        }

        // 로그아웃 버튼을 패널에 넣어 정렬을 유지
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
            background = MyColor.DARK_NAVY
            add(logoutButton)
        }

        add(titlePanel, BorderLayout.WEST)
        add(buttonPanel, BorderLayout.EAST)
    }
}