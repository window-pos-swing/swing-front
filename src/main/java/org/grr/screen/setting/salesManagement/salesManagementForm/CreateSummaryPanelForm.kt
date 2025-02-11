package org.grr.screen.setting.salesManagement.salesManagementForm

import org.grr.`object`.JsonFormatter
import org.grr.screen.setting.salesManagement.*
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import java.awt.*
import javax.swing.*

class CreateSummaryPanelForm: JPanel() {
    private val paymentCompletedLabel = JLabel()
    private val paymentCompletedPriceLabel = JLabel()
    private val meetPaymentCompletedCardLabel = JLabel()
    private val meetPaymentCompletedCardPriceLabel = JLabel()
    private val meetPaymentCompletedCashLabel = JLabel()
    private val meetPaymentCompletedCashPriceLabel = JLabel()
    private val deliveryCompletedLabel = JLabel()
    private val deliveryCompletedPriceLabel = JLabel()
    private val deliveryCancelLabel = JLabel()
    private val deliveryCancelPriceLabel = JLabel()
    private val takeOutCompletedLabel = JLabel()
    private val takeOutCompletedPriceLabel = JLabel()
    private val takeOutCancelLabel = JLabel()
    private val takeOutCancelPriceLabel = JLabel()

    init {
        layout = GridLayout(1, 2, 0, 0)
        background = Color.WHITE
        border = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY)

