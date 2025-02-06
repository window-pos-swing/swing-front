import org.grr.api.MenuAPI
import org.grr.model.MenuCategory
import org.grr.model.MenuData
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.CustomScrollBarUI
import org.grr.widgets.FillRoundedButton
import org.grr.widgets.RoundedPanel
import org.grr.widgets.RoundedPanel2
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class SoldOutManagementDialog: JPanel() {
    // 독립적인 복사본 데이터를 저장
    private val copiedMenuCategories: MutableList<MenuCategory> = mutableListOf()
    private val tableModel: DefaultTableModel
    private val menuTable: JTable
    private val categoryComboBox: JComboBox<String>
    private var isFilteringSoldOut = false // 품절 필터링 상태를 저장

    init {
        layout = BorderLayout()
        background = MyColor.DARK_NAVY
        border = BorderFactory.createEmptyBorder(20, 0, 0, 0)

        // 초기 데이터 로드
        val currentPage = 0
        val pageSize = 10
        val (success, menuList, categoryList) = MenuAPI().fetchMenuList(currentPage, pageSize)
        val menuListTable = MenuAPI().parseMenuData(menuList.toString())

        // 둥근 패널 생성
        val roundedPanel = RoundedPanel(30, 30).apply {
            layout = BorderLayout()
            background = MyColor.LOGIN_TITLEBAR // 둥근 패널 배경색
            border = BorderFactory.createEmptyBorder(0, 20, 0, 20) // 내부 여백 설정
        }

        // 상단 필터 패널
        val filterPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
            background = MyColor.LOGIN_TITLEBAR

            // 메뉴 그룹 콤보박스
            add(JLabel("메뉴 그룹:").apply {
                font = MyFont.Bold(20f)
                foreground = Color.WHITE
            })
            val categories = MenuData.createSampleData() // 샘플 데이터 생성

            // 원본 데이터를 깊은 복사하여 복사본 생성
            copiedMenuCategories.clear()
            copiedMenuCategories.addAll(categoryList!!.map { MenuCategory(id = -1, categoryName = it.toString(), menuList = emptyList()) })

            // DefaultComboBoxModel 생성 및 카테고리 추가
            val categoryModel = DefaultComboBoxModel<String>().apply {
                addElement("전체") // "전체" 기본 추가
                copiedMenuCategories.forEach { category ->
                    addElement(category.categoryName) // ✅ MenuCategory 객체에서 categoryName만 추가
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
                        if (success && menuList != null) {
                            filterSoldOut(menuListTable)
                        } else {
                            JOptionPane.showMessageDialog(this, "데이터를 불러오지 못했습니다.")
                        }
                    } else {
                        if (success && menuList != null) {
                            updateTable(menuListTable)
                        } else {
                            JOptionPane.showMessageDialog(this, "데이터를 불러오지 못했습니다.")
                        }
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
                        if (success && menuList != null) {
                            filterSoldOut(menuListTable)
                        } else {
                            JOptionPane.showMessageDialog(this, "데이터를 불러오지 못했습니다.")
                        }
                        backgroundColor = MyColor.LIGHT_BLUE
                        borderColor = MyColor.LIGHT_BLUE
                    } else {
                        // 모든 상품 보기 동작
                        if (success && menuList != null) {
                            updateTable(menuListTable)
                        } else {
                            JOptionPane.showMessageDialog(this, "데이터를 불러오지 못했습니다.")
                        }
                        backgroundColor = MyColor.LIGHT_GREY2
                        borderColor = MyColor.LIGHT_GREY2
                    }
                    isFilteringSoldOut = !isFilteringSoldOut // 상태 변경
                    repaint()
                }
            }
            add(soldOutButton)
        }

        // [테이블] 품절 관리, 메뉴 그룹, 메뉴 이름
        tableModel = object : DefaultTableModel(arrayOf("품절 관리", "메뉴 그룹", "메뉴 이름"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return column == 0  // "품절 관리" 열만 편집 가능
            }
        }
        menuTable = JTable(tableModel).apply {
            rowHeight = 60
            background = MyColor.DARK_NAVY
            foreground = Color.WHITE
            font = MyFont.Bold(22f)
            columnModel.getColumn(1).cellEditor = null // "메뉴 그룹"
            columnModel.getColumn(2).cellEditor = null // "메뉴 이름"
//            columnModel.getColumn(2).cellRenderer = object : DefaultTableCellRenderer() {
//                override fun getTableCellRendererComponent(
//                    table: JTable,
//                    value: Any?,
//                    isSelected: Boolean,
//                    hasFocus: Boolean,
//                    row: Int,
//                    column: Int
//                ): Component {
//                    // 패널 생성
//                    val panel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 0)).apply {
//                        background = if (isSelected) MyColor.LIGHT_BLUE_46 else MyColor.DARK_NAVY
//                    }
//
//                    // 메뉴 이름 라벨 추가
//                    val menuNameLabel = JLabel(value.toString()).apply {
//                        font = MyFont.SemiBold(18f)
//                        foreground = Color.WHITE
//                    }
//                    panel.add(menuNameLabel)
//
//                    // 품절 상태에 따른 "품절" 태그 추가
//                    val isSoldOut = table.getValueAt(row, 0) as Boolean
//                    if (isSoldOut) {
//                        val soldOutLabel = JLabel("품절").apply {
//                            font = MyFont.Bold(14f)
//                            foreground = Color.WHITE
//                            background = Color.PINK
//                            border = BorderFactory.createEmptyBorder(5, 10, 5, 10) // 내부 패딩
//                            isOpaque = true // 배경색 적용
//                        }
//                        panel.add(soldOutLabel)
//                    }
//
//                    return panel
//                }
//            }

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
                    component.background = MyColor.LIGHT_BLUE // 헤더 배경색 설정
                    component.foreground = Color.white // 헤더 텍스트 색상
                    horizontalAlignment = JLabel.CENTER // 텍스트 가운데 정렬
                    border = BorderFactory.createLineBorder(MyColor.LIGHT_GREY) // 헤더 테두리 추가
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
            val totalWeight = 2 + 3 + 10
            val totalWidth = 1200 // 테이블 전체 너비 (스크롤 없이 표시되도록 설정)
            columnModel.getColumn(0).preferredWidth = (totalWidth * 2 / totalWeight).toInt() // 품절 관리
            columnModel.getColumn(1).preferredWidth = (totalWidth * 3 / totalWeight).toInt() // 메뉴 그룹
            columnModel.getColumn(2).preferredWidth = (totalWidth * 10 / totalWeight).toInt() // 메뉴 이름

            // "품절 관리" 열에 버튼 렌더러와 에디터 추가
            val buttonRendererEditor = object : AbstractCellEditor(), TableCellRenderer, TableCellEditor {
                private val button = FillRoundedButton(
                    text = "",
                    borderColor = Color.BLACK,
                    backgroundColor = Color.LIGHT_GRAY,
                    textColor = Color.WHITE,
                    borderRadius = 10,
                    borderWidth = 2,
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
            // 스크롤 패널 크기 설정
            preferredSize = Dimension(600, 200)
            border = BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1)

            // 커스텀 스크롤바 적용
            verticalScrollBar.ui = CustomScrollBarUI()
            horizontalScrollBar.ui = CustomScrollBarUI()

            // 스크롤바 크기 조정
            verticalScrollBar.preferredSize = Dimension(10, 0) // 세로 스크롤바 너비
            horizontalScrollBar.preferredSize = Dimension(0, 12) // 가로 스크롤바 높이
        }

        // 하단 버튼
        val registerButton= FillRoundedButton(
            text = "등록",
            borderColor = Color(0, 0, 0),
            backgroundColor = MyColor.LIGHT_BLUE,  // 기본 선택된 상태
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
            border = BorderFactory.createEmptyBorder(20, 0, 20, 0)
            background = MyColor.LOGIN_TITLEBAR
            add(registerButton)
        }

        // 둥근 패널에 추가
        roundedPanel.add(filterPanel, BorderLayout.NORTH)
        roundedPanel.add(scrollPane, BorderLayout.CENTER)
        roundedPanel.add(buttonPanel, BorderLayout.SOUTH)

        // 메인 패널에 둥근 패널 추가
        add(roundedPanel, BorderLayout.CENTER)

//        println("메뉴 데이터 : ${menuList.toString()}")
//        println("카테고리 데이터 : ${categoryList.toString()}")

        if (success && menuList != null) {
            updateTable(menuListTable) // 테이블 업데이트
        } else {
            JOptionPane.showMessageDialog(this, "데이터를 불러오지 못했습니다.")
        }
    }

    private fun updateTable(menuCategories: List<MenuCategory>) {
        tableModel.rowCount = 0 // 기존 테이블 데이터 초기화
        val selectedCategory = categoryComboBox.selectedItem as String
        menuCategories.forEach { category ->
            if (selectedCategory == "전체" || selectedCategory == category.categoryName) {
                category.menuList.forEach { menu ->
                    tableModel.addRow(
                        arrayOf(
                            menu.isSoldOut, // 품절 상태
                            category.categoryName, // 메뉴 그룹
                            menu.menuName // 메뉴 이름
                        )
                    )
                }
            }
        }
    }

    // 품절 상품 필터링 동작
    private fun filterSoldOut(menuCategories: List<MenuCategory>) {
        tableModel.rowCount = 0
        val selectedCategory = categoryComboBox.selectedItem as String
        menuCategories.forEach { category ->
            println("카테고리 명 : ${category}, ${selectedCategory}")
            if (selectedCategory == "전체" || selectedCategory == category.categoryName) {
                category.menuList.filter { it.isSoldOut }.forEach { menu ->
                    tableModel.addRow(arrayOf(menu.isSoldOut, category.categoryName, menu.menuName))
                }
            }
        }
    }
}
