package org.grr.screen.setting.deliveryIntegration.deliveryIntegration_modal

import CustomRoundedDialog
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.CustomScrollBarUI
import org.grr.widgets.RoundedPanel
import java.awt.*
import javax.swing.*
import javax.swing.ListSelectionModel.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class DeliveryIntegrationModalDialog(
    parent: JFrame,
    title: String,
    callback: ((String) -> Unit)? = null
) : CustomRoundedDialog(parent, title, 650, 750, null, callback) {

    private val agencyList = listOf("젠들리", "부릉", "스파이더", "생각대로", "만나플러스", "더가치플래닛", "모아라인", "바로고", "딜버")
    private var selectedAgency: String? = null
    init {
        setSize(650, 720)
        setLocationRelativeTo(parent)
        background = Color.WHITE

        // 선택된 배달대행사 표시 패널
        val selectedAgencyPanel = JLabel("선택된 대행사가 없습니다").apply {
            font = MyFont.Bold(22f)
            foreground = MyColor.LIGHT_BLUE
            horizontalAlignment = SwingConstants.CENTER
            border = BorderFactory.createEmptyBorder(10, 0, 20, 0)
        }

        // 테이블 데이터 설정
        val rowData = agencyList.map { arrayOf(it) }.toTypedArray()
        val tableModel = object : DefaultTableModel(rowData, arrayOf("")) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false // 테이블 셀 편집 불가
            }
        }

        val table = JTable(tableModel).apply {
            font = MyFont.Bold(24f) // 테이블 폰트 설정
            rowHeight = 72 // 행 높이 설정
            gridColor = MyColor.LIGHT_GREY // 테이블 그리드 색상
            background = MyColor.LIGHT_GREY // 배경색
            foreground = Color.BLACK // 텍스트 색상
            selectionBackground = MyColor.LIGHT_BLUE // 선택된 행 배경색
            selectionForeground = Color.WHITE // 선택된 행 텍스트 색상
            tableHeader = null // 테이블 헤더 숨기기

            // 단일 선택 모드 설정
            setSelectionMode(SINGLE_SELECTION)

            // 커스텀 셀 렌더러 정의
            val cellRenderer = object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable?,
                    value: Any?,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    if (component is JLabel) {
                        component.horizontalAlignment = SwingConstants.LEFT // 왼쪽 정렬
                        component.border = BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, MyColor.DARK_GREY), // 경계선
                            BorderFactory.createEmptyBorder(0, 20, 0, 0) // 내부 여백 (왼쪽 20px)
                        )
                        if (isSelected) {
                            component.background = MyColor.LIGHT_BLUE // 선택된 행 배경색
                            component.foreground = Color.WHITE // 선택된 행 텍스트 색상
                        } else {
                            component.background = MyColor.LIGHT_GREY // 비선택 행 배경색
                            component.foreground = Color.BLACK // 비선택 행 텍스트 색상
                        }
                    }
                    return component
                }
            }

            // 각 컬럼에 렌더러 적용
            for (i in 0 until columnModel.columnCount) {
                columnModel.getColumn(i).cellRenderer = cellRenderer
            }

            // 선택 이벤트
            selectionModel.addListSelectionListener {
                val selectedRow = selectedRow
                if (selectedRow != -1) {
                    selectedAgency = getValueAt(selectedRow, 0).toString()
                    selectedAgencyPanel.text = selectedAgency // 선택된 대행사 표시
                }
            }
        }

        // 테이블 스크롤 가능하도록 JScrollPane 생성
        val scrollPane = JScrollPane(table).apply {
            preferredSize = Dimension(580, 440) // 스크롤 영역 크기 설정
            minimumSize = Dimension(580, 440) // 최소 크기
            maximumSize = Dimension(580, 440) // 최대 크기
            border = BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1) // 테두리 설정

            // 커스텀 스크롤바 적용
            verticalScrollBar.ui = CustomScrollBarUI()
            horizontalScrollBar.ui = CustomScrollBarUI()

            // 스크롤바 크기 조정
            verticalScrollBar.preferredSize = Dimension(10, 0) // 세로 스크롤바 너비
            horizontalScrollBar.preferredSize = Dimension(0, 12) // 가로 스크롤바 높이
        }

        // 테이블을 고정 크기로 감싸는 패널
        val tableContainer = JPanel().apply {
            layout = BorderLayout() // 테이블을 고정 크기로 추가
            background = Color.WHITE
            preferredSize = Dimension(580, 440)
            minimumSize = Dimension(580, 440)
            maximumSize = Dimension(580, 440)
            add(selectedAgencyPanel, BorderLayout.NORTH)
            add(scrollPane, BorderLayout.CENTER)
        }

        // 확인 버튼
        val confirmButton = JButton("확인").apply {
            font = MyFont.Bold(18f)
            preferredSize = Dimension(300, 62)
            background = Color.WHITE
            foreground = Color.RED
            border = BorderFactory.createLineBorder(Color.RED, 1)
            isFocusPainted = false

            addActionListener {
                val selectedRow = table.selectedRow // 선택된 행의 인덱스
                if (selectedRow != -1) {
                    selectedAgency = table.getValueAt(selectedRow, 0).toString() // 선택된 배달대행사
                    val parentFrame = SwingUtilities.getWindowAncestor(this@DeliveryIntegrationModalDialog) as? JFrame
                    if (parentFrame != null) {
                        dispose()
                        // 다이얼로그 열기
                        DeliveryIntegrationConnectModalDialog(parentFrame, "배달대행사 연동", selectedAgency!!) { agencyName ->
                            if (!agencyName.isNullOrEmpty()) {
                                callback?.invoke(agencyName) // 부모로 성공 상태 전달
                                println("배달대행사 연동 완료: $agencyName")
                            } else {
                                println("배달대행사 연동 취소됨.")
                            }
                        }.isVisible = true
                    }
                } else {
                    JOptionPane.showMessageDialog(
                        this@DeliveryIntegrationModalDialog,
                        "배달대행사를 선택해주세요.",
                        "선택 필요",
                        JOptionPane.WARNING_MESSAGE
                    )
                }
            }
        }

        // 하단 버튼 패널
        val buttonPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER)
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(10, 0, 0, 0) // 버튼 상단 간격
            add(confirmButton)
        }

        // 둥근 패널 구성
        val roundedPanel = RoundedPanel(10, 10).apply {
            layout = BorderLayout()
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
            add(tableContainer, BorderLayout.CENTER) // 테이블 추가
            add(buttonPanel, BorderLayout.SOUTH) // 버튼 추가
        }

        // 다이얼로그에 둥근 패널 추가
        contentPane.add(roundedPanel, BorderLayout.CENTER)
    }
}