        // 왼쪽 패널 (결제완료, 후불결제)
        val leftPanel = JPanel().apply {
            layout = GridLayout(1, 3, 0, 0) // 내부 간격 0
            background = Color.WHITE
            border = BorderFactory.createLineBorder(Color.GRAY, 1)

            // 결제완료 패널
            val paymentCompletePanel = JPanel().apply {
                layout = BorderLayout()
                background = Color.WHITE
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 0, 10, 0), // 위(top), 왼쪽(left), 아래(bottom), 오른쪽(right) 여백 추가
                    BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY) // 오른쪽 경계선 추가
                )

                add(JLabel("결제완료", SwingConstants.CENTER).apply {
                    font = MyFont.Bold(16f)
                    foreground = MyColor.DARK_NAVY
                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
                }, BorderLayout.NORTH)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0) // 숫자와 단위를 가로로 정렬
                    background = Color.WHITE
                    add(paymentCompletedLabel.apply {
                        text = JsonFormatter.formatNumber(getPaymentCompletedCount())
                        font = MyFont.Bold(20f)
                        foreground = MyColor.LIGHT_BLUE
                    })
                    add(JLabel(" 건").apply {
                        font = MyFont.Medium(15f)
                        foreground = Color.BLACK
                    })
                }, BorderLayout.CENTER)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                    background = Color.WHITE
                    add(paymentCompletedPriceLabel.apply {
                        text = JsonFormatter.formatNumber(getPaymentCompletedPrice())
                        font = MyFont.Bold(20f)
                        foreground = MyColor.LIGHT_BLUE
                    })
                    add(JLabel(" 원").apply {
                        font = MyFont.Medium(15f)
                        foreground = Color.BLACK
                    })
                }, BorderLayout.SOUTH)
            }

            // 후불결제 - 카드 패널
            val postpaidCardPanel = JPanel().apply {
                layout = BorderLayout()
                background = Color.WHITE
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 0, 10, 0), // 위(top), 왼쪽(left), 아래(bottom), 오른쪽(right) 여백 추가
                    BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY) // 오른쪽 경계선 추가
                )

                add(JLabel("만나서 카드 결제", SwingConstants.CENTER).apply {
                    font = MyFont.Bold(16f)
                    foreground = MyColor.DARK_NAVY
                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
                }, BorderLayout.NORTH)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                    background = Color.WHITE
                    add(meetPaymentCompletedCardLabel.apply {
                        text = JsonFormatter.formatNumber(getMeetPaymentCompletedCardCount())
                        font = MyFont.Bold(20f)
                        foreground = MyColor.LIGHT_BLUE
                    })
                    add(JLabel(" 건").apply {
                        font = MyFont.Medium(15f)
                        foreground = Color.BLACK
                    })
                }, BorderLayout.CENTER)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                    background = Color.WHITE
                    add(meetPaymentCompletedCardPriceLabel.apply {
                        text = JsonFormatter.formatNumber(getMeetPaymentCompletedCardPrice())
                        font = MyFont.Bold(20f)
                        foreground = MyColor.LIGHT_BLUE
                    })
                    add(JLabel(" 원").apply {
                        font = MyFont.Medium(15f)
                        foreground = Color.BLACK
                    })
                }, BorderLayout.SOUTH)
            }

            // 후불결제 - 현금 패널 (마지막이므로 경계선 없음)
            val postpaidCashPanel = JPanel().apply {
                layout = BorderLayout()
                background = Color.WHITE
                border = BorderFactory.createEmptyBorder(10, 0, 10, 0)

                add(JLabel("만나서 현금 결제", SwingConstants.CENTER).apply {
                    font = MyFont.Bold(16f)
                    foreground = MyColor.DARK_NAVY
                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
                }, BorderLayout.NORTH)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                    background = Color.WHITE
                    add(meetPaymentCompletedCashLabel.apply {
                        text = JsonFormatter.formatNumber(getMeetPaymentCompletedCashCount())
                        font = MyFont.Bold(20f)
                        foreground = MyColor.LIGHT_BLUE
                    })
                    add(JLabel(" 건").apply {
                        font = MyFont.Medium(15f)
                        foreground = Color.BLACK
                    })
                }, BorderLayout.CENTER)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                    background = Color.WHITE
                    add(meetPaymentCompletedCashPriceLabel.apply {
                        text = JsonFormatter.formatNumber(getMeetPaymentCompletedCashPrice())
                        font = MyFont.Bold(20f)
                        foreground = MyColor.LIGHT_BLUE
                    })
                    add(JLabel(" 원").apply {
                        font = MyFont.Medium(15f)
                        foreground = Color.BLACK
                    })
                }, BorderLayout.SOUTH)
            }

            // 패널 추가
            add(paymentCompletePanel)
            add(postpaidCardPanel)
            add(postpaidCashPanel)
        }

        // 오른쪽 패널 (배달, 포장)
        val rightPanel = JPanel().apply {
            layout = GridLayout(1, 2, 0, 0)
            background = Color.WHITE
            border = BorderFactory.createLineBorder(Color.GRAY, 1) // 테두리 추가

//                배달 패널
            val deliveryPanel = JPanel().apply {
                layout = BorderLayout()
                background = Color.WHITE
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 0, 10, 0), // 위(top), 왼쪽(left), 아래(bottom), 오른쪽(right) 여백 추가
                    BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY) // 오른쪽 경계선 추가
                )

                add(JLabel("배달", SwingConstants.CENTER).apply {
                    font = MyFont.Bold(16f)
                    foreground = MyColor.DARK_NAVY
                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
                }, BorderLayout.NORTH)

                add(JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS) // 세로로 정렬
                    background = Color.WHITE

                    add(JPanel().apply {
                        layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                        background = Color.WHITE

                        add(RoundedButton("완료").apply {
                            font = MyFont.Bold(14f)
                            foreground = Color.WHITE
                            setCustomBackground(Color.PINK)
                            border = null
                            preferredSize = Dimension(45, 24)
                            horizontalAlignment = SwingConstants.CENTER
                        })
                        add(Box.createHorizontalStrut(10))
                        add(deliveryCompletedLabel.apply {
                            text = JsonFormatter.formatNumber(getDeliveryCompletedCount())
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })
                        add(Box.createHorizontalStrut(3))
                        add(JLabel(" 건").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })
                        add(Box.createHorizontalStrut(5))
                        add(deliveryCompletedPriceLabel.apply {
                            text = JsonFormatter.formatNumber(getDeliveryCompletedPrice())
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })
                        add(Box.createHorizontalStrut(3))
                        add(JLabel(" 원").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })
                    })

                    add(Box.createVerticalStrut(10))

                    add(JPanel().apply {
                        layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                        background = Color.WHITE

                        add(RoundedButton("취소").apply {
                            font = MyFont.Bold(14f)
                            foreground = Color.WHITE
                            setCustomBackground(Color.GRAY)
                            border = null
                            preferredSize = Dimension(45, 24)
                            horizontalAlignment = SwingConstants.CENTER
                        })
                        add(Box.createHorizontalStrut(10))
                        add(deliveryCancelLabel.apply {
                            text = JsonFormatter.formatNumber(getDeliveryCancelCount())
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })
                        add(Box.createHorizontalStrut(3))
                        add(JLabel(" 건").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })
                        add(Box.createHorizontalStrut(5))
                        add(deliveryCancelPriceLabel.apply {
                            text = JsonFormatter.formatNumber(getDeliveryCancelPrice())
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })
                        add(Box.createHorizontalStrut(3))
                        add(JLabel(" 원").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })
                    })
                }, BorderLayout.CENTER)
            }

