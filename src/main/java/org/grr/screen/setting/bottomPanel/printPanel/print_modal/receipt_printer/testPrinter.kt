import com.fazecast.jSerialComm.SerialPort
import org.grr.screen.setting.bottomPanel.printPanel.print_modal.PrintManager
import java.io.OutputStream
import java.nio.charset.Charset

class TestPrinter(private val printerPort: String, private val printerSpeed: Int) {
    var printManager = PrintManager()
    fun printTestPage() {
        println("🔹 테스트 인쇄 시작")
        println("📌 프린터 포트: $printerPort")
        println("📌 프린터 속도: $printerSpeed")

        // ✅ 1. 프린터 포트 열기
        val serialPort = SerialPort.getCommPort(printerPort).apply {
            baudRate = printerSpeed
            numDataBits = 8
            numStopBits = SerialPort.ONE_STOP_BIT
            parity = SerialPort.NO_PARITY
        }

        if (!serialPort.openPort()) {
            println("❌ 프린터 포트 열기 실패")
            return
        }

        try {
            // ✅ 2. 출력 스트림 가져오기
            val outputStream = serialPort.outputStream

            // ✅ 3. 프린터 초기화
            printManager.initializePrinter(outputStream)

            // ✅ 4. 테스트 출력
            printManager.sendText(outputStream, "테스트 페이지 입니다.")
            printManager.sendText(outputStream, "프린터 연결 성공!")
            printManager.sendText(outputStream, "인쇄 테스트 중...")

            printManager.LineSpace(outputStream, 5)

            // ✅ 5. 컷팅 명령어 (용지가 자동으로 잘리게)
            printManager.cutPaper(outputStream)

            println("✅ 테스트 인쇄 완료")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // ✅ 6. 포트 닫기
            serialPort.closePort()
        }
    }



}
