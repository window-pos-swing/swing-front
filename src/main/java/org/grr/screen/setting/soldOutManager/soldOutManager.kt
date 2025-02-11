import org.grr.api.MenuAPI
import org.grr.model.MenuCategory
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.CustomScrollBarUI
import org.grr.widgets.FillRoundedButton
import org.grr.widgets.RoundedPanel
import org.json.JSONArray
import java.awt.*
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class SoldOutManagementDialog : JPanel() {
    // 독립적인 복사본 데이터를 저장
    private val tableModel: DefaultTableModel
    private val menuTable: JTable
    private val categoryComboBox: JComboBox<String>
    private var isFilteringSoldOut = false // 품절 필터링 상태를 저장
    private val selectedMenuIds = mutableSetOf<Int>()
    private var categoryModel: DefaultComboBoxModel<String> = DefaultComboBoxModel()

    private var currentPage = 0
    private val pageSize = 10
    private var isLoading = false
    private var hasMenuData = true // 데이터가 없을 경우 api호출을 막는 구문
    private val menuListTable: MutableList<MenuCategory> = mutableListOf()
    private var soldOut: Boolean? = null
    private var selectCategory: String? = null
    private var previousCategory: String? = null

    init {
        layout = BorderLayout()
        background = MyColor.DARK_NAVY
        border = BorderFactory.createEmptyBorder(20, 0, 0, 0)

        // [테이블] 품절 관리, 메뉴 그룹, 메뉴 이름
        tableModel = object : DefaultTableModel(arrayOf("품절 관리", "메뉴 그룹", "메뉴 이름"), 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return column == 0  // "품절 관리" 열만 편집 가능
            }
        }

        categoryModel = DefaultComboBoxModel<String>().apply {
            addElement("전체") // "전체" 기본 추가
        }

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

            // 카테고리 이름 콤보박스
            categoryComboBox = RoundedComboBox(categoryModel).apply {
                preferredSize = Dimension(430, 55)
                maximumSize = Dimension(430, 55)
                minimumSize = Dimension(430, 55)
                font = MyFont.Bold(22f)

                addActionListener {
                    selectCategory = selectedItem?.toString()?.takeIf { it != "전체" }
                    if (isFilteringSoldOut) {
                        filterSoldOut()
                    } else {
                        updateTable()
                    }
                }
            }

            add(categoryComboBox)
            add(Box.createRigidArea(Dimension(20, 0)))

            // 초기 데이터 로드
            loadMoreData()

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
                        soldOut = true
                        filterSoldOut()
                        backgroundColor = MyColor.LIGHT_BLUE
                        borderColor = MyColor.LIGHT_BLUE
                    } else {
                        soldOut = null
                        // 모든 상품 보기 동작
                        updateTable()
                        backgroundColor = MyColor.LIGHT_GREY2
                        borderColor = MyColor.LIGHT_GREY2
                    }
                    isFilteringSoldOut = !isFilteringSoldOut // 상태 변경
                    repaint()
                }
            }
            add(soldOutButton)
        }

        menuTable = JTable(tableModel).apply {
            rowHeight = 60
            background = MyColor.DARK_NAVY
            foreground = Color.WHITE
            font = MyFont.Bold(22f)
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
                    button.actionListeners.forEach { button.removeActionListener(it) }

                    // 테이블에서 현재 메뉴 정보를 가져옴
                    val menuName = table.getValueAt(currentRow, 2) as String
                    val menu = menuListTable.flatMap { it.menuList }.find { it.menuName == menuName }
//                    println("메뉴 리스트 : ${menuListTable}")
                    // 버튼 클릭 시 상태 변경 및 복사본 데이터 업데이트
                    button.addActionListener {
//                        println("선택된 메뉴 : , ${menu}")
                        if (menu != null) {
                            // 버튼 상태 반전에 따라 isSoldOut 값 설정
                            menu.isSoldOut = !menu.isSoldOut
                            currentValue = menu.isSoldOut

                            if (selectedMenuIds.contains(menu.id)) {
                                selectedMenuIds.remove(menu.id) // 선택 취소 시 제거
                            } else {
                                selectedMenuIds.add(menu.id) // 선택 시 추가
                            }

                            updateButtonAppearance()
                            button.repaint()

                            (table.model as DefaultTableModel).fireTableCellUpdated(currentRow, column)

                            fireEditingStopped()
                        } else {
                            println("Error: Menu not found in copiedMenuCategories!")
                        }
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

        scrollPane.verticalScrollBar.addAdjustmentListener(object : AdjustmentListener {
            override fun adjustmentValueChanged(e: AdjustmentEvent) {
                val scrollBar = e.adjustable
                val maxScroll = scrollBar.maximum - scrollBar.visibleAmount
                val currentScroll = scrollBar.value

                if (currentScroll >= maxScroll - 20) { // 스크롤이 거의 끝에 도달했을 때
                    loadMoreData()
                }
            }
        })

        // 하단 버튼
        val registerButton = FillRoundedButton(
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
                if (selectedMenuIds.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "품절 처리할 메뉴를 선택해주세요.")
                    return@addActionListener
                }

                //  품절 API 호출
                val (success, message) = MenuAPI().soldOut(selectedMenuIds.toList())

                if (success) {
                    JOptionPane.showMessageDialog(this, "품절 설정 완료!")
                    println("품절 처리된 메뉴 ID 리스트: $selectedMenuIds")
                } else {
                    JOptionPane.showMessageDialog(this, "품절 처리 실패: $message")
                }

                selectedMenuIds.clear()
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

        updateTable() // 테이블 업데이트
    }

    private fun updateTable() {
        tableModel.rowCount = 0 // 기존 테이블 데이터 초기화
        val selectedCategory = categoryComboBox.selectedItem?.toString() ?: "전체"

        menuListTable.forEach { category ->
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
    private fun filterSoldOut() {
        tableModel.rowCount = 0
        val selectedCategory = categoryComboBox.selectedItem?.toString() ?: "전체"
        menuListTable.forEach { category ->
            if (selectedCategory == "전체" || selectedCategory == category.categoryName) {
                category.menuList.filter { it.isSoldOut }.forEach { menu ->
                    tableModel.addRow(arrayOf(menu.isSoldOut, category.categoryName, menu.menuName))
                }
            }
        }
    }

    private fun loadMoreData() {
        if (isLoading) return
        isLoading = true

        if (selectCategory != previousCategory) {
            currentPage = 0
            menuListTable.clear()
            hasMenuData = true
            previousCategory = selectCategory
        }

        // API 호출
        val (success, menuListResponse, categoryListResponse) = MenuAPI().fetchMenuList(
            pageNumber = currentPage,
            pageSize = pageSize,
            selectCategory,
            soldOut
        )

        updateCategoryComboBox(categoryListResponse)

        if (success && menuListResponse != null) {
            val newMenus = MenuAPI().parseMenuData(menuListResponse.toString())

            if (newMenus.isNotEmpty()) {
                menuListTable.addAll(newMenus) // 기존 데이터에 추가 el")
                currentPage++ // 페이지 증가
                updateTable() // 테이블 업데이트
            }
        }
        isLoading = false
    }

    private fun updateCategoryComboBox(categoryList: JSONArray?) {
        val previousSelected = categoryComboBox.selectedItem

        categoryModel.removeAllElements()
        categoryModel.addElement("전체")

        categoryList?.let {
            for (i in 0 until it.length()) {
                categoryModel.addElement(it.getString(i))
            }
        }

        categoryComboBox.model = categoryModel
        categoryComboBox.selectedItem = previousSelected
    }
}