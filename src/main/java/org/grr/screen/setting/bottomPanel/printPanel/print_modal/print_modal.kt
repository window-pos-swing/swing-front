
import org.grr.`object`.Storage
import org.grr.style.MyColor
import org.grr.util.MyFont
import java.awt.*
import javax.swing.*

// 프린터 버튼 클래스
class PrinterButton(name: String) : JButton(name) {

    init {
        background = Color(217, 217, 217)  // 기본 배경색
        foreground = Color(120, 120, 120)  // 기본 글자색
        isOpaque = true
        isBorderPainted = false
        font = MyFont.Bold(20f)
        preferredSize = Dimension(200, 50)
        maximumSize = Dimension(200, 50)
        minimumSize = Dimension(200, 50)
    }

    fun setClickedStyle() {
        background = Color(13, 130, 191)  // 클릭 시 배경색
        foreground = Color.WHITE           // 클릭 시 글자색
        repaint() // 디자인 변경 후 다시 그리기
    }

    fun resetStyle() {
        background = Color(217, 217, 217)  // 기본 배경색으로 되돌리기
        foreground = Color(120, 120, 120)  // 기본 글자색으로 되돌리기
        repaint()
    }
}

// 둥근 모서리를 가진 버튼 클래스
class RoundedButton2(text: String) : JButton(text) {
    init {
        isContentAreaFilled = false  // 기본 배경 채우기 제거
        isFocusPainted = false  // 포커스 테두리 제거
        isOpaque = false  // 불투명 설정 제거
        border = BorderFactory.createEmptyBorder()  // 테두리 제거
        font = MyFont.Bold(20f)  // 폰트 설정
        foreground = Color.WHITE  // 텍스트 색상
    }

    // 배경을 둥근 모서리로 그리기 위해 paintComponent 오버라이드
    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 둥근 배경을 그리기
        g2.color = background  // 설정된 배경색 사용
        g2.fillRoundRect(0, 0, width, height, 20, 20)  // 둥근 모서리 (반지름 20)

        super.paintComponent(g)  // 텍스트 및 기타 컴포넌트 렌더링
    }
}

