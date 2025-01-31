package org.grr.screen.setting.salesManagement.salesManagementForm

import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.RoundedButton
import java.awt.*
import javax.swing.*

class CreateSummaryPanelForm: JPanel() {
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
                    add(JLabel("1231223").apply {
                        font = MyFont.Bold(20f)
                        foreground = MyColor.LIGHT_BLUE // 숫자 색상 조정
                    })
                    add(JLabel(" 건").apply {
                        font = MyFont.Medium(15f)
                        foreground = Color.BLACK
                    })
                }, BorderLayout.CENTER)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                    background = Color.WHITE
                    add(JLabel("123,111,123").apply {
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

                add(JLabel("후불결제 - 카드", SwingConstants.CENTER).apply {
                    font = MyFont.Bold(16f)
                    foreground = MyColor.DARK_NAVY
                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
                }, BorderLayout.NORTH)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                    background = Color.WHITE
                    add(JLabel("1231223").apply {
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
                    add(JLabel("123,111,123").apply {
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

                add(JLabel("후불결제 - 현금", SwingConstants.CENTER).apply {
                    font = MyFont.Bold(16f)
                    foreground = MyColor.DARK_NAVY
                    border = BorderFactory.createEmptyBorder(10, 0, 10, 0)
                }, BorderLayout.NORTH)

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                    background = Color.WHITE
                    add(JLabel("1231223").apply {
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
                    add(JLabel("123,111,123").apply {
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

                        add(JLabel(" 1231223").apply {
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })

                        add(JLabel(" 건").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })

                        add(JLabel(" 123,111,123").apply {
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })

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

                        add(JLabel(" 1231223").apply {
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })

                        add(JLabel(" 건").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })

                        add(JLabel(" 123,111,123").apply {
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })

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

                        add(JLabel(" 1231223").apply {
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })

                        add(JLabel(" 건").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })

                        add(JLabel(" 123,111,123").apply {
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })

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

                        add(JLabel(" 1231223").apply {
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })

                        add(JLabel(" 건").apply {
                            font = MyFont.Medium(15f)
                            foreground = Color.BLACK
                        })

                        add(JLabel(" 123,111,123").apply {
                            font = MyFont.Bold(20f)
                            foreground = MyColor.LIGHT_BLUE
                        })

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
}