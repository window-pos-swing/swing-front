package org.example.model

import org.example.observer.OrderObserver
import org.example.view.states.PendingState
import javax.swing.JPanel

data class Order(
    val orderNumber: Int,
    val orderType: String,
    val request: String,
    val address: String,
    val deliveryFee: Int,
    val spoonFork: Boolean,
    val menuList: List<Menu>,
    var state: OrderState = PendingState()  // 기본 상태는 접수대기
) {
    private val observers = mutableListOf<OrderObserver>()

    // 상태 변경 시 옵저버들에게 알림
    fun changeState(newState: OrderState) {
        state = newState
//        notifyObservers() // 상태 변경 시 UI 자동 업데이트
    }

    // UI 업데이트를 위해 현재 상태의 UI 반환
    fun getUI(): JPanel {
        return state.getUI(this)
    }

    // 옵저버 등록
    fun addObserver(observer: OrderObserver) {
        observers.add(observer)
    }

    // 모든 옵저버에게 상태 변경 알림
    fun notifyObservers() {
        observers.forEach { it.update(this) }
        println("Observers notified for Order #${orderNumber}")
    }
}