class PrinterSettingDialog(parent: JFrame, title: String, callback: ((Boolean) -> Unit)? = null) : CustomRoundedDialog(
    parent,
    title,
    1100,
    530,
    callback
) {
    // 프린트 추가 시 인덱스를 관리할 변수
    private var printerCount = 1
    private val buttonStrutMap = mutableMapOf<JButton, Box.Filler>()
    private val printerButtons = mutableListOf<PrinterButton>()
    private val myPrinters = mutableListOf<Storage.PrinterInfo>() // 프린터 정보 리스트
    // 왼쪽 프린터 목록 패널
    val leftPanel = JPanel().apply {
        background = Color.WHITE
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    // 오른쪽 패널: 프린터 설정
    val rightPanel = JPanel(CardLayout()).apply {
        preferredSize = Dimension(850, 350)
        border = BorderFactory.createEmptyBorder(0,10,0,0)
        background = Color.WHITE
    }

    val addPrinterButton = createAddPrinterButton()

    val gbc = GridBagConstraints().apply {
        gridx = 0
        gridy = 0
        weightx = 1.0
        fill = GridBagConstraints.HORIZONTAL
    }

    init {
        setSize(1100, 530)  // 다이얼로그 크기 설정
        setLocationRelativeTo(parent)

        // 설정 패널 구성 (전체 레이아웃은 GridBagLayout)
        val contentPanel = JPanel(GridBagLayout()).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)  // 패널 마진 추가 (좌우 20, 상하 20)
        }



        // ✅ 기존에 저장된 프린터 설정 불러오기
        val savedPrinters = Storage.loadPrinterSettings()
        myPrinters.addAll(savedPrinters)
        printerCount = savedPrinters.size

        // ✅ 저장된 프린터 버튼 추가
        addSavedPrinters(savedPrinters)

        // ✅ 프린터 추가 버튼
        leftPanel.add(Box.createVerticalStrut(5))
        leftPanel.add(addPrinterButton)

        // 왼쪽 패널 및 오른쪽 패널을 contentPanel에 추가
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 0.3
        gbc.fill = GridBagConstraints.BOTH
        contentPanel.add(leftPanel, gbc)

        gbc.gridx = 1
        gbc.gridy = 0
        gbc.weightx = 0.7
        gbc.fill = GridBagConstraints.BOTH
        contentPanel.add(rightPanel, gbc)

        // 설정 저장 버튼 추가
        val saveButton = JButton("설정 저장").apply {
            background = Color(27, 43, 66)
            foreground = Color.WHITE
            font = MyFont.Bold(26f)
            isOpaque = true  // 배경이 그려지도록 설정
            isBorderPainted = false  // 테두리는 그리지 않음
            preferredSize = Dimension(300, 60)  // 버튼 크기 설정
            minimumSize = Dimension(300, 60)  // 버튼 크기 설정
            maximumSize = Dimension(300, 60)  // 버튼 크기 설정
        }
        saveButton.addActionListener {
            // ✅ 포트가 설정되지 않은 프린터 찾기
            val printersWithoutPort = myPrinters.filter { it.port.isBlank() }

            if (printersWithoutPort.isNotEmpty()) {
                val printerNames = printersWithoutPort.joinToString("\n") { it.name } // 🔹 프린터 이름 리스트 생성

                JOptionPane.showMessageDialog(
                    null,
                    "설정되지 않은 프린터 포트가 있습니다.\n다음 프린터의 포트를 설정해주세요:\n\n$printerNames",
                    "경고",
                    JOptionPane.WARNING_MESSAGE
                )
                return@addActionListener
            }

            Storage.savePrinterSettings(myPrinters)
            JOptionPane.showMessageDialog(this@PrinterSettingDialog, "프린터 설정이 저장되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE)
        }
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 4
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.NONE
        gbc.anchor = GridBagConstraints.CENTER
        gbc.insets = Insets(20, 0, 0, 0)  // 상단 여백 추가
        contentPanel.add(saveButton, gbc)

        // 다이얼로그에 contentPanel 추가
        add(contentPanel)

        // 다이얼로그 설정
        isVisible = true
        setLocationRelativeTo(parent)  // 부모 창의 가운데에 띄우기
    }

    // 프린터 설정 패널 생성 함수
    private fun createPrinterSettingPanel(
        printer: Storage.PrinterInfo,
        name: String,
        leftPanel: JPanel,
        buttonToRemove: JButton,
        rightPanel: JPanel,
        buttonToUpdate: JButton
    ): JPanel {
        //테스트 버튼
        var testPrintButton = RoundedButton2("테스트 인쇄").apply {
            background = if(printer.port.isBlank()) MyColor.GREY500 else MyColor.PINK
            foreground = Color.WHITE
            isOpaque = false
            isBorderPainted = false
            addActionListener {
                if (printer.port.isBlank()) {
                    JOptionPane.showMessageDialog(null, "프린터 포트를 설정해주세요.", "경고", JOptionPane.WARNING_MESSAGE)
                    return@addActionListener
                }
                // ✅ 현재 프린터의 포트와 속도 가져오기
                val port = printer.port.ifBlank { "포트가 설정되지 않음" }
                val speed = printer.speed.toIntOrNull() ?: 9600

                println("🖨 테스트 인쇄 버튼 클릭됨")
                println("🔍 현재 설정된 프린터 정보: 포트=$port, 속도=$speed")

                // ✅ testPrinter 실행
                val testPrinterInstance = TestPrinter(port, speed)
                testPrinterInstance.printTestPage()
            }
        }
        val panel = JPanel().apply {
            background = Color.WHITE
            layout = GridBagLayout()
            preferredSize = Dimension(725, 300)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color(154, 200, 224), 1),  // 회색 테두리, 두께 1
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )   // 내부 여백
        }

        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            anchor = GridBagConstraints.NORTHWEST
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(0, 10, 0, 10)  // 여백 설정
        }

        // 프린터 이름 라벨 및 텍스트 필드
        val printerNameLabel = JLabel("프린터 이름 :").apply {
            font = MyFont.Bold(16f)
            foreground = MyColor.LIGHT_GREY2
        }
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.insets = Insets(0, 10, 0, 5) // ✅ 오른쪽 여백 축소
        gbc.anchor = GridBagConstraints.WEST
        panel.add(printerNameLabel, gbc)

        val printerNameField = JTextField(name).apply {
            preferredSize = Dimension(300, 40)
            font = MyFont.Bold(22f)
            border = BorderFactory.createEmptyBorder()
        }

        // 텍스트 필드의 값이 변경될 때 버튼 이름을 동기화
        printerNameField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) {
                buttonToUpdate.text = printerNameField.text
                printer.name = buttonToUpdate.text
            }

            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) {
                buttonToUpdate.text = printerNameField.text
                printer.name = buttonToUpdate.text
            }

            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) {
                buttonToUpdate.text = printerNameField.text
                printer.name = buttonToUpdate.text
            }
        })

        gbc.gridx = 1
        gbc.gridy = 0
        gbc.gridwidth = 2
        panel.add(printerNameField, gbc)

        // 프린터 삭제 버튼
        val deleteButton = RoundedButton2("프린트 삭제").apply {
            font = MyFont.Bold(18f)
            background = Color(13, 130, 191)
            foreground = Color.WHITE
            isOpaque = false
            isBorderPainted = false
        }

        gbc.gridx = 3
        gbc.gridy = 0
        gbc.weightx = 0.0
        gbc.anchor = GridBagConstraints.EAST
        gbc.fill = GridBagConstraints.NONE
        gbc.ipadx = 60
        gbc.ipady = 15
        panel.add(deleteButton, gbc)

        // ✅ 삭제 버튼 클릭 시 해당 프린터 삭제
        deleteButton.addActionListener {
            myPrinters.removeIf { it.name == printer.name } // ✅ 리스트에서도 삭제

            val verticalStrut = buttonStrutMap[buttonToRemove]
            if (verticalStrut != null) {
                leftPanel.remove(verticalStrut) // 마진 제거
            }
            leftPanel.remove(buttonToRemove)  // 왼쪽에서 버튼 제거
            leftPanel.revalidate()
            leftPanel.repaint()

            // 오른쪽 패널에서 해당 설정 제거
            rightPanel.remove(panel)
            rightPanel.revalidate()
            rightPanel.repaint()

            printerButtons.remove(buttonToRemove as PrinterButton) // ✅ 삭제된 버튼을 리스트에서도 제거

            printerCount--

            // ✅ 삭제 후 남아있는 프린터 중 첫 번째 프린터를 선택
            if (myPrinters.isNotEmpty()) {
                println("첫 번째 프린터 자동 선택 : ${myPrinters[0].name}")

                myPrinters.forEach { it.selectPrint = false } // 기존 선택 초기화
                myPrinters[0].selectPrint = true // 첫 번째 프린터 선택

                // ✅ UI에서 첫 번째 프린터 버튼 강조
                printerButtons.forEach { it.resetStyle() }

                // ✅ 첫 번째 버튼 스타일 변경
                if (printerButtons.isNotEmpty()) {
                    printerButtons.first().setClickedStyle()
                }

                // ✅ 첫 번째 프린터 설정 화면으로 변경
                val layout = rightPanel.layout as CardLayout
                layout.show(rightPanel, myPrinters[0].name)

                // ✅ UI 갱신 강제 적용
                leftPanel.revalidate()
                leftPanel.repaint()
                rightPanel.revalidate()
                rightPanel.repaint()
            }
        }


        // 가로 경계선 추가 (프린터 이름과 포트 사이)
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 5  // 전체 너비 차지
        gbc.anchor = GridBagConstraints.CENTER
        gbc.insets = Insets(10, 0, 0, 0)
        panel.add(createSeparator(SwingConstants.HORIZONTAL, 680, 1), gbc)

        // ✅ 프린터 포트 : & 연결됨 or 연결안됨
        printPortSetting(printer, panel)


        // ✅ 사용 가능한 포트 목록 가져오기
        val portComboBox = createPortComboBox(printer,panel,testPrintButton)

        gbc.gridx = 1
        gbc.gridy = 2
        gbc.gridwidth = 1
        panel.add(portComboBox, gbc)

