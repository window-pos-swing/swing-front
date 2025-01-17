package org.grr.model

import org.grr.command.RejectedReasonType
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.`object`.OrderController
import org.grr.observer.OrderObserver
import org.grr.screen.main.main_widget.order_states_ui.CompletedState
import org.grr.screen.main.main_widget.order_states_ui.PendingState
import org.grr.screen.main.main_widget.order_states_ui.ProcessingState
import org.grr.screen.main.main_widget.order_states_ui.RejectedState
import org.grr.util.MyDateFormat
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer  // javax.swing.Timer 사용

data class ReceiveOrderModel(
    val id: Int,
    val orderId: Int,
    val orderNumber: String,
    val orderDate: List<Int>,
    val modifyOrderDate: List<Int>,
    val userAppStoreMemberId: Int,
    val appMemberAddress: String,
    val appMemberPhone: String,
    val disposable: Boolean,
    val sideDish: Boolean,
    val storeRequest: String,
    val riderRequest: String,
    val orderReceiveType: String,
    val posOrderStatus : String,
    val posOrderStatusType: String,
    val expectedPrice: Int,
    val totalOrderPrice: Int,
    val orderPrice: Int,
    val deliveryPrice: Int,
    val couponName: String?,
    val couponDiscountPrice: Int,
    val cashDiscountPrice: Int,
    val rejectionReason: String?,
    val estimatedCookingTime: Int?,
    val estimatedArrivalTime: Int?,
    val menuList: List<Menu>,

    // 추가 필드
    var parentFrame: JFrame,  // 부모 프레임
    var cardPanel: JPanel,    // 카드 패널

    var state: OrderState = PendingState(parentFrame, cardPanel), // 기본 상태는 접수대기
    var elapsedTime: Int = 0,
    var cookTime: Int = 0,
    var deliveryTime: Int = 0,
) {
    var isCompleted: Boolean = false  // 주문 완료 여부
    var isResent: Boolean = false  // 배달 대행사로 주문번호 재전송 여부
    var isPickupCompleted: Boolean = false  // 주문 완료 여부
    var isPickupWait: Boolean = false  // 주문 완료 여부
    var isOnDelivery: Boolean = false

    private val stateObservers = mutableListOf<OrderObserver>()
    private val timerObservers = mutableListOf<OrderObserver>()

    var progressBarTimer: Timer? = null
    var eventTimer: Timer? = null // 이벤트 발생용 타이머

    // 주문 완료 / 주문 재전송 이벤트 타이머 초기화
    fun initializeEventTimer(delay: Int, action: () -> Unit): Timer {
        if (this.eventTimer != null && this.eventTimer!!.isRunning) {
            return this.eventTimer as Timer
        }

        this.eventTimer = Timer(delay) {
            action()
        }.apply {
            isRepeats = false
            start()
        }
        return this.eventTimer as Timer
    }

    // ReceiveOrderModel.kt
    fun updateStateBasedOnStatus() {
//        println("posOrderStatusType : $posOrderStatusType")
        var totalTime = 0
        if(estimatedCookingTime != null && estimatedArrivalTime != null) {
            totalTime = estimatedCookingTime + estimatedArrivalTime
        }
        state = when (posOrderStatusType) {

            ServerOrderStatus.REQUEST.name -> PendingState(parentFrame, cardPanel)

            ServerOrderStatus.COOKING.name,
            ServerOrderStatus.ACCEPT.name -> {
                elapsedTime = MyDateFormat.calculateTimeDifferenceInMinutes(modifyOrderDate)
//                println("elapsedTime  : $elapsedTime")
//                println("totalTime : $totalTime")
                startTimer(totalTime)
                ProcessingState(totalTime = totalTime , parentFrame, cardPanel)
            }

            ServerOrderStatus.COOKED.name -> {
                if (orderReceiveType == OrderReceiveType.DELIVERY.name) {
                    isPickupWait = true
                } else {
                    isPickupCompleted = true
                }
                ProcessingState(totalTime = totalTime , parentFrame, cardPanel)
            }

            ServerOrderStatus.DELIVERY.name ->  {
                isPickupWait = false
                isOnDelivery = true
                ProcessingState(totalTime = totalTime , parentFrame, cardPanel)
            }

            ServerOrderStatus.PICKUP_COMPLETE.name,
            ServerOrderStatus.DELIVERY_COMPLETE.name -> CompletedState()

            ServerOrderStatus.STORE_CANCEL.name-> RejectedState(
                rejectReason = rejectionReason!!,
                rejectDate = "${modifyOrderDate[0]}.${modifyOrderDate[1]}.${modifyOrderDate[2]}",
                rejectType = RejectedReasonType.STORE_REJECT,
                rejectPanel = PosOrderStatus.STORE_CANCEL
            )
            ServerOrderStatus.USER_CANCEL.name -> RejectedState(
                rejectReason = rejectionReason!!,
                rejectDate = "${modifyOrderDate[0]}.${modifyOrderDate[1]}.${modifyOrderDate[2]}",
                rejectType = RejectedReasonType.CUSTOMER_CANCEL,
                rejectPanel = PosOrderStatus.USER_CANCEL
            )



            else -> PendingState(parentFrame, cardPanel) // 기본 상태
        }
    }


    // 진행 시간 타이머 시작
    fun startTimer(totalTime: Int) {
        stopTimers()
        //60000 = 1분
        progressBarTimer = Timer(1000) {  // 1초마다 실행
            elapsedTime++
            notifyTimerObservers()  // 매초 옵저버 알림

            if (elapsedTime >= totalTime) {
                progressBarTimer?.stop()
            }
        }.apply {
            start()
        }
    }

    fun stopTimers() {
        progressBarTimer?.stop()
        progressBarTimer = null

        eventTimer?.stop()
        eventTimer = null

        parentFrame.revalidate()
        parentFrame.repaint()
    }

    // 상태 변경
    fun changeState(newState: OrderState) {
        state = newState
        notifyStateObservers()
    }

    // UI 반환
    fun getUI(): JPanel {
        return state.getUI(this).apply { putClientProperty("orderNumber", orderNumber) }
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

    // 상태 변경 옵저버 알림
    fun notifyStateObservers() {
        val observersSnapshot = ArrayList(stateObservers)
        observersSnapshot.forEach { observer ->
            observer.update(this)
        }
        println("[상태변경 옵저버 호출] #$orderId")
    }

    // 타이머 업데이트 옵저버 알림
    private fun notifyTimerObservers() {
        timerObservers.forEach { observer -> observer.update(this) }
    }

    data class Menu(
        val id: Int,
        val menuName: String,
        val quantity: Int,
        val menuTotalPrice : Int,
        val menuOptionList: List<MenuOption>
    ) {
        data class MenuOption(
            val id: Int,
            val categoryName: String,
            val menuOptionName: String,
            val menuOptionPrice: Int
        )
    }

    //String To Model
    companion object {
        fun fromJson(
            json: String,
            parentFrame: JFrame,
            cardPanel: JPanel
        ):
                ReceiveOrderModel {
            val jsonObject = JSONObject(json)

            val menuList = jsonObject.getJSONArray("menuList").map { menuJson ->
                val menuObject = menuJson as JSONObject
                val menuOptionList = menuObject.getJSONArray("menuOptionList").map { optionJson ->
                    val optionObject = optionJson as JSONObject
                    Menu.MenuOption(
                        id = optionObject.getInt("id"),
                        categoryName = optionObject.getString("categoryName"),
                        menuOptionName = optionObject.getString("menuOptionName"),
                        menuOptionPrice = optionObject.getInt("menuOptionPrice")
                    )
                }.toList()
                Menu(
                    id = menuObject.getInt("id"),
                    menuTotalPrice = menuObject.getInt("menuTotalPrice"),
                    menuName = menuObject.getString("menuName"),
                    quantity = menuObject.getInt("quantity"),
                    menuOptionList = menuOptionList
                )
            }.toList()

            return ReceiveOrderModel(
                id = jsonObject.getInt("id"),
                orderId = jsonObject.getInt("orderId"),
                orderNumber = jsonObject.getString("orderNumber"),
                orderDate = jsonObject.getJSONArray("orderDate").map { it as Int },
                userAppStoreMemberId = jsonObject.getInt("userAppStoreMemberId"),
                appMemberAddress = jsonObject.getString("appMemberAddress"),
                appMemberPhone = jsonObject.getString("appMemberPhone"),
                disposable = jsonObject.getBoolean("disposable"),
                sideDish = jsonObject.getBoolean("sideDish"),
                storeRequest = jsonObject.getString("storeRequest"),
                riderRequest = jsonObject.getString("riderRequest"),
                orderReceiveType = jsonObject.getString("orderReceiveType"),
                posOrderStatus = jsonObject.getString("posOrderStatus"),
                posOrderStatusType = jsonObject.getString("posOrderStatusType"),
                expectedPrice = jsonObject.getInt("expectedPrice"),
                totalOrderPrice = jsonObject.getInt("totalOrderPrice"),
                orderPrice = jsonObject.getInt("orderPrice"),
                deliveryPrice = jsonObject.getInt("deliveryPrice"),
                couponName = jsonObject.optString("couponName", null),
                couponDiscountPrice = jsonObject.getInt("couponDiscountPrice"),
                cashDiscountPrice = jsonObject.getInt("cashDiscountPrice"),
                modifyOrderDate = jsonObject.getJSONArray("modifyOrderDate").map { it as Int },
                rejectionReason = jsonObject.optString("rejectionReason", null),
                estimatedCookingTime = if (jsonObject.isNull("estimatedCookingTime")) null else jsonObject.getInt("estimatedCookingTime"),
                estimatedArrivalTime = if (jsonObject.isNull("estimatedArrivalTime")) null else jsonObject.getInt("estimatedArrivalTime"),
                menuList = menuList,
                parentFrame = parentFrame, // 전달받은 parentFrame
                cardPanel = cardPanel      // 전달받은 cardPanel
            ).apply {
                updateStateBasedOnStatus() // 상태 초기화
            }
        }


        //JSON To Model
        fun fromJsonObject(
            jsonObject: JSONObject,
            parentFrame: JFrame,
            cardPanel: JPanel
        ): ReceiveOrderModel {
            val menuList = jsonObject.getJSONArray("menuList").map { menuJson ->
                val menuObject = menuJson as JSONObject
                val menuOptionList = menuObject.getJSONArray("menuOptionList").map { optionJson ->
                    val optionObject = optionJson as JSONObject
                    Menu.MenuOption(
                        id = optionObject.getInt("id"),
                        categoryName = optionObject.getString("categoryName"),
                        menuOptionName = optionObject.getString("menuOptionName"),
                        menuOptionPrice = optionObject.getInt("menuOptionPrice")
                    )
                }.toList()
                Menu(
                    id = menuObject.getInt("id"),
                    menuTotalPrice = menuObject.getInt("menuTotalPrice"),
                    menuName = menuObject.getString("menuName"),
                    quantity = menuObject.getInt("quantity"),
                    menuOptionList = menuOptionList
                )
            }.toList()

            return ReceiveOrderModel(
                id = jsonObject.getInt("id"),
                orderId = jsonObject.getInt("orderId"),
                orderNumber = jsonObject.getString("orderNumber"),
                orderDate = jsonObject.getJSONArray("orderDate").map { it as Int },
                userAppStoreMemberId = jsonObject.getInt("userAppStoreMemberId"),
                appMemberAddress = jsonObject.getString("appMemberAddress"),
                appMemberPhone = jsonObject.getString("appMemberPhone"),
                disposable = jsonObject.getBoolean("disposable"),
                sideDish = jsonObject.getBoolean("sideDish"),
                storeRequest = jsonObject.getString("storeRequest"),
                riderRequest = jsonObject.getString("riderRequest"),
                orderReceiveType = jsonObject.getString("orderReceiveType"),
                posOrderStatus = jsonObject.getString("posOrderStatus"),
                posOrderStatusType = jsonObject.getString("posOrderStatusType"),
                expectedPrice = jsonObject.getInt("expectedPrice"),
                totalOrderPrice = jsonObject.getInt("totalOrderPrice"),
                orderPrice = jsonObject.getInt("orderPrice"),
                deliveryPrice = jsonObject.getInt("deliveryPrice"),
                couponName = jsonObject.optString("couponName", null),
                couponDiscountPrice = jsonObject.getInt("couponDiscountPrice"),
                cashDiscountPrice = jsonObject.getInt("cashDiscountPrice"),
                modifyOrderDate = jsonObject.getJSONArray("modifyOrderDate").map { it as Int },
                rejectionReason = jsonObject.optString("rejectionReason", null),
                estimatedCookingTime = if (jsonObject.isNull("estimatedCookingTime")) null else jsonObject.getInt("estimatedCookingTime"),
                estimatedArrivalTime = if (jsonObject.isNull("estimatedArrivalTime")) null else jsonObject.getInt("estimatedArrivalTime"),
                menuList = menuList,
                parentFrame = parentFrame,
                cardPanel = cardPanel
            ).apply {
                updateStateBasedOnStatus() // 상태 초기화
            }
        }
        fun fromJsonArray(
            jsonArray: JSONArray,
            parentFrame: JFrame,
            cardPanel: JPanel
        ): List<ReceiveOrderModel> {
            return jsonArray.map { item ->
                fromJsonObject(item as JSONObject, parentFrame, cardPanel)
            }
        }
    }



}
