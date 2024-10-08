package org.example.screen.main.main_widget.dialog

import CustomRoundedDialog
import LoadImage
import LoadImage.loadImage
import org.example.MyFont
import org.example.MyFont.Bold
import org.example.style.MyColor
import org.example.widgets.FillRoundedButton
import org.example.widgets.IconRoundBorder
import java.awt.*
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer


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

        // 헤더 패널 추가
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

        // contentPanel 생성 (좌우로 나뉨)
        val contentPanel = JPanel(GridLayout(1, 2, 10, 0)).apply {
            background = Color.WHITE
            border = CompoundBorder(
                LineBorder(MyColor.GREY400, 1), // 기존 테두리 추가
                EmptyBorder(10, 10, 10, 10) // 안쪽 패딩 10씩 추가
            )
            preferredSize = Dimension(940, 182) // 원하는 높이와 너비 설정
            minimumSize = Dimension(940, 182) // 최소 높이와 너비 설정
            maximumSize = Dimension(940, 182) // 최대 높이와 너비 설정
        }

        // leftPanel 생성 (좌우로 나뉨)
        val leftPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            border = CompoundBorder(
                EmptyBorder(0, 0, 0, 0), // 상하 패딩 10씩 추가
                BorderFactory.createMatteBorder(0, 0, 0, 1, MyColor.GREY400), // 오른쪽에만 테두리 추가
            )
        }

        // leftInleftPanel: 레이블 패널 (주소, 연락처, 결제방법)
        val leftInleftPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            preferredSize = Dimension(80, 250)
            add(JLabel("주소").apply { font = MyFont.Bold(20f) })
            add(Box.createVerticalStrut(35))
            add(JLabel("연락처").apply { font = MyFont.Bold(20f) })
            add(Box.createVerticalStrut(30))
            add(JLabel("결제방법").apply { font = MyFont.Bold(20f) })
        }

        // rigthInleftPanel: 실제 값 패널 (주소 값, 연락처 값, 결제방법 값)
        val rigthInleftPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            alignmentX = Component.LEFT_ALIGNMENT
            border = EmptyBorder(0, 20, 0, 20)

            // 주소 값 - JTextArea 사용
            add(JTextArea("서울시 송파구 무슨빌라 어쩌구 저쩌구 고객주소 101호").apply {
                font = MyFont.SemiBold(18f)
                lineWrap = true   // 텍스트가 길어질 경우 줄바꿈 허용
                wrapStyleWord = true  // 단어 단위로 줄바꿈
                isEditable = false
                isOpaque = false
                border = null
                maximumSize = Dimension(400, 50) // 높이를 제한
                alignmentX = Component.LEFT_ALIGNMENT
                alignmentY = Component.TOP_ALIGNMENT
            })

            add(Box.createVerticalStrut(15))

            // 연락처 값 - JLabel 사용
            add(JLabel("050-1234-1234").apply {
                font = MyFont.SemiBold(18f)
                alignmentX = Component.LEFT_ALIGNMENT
                alignmentY = Component.TOP_ALIGNMENT
                maximumSize = Dimension(400, 30)
            })

            add(Box.createVerticalStrut(30))

            // 결제방법 값 - JLabel 사용
            add(JLabel("꼬르륵 앱 결제 완료").apply {
                font = MyFont.SemiBold(18f)
                alignmentX = Component.LEFT_ALIGNMENT
                alignmentY = Component.TOP_ALIGNMENT
                maximumSize = Dimension(400, 30)
            })
        }

        // leftPanel에 leftInleftPanel과 rigthInleftPanel 추가
        leftPanel.add(leftInleftPanel, BorderLayout.WEST)
        leftPanel.add(rigthInleftPanel, BorderLayout.CENTER)

        // rightPanel 생성 (좌우로 나뉨)
        val rightPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
        }

        // leftInrightPanel: 레이블 패널 (접수일시, 예상조리시간, 배달예상시간)
        val leftInrightPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            preferredSize = Dimension(200, 250)
            add(JLabel("접수 일시").apply { font = MyFont.Bold(20f) })
            add(Box.createVerticalStrut(30))
            add(JLabel("예상 조리 시간 (라이더)").apply { font = MyFont.Bold(20f) })
            add(Box.createVerticalStrut(30))
            add(JLabel("배달 예상 시간 (고객)").apply { font = MyFont.Bold(20f) })
        }

        // rightInrightPanel: 실제 값 패널 (접수일시 값, 예상조리시간 값, 배달예상시간 값)
        val rightInrightPanel = JPanel().apply {
            border = EmptyBorder(0, 20, 0, 20)
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            add(JLabel("24-09-09 AM 05:00").apply { font = MyFont.SemiBold(18f) })
            add(Box.createVerticalStrut(35))
            add(JLabel("20분").apply { font = MyFont.SemiBold(18f) })
            add(Box.createVerticalStrut(35))
            add(JLabel("45분").apply { font = MyFont.SemiBold(18f) })
        }

        // rightPanel에 leftInrightPanel과 rightInrightPanel 추가
        rightPanel.add(leftInrightPanel, BorderLayout.WEST)
        rightPanel.add(rightInrightPanel, BorderLayout.CENTER)

        // contentPanel에 leftPanel과 rightPanel 추가
        contentPanel.add(leftPanel)
        contentPanel.add(rightPanel)

        // panel에 contentPanel 추가
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
                LineBorder(MyColor.GREY400, 1),  // 테두리 색상과 두께 설정
                EmptyBorder(10, 10, 10, 10)  // 기본 내부 여백 설정
            )
        }

        // 요청 사항 텍스트
        val requestText = "요청사항입니다. 반드시 들어줘야함... 단무지 빼주세요 피클 많이 주세요 등등 테스트 요청사항 입니다 문앞에 두고가주세요 맵게 해주세요 맛있게 해주세요"

        // truncateTextForMultipleLines 함수를 사용해 텍스트 줄바꿈 처리
        val truncatedText = truncateTextForMultipleLines(requestText, 650, contentPanel)