//        세로 경계선
        gbc.gridx = 2
        gbc.gridy = 2
        gbc.gridwidth = 1
        gbc.insets = Insets(0, 40, 0, 0)
        panel.add(createSeparator(SwingConstants.VERTICAL, 1, 65), gbc)

        // 프린터 속도 선택
        val printerSpeedLabel = JLabel("프린터 속도 :").apply {
            font = MyFont.Bold(16f)
            foreground = MyColor.LIGHT_GREY2
        }
        gbc.gridx = 3
        gbc.gridy = 2
        gbc.gridwidth = 1
        gbc.insets = Insets(0, 0, 0, 0)
        panel.add(printerSpeedLabel, gbc)

        val speedComboBox =
            RoundedComboBox(DefaultComboBoxModel(arrayOf("9600", "19200", "38400", "57600", "115200"))).apply {
                selectedItem = printer.speed // ✅ 기존 저장된 값 로드
                preferredSize = Dimension(205, 50)
                font = MyFont.Bold(20f)
                // ✅ 속도 변경 시 프린터 객체 업데이트
                addActionListener {
                    printer.speed = selectedItem as String
                }
            }
        gbc.gridx = 4
        gbc.gridy = 2
        gbc.gridwidth = 1
        gbc.insets = Insets(0, 0, 0, 10)
        panel.add(speedComboBox, gbc)

