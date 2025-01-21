package org.grr.`object`

import org.grr.api.OrderAPI
import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus
import org.grr.model.OrderFilter
import org.grr.model.ReceiveOrderModel
import org.grr.`object`.OrderController.handleOrderStateChange
import org.grr.observer.OrderObserver
import org.json.JSONArray
import javax.swing.JFrame
import javax.swing.JPanel

// 주문 데이터 싱글톤 관리
object OrderListSingleTon {
    val PAGE_SIZE: Int = 3
    val MAX_PAGE_REQUESTS: Int = 100

    // 데이터 상태
    val orders = mutableMapOf<String, MutableList<ReceiveOrderModel>>()
    val pageNumbers = mutableMapOf<String, Int>()
    val counts = mutableMapOf<String, Int>()
//    val realTime = mutableMapOf<String, Boolean>()
    // 초기화
    init {
        initializeKeys()
    }

    private fun initializeKeys() {
        val keys = listOf(
            "allOrders",
            "pendingOrders",
            "pendingDeliveryOrders",
            "pendingTakeOutOrders",
            "processingOrders",
            "processingDeliveryOrders",
            "processingTakeOutOrders",
            "completedOrders",
            "completedDeliveryOrders",
            "completedTakeOutOrders",
            "rejectOrders",
            "rejectUserOrders",
            "rejectStoreOrders",
            "rejectRefundOrders"
        )

        keys.forEach { key ->
            orders[key] = mutableListOf()
            pageNumbers[key] = 0
            counts[key] = 0
        }
    }

    // API 초기화
    fun initOrderData(parentFrame: JFrame, cardPanel: JPanel) {
        val filters = listOf(
            OrderFilter(), //전체 보기
            OrderFilter(posOrderStatus = PosOrderStatus.WAITING), // 접수대기 (전체)
            OrderFilter(posOrderStatus = PosOrderStatus.WAITING, orderReceiveType = OrderReceiveType.DELIVERY), // 접수대기 (배달)
            OrderFilter(posOrderStatus = PosOrderStatus.WAITING, orderReceiveType = OrderReceiveType.TAKEOUT), // 접수대기 (포장)
            OrderFilter(posOrderStatus = PosOrderStatus.IN_PROGRESS), // 접수처리중 (전체)
            OrderFilter(posOrderStatus = PosOrderStatus.IN_PROGRESS, orderReceiveType = OrderReceiveType.DELIVERY), // 접수처리중 (배달)
            OrderFilter(posOrderStatus = PosOrderStatus.IN_PROGRESS, orderReceiveType = OrderReceiveType.TAKEOUT), // 접수처리중 (포장)
            OrderFilter(posOrderStatus = PosOrderStatus.COMPLETED), // 접수완료 (전체)
            OrderFilter(posOrderStatus = PosOrderStatus.COMPLETED, orderReceiveType = OrderReceiveType.DELIVERY), // 접수완료 (배달)
            OrderFilter(posOrderStatus = PosOrderStatus.COMPLETED, orderReceiveType = OrderReceiveType.TAKEOUT), // 접수완료 (포장)
            OrderFilter(serverOrderStatus = ServerOrderStatus.STORE_CANCEL), // 주문거절 (가게거절)
            OrderFilter(serverOrderStatus = ServerOrderStatus.USER_CANCEL), // 주문거절 (고객거절)
            OrderFilter(serverOrderStatus = ServerOrderStatus.REFUND) // 주문거절 ( 환불 )
        )

        filters.forEach { filter ->
            OrderAPI().fetchOrders(parentFrame, cardPanel, filter, 0)
        }
    }

