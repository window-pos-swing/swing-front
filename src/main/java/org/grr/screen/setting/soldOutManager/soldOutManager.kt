import org.grr.model.MenuCategory
import org.grr.model.MenuData
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class SoldOutManagementDialog(parent: JFrame) : CustomRoundedDialog(parent, "품절 관리", 1350, 800) {
    // 독립적인 복사본 데이터를 저장
    private val copiedMenuCategories: MutableList<MenuCategory> = mutableListOf()

    private val tableModel: DefaultTableModel
    private val menuTable: JTable
    private val categoryComboBox: JComboBox<String>
    private var isFilteringSoldOut = false // 품절 필터링 상태를 저장

    init {
        // 메인 패널 설정
        val mainPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }
        add(mainPanel, BorderLayout.CENTER)

        // 상단 필터 패널
        val filterPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 20, 0)
            background = Color.WHITE

            // 메뉴 그룹 콤보박스
            add(JLabel("메뉴 그룹:").apply {
                font = MyFont.Bold(20f)
            })
            val categories = MenuData.createSampleData() // 샘플 데이터 생성

            // 원본 데이터를 깊은 복사하여 복사본 생성
            copiedMenuCategories.addAll(categories.map { category ->
                category.copy(menuList = category.menuList.map { it.copy() })
            })

            // DefaultComboBoxModel 생성 및 카테고리 추가
            val categoryModel = DefaultComboBoxModel<String>().apply {
                addElement("전체") // 기본값
                categories.forEach { category ->
                    addElement(category.categoryName) // 각 카테고리 이름 추가
                }
            }

            // 카테고리 이름 콤보박스
            categoryComboBox = RoundedComboBox(categoryModel).apply {
                preferredSize = Dimension(430, 55)
                maximumSize = Dimension(430, 55)
                minimumSize = Dimension(430, 55)
                font = MyFont.Bold(22f)
                addActionListener {
                    if (isFilteringSoldOut) {
                        filterSoldOut(copiedMenuCategories) // 품절 필터링
                    } else {
                        updateTable(copiedMenuCategories) // 전체 상품 보기
                    }
                }
            }

            add(categoryComboBox)
            add(Box.createRigidArea(Dimension(20, 0)))

            // 품절 상품 보기 버튼
            val soldOutButton = FillRoundedButton(
                text = "품절 상품 보기",
                borderColor = MyColor.LIGHT_GREY2,
                backgroundColor = MyColor.LIGHT_GREY2,
                textColor = Color.WHITE,
                borderRadius = 13,
                borderWidth = 1,
                textAlignment = SwingConstants.CENTER,
                padding = Insets(0, 0, 0, 0),
                buttonSize = Dimension(200, 55),
                customFont = MyFont.Bold(22f)
            ).apply {
                addActionListener {
                    if (!isFilteringSoldOut) {
                        // 품절 상품 필터링 동작
                        filterSoldOut(copiedMenuCategories)
                        backgroundColor = MyColor.LIGHT_BLUE
                        borderColor = MyColor.LIGHT_BLUE
                    } else {
                        // 모든 상품 보기 동작
                        updateTable(copiedMenuCategories)
                        backgroundColor = MyColor.LIGHT_GREY2
                        borderColor = MyColor.LIGHT_GREY2
                    }
                    isFilteringSoldOut = !isFilteringSoldOut // 상태 변경
                    repaint()
                }
            }
            add(soldOutButton)
        }
        mainPanel.add(filterPanel, BorderLayout.NORTH)

        // [테이블] 품절 관리, 메뉴 그룹, 메뉴 이름
        tableModel = object : DefaultTableModel(arrayOf("품절 관리", "메뉴 그룹", "메뉴 이름"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return column == 0  // "품절 관리" 열만 편집 가능
            }
        }
        menuTable = JTable(tableModel).apply {
            rowHeight = 60
            font = MyFont.SemiBold(20f)
            columnModel.getColumn(1).cellEditor = null // "메뉴 그룹"
            columnModel.getColumn(2).cellEditor = null // "메뉴 이름"
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
                    component.font = MyFont.Bold(20f) // 폰트 설정
                    component.background = MyColor.LIGHT_BLUE_14 // 헤더 배경색 설정
                    component.foreground = MyColor.LIGHT_BLUE // 헤더 텍스트 색상
                    horizontalAlignment = JLabel.CENTER // 텍스트 가운데 정렬
                    border = BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1) // 헤더 테두리 추가
                    return component
                }
            }

            // 헤더 렌더러 적용
            for (i in 0 until columnModel.columnCount) {
                columnModel.getColumn(i).headerRenderer = headerRenderer
            }

            tableHeader.reorderingAllowed = false
            tableHeader.preferredSize = Dimension(0, 50) // 헤더 높이를 50으로 설정

            // 셀 경계 설정
            setShowGrid(true) // 셀 테두리 보이게 설정
            gridColor = MyColor.LIGHT_GREY // 셀 테두리 색상

            // 열 비율 조정 (3:4:10)
            val totalWeight = 3 + 4 + 10
            val totalWidth = 1350 // 테이블 전체 너비 (스크롤 없이 표시되도록 설정)
            columnModel.getColumn(0).preferredWidth = (totalWidth * 3 / totalWeight).toInt() // 품절 관리
            columnModel.getColumn(1).preferredWidth = (totalWidth * 4 / totalWeight).toInt() // 메뉴 그룹
            columnModel.getColumn(2).preferredWidth = (totalWidth * 10 / totalWeight).toInt() // 메뉴 이름

            // "품절 관리" 열에 버튼 렌더러와 에디터 추가
            val buttonRendererEditor = object : AbstractCellEditor(), TableCellRenderer, TableCellEditor {
                private val button = FillRoundedButton(
                    text = "",
                    borderColor = Color.BLACK,
                    backgroundColor = Color.LIGHT_GRAY,
                    textColor = Color.WHITE,
                    borderRadius = 6,
                    borderWidth = 1,
                    textAlignment = SwingConstants.CENTER,
                    padding = Insets(0, 0, 0, 0),
                    buttonSize = Dimension(120, 40),
                    customFont = MyFont.Bold(22f)
                )

                private var currentValue: Boolean = false // 현재 셀 상태를 저장
                private var currentRow: Int = -1 // 현재 행 번호

                override fun getTableCellRendererComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    currentValue = value as Boolean
                    updateButtonAppearance()
                    return button
                }

                override fun getTableCellEditorComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    currentValue = value as Boolean
                    currentRow = row // 현재 행 저장

                    // 기존 ActionListener 제거
                    for (listener in button.actionListeners) {
                        button.removeActionListener(listener)
                    }

                    // 버튼 클릭 시 상태 변경 및 복사본 데이터 업데이트
                    button.addActionListener {
                        // 현재 상태 출력 (Before)
                        println("Before update: Row $currentRow, Value: $currentValue")

                        // 테이블에서 현재 메뉴 정보를 가져옴
                        val categoryName = table.getValueAt(currentRow, 1) as String
                        val menuName = table.getValueAt(currentRow, 2) as String

                        val category = copiedMenuCategories.find { it.categoryName == categoryName }
                        val menu = category?.menuList?.find { it.menuName == menuName }

                        if (menu != null) {
                            // Before 상태 출력
                            println("Before  : $menu")

                            // 버튼 상태 반전에 따라 isSoldOut 값 설정
                            menu.isSoldOut = !menu.isSoldOut
                            currentValue = menu.isSoldOut
                            // After 상태 출력
                            println("After   : $menu")

                            // 테이블 모델 값 업데이트
                            val model = table.model as DefaultTableModel
                            model.setValueAt(menu.isSoldOut, currentRow, 0)

                            // 테이블 셀 강제 렌더링
                            model.fireTableCellUpdated(currentRow, 0)

                            // 테이블 UI 강제 갱신
                            table.repaint()
                            table.revalidate()
                        } else {
                            println("Error: Menu not found in copiedMenuCategories!")
                        }

                        fireEditingStopped() // 편집 종료
                    }


                    return button
                }

                override fun getCellEditorValue(): Any {
                    return currentValue // 변경된 상태 반환
                }

                private fun updateButtonAppearance() {
                    //품절 판매 버튼 UI는 여기서 변경 해주어야함
                    button.font = MyFont.Bold(22f)
                    button.preferredSize = Dimension(100, 40) // 버튼 크기 설정
                    button.minimumSize = Dimension(100, 40) // 최소 크기
                    button.maximumSize = Dimension(100, 40) // 최대 크기
                    button.foreground = Color.WHITE
                    button.text = if (currentValue) "품절" else "판매"
                    button.backgroundColor = if (currentValue) Color.PINK else Color.LIGHT_GRAY
                }
            }

            columnModel.getColumn(0).apply {
                cellRenderer = buttonRendererEditor
                cellEditor = buttonRendererEditor
            }

            // 셀 렌더러 설정 (품절 관리, 메뉴 그룹만 가운데 정렬)
            val centerRenderer = DefaultTableCellRenderer().apply {
                horizontalAlignment = JLabel.CENTER
            }
            columnModel.getColumn(1).cellRenderer = centerRenderer // 메뉴 그룹

            // [메뉴 이름] 열의 왼쪽 여백 추가
            val leftPaddingRenderer = object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    (component as JLabel).apply {
                        horizontalAlignment = JLabel.LEFT
                        border = BorderFactory.createEmptyBorder(0, 20, 0, 0) // 왼쪽 여백 10px 추가
                    }
                    return component
                }
            }
            columnModel.getColumn(2).cellRenderer = leftPaddingRenderer // 메뉴 이름

        }

        val scrollPane = JScrollPane(menuTable).apply {
            border = BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1) // 외부 테두리 추가
        }

        mainPanel.add(scrollPane, BorderLayout.CENTER)

        // 하단 버튼
        val registerButton= FillRoundedButton(
            text = "등록",
            borderColor = Color(0, 0, 0),
            backgroundColor = MyColor.DARK_NAVY,  // 기본 선택된 상태
            textColor = Color.WHITE,
            borderRadius = 0,
            borderWidth = 1,
            textAlignment = SwingConstants.CENTER,
            padding = Insets(8, 16, 8, 16),  // 패딩 줄이기
            buttonSize = Dimension(300, 60),
            customFont = MyFont.Bold(26f)  // 버튼 글자 크기 줄임
        ).apply {
            addActionListener {
                println("Submitting copied data: $copiedMenuCategories")
            }
        }
        //만든 등록 버튼을 패널에 추가
        val buttonPanel = JPanel().apply {
            border = BorderFactory.createEmptyBorder(20, 0, 0, 0)
            background = Color.WHITE
            add(registerButton)
        }
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)

        // 초기 데이터 로드
        updateTable(copiedMenuCategories)

        setSize(1350, 800)
        setLocationRelativeTo(parent)
        isVisible = true
    }

    private fun updateTable(menuCategories: List<MenuCategory>) {
        tableModel.rowCount = 0 // 테이블 초기화
        val selectedCategory = categoryComboBox.selectedItem as String

        menuCategories.forEach { category ->
            if (selectedCategory == "전체" || selectedCategory == category.categoryName) {
                category.menuList.forEach { menu ->
                    tableModel.addRow(
                        arrayOf(
                            menu.isSoldOut,
                            category.categoryName,
                            menu.menuName
                        )
                    )
                }
            }
        }
    }

    // 품절 상품 필터링 동작
    private fun filterSoldOut(menuCategories: List<MenuCategory>) {
        tableModel.rowCount = 0 // 테이블 초기화
        val selectedCategory = categoryComboBox.selectedItem as String // 선택된 카테고리

        menuCategories.forEach { category ->
            // 선택된 카테고리와 매칭되거나 "전체"인 경우에만 처리
            if (selectedCategory == "전체" || selectedCategory == category.categoryName) {
                category.menuList.filter { it.isSoldOut }.forEach { menu ->
                    tableModel.addRow(
                        arrayOf(
                            menu.isSoldOut,
                            category.categoryName,
                            menu.menuName
                        )
                    )
                }
            }
        }
    }

}
