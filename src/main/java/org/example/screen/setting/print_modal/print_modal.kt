import com.fazecast.jSerialComm.SerialPort
import java.awt.*
import javax.swing.*

class PrinterSettingDialog : JDialog() {

    private val tabPane: JTabbedPane

    init {
        title = "프린트 출력 설정"
        setSize(600, 550)
        layout = BorderLayout()

        // 제목 패널 설정
        val topPanel = JPanel()
        topPanel.layout = BorderLayout()

        val titleLabel = JLabel("프린트 출력 설정", SwingConstants.CENTER)
        titleLabel.font = Font("Arial", Font.BOLD, 18)
        topPanel.add(titleLabel, BorderLayout.CENTER)

        add(topPanel, BorderLayout.NORTH) // X버튼 제거된 상단 패널만 추가

        tabPane = JTabbedPane()
        addTab("기본 프린터", false)
        add(tabPane, BorderLayout.CENTER)

        val buttonPanel = JPanel(FlowLayout())
        val saveButton = JButton("설정 저장")
        saveButton.preferredSize = Dimension(220, 60)
        saveButton.addActionListener { saveSettings() }
        buttonPanel.add(saveButton)

        add(buttonPanel, BorderLayout.SOUTH)

        isVisible = true
    }

    // 탭을 추가하는 함수
    private fun addTab(name: String, removable: Boolean) {
        val panel = JPanel()
        panel.layout = GridBagLayout()
        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.insets = Insets(10, 10, 10, 10)

        // 프린터 이름 입력 필드
        val printerNameLabel = JLabel("프린터 이름")
        panel.add(printerNameLabel, gbc)
        val printerNameField = JTextField(name)
        printerNameField.preferredSize = Dimension(200, 50)
        gbc.gridy = 1
        panel.add(printerNameField, gbc)

        // 프린터 포트 선택 콤보박스
        val printerPortLabel = JLabel("프린터 포트")
        gbc.gridy = 2
        panel.add(printerPortLabel, gbc)
        val portComboBox = JComboBox<String>()
        portComboBox.preferredSize = Dimension(200, 50)

        // 직접 포트를 추가하는 방식으로 수정
        val availablePorts = SerialPort.getCommPorts().map { it.systemPortName }
        availablePorts.forEach { portComboBox.addItem(it) }

        gbc.gridy = 3
        panel.add(portComboBox, gbc)

        // 프린터 속도 콤보박스
        val printerSpeedLabel = JLabel("프린터 속도")
        gbc.gridy = 4
        panel.add(printerSpeedLabel, gbc)
        val speedComboBox = JComboBox(arrayOf("9600", "19200", "38400", "57600", "115200"))
        speedComboBox.preferredSize = Dimension(200, 50)
        gbc.gridy = 5
        panel.add(speedComboBox, gbc)

        // 테스트 출력 버튼
        val testPrintButton = JButton("테스트 출력")
        testPrintButton.preferredSize = Dimension(200, 50)
        testPrintButton.addActionListener {
            val port = portComboBox.selectedItem.toString()
            val speed = speedComboBox.selectedItem.toString().toInt()

            // 프린터 설정을 ReceiptPrinter에 전달
            val printer = ReceiptPrinter.getInstance()
            printer.setPrinterSettings(port, speed)
            val orderData = mapOf(
                "orderNumber" to "123456",
                "request" to "매운맛으로 해주세요",
                "spoonFork" to "수저포크 포함",
                "menuList" to listOf(
                    mapOf(
                        "menuName" to "김치찌개",
                        "count" to 2,
                        "price" to 10000,
                        "menuOptions" to listOf(
                            mapOf(
                                "optionName" to "밥 추가",
                                "optionPrice" to 1000
                            )
                        )
                    ),
                    mapOf(
                        "menuName" to "된장찌개",
                        "count" to 1,
                        "price" to 13000,
                        "menuOptions" to listOf(
                            mapOf(
                                "optionName" to "고기 추가",
                                "optionPrice" to 3000
                            )
                        )
                    )
                ),
                "delivery_fee" to 3000,
                "totalPrice" to 23000,
                "address" to "서울시 강남구",
                "phoneNumber" to "010-1234-5678",
                "payment_method" to "카드 결제"
            )
            printer.ForBurialOrderSheet(orderData) //조금 더 테스트 진행
            //printer.ForCustomersOrderSheet(orderData) //완
        }
        gbc.gridy = 6
        panel.add(testPrintButton, gbc)

        tabPane.addTab(name, panel)
    }

    // 설정 저장 함수 (미완성)
    private fun saveSettings() {
        // 설정 저장 로직 작성
    }
}

fun main() {
    PrinterSettingDialog()
}
