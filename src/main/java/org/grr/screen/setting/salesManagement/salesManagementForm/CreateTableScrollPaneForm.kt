package org.grr.screen.setting.salesManagement.salesManagementForm

import org.grr.screen.setting.salesManagement.ShareData.tableModel
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.CustomScrollBarUI
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class CreateTableScrollPaneForm: JScrollPane() {
    init {
        // 테이블 컬럼 설정
        val columnNames = arrayOf("주문일시", "주문번호", "분류", "상태", "금액", "결제방법")
        tableModel = object : DefaultTableModel(columnNames, 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false // 모든 셀을 편집 불가로 설정
            }
        }

        // 테이블 생성
        val table = JTable(tableModel).apply {
            font = MyFont.Medium(18f)
            rowHeight = 55
            background = Color.WHITE
            foreground = Color.BLACK
            showVerticalLines = true
            showHorizontalLines = true

            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

            // 셀 편집 비활성화
            setDefaultEditor(Any::class.java, null)

            // 클릭 이벤트 처리
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    val row = rowAtPoint(e.point)
                    val column = columnAtPoint(e.point)

                    if (column == 1) {
                        // 주문번호 클릭 시 주문 상세정보 조회
                        val cellValue = getValueAt(row, column)
                        println("주문번호 클릭됨: $cellValue")
                    }
                }
            })

            // 테이블 헤더 커스텀 렌더러
            val headerRenderer = object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    component.font = MyFont.Medium(20f) // 폰트 설정
                    component.background = MyColor.LIGHT_BLUE_14 // 헤더 배경색 설정
                    component.foreground = MyColor.LIGHT_BLUE // 헤더 텍스트 색상
                    horizontalAlignment = SwingConstants.CENTER // 텍스트 가운데 정렬
                    border = BorderFactory.createLineBorder(Color.GRAY) // 헤더 테두리 추가
                    return component
                }
            }

            // 각 컬럼에 대해 가운데 정렬 렌더러 적용
            for (i in 0 until columnModel.columnCount) {
                columnModel.getColumn(i).headerRenderer = headerRenderer
            }

            tableHeader.reorderingAllowed = false
            tableHeader.preferredSize = Dimension(0, 50) // 헤더 높이를 50으로 설정

            val cellRenderer = DefaultTableCellRenderer().apply {
                horizontalAlignment = SwingConstants.CENTER // 텍스트 가운데 정렬
            }

            for (i in 0 until columnModel.columnCount) {
                columnModel.getColumn(i).cellRenderer = cellRenderer // 각 컬럼에 렌더러 적용
            }

            // 셀 경계 설정
            setShowGrid(true) // 셀 테두리 보이게 설정
            gridColor = Color.GRAY // 셀 테두리 색상

            // 열 비율 조정
            val totalWeight = 2 + 4 + 1 + 1 + 2 + 2
            val totalWidth = 1200 // 테이블 전체 너비
            columnModel.getColumn(0).preferredWidth = (totalWidth * 2 / totalWeight)
            columnModel.getColumn(1).preferredWidth = (totalWidth * 4 / totalWeight)
            columnModel.getColumn(2).preferredWidth = (totalWidth * 1 / totalWeight)
            columnModel.getColumn(3).preferredWidth = (totalWidth * 1 / totalWeight)
            columnModel.getColumn(4).preferredWidth = (totalWidth * 2 / totalWeight)
            columnModel.getColumn(5).preferredWidth = (totalWidth * 2 / totalWeight)
        }

        // JScrollPane 설정
        setViewportView(table)
        preferredSize = Dimension(1270, 380)
        border = BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1)

        // 커스텀 스크롤바 적용
        verticalScrollBar.ui = CustomScrollBarUI()
        horizontalScrollBar.ui = CustomScrollBarUI()

        // 스크롤바 크기 조정
        verticalScrollBar.preferredSize = Dimension(10, 0) // 세로 스크롤바 너비
        horizontalScrollBar.preferredSize = Dimension(0, 12) // 가로 스크롤바 높이
    }
}