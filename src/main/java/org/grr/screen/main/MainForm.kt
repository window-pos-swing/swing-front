package org.grr.screen.main

import org.grr.`object`.OrderController
import kotlinx.coroutines.*
import org.grr.api.CurrentLoginStoreToServer
import org.grr.model.SettingModel
import org.grr.`object`.OrderListSingleTon
import org.grr.`object`.Storage
import org.grr.screen.main.main_widget.tab_manager.CustomTabbedPane
import org.grr.websocket.PosWebSocketClient
import org.grr.`object`.OverlayManager
import org.grr.widgets.custom_titlebar.MainCustomTitlebar
import org.json.JSONObject
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color
import java.awt.Dimension
import java.net.URI
import javax.swing.*


class MainForm : JFrame() {
    private var isInitialized = false // 초기화 여부 확인
    val cardPanel = JPanel(CardLayout())  // 카드 패널 생성
    private lateinit var tabbedPane: CustomTabbedPane // CustomTabbedPane 지연 초기화
    private lateinit var webSocketClient: PosWebSocketClient // WebSocketClient 지연 초기화

    init {
        if (!isInitialized) {
            isInitialized = true

            // 기존 타이틀바 제거 및 창 리사이즈 가능 설정
            isUndecorated = true
            isResizable = true  // 창 리사이즈 가능

            // 데이터를 비동기로 로드
            GlobalScope.launch {
                delay(300) // 0.3초 대기

                val currentMember = CurrentLoginStoreToServer()
                val (isSuccess, message) = currentMember.currentLoginStoreMemberToServer()

                delay(300) // 0.3초 대기

                SwingUtilities.invokeLater {
                    if (isSuccess) {
                        val memberData = JSONObject(message.substringAfter(""))
                        Storage.saveMemberInfo(memberData)
                        println("메인 화면 접속 시 세팅 정보 : ${memberData}")

                        // 로컬 데이터 및 주문 리스트 로드
                        println("===============================")
                        fetchUserAndOrders() // 데이터를 로드
                        println("===============================")

                        // 데이터 로드 완료 후 CustomTabbedPane 초기화
                        initializeTabbedPane()
                        // CustomTabbedPane 초기화 후 WebSocketClient와 org.grr.`object`.OrderController 생성
                        initializeWebSocketClient()

                        // 화면 갱신
                        revalidate()
                        repaint()
                    } else {
                        JOptionPane.showMessageDialog(this@MainForm, message, "오류", JOptionPane.ERROR_MESSAGE)
                    }
                }
            }

            // JFrame 기본 설정
            setupFrame()
        }
    }

    private fun setupFrame() {
        // JFrame의 여백 제거
        rootPane.border = BorderFactory.createEmptyBorder()

        // JFrame의 레이아웃을 BorderLayout으로 설정
        layout = BorderLayout()

        // 타이틀바와 콘텐츠를 구분하는 패널 생성
        val titleAndContentPanel = JPanel().apply {
            layout = BorderLayout()
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 여백 제거
        }

        // 상단에 MainCustomTitlebar 추가
        val customTitleBar = MainCustomTitlebar(this)
        titleAndContentPanel.add(customTitleBar, BorderLayout.NORTH)

        // 중앙에 cardPanel 추가
        cardPanel.background = Color(245, 245, 245)  // 기본 배경색 설정
        cardPanel.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 여백 제거
        titleAndContentPanel.add(cardPanel, BorderLayout.CENTER)

        // 타이틀바와 cardPanel을 담은 패널을 우측에 배치
        add(titleAndContentPanel, BorderLayout.CENTER)

        // 기본 창 설정
        setSize(1440, 1024)
        setLocationRelativeTo(null)  // 화면 중앙에 배치
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    private fun fetchUserAndOrders() {
        // [유저 셋팅 가져오기]
        SettingModel.loadCookDeliveryTime()
        SettingModel.loadBreakTime()
        SettingModel.loadOperateTime()
        SettingModel.loadHoliday()

        // [주문리스트 가져오기]
        OrderListSingleTon.initOrderData(parentFrame = this@MainForm, cardPanel = cardPanel)
    }

    private fun initializeTabbedPane() {
        // 오버레이 초기화
        OverlayManager.initialize(this, cardPanel)

        tabbedPane = CustomTabbedPane(this).apply {
            preferredSize = Dimension(200, height)
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)  // 여백 제거
        }

        // 좌측에 CustomTabbedPane 추가 (탭바)
        add(tabbedPane, BorderLayout.WEST)

        // CustomTabbedPane에 cardPanel 전달
        tabbedPane.setCardPanel(cardPanel)
        OverlayManager.update(this, cardPanel)
    }

    private fun initializeWebSocketClient() {
        val (savedEmail, savedPassword, autoCheck, storeCode) = Storage.getLoginInfo()

        // org.grr.`object`.OrderController 초기화
        OrderController.initialize(tabbedPane)
        OrderController.initializeOrders(OrderListSingleTon.orders["allOrders"]!!)

        // WebSocketClient 생성
        webSocketClient = PosWebSocketClient(
            URI("ws://localhost:8080/ws/orders?uid=${storeCode}"),
            parentFrame = this,
            cardPanel = cardPanel
        )

        // WebSocket 연결
        webSocketClient.connect()
    }

}

fun main() {
    val mainForm = MainForm()
    mainForm.isVisible = true
}
