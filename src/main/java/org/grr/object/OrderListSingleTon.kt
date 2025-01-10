package org.grr.`object`

import org.grr.model.ReceiveOrderModel
import org.json.JSONArray
import javax.swing.JFrame
import javax.swing.JPanel

//주문 데이터 싱글톤 관리
object OrderListSingleTon {
    private val orders: MutableList<ReceiveOrderModel> = mutableListOf()
    var isLoadData : Boolean = false

    // 새로운 주문을 배열의 맨 앞에 추가
    fun addOrder(order: ReceiveOrderModel) {
        orders.add(0, order) // 인덱스 0에 삽입
    }

    fun addAllOrder(orderListJson: String, parentFrame: JFrame, cardPanel: JPanel) {
        try {
            val jsonArray = JSONArray(orderListJson) // JSON 배열로 변환
            val newOrders = ReceiveOrderModel.fromJsonArray(jsonArray, parentFrame, cardPanel) // JSON 배열 -> 모델 리스트 변환

            orders.addAll(newOrders) // 리스트 추가
            println("addAllOrder: Successfully added ${newOrders.size} orders.")
        } catch (e: Exception) {
            e.printStackTrace()
            println("addAllOrder: Error parsing orderListJson - ${e.message}")
        }
        isLoadData = true;
    }

    // 전체 주문 가져오기
    fun getOrders(): List<ReceiveOrderModel> {
        println("[getOrders] : $orders")
        return orders
    }

    // 특정 주문 가져오기
    fun getOrderById(id: Int): ReceiveOrderModel? {
        return orders.find { it.id == id }
    }

    // 주문 초기화 (필요시)
    fun clearOrders() {
        orders.clear()
    }
}
