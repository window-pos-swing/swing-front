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

//        회원정보 갖고오는 구문
        val storeInfo = Storage.getStoreInfo()

        val storeName = storeInfo?.optString("storeName", "점주님") ?: "점주님"

        val titleLabel = JLabel("$storeName 점주님").apply {
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

        add(titlePanel, BorderLayout.WEST)
    }
}