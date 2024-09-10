import com.fazecast.jSerialComm.SerialPort
import java.io.OutputStream
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReceiptPrinter private constructor() {
    private var printerPort: String? = null
    private var printerSpeed: Int = 9600

    companion object {
        private var instance: ReceiptPrinter? = null
        fun getInstance(): ReceiptPrinter {
            if (instance == null) {
                instance = ReceiptPrinter()
            }
            return instance!!
        }
    }

    fun setPrinterSettings(port: String, speed: Int) {
        printerPort = port
        printerSpeed = speed
    }

    private fun initializePrinter(outputStream: OutputStream) {
        // 프린터 초기화 (ESC @)
        outputStream.write(byteArrayOf(0x1B, 0x40))
    }

    private fun cutPaper(outputStream: OutputStream) {
        // 용지를 자르는 ESC/POS 명령어 (ESC i)
        outputStream.write(byteArrayOf(0x1B, 0x69))
    }

    private fun getCurrentTime(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm")
        return now.format(formatter)
    }

    //[매장용]
    fun ForBurialOrderSheet(orderData: Map<String, Any>) {
        if (printerPort == null) {
            println("프린터 설정이 없습니다.")
            return
        }

        val serialPort = SerialPort.getCommPort(printerPort)
        serialPort.baudRate = printerSpeed

        if (!serialPort.openPort()) {
            println("프린터 포트 열기 실패")
            return
        }

        try {
            val outputStream: OutputStream = serialPort.outputStream
            initializePrinter(outputStream)

            val currentTime = getCurrentTime()
            val normalSize = byteArrayOf(0x1D, 0x21, 0x00) // 기본 크기
            val oneAndHalfSize = byteArrayOf(0x1D, 0x21, 0x0A) // 1.5배 크기
            val doubleSize = byteArrayOf(0x1D, 0x21, 0x11) // 2배 크기
            val inverseModeOn = byteArrayOf(0x1D, 0x42, 0x01) // 반전 모드 켜기
            val inverseModeOff = byteArrayOf(0x1D, 0x42, 0x00) // 반전 모드 끄기
            val alignCenter = byteArrayOf(0x1B, 0x61, 0x01) // 중앙 정렬
            val alignLeft = byteArrayOf(0x1B, 0x61, 0x00) // 왼쪽 정렬

            // 매장용 출력
            outputStream.write(alignLeft)
            outputStream.write("[매장용]\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(doubleSize)
            outputStream.write(alignCenter)
            outputStream.write("꼬르륵\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))
            outputStream.write(normalSize)
            outputStream.write(alignLeft)
            outputStream.write("주문 번호: #${orderData["orderNumber"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(doubleSize)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))

            // 요청사항과 수저포크 출력
            outputStream.write(inverseModeOn)
            outputStream.write("요청사항: ${orderData["request"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write("수저포크: ${orderData["spoonFork"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(inverseModeOff)

            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))

            // 메뉴 출력 (글자 작게)
            outputStream.write(normalSize)
            outputStream.write("메뉴                               수량\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(doubleSize)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))

            outputStream.write(oneAndHalfSize)
            for (menu in orderData["menuList"] as List<Map<String, Any>>) {
                val menuName = menu["menuName"] as String
                val count = menu["count"] as Int

                // 메뉴명 출력
                val menuDisplayLength = menuName.toByteArray(Charset.forName("CP949")).size
                val paddingSpace = " ".repeat(40 - menuDisplayLength - 4)
                outputStream.write("$menuName$paddingSpace$count\n".toByteArray(Charset.forName("CP949")))

                // 메뉴 옵션 출력 (normalSize로 출력)
                outputStream.write(normalSize)  // 옵션 출력 시 normalSize로 설정
                val menuOptions = menu["menuOptions"] as? List<Map<String, String>>
                menuOptions?.forEach { option ->
                    val optionName = option["optionName"]
                    if (!optionName.isNullOrEmpty()) {
                        val optionPadding = " ".repeat(4) // 옵션 앞에 여백 추가
                        outputStream.write("$optionPadding+ $optionName\n".toByteArray(Charset.forName("CP949")))
                    }
                }

                outputStream.write(oneAndHalfSize)  // 다시 메뉴 출력 크기로 복원
            }

            outputStream.write(doubleSize)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))

            // 주문 일자 (글자 작게)
            outputStream.write(normalSize)
            outputStream.write("주문 일자: $currentTime\n".toByteArray(Charset.forName("CP949")))

            // 공백 추가 후 용지 자르기
            outputStream.write("\n".repeat(5).toByteArray(Charset.forName("CP949")))
            cutPaper(outputStream)
            outputStream.flush()
        } catch (e: Exception) {
            println("매장용 주문표 인쇄 실패: ${e.message}")
        } finally {
            serialPort.closePort()
        }
    }


    //================================================================================================
    //[고객용]
    fun ForCustomersOrderSheet(orderData: Map<String, Any>) {
        if (printerPort == null) {
            println("프린터 설정이 없습니다.")
            return
        }

        val serialPort = SerialPort.getCommPort(printerPort)
        serialPort.baudRate = printerSpeed

        if (!serialPort.openPort()) {
            println("프린터 포트 열기 실패")
            return
        }

        try {
            val outputStream: OutputStream = serialPort.outputStream
            initializePrinter(outputStream)

            val currentTime = getCurrentTime()
            val normalSize = byteArrayOf(0x1D, 0x21, 0x00) // 기본 크기
            val oneAndHalfSize = byteArrayOf(0x1D, 0x21, 0x0A) // 1.5배 크기
            val doubleSize = byteArrayOf(0x1D, 0x21, 0x11) // 2배 크기
            val inverseModeOn = byteArrayOf(0x1D, 0x42, 0x01) // 반전 모드 켜기
            val inverseModeOff = byteArrayOf(0x1D, 0x42, 0x00) // 반전 모드 끄기
            val alignCenter = byteArrayOf(0x1B, 0x61, 0x01) // 중앙 정렬
            val alignLeft = byteArrayOf(0x1B, 0x61, 0x00) // 왼쪽 정렬

            // 고객용 출력
            outputStream.write("[고객용]\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(doubleSize)
            outputStream.write(alignCenter)
            outputStream.write("꼬르륵\n".toByteArray(Charset.forName("CP949")))

            outputStream.write(alignLeft)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))
            outputStream.write(oneAndHalfSize)
            outputStream.write("주문 번호: #${orderData["orderNumber"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write("결제방법: ${orderData["payment_method"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(doubleSize)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))
            // 주문 정보 출력
            outputStream.write(inverseModeOn)
            outputStream.write("[요청사항]\n".toByteArray(Charset.forName("CP949")))
            outputStream.write("가게 : ${orderData["request"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write("배달 : 직접 받을게요\n".toByteArray(Charset.forName("CP949")))
            outputStream.write("수저포크: ${orderData["spoonFork"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(inverseModeOff)

            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))
            outputStream.write(normalSize)
            outputStream.write("배달주소: ${orderData["address"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write("연락처: ${orderData["phoneNumber"]}\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(doubleSize)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))

            // 메뉴 출력
            outputStream.write(normalSize)
            outputStream.write("메뉴                        수량    가격\n".toByteArray(Charset.forName("CP949")))
            outputStream.write(doubleSize)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))
            outputStream.write(oneAndHalfSize)
            for (menu in orderData["menuList"] as List<Map<String, Any>>) {
                val menuName = menu["menuName"] as String
                val count = menu["count"] as Int
                val price = menu["price"] as Int

                val maxCharsPerLine = 26
                val totalLineLength = 40

                val menuNameLength = menuName.toByteArray(Charset.forName("CP949")).size

                outputStream.write(oneAndHalfSize)
                // 메뉴 이름이 한 줄을 넘으면 나눠서 출력
                if (menuNameLength > maxCharsPerLine) {
                    var firstLine = ""
                    var remainingLine = menuName
                    var currentLength = 0

                    for (char in menuName) {
                        val charLength = if (char.code > 127) 2 else 1
                        if (currentLength + charLength > maxCharsPerLine) break
                        firstLine += char
                        remainingLine = remainingLine.drop(1)
                        currentLength += charLength
                    }

                    val firstLineDisplayLength = firstLine.toByteArray(Charset.forName("CP949")).size
                    // 메뉴 이름 출력 후 남은 공간을 계산하여 count와 price 사이에 여백 추가
                    val paddingSpaceFirst = " ".repeat(totalLineLength - firstLineDisplayLength - 14)
                    outputStream.write("$firstLine$paddingSpaceFirst${count.toString().padStart(4)}${price.toString().padStart(10)}\n".toByteArray(Charset.forName("CP949")))
                    // count와 price 사이에 여백을 추가하기 위해 padStart 사용
                    outputStream.write("$remainingLine\n".toByteArray(Charset.forName("CP949")))
                } else {
                    val menuDisplayLength = menuName.toByteArray(Charset.forName("CP949")).size
                    val paddingSpace = " ".repeat(totalLineLength - menuDisplayLength - 14)
                    // 여백 추가 부분 - count와 price 사이에 여백을 추가하기 위해 padStart 사용
                    outputStream.write("$menuName$paddingSpace${count.toString().padStart(4)}${price.toString().padStart(10)}\n".toByteArray(Charset.forName("CP949")))
                }

                outputStream.write(normalSize) // Reset size

                // 옵션 출력
                val options = menu["menuOptions"] as? List<Map<String, Any>> ?: emptyList()
                for (option in options) {
                    val optionName = option["optionName"] as String
                    val optionPrice = option["optionPrice"] as? Int ?: 0

                    val maxOptionNameLength = 20 // 최대 옵션 이름 길이
                    var optionNameDisplay = optionName
                    val optionNameLength = optionName.toByteArray(Charset.forName("CP949")).size

                    if (optionNameLength > maxOptionNameLength) {
                        var currentLength = 0
                        optionNameDisplay = ""
                        for (char in optionName) {
                            val charLength = if (char.code > 127) 2 else 1
                            if (currentLength + charLength > maxOptionNameLength) break
                            optionNameDisplay += char
                            currentLength += charLength
                        }
                    }

                    // 가격 출력 포맷에 따라 오른쪽 정렬
                    val priceLength = optionPrice.toString().length
                    val fixedPosition = 30 // 고정된 출력 위치
                    val padding = " ".repeat(fixedPosition - optionNameDisplay.toByteArray(Charset.forName("CP949")).size)
                    val optionLine = "  + $optionNameDisplay$padding${"%5d".format(optionPrice)}\n"
                    outputStream.write(optionLine.toByteArray(Charset.forName("CP949")))
                }
            }

            outputStream.write(doubleSize)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))

            val totalPrice = orderData["totalPrice"] as Int  // 가격은 Int형이라고 가정
            val totalLineLength = 40  // 한 줄에 출력할 전체 길이
            val totalLabel = "합계"  // 왼쪽에 표시할 텍스트
            outputStream.write(oneAndHalfSize)
            // totalLabel의 길이를 CP949로 인코딩한 후 길이를 구함
            val labelLength = totalLabel.toByteArray(Charset.forName("CP949")).size

            // totalPrice는 숫자이므로 문자열로 변환 후 CP949 인코딩 길이를 구함
            val priceString = totalPrice.toString()
            val priceLength = priceString.toByteArray(Charset.forName("CP949")).size

            // 전체 라인 길이에서 합계와 가격 길이를 제외한 만큼의 여백을 계산
            val paddingSpace = " ".repeat(totalLineLength - labelLength - priceLength)

            // 출력
            outputStream.write("$totalLabel$paddingSpace$priceString\n".toByteArray(Charset.forName("CP949")))

            outputStream.write(doubleSize)
            outputStream.write(("-".repeat(21) + "\n").toByteArray(Charset.forName("CP949")))
            outputStream.write(normalSize)
            outputStream.write("주문 일자: $currentTime\n".toByteArray(Charset.forName("CP949")))

            // 공백 추가 후 용지 자르기
            outputStream.write("\n".repeat(5).toByteArray(Charset.forName("CP949")))
            cutPaper(outputStream)
            outputStream.flush()
        } catch (e: Exception) {
            println("고객용 주문표 인쇄 실패: ${e.message}")
        } finally {
            serialPort.closePort()
        }
    }
}
