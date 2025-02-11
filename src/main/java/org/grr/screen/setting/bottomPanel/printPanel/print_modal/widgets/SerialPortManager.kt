import jssc.SerialPortList


object SerialPortManager {

    // ✅ 사용 가능한 시리얼 포트 목록 가져오기
    fun getAvailableSerialPorts(): List<String> {
        val ports = SerialPortList.getPortNames().toList()
//        println("🔍 [DEBUG] 사용 가능한 시리얼 포트 목록: $ports") // ✅ 디버깅 로그 추가
        return ports
    }

    // ✅ 특정 포트가 사용 가능한지 확인하는 함수
    fun isPortAvailable(portName: String): Boolean {
        val availablePorts = getAvailableSerialPorts()
        val isAvailable = availablePorts.contains(portName)
//        println("🔍 [DEBUG] 포트 사용 가능 여부 확인 - $portName: $isAvailable") // ✅ 디버깅 로그 추가
        return isAvailable
    }

}