    // 주문 데이터 추가
    fun addAllOrder(
        orderListJson: String,
        parentFrame: JFrame,
        cardPanel: JPanel,
        filter: OrderFilter,
        totalElements: Int
    ) {
        try {
            val jsonArray = JSONArray(orderListJson)
            val newOrders = ReceiveOrderModel.fromJsonArray(jsonArray, parentFrame, cardPanel)

            // 키 결정
            val key = determineKey(filter)
            println("[key] : $key")

            // 기존 주문 리스트에서 주문 번호 추출
            val existingOrderNumbers = orders[key]?.map { it.orderNumber } ?: emptyList()

            // 중복 제거
            val uniqueOrders = newOrders.filter { it.orderNumber !in existingOrderNumbers }
            // 데이터 추가 여부 확인
            if (uniqueOrders.isEmpty() && pageNumbers[key] != 0 ) {
                println("[$key] No unique orders found.")

                // 현재 로드된 데이터와 서버 총 데이터 비교
                val currentLoadedCount = orders[key]?.size ?: 0
                if (currentLoadedCount >= totalElements) {
                    println("[$key] 모든 데이터 가져옴")
                    pageNumbers[key] = -1 // 페이지 번호를 -1로 설정하여 요청 중단
                    return
                }

                // 다음 페이지 요청
                val nextPage = (pageNumbers[key] ?: 0) + 1
                // 최대 요청 횟수 제한
                if (nextPage > MAX_PAGE_REQUESTS) {
                    println("[$key] 요청 제한 초과 - 무한 루프 방지")
                    pageNumbers[key] = -1
                    return
                }
                pageNumbers[key] = nextPage // 페이지 번호 업데이트
                println("[$key] 중복체크 비었음  서버 데이터 있음 다음 페이지 요청 : $nextPage")
                OrderAPI().fetchOrders(parentFrame, cardPanel, filter, nextPage)
                return
            }

            // 옵저버 등록
            uniqueOrders.forEach { order ->
                order.addStateObserver(object : OrderObserver {
                    override fun update(updatedOrder: ReceiveOrderModel) {
                        handleOrderStateChange(updatedOrder)
                    }
                })
            }

            // 주문 추가
            orders[key]?.addAll(uniqueOrders)

            // 카운트 및 페이지 번호 업데이트
            counts[key] = totalElements
            pageNumbers[key] = if (newOrders.size < PAGE_SIZE) -1 else (pageNumbers[key] ?: 0) + 1

            println("[$key]  pageNumber: ${pageNumbers[key]}")
            println("[$key]  Added unique orders: ${uniqueOrders.map { it.orderNumber }}")
            // 전체 orderId 순회하여 프린트
            val allOrderIds = orders[key]?.map { it.orderNumber } ?: emptyList()
            println("[addAllOrder] Current ${key} orders: $allOrderIds")

        } catch (e: Exception) {
            e.printStackTrace()
            println("Error adding orders: ${e.message}")
        }
    }


    // 키 결정
    private fun determineKey(filter: OrderFilter): String {
        return when {
            filter.posOrderStatus == PosOrderStatus.WAITING && filter.orderReceiveType == OrderReceiveType.DELIVERY -> "pendingDeliveryOrders"
            filter.posOrderStatus == PosOrderStatus.WAITING && filter.orderReceiveType == OrderReceiveType.TAKEOUT -> "pendingTakeOutOrders"
            filter.posOrderStatus == PosOrderStatus.WAITING -> "pendingOrders"
            filter.posOrderStatus == PosOrderStatus.IN_PROGRESS && filter.orderReceiveType == OrderReceiveType.DELIVERY -> "processingDeliveryOrders"
            filter.posOrderStatus == PosOrderStatus.IN_PROGRESS && filter.orderReceiveType == OrderReceiveType.TAKEOUT -> "processingTakeOutOrders"
            filter.posOrderStatus == PosOrderStatus.IN_PROGRESS -> "processingOrders"
            filter.posOrderStatus == PosOrderStatus.COMPLETED && filter.orderReceiveType == OrderReceiveType.DELIVERY -> "completedDeliveryOrders"
            filter.posOrderStatus == PosOrderStatus.COMPLETED && filter.orderReceiveType == OrderReceiveType.TAKEOUT -> "completedTakeOutOrders"
            filter.posOrderStatus == PosOrderStatus.COMPLETED -> "completedOrders"
            filter.serverOrderStatus == ServerOrderStatus.STORE_CANCEL -> "rejectStoreOrders"
            filter.serverOrderStatus == ServerOrderStatus.USER_CANCEL -> "rejectUserOrders"
            filter.serverOrderStatus == ServerOrderStatus.REFUND -> "rejectRefundOrders"
            else -> "allOrders"
        }
    }

    // 특정 주문 가져오기
    fun findOrderByNumber(statusKey: String, orderNumber: String): ReceiveOrderModel? {
        return orders[statusKey]?.find { it.orderNumber == orderNumber }
    }
}
