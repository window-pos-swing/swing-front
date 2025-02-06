import org.grr.screen.setting.centerPanel.breakTimePanel.breakTime_modal.ShareButton.selectedDays
import org.grr.util.MyFont
import org.grr.style.MyColor
import org.grr.widgets.CustomToggleButton2
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.RoundRectangle2D
import javax.swing.*
import javax.swing.border.AbstractBorder

class CustomToggleButton3(
    private val onSelectionChanged: (Int) -> Unit
) : JToggleButton() {
    var selectedIndex = 0
    private var previousIndex = 0

    init {
        // 토글 버튼의 초기 상태를 OFF (false)로 설정
        isOpaque = false
        background = Color(217, 217, 217)
        isSelected = true
        isFocusPainted = false
        preferredSize = Dimension(680, 50)
        maximumSize = Dimension(680, 50)
        border = null // 경계선을 없앰

        // 상태 변화에 따른 동작을 추가
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val clickX = e.x
                val buttonWidth = width / 3

                // 클릭된 영역에 따라 selectedIndex 변경
                val newIndex = when {
                    clickX < buttonWidth -> 0 // 왼쪽 버튼
                    clickX < 2 * buttonWidth -> 1 // 가운데 버튼
                    else -> 2 // 오른쪽 버튼
                }

                if (!canChangePanel(newIndex)) {
                    selectedIndex = previousIndex // 선택을 원래 상태로 복원
                    repaint()
                    return
                }

                selectedIndex = newIndex
                previousIndex = newIndex
                onSelectionChanged(selectedIndex)
                repaint() // 상태 변경 후 다시 그리기
            }
        })
    }

    private fun canChangePanel(newIndex: Int): Boolean {

        // "평일/주말"이 선택된 상태에서 다른 패널을 클릭하면 차단
        if (newIndex == 0 && selectedDays.any { it == "평일" } ||
            newIndex == 2 && selectedDays.any { it == "평일" } ||
            newIndex == 0 && selectedDays.any { it == "주말" } ||
            newIndex == 2 && selectedDays.any { it == "주말" }
            ) {
            JOptionPane.showMessageDialog(
                this,
                "'평일/주말'이 선택되어 있어서 다른 선택을 할 수 없습니다!",
                "오류",
                JOptionPane.ERROR_MESSAGE
            )
            return false
        }

        // 🔥 "요일별 선택"에서 개별 요일이 추가된 경우, 전체(0) 또는 평일/주말(1) 선택 차단
        if (newIndex in listOf(0, 1) && selectedDays.any { it in listOf("월", "화", "수", "목", "금", "토", "일") }) {
            JOptionPane.showMessageDialog(
                this,
                "요일별 선택이 선택된 상태에서는 다른 선택을 선택할 수 없습니다!",
                "오류",
                JOptionPane.ERROR_MESSAGE
            )
            return false
        }

        return true // ✅ 패널 변경 가능
    }

    override fun paintComponent(g: Graphics) {
        val g2d = g as Graphics2D
        val width = width
        val height = height
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경 색 그리기 (테두리 효과 방지)
        g2d.color = MyColor.GREY200
        g2d.fillRoundRect(0, 0, width, height, height, height)

        // 선택된 상태에 따른 배경색 그리기
        when (selectedIndex) {
            0 -> { // 전체 선택
                g2d.clipRect(0, 0, width / 3, height)
                g2d.color = MyColor.DARK_NAVY
                g2d.fillRoundRect(0, 0, width, height, height, height)
            }
            1 -> { // 평일/주말 선택
                g2d.clipRect(width / 3, 0, width / 3, height)
                g2d.color = MyColor.DARK_NAVY
                g2d.fillRoundRect(0, 0, width, height, height, height)
            }
            2 -> { // 요일별 선택
                g2d.clipRect(2 * width / 3, 0, width / 3, height)
                g2d.color = MyColor.DARK_NAVY
                g2d.fillRoundRect(0, 0, width, height, height, height)
            }
        }

        // 클립 리셋 및 텍스트 색상 처리
        g2d.clip = null
        g2d.font = MyFont.Bold(22f)
        g2d.color = if (selectedIndex == 0) Color.WHITE else Color(137, 137, 137)
        g2d.drawString("전체 선택", width / 6 - g2d.fontMetrics.stringWidth("전체 선택") / 2, height / 2 + g2d.fontMetrics.ascent / 3)

        g2d.color = if (selectedIndex == 1) Color.WHITE else Color(137, 137, 137)
        g2d.drawString("평일/주말 선택", width / 2 - g2d.fontMetrics.stringWidth("평일/주말 선택") / 2, height / 2 + g2d.fontMetrics.ascent / 3)

        g2d.color = if (selectedIndex == 2) Color.WHITE else Color(137, 137, 137)
        g2d.drawString("요일별 선택", 5 * width / 6 - g2d.fontMetrics.stringWidth("요일별 선택") / 2, height / 2 + g2d.fontMetrics.ascent / 3)

        // 포커스와 관련된 그리기 동작을 제거
        isFocusPainted = false
    }
}

