package org.example.view.states

import org.example.model.Order
import org.example.model.OrderState
import org.example.observer.OrderObserver
import org.example.view.components.BaseOrderPanel
import javax.swing.*
import java.awt.*

class ProcessingState(val totalTime: Int) : OrderState {
    override fun handle(order: Order) {
        // 주문 진행 처리 로직
        //order.startTimer(totalTime)  // 타이머 시작
    }

    override fun getUI(order: Order): JPanel {
        return BaseOrderPanel(order).apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)

            val footerPanel = getFooterPanel()

            // 프로그레스 바 생성, 진행된 시간을 반영
            val progressBar = JProgressBar(0, totalTime).apply {
                value = order.elapsedTime  // 저장된 진행 시간 반영
                isStringPainted = true
                preferredSize = Dimension(150, 30)
                string = "${order.elapsedTime} / $totalTime 분"
            }

            // 타이머가 매초 증가할 때마다 프로그레스바를 업데이트
            // 타이머가 매초 증가할 때마다 프로그레스바를 업데이트
            order.addTimerObserver(object : OrderObserver {
                override fun update(order: Order) {
                    // 프로그레스바만 다시 그리기
                    progressBar.value = order.elapsedTime
                    progressBar.string = "${order.elapsedTime} / $totalTime 분"
                    progressBar.repaint()  // 프로그레스바만 리페인트
//                    println("update addTimerObserver - 시간업데이트 처리")
                }
            })

            if (footerPanel != null) {
                footerPanel.add(Box.createHorizontalGlue())
                footerPanel.add(progressBar)
            } else {
                println("Error: footerPanel is null")
            }
        }
    }
}






