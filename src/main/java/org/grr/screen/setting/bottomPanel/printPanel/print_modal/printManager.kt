package org.grr.screen.setting.bottomPanel.printPanel.print_modal

import java.io.OutputStream
import java.nio.charset.Charset

class PrintManager {

    // 프린터 초기화 (ESC @)
    fun initializePrinter(outputStream: OutputStream) {
        outputStream.write(byteArrayOf(0x1B, 0x40)) // ESC @ (프린터 초기화)
    }

    // 텍스트 전송 함수 (CP949 인코딩 적용)
    fun sendText(outputStream: OutputStream, text: String) {
        outputStream.write(text.toByteArray(Charset.forName("CP949"))) // ✅ 한글 인코딩 (CP949)
        outputStream.write(byteArrayOf(0x0A)) // 줄바꿈 추가 (LF, Line Feed)
        outputStream.flush()
    }

    //여러 줄을 출력하도록 count 값만큼 \n (줄바꿈) 명령을 반복해서 전송하는 함수
    fun LineSpace(outputStream: OutputStream, count: Int) {
        outputStream.write("\n".repeat(count).toByteArray(Charset.forName("CP949")))
    }

    // 용지 컷팅 명령어
    fun cutPaper(outputStream: OutputStream) {
        outputStream.write(byteArrayOf(0x1D, 0x56, 0x00)) // ESC/POS 컷팅 명령
        outputStream.flush()
    }

}