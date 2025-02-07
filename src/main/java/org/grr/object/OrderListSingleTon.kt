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
        totalElements: Int,
        totalPages: Int,
    ) {
        try {
            val jsonArray = JSONArray(orderListJson)
            val newOrders = ReceiveOrderModel.fromJsonArray(jsonArray, parentFrame, cardPanel)

            // 키 결정
            val key = determineKey(filter)
            println("[key] : $key")

            // 현재 저장된 주문 리스트
            val currentOrders = orders[key] ?: mutableListOf()
            // 기존 orderNumber 목록 추출 (Set 사용하여 중복 체크 빠르게 수행)
            val existingOrderNumbers = currentOrders.map { it.orderNumber }.toSet()

            // ✅ 중복 제거: 기존에 없는 주문만 필터링
            val uniqueOrders = newOrders.filter { it.orderNumber !in existingOrderNumbers }

            if (uniqueOrders.isEmpty()) {
                println("[DEBUG] No new unique orders to add for key: $key")
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
            currentOrders.addAll(uniqueOrders)
            orders[key] = currentOrders // 변경된 리스트를 다시 저장
            // 카운트 및 페이지 번호 업데이트
            counts[key] = totalElements
            pageNumbers[key] = if (totalPages-1 == pageNumbers[key]!!) -1 else (pageNumbers[key] ?: 0) + 1

            println("[$key]  pageNumber: ${pageNumbers[key]}")
            println("[$key]  Added unique orders: ${newOrders.map { it.orderNumber }}")

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
