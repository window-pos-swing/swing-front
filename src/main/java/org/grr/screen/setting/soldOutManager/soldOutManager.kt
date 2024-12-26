import org.grr.model.MenuCategory
import org.grr.model.MenuData
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.FillRoundedButton
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class SoldOutManagementDialog(parent: JFrame) : CustomRoundedDialog(parent, "품절 관리", 1350, 800) {

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
            background = Color.WHITE

            // 메뉴 그룹 콤보박스
            add(JLabel("메뉴 그룹:").apply {
                font = MyFont.Bold(20f)
            })
            val categories = MenuData.createSampleData() // 샘플 데이터 생성

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
                        filterSoldOut(categories)
                        backgroundColor = MyColor.LIGHT_BLUE
                        borderColor = MyColor.LIGHT_BLUE
                        this.text = "모든 상품 보기"
                        font = MyFont.Bold(23f) // 폰트 유지
                        foreground = Color.WHITE // 텍스트 색상 유지
                    } else {
                        // 모든 상품 보기 동작
                        updateTable(categories)
                        backgroundColor = MyColor.LIGHT_GREY2
                        borderColor = MyColor.LIGHT_GREY2
                        this.text = "품절 상품 보기"
                        font = MyFont.Bold(23f) // 폰트 유지
                        foreground = Color.WHITE // 텍스트 색상 유지
                    }
                    isFilteringSoldOut = !isFilteringSoldOut // 상태 변경
                    repaint()
                }
            }
            add(soldOutButton)
        }
        mainPanel.add(filterPanel, BorderLayout.NORTH)

        // [테이블] 품절 관리, 메뉴 그룹, 메뉴 이름
        tableModel = DefaultTableModel(arrayOf("품절 관리", "메뉴 그룹", "메뉴 이름"), 0)
        menuTable = JTable(tableModel).apply {
            rowHeight = 40
            font = Font("Arial", Font.PLAIN, 14)
            tableHeader.font = Font("Arial", Font.BOLD, 14)
            tableHeader.background = Color(230, 240, 255) // 헤더 배경색
            tableHeader.reorderingAllowed = false
        }
        val scrollPane = JScrollPane(menuTable).apply {
            border = BorderFactory.createEmptyBorder(20, 0, 0, 0) // 상단에 20px 여백 추가
        }
        mainPanel.add(scrollPane, BorderLayout.CENTER)

        // 하단 버튼
        val bottomPanel = JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            background = Color.WHITE
            add(JButton("등록").apply {
                preferredSize = Dimension(200, 50)
                background = Color(30, 144, 255) // 파란색
                foreground = Color.WHITE
                font = Font("Arial", Font.BOLD, 16)
                addActionListener {
                    JOptionPane.showMessageDialog(this@SoldOutManagementDialog, "등록 완료!")
                }
            })
        }
        mainPanel.add(bottomPanel, BorderLayout.SOUTH)

        // 초기 데이터 로드
        updateTable(MenuData.createSampleData())

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
                            if (menu.isSoldOut) "품절" else "판매",
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
        menuCategories.forEach { category ->
            category.menuList.filter { it.isSoldOut }.forEach { menu ->
                tableModel.addRow(
                    arrayOf(
                        "품절",
                        category.categoryName,
                        menu.menuName
                    )
                )
            }
        }
    }
}
