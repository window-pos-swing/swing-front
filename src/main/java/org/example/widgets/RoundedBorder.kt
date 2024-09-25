package org.example.widgets

import org.example.MyFont
import sun.tools.jstat.Alignment
import java.awt.*
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.LineBorder

//아이콘 있는 버튼 스타일링
class IconRoundBorder {
    companion object {
        // 둥근 테두리와 하얀 배경을 가진 버튼을 만드는 함수
        fun createRoundedButton(iconPath: String): JButton {
            val icon = ImageIcon(javaClass.getResource(iconPath))  // 아이콘 로드

            return object : JButton(icon) {
                init {
                    preferredSize = Dimension(40, 40)
                    isContentAreaFilled = false  // 기본 내용 배경 제거
                    isFocusPainted = false  // 포커스 표시 제거
                    border = null
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    margin = Insets(5, 5, 5, 5)  // 버튼과 테두리 사이에 여백 추가
                }

                override fun paintComponent(g: Graphics) {
                    val g2 = g as Graphics2D
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                    // 버튼의 둥근 배경을 흰색으로 채움
                    g2.color = Color.WHITE
                    g2.fillRoundRect(0, 0, width, height, 20, 20)

                    // 버튼의 아이콘을 그린 후 테두리를 그려줌
                    super.paintComponent(g)

                    // 테두리 그리기
                    g2.color = Color.LIGHT_GRAY
                    g2.drawRoundRect(0, 0, width - 1, height - 1, 20, 20)
                }
            }
        }
    }
}

// 서브탭 버튼 스타일링
class SelectButtonRoundedBorder(private val radius: Int) : LineBorder(Color.GRAY, 2, true) {

    // 둥근 테두리 적용
    override fun getBorderInsets(c: Component): Insets {
        return Insets(radius + 1, radius + 1, radius + 2, radius)
    }

    override fun isBorderOpaque(): Boolean {
        return false
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        val g2 = g as Graphics2D
        g2.color = lineColor
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
    }

    // 둥근 버튼 생성 함수
    fun createRoundedButton(
        text: String,
        selectedColor: Color,
        unselectedColor: Color,
        selectedTextColor: Color,
        unselectedTextColor: Color
    ): JButton {
        return object : JButton(text) {
            // 버튼 내부를 둥근 테두리 안쪽만 채우도록 재정의
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                // 현재 상태에 따른 배경색 설정
                if (this.model.isSelected) {
                    g2.color = selectedColor
                } else {
                    g2.color = background // 현재 설정된 배경색 사용
                }

                // 둥근 사각형 내부에만 배경을 채움
                g2.fillRoundRect(0, 0, width, height, radius, radius)

                // 부모 클래스의 paintComponent 호출하여 텍스트 처리
                super.paintComponent(g)
            }

            // 기본 배경 채우기 비활성화
            override fun isContentAreaFilled(): Boolean {
                return false
            }
        }.apply {
            preferredSize = Dimension(230, 60)
            maximumSize = Dimension(230, 60)
            minimumSize = Dimension(230, 60)

            // 초기 텍스트 색상 설정
            foreground = unselectedTextColor

            // 둥근 테두리 적용
            border = SelectButtonRoundedBorder(30) // 둥글기 정도 설정

            // 버튼의 텍스트 폰트 설정
            font = MyFont.Bold(24f)

            // 텍스트 가운데 정렬
            horizontalAlignment = SwingConstants.CENTER

            // 선택 여부에 따른 배경색 변경
            background = unselectedColor
        }
    }
}

