package org.example.model

import org.example.observer.OrderObserver
import org.example.view.states.PendingState
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
    private val stateObservers = mutableListOf<OrderObserver>()
    private val timerObservers = mutableListOf<OrderObserver>()

    private var timer: Timer? = null

    fun startTimer(totalTime: Int) {
        timer?.stop() // 기존 타이머 정지

        timer = Timer(1000) {  // 1초마다 실행
            elapsedTime++
            notifyTimerObservers()  // 매초 옵저버에게 알림
            if (elapsedTime >= totalTime) {
                timer?.stop()
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
        println("Observers notified for Order #${orderNumber}")
    }

    // 타이머 업데이트 옵저버에게 알림
    private fun notifyTimerObservers() {
        timerObservers.forEach { observer -> observer.update(this) }
//        println("Timer Observers notified for Order #${orderNumber}")
    }
}
