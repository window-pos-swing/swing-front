package org.example.model

import org.example.observer.OrderObserver
import org.example.view.states.PendingState
import org.example.view.states.ProcessingState
import javax.swing.JPanel
import javax.swing.Timer  // javax.swing.Timer 사용

data class Order(
    val orderNumber: Int,
    val orderTime: String,
    val orderType: String,  // DELIVERY or TAKEOUT
    val request: String,
    val address: String,
    val deliveryFee: Int,
    val spoonFork: Boolean,
    val menuList: List<Menu>,
    var state: OrderState = PendingState(),  // 기본 상태는 접수대기
    var elapsedTime: Int = 0
) {
    var isCompleted: Boolean = false  // 주문 완료 여부
    var isResent: Boolean = false  // 배달 대행사로 주문번호 재전송 여부

    private val stateObservers = mutableListOf<OrderObserver>()
    private val timerObservers = mutableListOf<OrderObserver>()

    var progressBarTimer: Timer? = null
    var eventTimer: Timer? = null  // 이벤트 발생용 타이머

    //[테스트용]주문완료 / 주문재전송 이벤트 타이머를 초기화하는 메서드
    fun initializeEventTimer(delay: Int, action: () -> Unit): Timer {
        // 이벤트 타이머가 이미 실행 중이면 그대로 반환
        if (this.eventTimer != null && this.eventTimer!!.isRunning) {
            return this.eventTimer as Timer
        }

        // 새로운 이벤트 타이머 생성 (한 번 실행 후 종료)
        this.eventTimer = Timer(delay) {
            action()
        }.apply {
            isRepeats = false  // 타이머가 한 번 실행되고 멈추도록 설정
            start()
        }

        return this.eventTimer as Timer
    }

    fun startTimer(totalTime: Int) {
        // 타이머가 이미 존재하면 중복해서 실행하지 않음
        if (progressBarTimer != null) {
            println("progressBarTimer 이미 실행중  #$orderNumber")
            return
        }

        progressBarTimer = Timer(1000) {  // 1초마다 실행
            elapsedTime++
            notifyTimerObservers()  // 매초 옵저버에게 알림

            if (elapsedTime >= totalTime) {
                progressBarTimer?.stop()
            }
        }.apply {
            start()
        }
    }

    // 상태 변경 시 옵저버들에게 알림
    fun changeState(newState: OrderState) {
        state = newState
    }

    // UI 업데이트를 위해 현재 상태의 UI 반환
    fun getUI(): JPanel {
        return state.getUI(this)
    }

    // 옵저버 등록
    fun addStateObserver(observer: OrderObserver) {
        if (!stateObservers.contains(observer)) {
            stateObservers.add(observer)
        }
    }


    // 타이머 업데이트 옵저버 등록
    fun addTimerObserver(observer: OrderObserver) {
        if (!timerObservers.contains(observer)) {
            timerObservers.add(observer)
        }
    }

    // 상태 변경 옵저버에게 알림
    fun notifyStateObservers() {
        val observersSnapshot = ArrayList(stateObservers)  // 리스트의 복사본을 사용
        observersSnapshot.forEach { observer ->
            observer.update(this)
        }
        println("[상태변경 옵저버 호출] #${orderNumber}")
    }

    // 타이머 업데이트 옵저버에게 알림
    private fun notifyTimerObservers() {
        timerObservers.forEach { observer -> observer.update(this) }
//        println("Timer Observers notified for Order #${orderNumber}")
    }
}
