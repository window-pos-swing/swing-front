package org.example.widgets

import org.example.MyFont
import org.example.style.MyColor
import java.awt.*
import javax.swing.*
import javax.swing.border.AbstractBorder
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

// 선택 버튼 스타일링
class SelectButtonRoundedBorder(private val radius: Int) : LineBorder(Color.GRAY, 2, true) {

    private var isSelected: Boolean = false  // 버튼 선택 여부를 추적하는 변수
    lateinit var button: JButton  // 나중에 생성된 버튼을 저장할 변수

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

    // 선택 상태에 따른 스타일 업데이트 함수
    fun setButtonStyle(selected: Boolean) {
        isSelected = selected
        if (this::button.isInitialized) {  // 버튼이 초기화되었을 때만 작동
            button.background = if (isSelected) MyColor.SELECTED_BACKGROUND_COLOR else MyColor.UNSELECTED_BACKGROUND_COLOR
            button.foreground = if (isSelected) MyColor.SELECTED_TEXT_COLOR else MyColor.GREY600
            button.border = SelectButtonRoundedBorder(radius).apply {
                lineColor = if (isSelected) MyColor.SELECTED_BACKGROUND_COLOR else MyColor.UNSELECTED_BACKGROUND_COLOR
            }
            button.repaint()
        }
    }

    // 둥근 버튼 생성 함수
    fun createRoundedButton(
        text: String,
        selectedColor: Color,
        unselectedColor: Color,
        selectedTextColor: Color,
        unselectedTextColor: Color,
        buttonSize: Dimension
    ): JButton {
        button = object : JButton(text) {
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                // 현재 상태에 따른 배경색 설정
                g2.color = background
                g2.fillRoundRect(0, 0, width, height, radius, radius)

                super.paintComponent(g)
            }

            override fun isContentAreaFilled(): Boolean {
                return false
            }
        }.apply {
            preferredSize = buttonSize
            maximumSize = buttonSize
            minimumSize = buttonSize

            foreground = unselectedTextColor  // 초기 텍스트 색상 설정
            background = unselectedColor  // 초기 배경색 설정
            border = SelectButtonRoundedBorder(radius).apply {
                lineColor = MyColor.UNSELECTED_BACKGROUND_COLOR  // 초기 테두리 색상 설정
            }
            font = MyFont.Bold(24f)
            horizontalAlignment = SwingConstants.CENTER


            isFocusPainted = false // 포커스 테두리 비활성화
            isFocusable = false // 버튼이 포커스를 받을 수 없도록 설정
            setContentAreaFilled(false) // 배경 칠하기 비활성화
            setBorderPainted(false) // 기본 테두리 제거 (필요시 추가)
        }

        return button  // 버튼을 리턴
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

class FillRoundedButton(
    text: String,
    var borderColor: Color,
    var backgroundColor: Color,  // var로 변경하여 값 변경 가능하게 수정
    var textColor: Color,        // var로 변경하여 값 변경 가능하게 수정
    private val borderRadius: Int,
    private val borderWidth: Int,
    private val textAlignment: Int,
    private val padding: Insets,
    private val iconPath: String? = null,  // 아이콘 경로 (없으면 텍스트만)
    private val buttonSize: Dimension? = null,  // 버튼 크기 설정 추가
    private val iconWidth: Int = 20,  // 아이콘 너비
    private val iconHeight: Int = 20,  // 아이콘 높이
    private val customFont: Font? = null  // 커스텀 폰트 설정
) : JButton(text) {

    init {
        isOpaque = false  // 배경을 직접 그리기 위해 투명하게 설정
        setContentAreaFilled(false)  // 기본 JButton의 배경 칠하기 기능 비활성화
        setBorderPainted(false)  // 기본 JButton의 테두리 칠하기 기능 비활성화
        isFocusPainted = false  // 포커스 라인 제거
        horizontalAlignment = SwingConstants.CENTER  // 아이콘과 텍스트의 가로 정렬을 중앙으로 설정
        verticalAlignment = SwingConstants.CENTER  // 아이콘과 텍스트의 세로 정렬을 중앙으로 설정
        foreground = textColor  // 텍스트 색상 설정

        // 아이콘 설정
        if (iconPath != null) {
            icon = ImageIcon(javaClass.getResource(iconPath)).apply {
                // 아이콘 크기 조정
                image = image.getScaledInstance(iconWidth, iconHeight, java.awt.Image.SCALE_SMOOTH)
            }
        }

        // 커스텀 폰트 설정
        if (customFont != null) {
            font = customFont
        }

        // 버튼 크기 설정 (필요 시)
        if (buttonSize != null) {
            preferredSize = buttonSize
            minimumSize = buttonSize
            maximumSize = buttonSize
        }
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경색과 테두리 그리기
        g2.color = backgroundColor
        g2.fillRoundRect(0, 0, width, height, borderRadius, borderRadius)

        g2.color = borderColor
        g2.stroke = BasicStroke(borderWidth.toFloat())
        g2.drawRoundRect(borderWidth / 2, borderWidth / 2, width - borderWidth, height - borderWidth, borderRadius, borderRadius)

        foreground = textColor
        // 기본 아이콘과 텍스트 그리기
        super.paintComponent(g)
    }

    override fun getPreferredSize(): Dimension {
        // 버튼 크기를 고정된 크기로 설정할 수 있게 수정
        return buttonSize ?: super.getPreferredSize()
    }
}

// 둥근 패널을 만들기 위한 커스텀 JPanel 클래스
class RoundedPanel(private val arcWidth: Int, private val arcHeight: Int) : JPanel() {
    init {
        isOpaque = false  // 배경을 투명하게 설정
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 배경색 설정
        g2.color = background

        // 둥근 사각형 그리기
        g2.fillRoundRect(0, 0, width, height, arcWidth, arcHeight)

        super.paintComponent(g)
    }

    override fun paintBorder(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 테두리 그리기
        g2.color = foreground
        g2.drawRoundRect(0, 0, width - 1, height - 1, arcWidth, arcHeight)
    }
}

// RoundedBorder 클래스: 모서리가 둥근 테두리
class RoundedBorder(private val radius: Int) : AbstractBorder() {
    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 빨간색 둥근 테두리 그리기
        g2.color = Color(13, 130, 191)
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
    }

    override fun getBorderInsets(c: Component): Insets {
        return Insets(radius + 5, radius + 5, radius + 5, radius + 5)  // 테두리 여백 설정
    }

    override fun isBorderOpaque(): Boolean {
        return false
    }
}

// RoundedButton 클래스: 모서리가 둥근 빨간색 배경을 가진 버튼
class RoundedButton(text: String) : JButton(text) {
    init {
        isContentAreaFilled = false  // 기본 배경 채우기 제거
        isFocusPainted = false  // 포커스 테두리 제거
        isOpaque = false  // 불투명 설정 제거
        border = RoundedBorder(20)  // 둥근 테두리 적용 (반지름 20)
        preferredSize = Dimension(100, 40)  // 버튼 크기 설정
        font = MyFont.Bold(20f)  // 폰트 설정
        foreground = Color.WHITE  // 텍스트 색상
    }

    // 배경을 그리기 위해 paintComponent 오버라이드
    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 빨간색 배경을 그리기
        g2.color = Color(13, 130, 191)
        g2.fillRoundRect(0, 0, width, height, 20, 20)  // 둥근 모서리 배경

        super.paintComponent(g)  // 텍스트 및 기타 컴포넌트 렌더링
    }
}
