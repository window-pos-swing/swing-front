package org.grr.screen.setting.centerPanel

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.grr.api.LogoutToServer
import org.grr.`object`.Storage
import org.grr.screen.login.LoginForm
import org.grr.screen.setting.centerPanel.breakTimePanel.BreakTime
import org.grr.screen.setting.centerPanel.cookingCompltePanel.CookingCompletionTime
import org.grr.screen.setting.centerPanel.deliveryMethodTimePanel.DeliveryMethodTime
import org.grr.screen.setting.centerPanel.holidayPanel.HolidayPanel
import org.grr.screen.setting.centerPanel.operateTimePanel.OperateTime
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedPanel
import java.awt.*
import javax.swing.*

class CenterPanelForm : JPanel() {
    init {
        layout = BorderLayout()
        background = MyColor.DARK_NAVY
        border = BorderFactory.createEmptyBorder(20, 0, 0, 0)

        // 둥근 패널 생성
        val roundedPanel = RoundedPanel(30, 30).apply {
            layout = GridBagLayout()
            background = MyColor.LOGIN_TITLEBAR // 둥근 패널 배경색
            preferredSize = Dimension(1380, 480)
            maximumSize = Dimension(Int.MAX_VALUE, 480)  // 최대 크기도 설정
            border = BorderFactory.createEmptyBorder(5, 20, 5, 20) // 내부 여백 설정
        }

        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.BOTH
            anchor = GridBagConstraints.NORTH  // 컴포넌트를 상단에 고정
        }

        // 조리 완료 시간 패널 배치
        gbc.gridx = 0  // 첫 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        gbc.weightx = 0.47
        gbc.insets = Insets(15, 10, 0, 10)
        roundedPanel.add(CookingCompletionTime(), gbc)

        // 경계선 추가 (조리 완료 시간과 배달 예정 시간 사이)
        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.01
        gbc.insets = Insets(15, 10, 15, 0)
        roundedPanel.add(createSeparator(SwingConstants.VERTICAL, 1, 150), gbc)

        // 배달 방법 및 배달 예정 시간 패널 배치
        gbc.gridx = 2  // 두 번째 열
        gbc.gridy = 0  // 첫 번째 행
        gbc.gridwidth = 1  // 1칸 차지
        gbc.weightx = 0.47
        gbc.insets = Insets(15, 10, 0, 10)
        roundedPanel.add(DeliveryMethodTime(), gbc)

        // 경계선 추가 (배달 방법과 브레이크 타임 사이)
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3  // 전체 너비 차지
        gbc.weightx = 0.0
        gbc.insets = Insets(0, 10, 0, 10)
        roundedPanel.add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 브레이크 타임 패널 추가
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        roundedPanel.add(BreakTime(), gbc)

        // 브레이크 타임 밑에 경계선 추가
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3  // 패널 아래 경계선이 전체 가로를 차지하게 설정
        gbc.insets = Insets(0, 10, 0, 10)
        roundedPanel.add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 영업 시간 패널 추가
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        roundedPanel.add(OperateTime(), gbc)

        // 브레이크 타임 밑에 경계선 추가
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 3  // 패널 아래 경계선이 전체 가로를 차지하게 설정
        gbc.insets = Insets(0, 10, 0, 10)
        roundedPanel.add(createSeparator(SwingConstants.HORIZONTAL, 1340, 1), gbc)

        // 휴무일 패널 추가
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 3
        gbc.weighty = 0.0
        gbc.insets = Insets(5, 10, 0, 10)
        roundedPanel.add(HolidayPanel(), gbc)

        add(roundedPanel, BorderLayout.NORTH)

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
//                            저장된 회원 정보 삭제
                            Storage.clearMemberInfo()

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
        add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun createSeparator(orientation: Int, width: Int, height: Int): JSeparator {
        return JSeparator(orientation).apply {
            foreground = Color.WHITE
            preferredSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
        }
    }
}