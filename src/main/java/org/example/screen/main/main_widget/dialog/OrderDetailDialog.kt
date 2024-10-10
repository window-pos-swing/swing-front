package org.example.screen.main.main_widget.dialog

import CustomRoundedDialog
import LoadImage
import LoadImage.loadImage
import org.example.MyFont
import org.example.MyFont.Bold
import org.example.model.Order
import org.example.style.MyColor
import org.example.widgets.FillRoundedButton
import org.example.widgets.FillRoundedLabel
import org.example.widgets.IconRoundBorder
import java.awt.*
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer


class OrderDetailDialog(
    parent: JFrame,
    cardPanel: JPanel,
    title: String,
    order: Order,
) : CustomRoundedDialog(parent, title, 1000, 833) {

    init {
        // 메인 패널을 생성하고, 내용을 넣음
        val mainPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)  // 수직 레이아웃
            border = EmptyBorder(10, 30, 30, 30)
            background = Color.WHITE
        }

        // 배달 정보 패널 추가
        val deliveryInfoPanel = createDeliveryInfoPanel(order).apply {
            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(deliveryInfoPanel)

        // 요청 사항 패널 추가
        val storeRequestInfoPanel = createRequestInfoPanel("가게" , order).apply {
            border = EmptyBorder(10, 0, 0, 0)
//            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
//            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(storeRequestInfoPanel)

        val deliveryRequestInfoPanel = createRequestInfoPanel("배달" , order).apply {
            border = EmptyBorder(10, 0, 0, 0)
//            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
//            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(deliveryRequestInfoPanel)

        // 주문 정보 패널 추가
        val orderInfoPanel = createOrderInfoPanel(order).apply {
            border = EmptyBorder(10, 0, 0, 0)
//            preferredSize = Dimension(940, preferredSize.height)  // 패널 너비 고정
//            maximumSize = Dimension(940, Int.MAX_VALUE)  // 패널이 확장되지 않도록 최대 크기 설정
        }
        mainPanel.add(orderInfoPanel)

        // 테스트용 임시 패널 추가 (내용이 많아져서 스크롤이 발생하게 만듦)
//        for (i in 1..10) {
//            mainPanel.add(JPanel().apply {
//                preferredSize = Dimension(900, 200)  // 큰 크기의 임시 패널
//                background = Color.LIGHT_GRAY
//                add(JLabel("테스트 패널 $i").apply {
//                    font = MyFont.Bold(20f)
//                })
//            })
//        }

        // 메인 패널을 스크롤 가능한 패널로 설정
        val scrollPane = JScrollPane(mainPanel).apply {
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            border = null  // 불필요한 테두리 없애기
        }
        scrollPane.getViewport().addChangeListener {
            scrollPane.repaint()
        }

        // 스크롤 가능한 패널을 다이얼로그에 추가
        add(scrollPane)

        // 다이얼로그 크기 설정
        setSize(1000, 833)
        setLocationRelativeTo(cardPanel)
        isVisible = true
    }


    // 배달 정보 패널 생성
    private fun createDeliveryInfoPanel(order:Order): JPanel {
        val panel = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
        }

        // 헤더 패널 추가
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(JLabel(LoadImage.loadImage("/pin_icon.png", 20, 20)))
            add(JLabel("배달 정보").apply {
                font = MyFont.Bold(24f)
                foreground = Color.BLACK
                border = EmptyBorder(20, 0, 0, 0)
            })
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // contentPanel 생성 (좌우로 나뉨)
        val contentPanel = JPanel(GridLayout(1, 2, 10, 0)).apply {
            background = Color.WHITE
            border = CompoundBorder(
                LineBorder(MyColor.GREY400, 1), // 기존 테두리 추가
                EmptyBorder(10, 10, 10, 10) // 안쪽 패딩 10씩 추가
            )
            preferredSize = Dimension(940, 182) // 원하는 높이와 너비 설정
            minimumSize = Dimension(940, 182) // 최소 높이와 너비 설정
            maximumSize = Dimension(940, 182) // 최대 높이와 너비 설정
        }

        // leftPanel 생성 (좌우로 나뉨)
        val leftPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
            border = CompoundBorder(
                EmptyBorder(0, 0, 0, 0), // 상하 패딩 10씩 추가
                BorderFactory.createMatteBorder(0, 0, 0, 1, MyColor.GREY400), // 오른쪽에만 테두리 추가
            )
        }

        // leftInleftPanel: 레이블 패널 (주소, 연락처, 결제방법)
        val leftInleftPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            preferredSize = Dimension(80, 250)
            add(JLabel("주소").apply { font = MyFont.Bold(20f) })
            add(Box.createVerticalStrut(35))
            add(JLabel("연락처").apply { font = MyFont.Bold(20f) })
            add(Box.createVerticalStrut(30))
            add(JLabel("결제방법").apply { font = MyFont.Bold(20f) })
        }

        // rigthInleftPanel: 실제 값 패널 (주소 값, 연락처 값, 결제방법 값)
        val rigthInleftPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            alignmentX = Component.LEFT_ALIGNMENT
            border = EmptyBorder(0, 20, 0, 20)

            // 주소 값 - JTextArea 사용
            add(JTextArea(order.address).apply {
                font = MyFont.SemiBold(18f)
                lineWrap = true   // 텍스트가 길어질 경우 줄바꿈 허용
                wrapStyleWord = true  // 단어 단위로 줄바꿈
                isEditable = false
                isOpaque = false
                border = null
                maximumSize = Dimension(400, 50) // 높이를 제한
                alignmentX = Component.LEFT_ALIGNMENT
                alignmentY = Component.TOP_ALIGNMENT
            })

            add(Box.createVerticalStrut(15))

            // 연락처 값 - JLabel 사용
            add(JLabel(order.CustomerPhonenumber).apply {
                font = MyFont.SemiBold(18f)
                alignmentX = Component.LEFT_ALIGNMENT
                alignmentY = Component.TOP_ALIGNMENT
                maximumSize = Dimension(400, 30)
            })

            add(Box.createVerticalStrut(30))

            // 결제방법 값 - JLabel 사용
            add(JLabel(order.paymentMethod).apply {
                font = MyFont.SemiBold(18f)
                alignmentX = Component.LEFT_ALIGNMENT
                alignmentY = Component.TOP_ALIGNMENT
                maximumSize = Dimension(400, 30)
            })
        }

        // leftPanel에 leftInleftPanel과 rigthInleftPanel 추가
        leftPanel.add(leftInleftPanel, BorderLayout.WEST)
        leftPanel.add(rigthInleftPanel, BorderLayout.CENTER)

        // rightPanel 생성 (좌우로 나뉨)
        val rightPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE
        }

        // leftInrightPanel: 레이블 패널 (접수일시, 예상조리시간, 배달예상시간)
        val leftInrightPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            preferredSize = Dimension(200, 250)
            add(JLabel("접수 일시").apply { font = MyFont.Bold(20f) })
            add(Box.createVerticalStrut(30))
            add(JLabel("예상 조리 시간 (라이더)").apply { font = MyFont.Bold(20f) })
            add(Box.createVerticalStrut(30))
            add(JLabel("배달 예상 시간 (고객)").apply { font = MyFont.Bold(20f) })
        }

        // rightInrightPanel: 실제 값 패널 (접수일시 값, 예상조리시간 값, 배달예상시간 값)
        val rightInrightPanel = JPanel().apply {
            border = EmptyBorder(0, 20, 0, 20)
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            val receivedTimeText = order.ReceivedTime?.toString() ?: "접수전"
            add(JLabel(receivedTimeText).apply { font = MyFont.SemiBold(18f) })
            add(Box.createVerticalStrut(35))
            add(JLabel("20분").apply { font = MyFont.SemiBold(18f) })
            add(Box.createVerticalStrut(35))
            add(JLabel("45분").apply { font = MyFont.SemiBold(18f) })
        }

        // rightPanel에 leftInrightPanel과 rightInrightPanel 추가
        rightPanel.add(leftInrightPanel, BorderLayout.WEST)
        rightPanel.add(rightInrightPanel, BorderLayout.CENTER)

        // contentPanel에 leftPanel과 rightPanel 추가
        contentPanel.add(leftPanel)
        contentPanel.add(rightPanel)

        // panel에 contentPanel 추가
        panel.add(contentPanel, BorderLayout.CENTER)

        return panel
    }

    private fun createRequestInfoPanel(type: String, order:Order): JPanel {
        val panel = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
        }

        // 아이콘 및 제목 추가
        val headerPanel = JPanel(BorderLayout()).apply {
            background = Color.WHITE

            // 왼쪽에 아이콘과 제목 추가
            val leftPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                background = Color.WHITE
                add(JLabel(LoadImage.loadImage("/pin_icon.png", 20, 20)))
                add(JLabel("${type} 요청 사항").apply {
                    font = MyFont.Bold(24f)
                    foreground = Color.BLACK
                    border = EmptyBorder(20, 0, 0, 0)
                })
            }
            add(leftPanel, BorderLayout.WEST)

            val spoonFork = if (order.spoonFork) "수저/포크 O" else "수저/포크 X"
            val deliveryType = "비대면 배달"

            // 오른쪽에 수저/포크 또는 배달 라벨 추가
            val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
                background = Color.WHITE
                border = EmptyBorder(25, 0, 0, 0) // 라벨 주위 여백 설정
                val labelText = if (type == "배달") deliveryType else spoonFork
                val labelBackgroundColor = if (type == "배달") MyColor.PINK else MyColor.Yellow

                add(
                    FillRoundedLabel(
                        text = labelText, // 라벨 텍스트 (배달 또는 수저/포크)
                        borderColor = labelBackgroundColor, // 테두리 색상
                        backgroundColor = labelBackgroundColor, // 배경색 설정
                        textColor = Color.WHITE, // 텍스트 색상
                        borderRadius = 30, // 라벨의 둥근 정도
                        borderWidth = 2, // 테두리 두께
                        textAlignment = SwingConstants.CENTER, // 텍스트 정렬
                        padding = Insets(5, 10, 5, 10) // 패딩 (위, 왼쪽, 아래, 오른쪽)
                    ).apply {
                        font = MyFont.SemiBold(16f)
                        preferredSize = Dimension(115, 28)  // 크기를 115x28로 설정
                        maximumSize = Dimension(115, 28)
                    }
                )
            }
            add(rightPanel, BorderLayout.EAST)
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // 세부 요청 사항 패널
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = CompoundBorder(
                LineBorder(MyColor.GREY400, 1),  // 테두리 색상과 두께 설정
                EmptyBorder(10, 10, 10, 10)  // 기본 내부 여백 설정
            )
        }

        // 요청 사항 텍스트를 JTextArea로 생성하여 추가
        val requestTextArea = JTextArea(order.request).apply {
            font = MyFont.Medium(18f)
            foreground = Color.BLACK
            background = Color.WHITE
            lineWrap = true  // 텍스트가 길어질 경우 줄바꿈 허용
            wrapStyleWord = true  // 단어 단위로 줄바꿈
            isEditable = false  // 수정 불가능하게 설정
            isOpaque = false  // 배경 투명 설정
            border = null  // 불필요한 경계 제거
        }

        // 텍스트 길이에 따라 동적으로 높이 조정
        contentPanel.add(requestTextArea)
        panel.add(contentPanel, BorderLayout.CENTER)

        // 패널의 크기를 동적으로 계산하도록 요청
        panel.revalidate()
        panel.repaint()

        return panel
    }

    // 주문 정보 패널 생성
    private fun createOrderInfoPanel(order: Order): JPanel {
        val panel = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
        }

        // 아이콘 및 제목 추가
        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            background = Color.WHITE
            add(JLabel(LoadImage.loadImage("/pin_icon.png", 20, 20)))
            add(JLabel("주문 정보").apply {
                font = MyFont.Bold(24f)
                foreground = Color.BLACK
                border = EmptyBorder(20, 0, 0, 0)
            })
        }
        panel.add(headerPanel, BorderLayout.NORTH)

        // 세부 주문 정보 패널 (메뉴, 수량, 금액)
        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = CompoundBorder(
                LineBorder(MyColor.GREY400, 1),  // 테두리 색상과 두께 설정
                EmptyBorder(15, 10, 0, 10)  // 기본 내부 여백 설정
            )
        }

        // 메뉴, 수량, 금액 정보를 담을 패널 생성
        val orderInfoContainer = JPanel().apply {
            layout = GridBagLayout()
            background = Color.WHITE
            border = EmptyBorder(0, 0, -15, 0)  // 기본 내부 여백 설정

        }

        val constraints = GridBagConstraints().apply {
            fill = GridBagConstraints.BOTH
            weighty = 1.0
            weighty = 0.0 // 여백 최소화
            insets = Insets(0, 0, 0, 0)
        }

        val menuPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            alignmentX = Component.LEFT_ALIGNMENT
        }
        val quantityPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            alignmentX = Component.CENTER_ALIGNMENT
        }
        val pricePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            alignmentX = Component.RIGHT_ALIGNMENT
        }

        // 각 패널을 orderInfoContainer에 추가 (비율에 따라 크기 설정)
        constraints.weightx = 0.7
        constraints.gridx = 0
        orderInfoContainer.add(menuPanel, constraints)

        constraints.weightx = 0.1
        constraints.gridx = 1
        orderInfoContainer.add(quantityPanel, constraints)

        constraints.weightx = 0.2
        constraints.gridx = 2
        orderInfoContainer.add(pricePanel, constraints)

        // 메뉴, 수량, 금액 타이틀 추가
        menuPanel.add(JLabel("메뉴").apply { font = MyFont.SemiBold(22f) })
        quantityPanel.add(JLabel("수량").apply { font = MyFont.SemiBold(22f); alignmentX = Component.CENTER_ALIGNMENT })
        pricePanel.add(JLabel("금액").apply { font = MyFont.SemiBold(22f); alignmentX = Component.RIGHT_ALIGNMENT })

        menuPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        quantityPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        pricePanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가

        menuPanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })
        quantityPanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })
        pricePanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })

        menuPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        quantityPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        pricePanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가

        // order.menuList의 데이터를 순회하며 각 메뉴, 수량, 가격을 패널에 추가
        order.menuList.forEach { menu ->
            // 메뉴 이름 추가
            menuPanel.add(JLabel(menu.menuName).apply {
                font = MyFont.SemiBold(22f)
            })

            // 옵션 추가
            menu.options.forEach { option ->
                menuPanel.add(JLabel("  • ${option.optionName}").apply {
                    font = MyFont.Regular(20f)
                })
            }

            // 수량 추가
            quantityPanel.add(JLabel(menu.count.toString()).apply {
                font = MyFont.SemiBold(22f)
                alignmentX = Component.CENTER_ALIGNMENT
            })

            // 옵션 수량은 빈 값으로 추가
            menu.options.forEach {
                quantityPanel.add(JLabel(" ").apply {
                    font = MyFont.Regular(20f)
                })
            }

            // 가격 추가
            pricePanel.add(JLabel("${menu.price}원").apply {
                font = MyFont.SemiBold(22f)
                alignmentX = Component.RIGHT_ALIGNMENT
            })

            // 옵션 가격 추가
            menu.options.forEach { option ->
                pricePanel.add(JLabel("${option.optionPrice}원").apply {
                    font = MyFont.Regular(20f)
                    alignmentX = Component.RIGHT_ALIGNMENT
                })
            }

            // 메뉴 사이클이 끝날 때 공백 추가
            menuPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
            quantityPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
            pricePanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가

        }

        menuPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        quantityPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        pricePanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가

        menuPanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })
        quantityPanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })
        pricePanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })

        menuPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        quantityPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        pricePanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가

        // 배달비 추가
        menuPanel.add(JLabel("배달비").apply {
            font = MyFont.SemiBold(22f)
        })
        quantityPanel.add(JLabel(" ").apply {
            font = MyFont.SemiBold(22f)
        })
        pricePanel.add(JLabel("${order.deliveryFee}원").apply {
            font = MyFont.SemiBold(22f)
            alignmentX = Component.RIGHT_ALIGNMENT
        })

        menuPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        quantityPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        pricePanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가

        // 구분선 추가
        menuPanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })
        quantityPanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })
        pricePanel.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            border = LineBorder(MyColor.GREY400, 1) // 기존 테두리 추가
            maximumSize = Dimension(Int.MAX_VALUE, 1)
        })

        menuPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        quantityPanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가
        pricePanel.add(Box.createVerticalStrut(15)) // 10픽셀 높이의 공백 추가

        // 총합 추가
        val totalCount = order.menuList.sumOf { it.count }
        val totalPrice = order.menuList.sumOf { it.price + it.options.sumOf { option -> option.optionPrice } } + order.deliveryFee

        menuPanel.add(JLabel("총합").apply {
            font = MyFont.Bold(22f)
            foreground = Color.RED
        })
        quantityPanel.add(JLabel(totalCount.toString()).apply {
            font = MyFont.Bold(22f)
            foreground = Color.RED
            alignmentX = Component.CENTER_ALIGNMENT
        })
        pricePanel.add(JLabel("${totalPrice}원").apply {
            font = MyFont.Bold(22f)
            foreground = Color.RED
            alignmentX = Component.RIGHT_ALIGNMENT
        })

        // 전체 contentPanel에 orderInfoContainer 추가
        contentPanel.add(orderInfoContainer)

        // contentPanel을 panel에 추가
        panel.add(contentPanel, BorderLayout.CENTER)

        return panel
    }


}