//        println("truncatedText ${truncatedText}")
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
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),  // 좌우 여백
                LineBorder(MyColor.GREY400, 1)
            )
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

        val customRenderer = object : DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
            ): Component {
                val cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column) as JLabel
                cell.font = MyFont.Bold(22f)  // 셀 폰트 설정
                cell.horizontalAlignment = when (column) {
                    0 -> JLabel.LEFT  // 첫 번째 열(메뉴) 왼쪽 정렬
                    1 -> JLabel.CENTER  // 두 번째 열(수량) 가운데 정렬
                    2 -> JLabel.RIGHT  // 세 번째 열(금액) 오른쪽 정렬
                    else -> JLabel.LEFT
                }

                // 메뉴와 금액 열에 여백 추가
                if (column == 0) {
                    cell.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)  // 메뉴 열에 왼쪽 여백 추가
                } else if (column == 2) {
                    cell.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)  // 금액 열에 오른쪽 여백 추가
                } else {
                    cell.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 나머지 셀에는 여백 없음
                }

                return cell
            }
        }

// JTable에서 특정 행에만 구분선을 추가
// 테이블 생성
        val table = object : JTable(tableData, columnNames) {
            override fun prepareRenderer(renderer: TableCellRenderer?, row: Int, column: Int): Component {
                val cell = super.prepareRenderer(renderer, row, column) as JComponent
                // 특정 행에만 구분선을 추가하는 로직
                if (row == 3 || row == 4) {  // 에스프레소와 배달비 행에 구분선 추가
                    cell.border = BorderFactory.createMatteBorder(0, 0, 1, 0, MyColor.GREY400)  // 하단 구분선
                } else {
                    cell.border = BorderFactory.createEmptyBorder()  // 나머지 행에는 구분선 없음
                }
                return cell
            }

            // 테이블이 수정되지 않도록 설정
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false  // 모든 셀에 대해 수정 불가
            }
        }.apply {
            gridColor = MyColor.GREY400  // 기본적으로 가로줄을 표시
            setShowVerticalLines(false)  // 세로 줄 제거
            setShowHorizontalLines(false)  // 가로 구분선 표시
            rowHeight = 50  // 각 행의 높이를 50 픽셀로 설정 (원하는 높이로 설정 가능)
            tableHeader.reorderingAllowed = false  // 열 이동 불가
        }


        // 각 열에 커스텀 렌더러 적용
        for (i in 0 until table.columnCount) {
            table.columnModel.getColumn(i).cellRenderer = customRenderer
        }

        // 헤더의 폰트와 정렬을 MyFont.Medium으로 설정, 각 열에 맞는 정렬 설정
        val headerRenderer = object : DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
            ): Component {
                val headerCell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column) as JLabel
                headerCell.font = MyFont.Medium(16f)  // 헤더에 MyFont.Medium 적용
                headerCell.horizontalAlignment = when (column) {
                    0 -> JLabel.LEFT    // 메뉴 열은 왼쪽 정렬
                    1 -> JLabel.CENTER  // 수량 열은 가운데 정렬
                    2 -> JLabel.RIGHT   // 금액 열은 오른쪽 정렬
                    else -> JLabel.CENTER
                }

                // 헤더의 메뉴에 왼쪽 여백 추가, 금액에 오른쪽 여백 추가
                if (column == 0) {
                    headerCell.border = BorderFactory.createEmptyBorder(0, 20, 0, 0)  // 메뉴 헤더 왼쪽 여백 추가
                } else if (column == 2) {
                    headerCell.border = BorderFactory.createEmptyBorder(0, 0, 0, 20)  // 금액 헤더 오른쪽 여백 추가
                }

                return headerCell
            }
        }

        // 헤더에 커스텀 렌더러 적용
        for (i in 0 until table.columnCount) {
            table.tableHeader.columnModel.getColumn(i).headerRenderer = headerRenderer
        }

        // 테이블 높이 설정: 테이블의 모든 행과 헤더를 포함하는 높이로 설정
        val tableHeight = table.rowHeight * table.rowCount + table.tableHeader.preferredSize.height
        table.preferredSize = Dimension(900, tableHeight)

        // 헤더 아래에 구분선을 추가
        table.tableHeader.border = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 10),  // 좌우 여백 설정 (상, 좌, 하, 우)
            BorderFactory.createMatteBorder(0, 0, 1, 0, MyColor.GREY400)  // 하단에만 구분선
        )

        // 테이블을 감싸는 tableContainer 패널을 사용하여 좌우 여백 적용
        val tableContainer = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10)  // 좌우에 10픽셀 여백 적용
            add(table.tableHeader, BorderLayout.NORTH)  // 테이블 헤더 추가
            add(table, BorderLayout.CENTER)  // 테이블 본문 추가
        }

        // 총합 행에 텍스트 스타일 적용
        val totalRow = table.getModel().getValueAt(5, 0) as String
        val totalAmount = table.getModel().getValueAt(5, 2) as String

        // 총합 텍스트를 빨간색으로 처리
        table.setValueAt("<html><b><font color='red'>$totalRow</font></b></html>", 5, 0)
        table.setValueAt("<html><b><font color='red'>$totalAmount</font></b></html>", 5, 2)

        // 테이블을 바로 추가 (스크롤 패널 없이)
        contentPanel.add(tableContainer)

        panel.add(contentPanel, BorderLayout.CENTER)

        return panel
    }


    // 레이블과 텍스트 생성 함수
    private fun createLabeledField(label: String, value: String, wide_section: Boolean = true): JPanel {
        val labelPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT)
            background = Color.WHITE
            add(JLabel(label).apply {
                font = MyFont.Bold(20f)
                foreground = Color.BLACK
            })
        }

        val valuePanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT)
            background = Color.WHITE
            add(JLabel(value).apply {
                font = MyFont.SemiBold(18f)
                foreground = Color.BLACK
            })
        }

        return JPanel(BorderLayout()).apply {
            background = Color.WHITE
            add(labelPanel, BorderLayout.WEST)
            add(valuePanel, BorderLayout.CENTER)
        }
    }


    override fun addButtonsToTitleBar(panel: JPanel) {
        // 먼저 인쇄 버튼을 추가하고 그다음에 닫기 버튼 추가
        val printButton = FillRoundedButton(
            text = "인쇄하기",
            borderColor = MyColor.GREY100,
            backgroundColor = MyColor.GREY100,
            textColor = MyColor.GREY600,
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