//                포장 패널
            val packingPanel = JPanel().apply {
                layout = BorderLayout()
                background = Color.WHITE
                border = BorderFactory.createEmptyBorder(10, 0, 10, 0)

                add(JLabel("포장", SwingConstants.CENTER).apply {
                    font = MyFont.Bold(16f)
                    foreground = MyColor.DARK_NAVY
                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
                }, BorderLayout.NORTH)

                add(JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS) // 세로로 정렬
                    background = Color.WHITE

                    add(JPanel().apply {
                        layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                        background = Color.WHITE

                        add(RoundedButton("완료").apply {
                            font = MyFont.Bold(14f)
                            foreground = Color.WHITE
                            setCustomBackground(Color.PINK)
                            border = null
                            preferredSize = Dimension(45, 24)
                            horizontalAlignment = SwingConstants.CENTER
                        })
                        add(Box.createHorizontalStrut(10))
                        add(takeOutCompletedLabel.apply {
                            text = JsonFormatter.formatNumber(getTakeOutCompletedCount())
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })
                        add(Box.createHorizontalStrut(3))
                        add(JLabel(" 건").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })
                        add(Box.createHorizontalStrut(5))
                        add(takeOutCompletedPriceLabel.apply {
                            text = JsonFormatter.formatNumber(getTakeOutCompletedPrice())
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })
                        add(Box.createHorizontalStrut(3))
                        add(JLabel(" 원").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })
                    })

                    add(Box.createVerticalStrut(10))

                    add(JPanel().apply {
                        layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                        background = Color.WHITE

                        add(RoundedButton("취소").apply {
                            font = MyFont.Bold(14f)
                            foreground = Color.WHITE
                            setCustomBackground(Color.GRAY)
                            border = null
                            preferredSize = Dimension(45, 24)
                            horizontalAlignment = SwingConstants.CENTER
                        })
                        add(Box.createHorizontalStrut(10))
                        add(takeOutCancelLabel.apply {
                            text = JsonFormatter.formatNumber(getTakeOutCancelCount())
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })
                        add(Box.createHorizontalStrut(3))
                        add(JLabel(" 건").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })
                        add(Box.createHorizontalStrut(5))
                        add(takeOutCancelPriceLabel.apply {
                            text = JsonFormatter.formatNumber(getTakeOutCancelPrice())
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })
                        add(Box.createHorizontalStrut(3))
                        add(JLabel(" 원").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })
                    })
                }, BorderLayout.CENTER)
            }

            add(deliveryPanel)
            add(packingPanel)
        }

        val summaryPanel = JPanel().apply {
            layout = GridLayout(1, 2, 10, 0) // 10px의 수평 간격(HGap), 0px의 수직 간격(VGap)
            background = Color.WHITE

            // 패널 추가
            add(leftPanel)
            add(rightPanel)
        }
        add(summaryPanel)
    }

    fun updateSummary() {
        paymentCompletedLabel.text = JsonFormatter.formatNumber(getPaymentCompletedCount())
        paymentCompletedPriceLabel.text = JsonFormatter.formatNumber(getPaymentCompletedPrice())
        meetPaymentCompletedCardLabel.text = JsonFormatter.formatNumber(getMeetPaymentCompletedCardCount())
        meetPaymentCompletedCardPriceLabel.text = JsonFormatter.formatNumber(getMeetPaymentCompletedCardPrice())
        meetPaymentCompletedCashLabel.text = JsonFormatter.formatNumber(getMeetPaymentCompletedCashCount())
        meetPaymentCompletedCashPriceLabel.text = JsonFormatter.formatNumber(getMeetPaymentCompletedCashPrice())
        deliveryCompletedLabel.text = JsonFormatter.formatNumber(getDeliveryCompletedCount())
        deliveryCompletedPriceLabel.text = JsonFormatter.formatNumber(getDeliveryCompletedPrice())
        deliveryCancelLabel.text = JsonFormatter.formatNumber(getDeliveryCancelCount())
        deliveryCancelPriceLabel.text = JsonFormatter.formatNumber(getDeliveryCancelPrice())
        takeOutCompletedLabel.text = JsonFormatter.formatNumber(getTakeOutCompletedCount())
        takeOutCompletedPriceLabel.text = JsonFormatter.formatNumber(getTakeOutCompletedPrice())
        takeOutCancelLabel.text = JsonFormatter.formatNumber(getTakeOutCancelCount())
        takeOutCancelPriceLabel.text = JsonFormatter.formatNumber(getTakeOutCancelPrice())

        // UI 업데이트 적용
        revalidate()
        repaint()
    }
}