// 밖에 라인만 둥근 컴포넌트
class OutLineRoundedLabel(
    text: String,
    private val borderColor: Color = Color.BLACK,  // 테두리 색상 (기본: 검은색)
    private val borderRadius: Int = 20,  // 둥근 정도
    private val borderWidth: Int = 2,  // 테두리 두께
    private val textColor: Color = Color.BLACK,  // 텍스트 색상
    private val padding: Insets = Insets(0, 0, 0, 0)  // 패딩 설정
) : JLabel(text) {

    init {
        isOpaque = false  // 배경을 투명하게 설정
        foreground = textColor  // 텍스트 색상 설정
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D

        // 안티앨리어싱 활성화 (부드러운 테두리)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 패널 크기 가져오기
        val width = width
        val height = height

        // 라벨의 둥근 테두리 그리기
        g2.color = borderColor
        g2.stroke = BasicStroke(borderWidth.toFloat())
        g2.drawRoundRect(
            borderWidth / 2,
            borderWidth / 2,
            width - borderWidth,
            height - borderWidth,
            borderRadius,
            borderRadius
        )

        // 텍스트 그리기
        super.paintComponent(g)
    }

    // Insets가 null인 경우 처리
    override fun getInsets(insets: Insets?): Insets {
        return insets ?: Insets(padding.top, padding.left, padding.bottom, padding.right)
    }

    override fun getInsets(): Insets {
        return Insets(padding.top, padding.left, padding.bottom, padding.right)
    }

    override fun getPreferredSize(): Dimension {
        val preferredSize = super.getPreferredSize()
        return Dimension(preferredSize.width + padding.left + padding.right, preferredSize.height + padding.top + padding.bottom)
    }
}
// 안에 꽉찬 둥근 라벨
class FillRoundedLabel(
    text: String,
    private val borderColor: Color,
    private val backgroundColor: Color,
    private val textColor: Color,  // 텍스트 색상
    private val borderRadius: Int,
    private val borderWidth: Int,
    private val textAlignment: Int,  // 텍스트 정렬 (SwingConstants.LEFT, CENTER, RIGHT)
    private val padding: Insets  // 패딩을 위한 Insets
) : JLabel(text , SwingConstants.LEFT) {

    init {
        isOpaque = false  // 배경을 직접 그릴 것이므로 투명하게 설정
        horizontalAlignment = textAlignment  // 텍스트 정렬 설정
        foreground = textColor  // 텍스트 색상 설정
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경색과 테두리 먼저 그리기
        g2.color = backgroundColor
        g2.fillRoundRect(borderWidth / 2, borderWidth / 2, width - borderWidth, height - borderWidth, borderRadius, borderRadius)

        g2.color = borderColor
        g2.stroke = BasicStroke(borderWidth.toFloat())
        g2.drawRoundRect(borderWidth / 2, borderWidth / 2, width - borderWidth, height - borderWidth, borderRadius, borderRadius)

        // 텍스트 그리기 전에 정렬에 따른 X 좌표 조정
        val fontMetrics = g2.fontMetrics
        val textX = when (horizontalAlignment) {
            SwingConstants.LEFT -> padding.left // 왼쪽 정렬일 때 패딩 적용
            SwingConstants.RIGHT -> width - padding.right - fontMetrics.stringWidth(text) // 오른쪽 정렬일 때 패딩 적용
            else -> (width - fontMetrics.stringWidth(text)) / 2  // 가운데 정렬일 때 패딩 무시
        }
        val textY = (height - fontMetrics.height) / 2 + fontMetrics.ascent // 수직 중앙 정렬

        // 텍스트 색상과 글꼴 설정
        g2.color = textColor
        g2.drawString(text, textX, textY)
    }

    override fun getInsets(): Insets {
        // 텍스트가 중앙 정렬일 때는 패딩을 무시
        return if (horizontalAlignment == SwingConstants.CENTER) {
            Insets(0, 0, 0, 0)
        } else {
            padding
        }
    }

    override fun getPreferredSize(): Dimension {
        val originalSize = super.getPreferredSize()
        return if (horizontalAlignment == SwingConstants.CENTER) {
            originalSize // 중앙 정렬일 때는 패딩 추가 안 함
        } else {
            Dimension(
                originalSize.width + padding.left + padding.right,
                originalSize.height + padding.top + padding.bottom
            )
        }
    }
}
