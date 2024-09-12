package org.example.view.states

import OrderRejectCancelDialog
import org.example.command.RejectOrderCommand
import org.example.model.Order
import org.example.model.OrderState
import org.example.view.components.BaseOrderPanel
import javax.swing.*
import java.awt.*

class ProcessingState(private val totalTime: Int) : OrderState {
    override fun handle(order: Order) {
        // 접수진행 상태에서 필요한 처리
    }

    override fun getUI(order: Order): JPanel {
        return BaseOrderPanel(order).apply {
            val progressBar = JProgressBar(0, totalTime).apply {
                value = 0  // 처음에는 0으로 시작
                isStringPainted = true  // 진행 상태를 텍스트로 표시
            }

            val timer = Timer(1000) {  // 1초마다 프로그레스바 업데이트
                if (progressBar.value < totalTime) {
                    progressBar.value += 1  // 1씩 증가
                    progressBar.string = "${progressBar.value} / $totalTime 분"
                } else {
                    (it.source as Timer).stop()  // 시간이 다 되면 타이머 중지
                }
            }
            timer.start()  // 타이머 시작

            val rightPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(JLabel("주문 진행 중..."))
                add(progressBar)

                // 주문 취소 버튼: 주문 취소 다이얼로그 호출
                add(JButton("주문 취소").apply {
                    addActionListener {
                        OrderRejectCancelDialog(
                            SwingUtilities.getWindowAncestor(this) as JFrame,
                            "주문 취소 사유 선택",
                            "주문 취소 사유를 선택해 주세요.",
                            "주문 취소",
                            onReject = { cancelReason ->
                                val rejectOrderCommand = RejectOrderCommand(order, cancelReason, this@ProcessingState)
                                rejectOrderCommand.execute()
                            }
                        )
                    }
                })
            }

            add(rightPanel, BorderLayout.EAST)
        }
    }
}