//        가로 경계선
        gbc.gridx = 0
        gbc.gridy = 3
        gbc.gridwidth = 5  // 전체 너비 차지
        gbc.anchor = GridBagConstraints.CENTER
        gbc.insets = Insets(10, 0, 0, 0)
        panel.add(createSeparator(SwingConstants.HORIZONTAL, 680, 1), gbc)

// 패널에 체크박스를 묶어서 배치
        val receiptCheckBox = JCheckBox("영수증 출력", printer.receiptPrint).apply {
            background = Color.WHITE
            font = MyFont.Bold(20f)
            addActionListener { printer.receiptPrint = isSelected }
        }

        val kitchenCheckBox = JCheckBox("주방 주문서 출력", printer.kitchenPrint).apply {
            background = Color.WHITE
            font = MyFont.Bold(20f)
            addActionListener { printer.kitchenPrint = isSelected }
        }

        val checkBoxPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER, 30, 0)  // 가운데 정렬
            background = Color.WHITE  // 패널 배경색 설정 (필요시)

            // 체크박스 추가
            add(receiptCheckBox)

            add(kitchenCheckBox)
        }

        // 체크박스 패널을 중앙에 배치
        gbc.gridx = 0
        gbc.gridy = 4
        gbc.gridwidth = 5  // 두 개의 열에 걸쳐서 위치하도록 설정
        gbc.anchor = GridBagConstraints.CENTER
        gbc.fill = GridBagConstraints.NONE
        gbc.insets = Insets(10, 0, 0, 0)  // 여백 설정
        panel.add(checkBoxPanel, gbc)


        gbc.gridx = 0
        gbc.gridy = 5
        gbc.gridwidth = 5
        gbc.anchor = GridBagConstraints.CENTER
        gbc.fill = GridBagConstraints.NONE
        gbc.insets = Insets(10, 0, 0, 0)
        gbc.ipadx = 100
        gbc.ipady = 20
        panel.add(testPrintButton, gbc)

        return panel
    }

    private fun createSeparator(orientation: Int, width: Int, height: Int): JSeparator {
        return JSeparator(orientation).apply {
            foreground = MyColor.LIGHT_GREY
            preferredSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
        }
    }

    /// == [SAVE LOAD] ==
    // ✅ 저장된 프린터 버튼 추가
    private fun addSavedPrinters(savedPrinters: List<Storage.PrinterInfo>) {
        savedPrinters.forEachIndexed { index, printer ->
            val printerButton = createPrinterButton(printer)

            // ✅ 버튼 사이에 간격 추가 (첫 번째 버튼 제외)
            if (index > 0) {
                val verticalStrut = Box.createVerticalStrut(5)
                leftPanel.add(verticalStrut)
                buttonStrutMap[printerButton] = verticalStrut as Box.Filler
            }

            printerButtons.add(printerButton)
            leftPanel.add(printerButton)

            val printerPanel = createPrinterSettingPanel(printer, printer.name, leftPanel, printerButton, rightPanel, printerButton)
            rightPanel.add(printerPanel, printer.name)

            // ✅ 처음 로드될 때, selectPrint가 true인 프린터 선택
            if (printer.selectPrint) {
                selectPrinter(printer, printerButton)
            }
        }
    }
    // ✅ 개별 프린터 버튼 생성
    private fun createPrinterButton(printer: Storage.PrinterInfo): PrinterButton {
        return PrinterButton(printer.name).apply {
            addActionListener {
                selectPrinter(printer, this)
            }
        }
    }
    // ✅ 프린터 선택 시 스타일 및 UI 변경
    private fun selectPrinter(printer: Storage.PrinterInfo, printerButton: PrinterButton) {
        // ✅ 모든 버튼 스타일 리셋
        printerButtons.forEach { it.resetStyle() }

        // ✅ 클릭된 버튼 스타일 적용
        printerButton.setClickedStyle()

        // ✅ 모든 프린터 selectPrint false 처리
        myPrinters.forEach { it.selectPrint = false }

        // ✅ 현재 선택된 프린터 selectPrint = true
        printer.selectPrint = true

        // ✅ 패널 변경
        val layout = rightPanel.layout as CardLayout
        layout.show(rightPanel, printer.name)

        // ✅ UI 갱신 강제 적용
        rightPanel.revalidate()
        rightPanel.repaint()
    }


    /// == [ADD BUTTON] ==
    // ✅ 프린터 추가 버튼 생성
    private fun createAddPrinterButton(): JButton {
        return JButton("프린트 추가").apply {
            preferredSize = Dimension(200, 50)
            minimumSize = Dimension(200, 50)
            maximumSize = Dimension(200, 50)
            background = Color.WHITE
            foreground = Color(27, 43, 66)
            isOpaque = true
            isBorderPainted = true
            font = MyFont.Bold(20f)
            border = BorderFactory.createLineBorder(foreground, 2)

            addActionListener { addPrinter() }
        }
    }
    // ✅ 프린터 추가 기능 (버튼 클릭)
    private fun addPrinter() {
        if (printerCount >= 4) {
            JOptionPane.showMessageDialog(null, "최대 4개의 프린터만 추가할 수 있습니다.")
            return
        }

        printerCount++
        val newPrinterName = "프린트$printerCount"

        // 왼쪽 패널에서 '프린트 추가' 버튼을 제거
        leftPanel.remove(addPrinterButton)

        val verticalStrut = Box.createVerticalStrut(5)
        val newPrinterButton = createPrinterButton2(newPrinterName)

        // ✅ 기존 프린터들 selectPrint = false 처리
        myPrinters.forEach { it.selectPrint = false }

        // 새로 추가된 버튼도 리스트에 추가
        printerButtons.add(newPrinterButton)
        buttonStrutMap[newPrinterButton] = verticalStrut as Box.Filler

        // 버튼 간 고정된 간격을 위한 Strut 추가
        if (printerCount > 1) {
            leftPanel.add(verticalStrut, leftPanel.componentCount - 1)  // 첫 번째 버튼에는 추가되지 않도록
        }

        leftPanel.add(newPrinterButton, leftPanel.componentCount - 1) // 프린트 추가 버튼 위에 추가
        leftPanel.add(addPrinterButton)

        leftPanel.revalidate()
        leftPanel.repaint()

        // ✅ 스토리지 추가될 변수에 추가
        val newPrinter = Storage.PrinterInfo(newPrinterName, "", "9600", true, true, true)
        myPrinters.add(newPrinter)

        // ✅ UI에서도 해당 프린터 버튼만 강조
        printerButtons.forEach { it.resetStyle() }
        newPrinterButton.setClickedStyle()

        // 오른쪽 패널에 해당 프린터 설정 패널 추가
        val newPrinterPanel = createPrinterSettingPanel(newPrinter, newPrinter.name, leftPanel, newPrinterButton, rightPanel, newPrinterButton)
        rightPanel.add(newPrinterPanel, newPrinterName)

        // ✅ 추가된 프린터의 설정 화면을 즉시 보여주기
        val layout = rightPanel.layout as CardLayout
        layout.show(rightPanel, newPrinterName)
    }
    // ✅ 개별 프린터 버튼 생성
    private fun createPrinterButton2(name: String): PrinterButton {
        return PrinterButton(name).apply {
            addActionListener { selectPrinter(myPrinters.last(), this) }
        }
    }

    // ✅ 프린터 포트 선택 콤보박스 생성 (수정됨)
    private fun createPortComboBox(printer: Storage.PrinterInfo, panel: JPanel,testPrintButton : RoundedButton2): JComboBox<String> {
        val availablePorts = SerialPortManager.getAvailableSerialPorts()
        val defaultMessage = "포트를 설정해주세요"  // ✅ 기본 메시지

        // ✅ 포트 리스트에 기본 메시지 추가 (빈 값 대신 표시)
        val portOptions = mutableListOf(defaultMessage).apply { addAll(availablePorts) }

        val portComboBox = RoundedComboBox(DefaultComboBoxModel(portOptions.toTypedArray())).apply {
            selectedItem = if (printer.port.isBlank()) defaultMessage else printer.port // ✅ 기본 메시지 설정
            preferredSize = Dimension(200, 50) // ✅ 콤보박스 크기 고정
            maximumSize = Dimension(200, 50)
            minimumSize = Dimension(200, 50)
            font = MyFont.Bold(20f)

            // ✅ 포트 이름이 길 경우 툴팁에 전체 표시
            renderer = object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(
                    list: JList<*>?,
                    value: Any?,
                    index: Int,
                    isSelected: Boolean,
                    cellHasFocus: Boolean
                ): Component {
                    val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                    if (component is JLabel) {
                        val text = value?.toString() ?: ""
                        component.toolTipText = text // ✅ 툴팁에 전체 포트 이름 표시
                        component.text = if (text.length > 15) text.take(12) + "..." else text // ✅ 긴 포트명 줄임표 표시
                    }
                    return component
                }
            }

            // ✅ 포트 변경 시 UI 갱신
            addActionListener {
                val selectedPort = selectedItem as String
                printer.port = if (selectedPort == defaultMessage) "" else selectedPort

                // ✅ 포트 상태 UI 업데이트
                updatePortStatus(printer, panel)

                // ✅ 테스트 인쇄 버튼 색상 업데이트
                testPrintButton.background = if (printer.port.isBlank()) MyColor.GREY500 else MyColor.PINK
            }
        }

        return portComboBox
    }


    private fun printPortSetting(printer: Storage.PrinterInfo, panel: JPanel) {

        // ✅ 프린터 포트 라벨
        val printerPortTitleLabel = JLabel("프린터 포트 :").apply {
            font = MyFont.Bold(16f)
            foreground = MyColor.LIGHT_GREY2
            name = "printerPortTitleLabel"
            preferredSize = Dimension(120, 20)
            minimumSize = Dimension(120,50)
            maximumSize = Dimension(120,50)
        }

        val gbcLocal = GridBagConstraints().apply {  // ✅ 새로운 GridBagConstraints 객체 생성
            gridx = 0
            gridy = 2
            gridwidth = 1
            insets = Insets(0, 10, 0, 10)  // ✅ 여백을 명확히 지정
        }

        if (printer.port.isNotBlank()) {
            // ✅ 포트 상태 라벨 (초기화)
            val printerPortStatusLabel = JLabel().apply {
                font = MyFont.Bold(14f)
                text = if (SerialPortManager.isPortAvailable(printer.port)) "연결됨" else "연결 안됨"
                foreground = if (text == "연결됨") Color(34, 139, 34) else Color.RED
                preferredSize = Dimension(120, 20)
                minimumSize = Dimension(120,50)
                maximumSize = Dimension(120,50)
                alignmentX = Component.LEFT_ALIGNMENT
            }

            // ✅ 프린터 포트 박스 (수직 정렬)
            val printerPortBox = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS) // ✅ 요소를 위아래 정렬
                background = Color.WHITE
                preferredSize = Dimension(120,50)
                minimumSize = Dimension(120,50)
                maximumSize = Dimension(120,50)
                name = "printerPortBox" // ✅ 반드시 name 설정
                add(printerPortTitleLabel)
                add(Box.createVerticalStrut(5)) // ✅ 여백 추가
                add(printerPortStatusLabel)
                alignmentX = Component.LEFT_ALIGNMENT
            }

            panel.add(printerPortBox, gbcLocal)  // ✅ 수정된 GridBagConstraints 사용
        } else {
            panel.add(printerPortTitleLabel, gbcLocal)  // ✅ 수정된 GridBagConstraints 사용
        }
    }


    private fun updatePortStatus(printer: Storage.PrinterInfo, panel: JPanel) {
        SwingUtilities.invokeLater {
            // ✅ 기존에 추가된 포트 상태 UI 제거
            val componentsToRemove = mutableListOf<Component>()

            for (component in panel.components) {
                // ✅ `printerPortBox` 또는 `printerPortTitleLabel`이면 삭제 대상
                if ((component is JPanel && component.name == "printerPortBox") ||
                    (component is JLabel && component.name == "printerPortTitleLabel")) {
                    componentsToRemove.add(component)
                }
            }

            println("제거할 컴포넌트 개수: ${componentsToRemove.size}")

            // ✅ 기존 UI 제거
            componentsToRemove.forEach { panel.remove(it) }

            // ✅ 새로운 포트 상태 UI 추가
            printPortSetting(printer, panel)

            // ✅ UI 강제 갱신
            panel.revalidate()
            panel.repaint()
        }
    }


}