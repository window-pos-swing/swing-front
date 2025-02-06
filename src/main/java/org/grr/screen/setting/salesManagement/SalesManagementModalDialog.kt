package org.grr.screen.setting.salesManagement

import CustomRoundedDialog
import org.grr.model.OrderCategory
import org.grr.model.OrderData
import org.grr.model.formatToDisplay
import org.grr.screen.setting.salesManagement.ShareData.tableModel
import org.grr.screen.setting.salesManagement.salesManagementForm.*
import java.awt.*
import javax.swing.*

class SalesManagementModalDialog(
    parent: JFrame,
    title: String,
    callback: ((Boolean) -> Unit)? = null
) : CustomRoundedDialog(parent, title, 1350, 890, callback) {

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

        val tabBarPanel = CreateTabBarPanelForm()

        // 매출 요약 정보 패널 1
        val orderTotalLabelPanel = CreateOrderTotalLabelPanelForm()

        // 매출 요약 정보 패널 2
        val summaryPanel = CreateSummaryPanelForm()

        // 주문 목록 패널 생성
        val orderLabelPanel = CreateOrderLabelPanelForm()

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


        updateTable(OrderData.createSampleData())
    }


    //    예시 데이터 생성 구문
    private fun updateTable(orderCategory: List<OrderCategory>) {
        // 기존 데이터 초기화
        tableModel.rowCount = 0

        // 새로운 데이터 추가
        orderCategory.forEach { order ->
            tableModel.addRow(
                arrayOf(
                    order.orderDate.formatToDisplay(),
                    order.orderNumber,
                    order.orderType,
                    order.orderStatus,
                    "${order.orderPrice} 원",
                    order.orderMethod
                )
            )
        }
    }
}