package org.grr.screen.setting.salesManagement

import CustomRoundedDialog
import org.grr.api.SaleManagementAPI
import org.grr.model.OrderCategory
import org.grr.model.formatToDisplay
import org.grr.screen.setting.salesManagement.SalesManagementData.totalLabelPanel
import org.grr.screen.setting.salesManagement.SalesManagementData.totalSalesSummary
import org.grr.screen.setting.salesManagement.ShareData.tableModel
import org.grr.screen.setting.salesManagement.salesManagementForm.*
import org.json.JSONArray
import org.json.JSONObject
import java.awt.*
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.*

class SalesManagementModalDialog(
    parent: JFrame,
    title: String,
    callback: ((Boolean) -> Unit)? = null
) : CustomRoundedDialog(parent, title, 1350, 890, callback) {
    private var currentPage = 0
    private val pageSize = 10
    private var startDate: String? = null
    private var endDate: String? = null
    private var isLoading = false
    private var stop: Boolean = false

    private val orderList = mutableListOf<OrderCategory>()

    init {
        setSize(1350, 890)
        setLocationRelativeTo(parent)
        background = Color.WHITE

        val mainPanel = JPanel().apply {
            layout = GridBagLayout()
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(10, 40, 20, 40)
        }

        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            weightx = 1.0
            insets = Insets(10, 0, 10, 0) // 각 컴포넌트 간의 간격
        }

        //        테이블 및 스크롤 패널 -> 테이블 초기화 항상 먼저 lateinit
        val tableScrollPane = CreateTableScrollPaneForm()

        tableScrollPane.verticalScrollBar.addAdjustmentListener(object : AdjustmentListener {
            override fun adjustmentValueChanged(e: AdjustmentEvent) {
                val scrollBar = e.adjustable
                val maxScroll = scrollBar.maximum - scrollBar.visibleAmount
                val currentScroll = scrollBar.value

                if (currentScroll >= maxScroll - 20) { // 스크롤이 거의 끝에 도달했을 때
                    loadOrderData()
                }
            }
        })

        loadOrderData()

        println("stop 버튼 : $stop")

        val tabBarPanel = CreateTabBarPanelForm()

        // 매출 요약 정보 패널 1
        val orderTotalLabelPanel = CreateOrderTotalLabelPanelForm()

        // 매출 요약 정보 패널 2
        val summaryPanel = CreateSummaryPanelForm()

        // 주문 목록 패널 생성
        val orderLabelPanel = CreateOrderLabelPanelForm()

        tabBarPanel.yesterdayButton.addActionListener {
            setSalesSelectedDate(getYesterdayDate())
            startDate = getYesterdayDate()
            endDate = getYesterdayDate()

            orderList.clear()
            updateTable(null)
            currentPage = 0
            stop = false

            loadOrderData()

            summaryPanel.updateSummary()
            orderTotalLabelPanel.updateTotal()
        }

        tabBarPanel.todayButton.addActionListener {
            setSalesSelectedDate(getYesterdayDate())
            startDate = getTodayDate()
            endDate = getTodayDate()

            orderList.clear()
            updateTable(null)
            currentPage = 0
            stop = false

            loadOrderData()

            summaryPanel.updateSummary()
            orderTotalLabelPanel.updateTotal()
        }
        var isFirstSelection = true

        tabBarPanel.startEndDatePicker.addDateChangeListener { dateEvent ->
            val selectedDate = dateEvent.newDate
            if (selectedDate != null) {
                if (isFirstSelection) {
                    // ✅ startDate를 즉시 업데이트
                    tabBarPanel.startDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    isFirstSelection = false  // 이제 종료 날짜 선택 대기
                } else {
                    // ✅ endDate를 즉시 업데이트
                    tabBarPanel.endDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                    // ✅ startDate와 endDate를 업데이트한 후 서버 요청 실행
                    startDate = tabBarPanel.startDate
                    endDate = tabBarPanel.endDate

                    setSalesSelectedDate("${startDate} - ${endDate}")
                    println("시작날짜 - 종료날짜 : $startDate - $endDate")

                    orderList.clear()
                    updateTable(null)
                    currentPage = 0
                    stop = false

                    loadOrderData()

                    summaryPanel.updateSummary()
                    orderTotalLabelPanel.updateTotal()

                    isFirstSelection = true  // ✅ 다음 선택을 위해 초기화
                }
            }
        }

        // 모든 패널 추가
        gbc.gridy = 0
        gbc.weighty = 0.1
        mainPanel.add(tabBarPanel, gbc)
        gbc.gridy = 1
        gbc.weighty = 0.1
        gbc.insets = Insets(10, 0, 0, 0)
        mainPanel.add(orderTotalLabelPanel, gbc)
        gbc.gridy = 2
        gbc.weighty = 0.2
        gbc.insets = Insets(0, 0, 10, 0)
        mainPanel.add(summaryPanel, gbc)
        gbc.gridy = 3
        gbc.weighty = 0.1
        gbc.insets = Insets(10, 0, 0, 0)
        mainPanel.add(orderLabelPanel, gbc)
        gbc.gridy = 4
        gbc.weighty = 0.5
        gbc.insets = Insets(10, 0, 10, 0)
        gbc.fill = GridBagConstraints.BOTH
        mainPanel.add(tableScrollPane, gbc)

        contentPane.add(mainPanel, BorderLayout.CENTER)
    }

    private fun loadOrderData() {
        if (isLoading) return // 중복 요청 방지
        isLoading = true

        if (!stop) {
            val (success, orderListDataResponse, posOrderSaleManagementTotalDataResponse) = SaleManagementAPI().getOrderListToServer(
                pageNumber = currentPage,
                pageSize = pageSize,
                startDate,
                endDate
            )
            totalSalesSummary = posOrderSaleManagementTotalDataResponse

            if (success && orderListDataResponse != null && orderListDataResponse.length() > 0) {
                val newOrders = orderListDataResponse.map { json ->
                    val order = json as JSONObject
                    OrderCategory(
                        id = order.optLong("id", 0L),
                        createAt = parseJsonDate(order.getJSONArray("createAt")), // ✅ 날짜 변환 추가
                        orderNumber = order.getString("orderNumber"),
                        orderReceiveType = order.getString("orderReceiveType"),
                        posOrderStatus = order.getString("posOrderStatus"),
                        totalOrderPrice = order.getInt("totalOrderPrice"),
                        paymentWayTypeStatus = order.getString("paymentWayTypeStatus")
                    )
                }
                orderList.addAll(newOrders)
                updateTable(orderList)

                currentPage++
            } else {
                stop = true
                println("추가 데이터 없음 or API 호출 실패")
            }
        }
        isLoading = false
    }

    //    예시 데이터 생성 구문
    private fun updateTable(orderCategory: List<OrderCategory>?) {
        // 기존 데이터 초기화
        tableModel.rowCount = 0

        // 새로운 데이터 추가
        orderCategory?.forEach { order ->
            tableModel.addRow(
                arrayOf(
                    order.createAt.formatToDisplay(),
                    order.orderNumber,
                    order.getFormattedOrderReceiveType(),
                    order.getFormattedPosOrderStatus(),
                    "${order.totalOrderPrice} 원",
                    order.getFormattedPaymentWayTypeStatus()
                )
            )
        }
    }

    // JSON 날짜 변환 함수
    private fun parseJsonDate(jsonArray: JSONArray): LocalDateTime {
        return LocalDateTime.of(
            jsonArray.getInt(0),  // Year
            jsonArray.getInt(1),  // Month
            jsonArray.getInt(2),  // Day
            jsonArray.getInt(3),  // Hour
            jsonArray.getInt(4),  // Minute
            jsonArray.getInt(5),  // Second
            jsonArray.getInt(6)   // Nanosecond
        )
    }
}