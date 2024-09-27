package org.example.screen.main.main_widget.dialog

import CustomRoundedDialog
import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.FillRoundedButton
import org.example.widgets.IconRoundBorder
import java.awt.*
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.text.View

class OrderDetailDialog(
    parent: JFrame,
    title: String,
    callback: ((Boolean) -> Unit)? = null
) : CustomRoundedDialog(parent, title, 1000, 833, callback) {

    init {
        // 메인 패널을 생성하고, 내용을 넣음
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 수직 레이아웃
            border = EmptyBorder(10, 30, 30, 30)
            background = Color.WHITE
        }

        // 배달 정보 패널 추가
        val deliveryInfoPanel = createDeliveryInfoPanel().apply {
            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(deliveryInfoPanel)

        // 요청 사항 패널 추가
        val storeRequestInfoPanel = createRequestInfoPanel("가게").apply {
            border = EmptyBorder(10, 0, 0, 0)
            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(storeRequestInfoPanel)

        val deliveryRequestInfoPanel = createRequestInfoPanel("배달").apply {
            border = EmptyBorder(10, 0, 0, 0)
            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(deliveryRequestInfoPanel)

        // 주문 정보 패널 추가
        val orderInfoPanel = createOrderInfoPanel().apply {
            border = EmptyBorder(10, 0, 0, 0)
            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(orderInfoPanel)

        // 테스트용 임시 패널 추가 (내용이 많아져서 스크롤이 발생하게 만듦)
//        for (i in 1..10) {
//            mainPanel.add(JPanel().apply {
//                preferredSize = Dimension(900, 200)  // 큰 크기의 임시 패널
//                background = Color.LIGHT_GRAY
//                add(JLabel("테스트 패널 $i").apply {
//                    font = MyFont.Bold(20f)
//                })
//            })
//        }

        // 메인 패널을 스크롤 가능한 패널로 설정
        val scrollPane = JScrollPane(mainPanel).apply {
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            border = null  // 불필요한 테두리 없애기
        }

        // 스크롤 가능한 패널을 다이얼로그에 추가
        add(scrollPane)

        // 다이얼로그 크기 설정
        setSize(1000, 833)
        setLocationRelativeTo(parent)
        isVisible = true
    }


    // 배달 정보 패널 생성
    private fun createDeliveryInfoPanel(): JPanel {
        val panel = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
        }

        // 아이콘 및 제목 추가
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(JLabel(LoadImage.loadImage("/pin_icon.png", 20, 20)))
            add(JLabel("배달 정보").apply {
                font = MyFont.Bold(24f)
                foreground = Color.BLACK
                border = EmptyBorder(20, 0, 0, 0)
            })
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // 세부 배달 정보 패널
        val contentPanel = JPanel(GridLayout(3, 2, 10, 0)).apply {
            background = Color.WHITE
            border = LineBorder(MyColor.BORDER_GRAY, 1)  // 여기만 테두리 추가
            add(createLabeledField("주소", "서울시 송파구 무슨빌라 어쩌구 저쩌구 고객주소 101호 테스트 길이 입니다",false))
            add(createLabeledField("접수 일시", "24-09-09 AM 05:00"))
            add(createLabeledField("연락처", "050-1234-1234",false))
            add(createLabeledField("예상 조리 시간 (라이더)", "20분"))
            add(createLabeledField("결제 방법", "꼬르륵 앱 결제 완료",false))
            add(createLabeledField("배달 예상 시간 (고객)", "45분"))
        }

        panel.preferredSize = Dimension(940, 250)
        panel.minimumSize = Dimension(940, 250)  // 최소 높이를 설정
        panel.maximumSize = Dimension(940, 250)

        panel.add(contentPanel, BorderLayout.CENTER)

        return panel
    }

    private fun createRequestInfoPanel(type: String): JPanel {
        val panel = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
        }

        // 아이콘 및 제목 추가
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(JLabel(LoadImage.loadImage("/pin_icon.png", 20, 20)))
            add(JLabel("$type 요청 사항").apply {
                font = MyFont.Bold(24f)
                foreground = Color.BLACK
                border = EmptyBorder(20, 0, 0, 0)
            })
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // 세부 요청 사항 패널
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = CompoundBorder(
                LineBorder(MyColor.BORDER_GRAY, 1),  // 테두리 색상과 두께 설정
                EmptyBorder(10, 10, 10, 10)  // 기본 내부 여백 설정
            )
        }

        // 요청 사항 텍스트
        val requestText = "요청사항입니다. 반드시 들어줘야함... 단무지 빼주세요 피클 많이 주세요 등등 테스트 요청사항 입니다 문앞에 두고가주세요 맵게 해주세요 맛있게 해주세요"

        // truncateTextForMultipleLines 함수를 사용해 텍스트 줄바꿈 처리
        val truncatedText = truncateTextForMultipleLines(requestText, 650, contentPanel)

        println("truncatedText ${truncatedText}")
        // HTML을 사용한 JLabel 생성
        val requestLabel = JLabel(truncatedText).apply {
            font = MyFont.SemiBold(18f)
            foreground = Color.BLACK
            background = Color.WHITE
            isOpaque = false  // 배경 투명 설정
            border = null  // 불필요한 경계 제거
            // 크기를 텍스트 길이에 맞게 자동 조정
            preferredSize = Dimension(900, preferredSize.height)
            minimumSize = Dimension(900, minimumSize.height)
        }

        contentPanel.add(requestLabel)
        panel.add(contentPanel, BorderLayout.CENTER)

        return panel
    }
    // 주문 정보 패널 생성
    private fun createOrderInfoPanel(): JPanel {
        val panel = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
            border = LineBorder(MyColor.BRIGHTER_GREY, 1)  // 테두리 추가
        }

        // 아이콘 및 제목 추가
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(JLabel(LoadImage.loadImage("/pin_icon.png", 20, 20)))
            add(JLabel("주문 정보").apply {
                font = MyFont.Bold(24f)
                foreground = Color.BLACK
                border = EmptyBorder(20, 0, 0, 0)
            })
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // 세부 주문 정보 패널 (메뉴 테이블, 배달비, 총합)
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = EmptyBorder(10, 10, 10, 10)  // 여백 조정
        }

        // 메뉴 테이블 데이터 설정
        val tableData = arrayOf(
            arrayOf("아메리카노", "2", "8000원"),
            arrayOf("  • 사이즈업", "", "1000원"),
            arrayOf("  • 샷추가", "", "1000원"),
            arrayOf("에스프레소", "1", "4000원"),
            arrayOf("배달비", "", "1000원"),
            arrayOf("총합", "3", "15000원")
        )

        // 테이블 컬럼 설정
        val columnNames = arrayOf("메뉴", "수량", "금액")

        // 테이블 생성
        val table = JTable(tableData, columnNames).apply {
            gridColor = Color.BLACK  // 테두리 색상
            setEnabled(false)  // 수정 불가
            autoResizeMode = JTable.AUTO_RESIZE_OFF  // 자동 크기 조정 해제
            columnModel.getColumn(0).preferredWidth = 500  // 메뉴 열 너비
            columnModel.getColumn(1).preferredWidth = 100  // 수량 열 너비
            columnModel.getColumn(2).preferredWidth = 100  // 금액 열 너비
        }

        // 총합 행에 텍스트 스타일 적용
        val totalRow = table.getModel().getValueAt(5, 0) as String
        val totalAmount = table.getModel().getValueAt(5, 2) as String

        // 총합 텍스트를 빨간색으로 처리
        table.setValueAt("<html><b><font color='red'>$totalRow</font></b></html>", 5, 0)
        table.setValueAt("<html><b><font color='red'>$totalAmount</font></b></html>", 5, 2)

        // 테이블을 스크롤 가능하도록 설정
        val scrollPane = JScrollPane(table).apply {
            preferredSize = Dimension(900, 150)  // 크기 조정
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        }

        // 테이블 추가
        contentPanel.add(scrollPane)

        // 총합 정보 추가
        contentPanel.add(JLabel("<html><b><font color='red'>총합: 15000원</font></b></html>").apply {
            font = MyFont.Bold(20f)
            foreground = Color.RED
            border = EmptyBorder(10, 10, 10, 10)
        })

        panel.add(contentPanel, BorderLayout.CENTER)

        return panel
    }



    // 레이블과 텍스트 생성 함수
    private fun createLabeledField(label: String, value: String, wide_section: Boolean = true): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)  // 수평 레이아웃 사용
            background = Color.WHITE
            border = EmptyBorder(20, 20, 0, 20)

            // 레이블 컴포넌트 설정
            val labelComponent = JLabel(label).apply {
                font = MyFont.Bold(20f)
                foreground = Color.BLACK
                if (!wide_section) {
                    preferredSize = Dimension(100, 20)
                    maximumSize = Dimension(100, 20)
                    minimumSize = Dimension(100, 20)
                } else {
                    preferredSize = Dimension(200, 20)
                    maximumSize = Dimension(200, 20)
                    minimumSize = Dimension(200, 20)
                }
                alignmentY = Component.TOP_ALIGNMENT // 수직 정렬을 맞추기 위해 상단 정렬
            }

            // 값 컴포넌트를 JTextArea로 변경하여 줄바꿈 허용
            val valueComponent = JTextArea(value).apply {
                font = MyFont.SemiBold(18f)
                foreground = Color.BLACK
                background = Color.WHITE
                lineWrap = true  // 텍스트가 길어질 경우 줄바꿈 허용
                wrapStyleWord = true
                isEditable = false  // 수정 불가능하게 설정
                isOpaque = false  // 배경을 투명하게 설정
                border = null  // 불필요한 경계 제거
                preferredSize = Dimension(250, 25)  // 원하는 크기로 지정
                minimumSize = Dimension(250, 25)  // 최소 높이를 레이블과 동일하게 설정
                alignmentY = Component.TOP_ALIGNMENT // 수직 정렬을 맞추기 위해 상단 정렬
            }

            // 컴포넌트 추가
            add(labelComponent)
            add(Box.createHorizontalStrut(10))  // 레이블과 값 사이에 여백 추가
            add(valueComponent)
        }
    }


    override fun addButtonsToTitleBar(panel: JPanel) {
        // 먼저 인쇄 버튼을 추가하고 그다음에 닫기 버튼 추가
        val printButton = FillRoundedButton(
            text = "인쇄하기",
            borderColor = MyColor.BRIGHTER_GREY,
            backgroundColor = MyColor.BRIGHTER_GREY,
            textColor = MyColor.UNSELECTED_TEXT_COLOR,
            borderRadius = 20,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(0, 16, 0, 16),
            buttonSize = Dimension(136, 45),
            customFont = MyFont.Bold(20f)
        )

        val closeButton = IconRoundBorder.createRoundedButton("/close_red_icon.png").apply {
            preferredSize = Dimension(45, 45)
            maximumSize = Dimension(45, 45)
            minimumSize = Dimension(45, 45)

            addActionListener {
                dispose()
            }
        }

        // 버튼 컨테이너에 인쇄 버튼과 닫기 버튼을 순서대로 추가
        val buttonContainer = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = EmptyBorder(0, 0, 10, 0)
            isOpaque = false
            add(printButton)
            add(Box.createHorizontalStrut(15))
            add(closeButton)
        }

        // 버튼 컨테이너를 타이틀 패널의 오른쪽에 추가
        panel.add(buttonContainer, BorderLayout.EAST)

        // 타이틀 여백을 조정
        updateTitleBorder(170)
    }

    //일정 텍스트 길이 넘어가면 줄바꿈 추가해주는 함수
    private fun truncateTextForMultipleLines(text: String, maxWidth: Int, component: JComponent): String {
        val fontMetrics: FontMetrics = component.getFontMetrics(component.font)
        val words = text.split(" ")

        var currentLine = ""
        var finalText = "<html>"

        for (word in words) {
            // 현재 줄에 단어를 추가했을 때 너비가 maxWidth를 넘으면 줄바꿈 처리
            if (fontMetrics.stringWidth(currentLine + " " + word) < maxWidth) {
                currentLine += if (currentLine.isEmpty()) word else " $word"
            } else {
                // 줄이 넘치면 새로운 줄로 이동하고 <br> 추가
                finalText += "$currentLine<br>"
                currentLine = word  // 새로운 줄에 단어 시작
            }
        }

        // 마지막 줄 추가
        finalText += currentLine + "</html>"
        return finalText
    }

}
