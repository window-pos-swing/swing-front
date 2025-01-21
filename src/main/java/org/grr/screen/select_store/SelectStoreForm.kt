package org.grr.screen.select_store;

import com.privatejgoodies.forms.layout.CellConstraints.Alignment
import org.grr.screen.main.MainForm
import org.grr.style.MyColor
import org.grr.util.LoadImage.loadImage
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import org.grr.widgets.custom_titlebar.LoginCustomTitleBar
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class SelectStoreForm(storeData: List<Pair<String, String>>) : JFrame() {

    init {
        // 기존 타이틀바 제거
        isUndecorated = true
        // JFrame의 레이아웃을 명시적으로 BorderLayout으로 설정
        layout = BorderLayout()

        // 커스텀 타이틀바 추가
        val loginCustomTitleBar = LoginCustomTitleBar(this)
        add(loginCustomTitleBar, BorderLayout.NORTH)  // 타이틀바를 명확하게 NORTH에 추가

        // 메인 패널
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = MyColor.DARK_NAVY// 짙은 남색 배경
            border = EmptyBorder(20, 20, 20, 20)

            // 상점 목록 추가
            storeData.forEach { (storeName, address) ->
                add(createStorePanel(storeName, address))
                add(Box.createVerticalStrut(20)) // 간격
            }
        }

        val scrollPane = JScrollPane(mainPanel).apply {
            border = null
            verticalScrollBar.unitIncrement = 16
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER // 수평 스크롤 비활성화
        }

        add(scrollPane, BorderLayout.CENTER)

        // 하단 콜센터 정보
        val footerPanel = JPanel().apply {
            background = MyColor.DARK_NAVY
            layout = FlowLayout(FlowLayout.RIGHT)
            add(JLabel("꼬르륵 콜센터 1600-1234").apply {
                foreground = Color.WHITE
                font = MyFont.Bold(24f)
            })
        }
        add(footerPanel, BorderLayout.SOUTH)

        setSize(1440, 1024)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null) // 화면 중앙에 배치
    }

    private fun createStorePanel(storeName: String, address: String): JPanel {
        return JPanel().apply {
            layout = BorderLayout()
            background = MyColor.DARK_NAVY // 짙은 배경
            preferredSize = Dimension(1400, 100)
            border = BorderFactory.createLineBorder(Color.WHITE, 2)

            // 상점 이름과 주소 패널
            val infoPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                background = MyColor.DARK_NAVY
                border = EmptyBorder(0, 20, 0, 20) // 좌우에 여백 추가

                // 상점 이름 추가
                add(JLabel(storeName).apply {
                    foreground = Color.WHITE
                    font = MyFont.Bold(35f)
                    alignmentX = Component.LEFT_ALIGNMENT // 왼쪽 정렬
                })

                // 주소와 아이콘을 포함한 패널
                val addressPanel = JPanel().apply {
                    layout = BorderLayout() // 텍스트가 공간에 맞게 표시되도록 수정
                    background = MyColor.DARK_NAVY

                    // 아이콘 로드
                    val logoIcon = ImageIcon(javaClass.getResource("/select_store_address_icon.png")).apply {
                        image = image.getScaledInstance(24, 24, Image.SCALE_SMOOTH)
                    }
                    // 아이콘 추가
                    val iconLabel = JLabel(logoIcon).apply {
                        border = EmptyBorder(0, 0, 0, 10) // 오른쪽에 10px 여백 추가
                    }
                    // 아이콘과 주소 추가
                    add(iconLabel, BorderLayout.WEST)
                    add(JLabel(address).apply {
                        foreground = Color.LIGHT_GRAY
                        font = MyFont.Bold(21f)
                    }, BorderLayout.CENTER) // 주소를 가운데 확장
                }
                addressPanel.alignmentX = Component.LEFT_ALIGNMENT // 왼쪽 정렬
                add(addressPanel) // 주소 패널 추가
            }
            infoPanel.alignmentX = Component.LEFT_ALIGNMENT // 전체 패널 왼쪽 정렬

            // 영업 시작 버튼
            val buttonPanel = JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER) // 버튼을 가운데 정렬
                background = MyColor.DARK_NAVY // 배경 색상 통일
                border = EmptyBorder(15, 20, 20, 20) // 좌우에 여백 추가

                add(FillRoundedButton(
                    text = "영업 시작",
                    borderColor = Color.WHITE,
                    backgroundColor = Color.WHITE,
                    textColor = Color.BLACK,
                    customFont = MyFont.Bold(33f),
                    borderRadius = 10,
                    borderWidth = 1,
                    textAlignment = SwingConstants.CENTER,
                    padding = Insets(10, 20, 10, 20),
                    buttonSize = Dimension(250, 60)
                ).apply {
                    preferredSize = Dimension(250, 60)
                })
            }

            add(infoPanel, BorderLayout.CENTER)
            add(buttonPanel, BorderLayout.EAST)
        }
    }


}

fun main() {
    // 가데이터: 상점 이름과 주소
    val storeData = listOf(
        "상점1" to "대전광역시 유성구 어쩌구동 어쩌구로 301-102",
        "상점2" to "대전광역시 유성구 다른구동 다른구로 123-456",
        "상점3" to "대전광역시 중구 어떤구동 어떤길 789-000",
        "상점4" to "서울특별시 강남구 테헤란로 456",
        "상점5" to "부산광역시 해운대구 해운대로 89-101",
        "상점6" to "대구광역시 달서구 달구벌대로 303-203",
        "상점7" to "대전광역시 유성구 어쩌구동 어쩌구로 301-102",
        "상점8" to "대전광역시 유성구 다른구동 다른구로 123-456",
        "상점9" to "대전광역시 중구 어떤구동 어떤길 789-000",
        "상점10" to "서울특별시 강남구 테헤란로 456",
        "상점11" to "부산광역시 해운대구 해운대로 89-101",
        "상점12" to "대구광역시 달서구 달구벌대로 303-203",
    )

    SwingUtilities.invokeLater {
        val selectStoreForm = SelectStoreForm(storeData)
        selectStoreForm.isVisible = true
    }
}