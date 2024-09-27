package org.example.screen.main.main_widget.dialog

import CustomRoundedDialog
import org.example.MyFont
import org.example.style.MyColor
import org.example.widgets.FillRoundedButton
import org.example.widgets.IconRoundBorder
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

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
        val requestInfoPanel = createRequestInfoPanel().apply {
            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(requestInfoPanel)

        // 주문 정보 패널 추가
        val orderInfoPanel = createOrderInfoPanel().apply {
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
                font = MyFont.Bold(20f)
                foreground = Color.BLACK
            })
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // 세부 배달 정보 패널
        val contentPanel = JPanel(GridLayout(2, 2, 10, 10)).apply {
            background = Color.WHITE
            border = LineBorder(MyColor.BRIGHTER_GREY, 1)  // 여기만 테두리 추가
            add(createLabeledField("주소", "서울시 송파구 무슨빌라 어쩌구 저쩌구 고객주소 101호"))
            add(createLabeledField("접수 일시", "24-09-09 AM 05:00"))
            add(createLabeledField("연락처", "050-1234-1234"))
            add(createLabeledField("예상 조리 시간 (라이더)", "20분"))
            add(createLabeledField("결제 방법", "꼬르륵 앱 결제 완료"))
            add(createLabeledField("배달 예상 시간 (고객)", "45분"))
        }
        panel.add(contentPanel, BorderLayout.CENTER)

        return panel
    }

    // 요청 사항 패널 생성
    private fun createRequestInfoPanel(): JPanel {
        val panel = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
        }

        // 아이콘 및 제목 추가
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(JLabel(LoadImage.loadImage("/pin_icon.png", 20, 20)))
            add(JLabel("요청 사항").apply {
                font = MyFont.Bold(20f)
                foreground = Color.BLACK
            })
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // 세부 요청 사항 패널
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = LineBorder(MyColor.BRIGHTER_GREY, 1)  // 여기만 테두리 추가
            add(JLabel("요청사항입니다. 반드시 들어줘야함. 단무지 빼주세요 피클 많이 주세요 등등").apply {
                font = MyFont.Regular(16f)
                foreground = Color.BLACK
            })
        }
        panel.add(contentPanel, BorderLayout.CENTER)

        // 추가 옵션 버튼 (비대면 배달, 수저/포크)
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
            background = Color.WHITE
        }
        val noContactButton = JButton("비대면 배달").apply {
            background = MyColor.DARK_RED
            foreground = Color.WHITE
        }
        val spoonForkButton = JButton("수저/포크 0").apply {
            background = MyColor.Yellow
            foreground = Color.WHITE
        }
        buttonPanel.add(noContactButton)
        buttonPanel.add(spoonForkButton)
        panel.add(buttonPanel, BorderLayout.SOUTH)

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
                font = MyFont.Bold(20f)
                foreground = Color.BLACK
            })
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // 세부 주문 정보 패널 (메뉴 테이블, 배달비, 총합)
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = EmptyBorder(10, 10, 10, 10)  // 여백 조정

            // 메뉴 테이블 추가
            val tableData = arrayOf(
                arrayOf("아메리카노", "2", "8000원"),
                arrayOf("사이즈업", "", "1000원"),
                arrayOf("샷추가", "", "1000원"),
                arrayOf("에스프레소", "1", "4000원"),
                arrayOf("배달비", "", "1000원"),
                arrayOf("총합", "3", "15000원")
            )
            val columnNames = arrayOf("메뉴", "수량", "금액")

            val table = JTable(tableData, columnNames).apply {
                gridColor = Color.GRAY
                setEnabled(false)
                autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS  // 자동 크기 조정
            }

            val scrollPane = JScrollPane(table).apply {
                preferredSize = Dimension(900, 150)  // 크기 조정
                horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            }

            add(scrollPane)

            // 총합 정보
            add(JLabel("총합: 15000원").apply {
                font = MyFont.Bold(16f)
                foreground = Color.RED
                border = EmptyBorder(10, 10, 10, 10)
            })
        }
        panel.add(contentPanel, BorderLayout.CENTER)

        return panel
    }


    // 레이블과 텍스트 생성 함수
    private fun createLabeledField(label: String, value: String): JPanel {
        return JPanel().apply {
            layout = GridLayout(1, 2)
            border = EmptyBorder(5, 5, 5, 5)
            background = Color.WHITE
            add(JLabel(label).apply {
                font = MyFont.Bold(16f)
                foreground = Color.BLACK
            })
            add(JLabel(value).apply {
                font = MyFont.Regular(16f)
                foreground = Color.BLACK
            })
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
}
