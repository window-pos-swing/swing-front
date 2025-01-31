package org.grr.screen.setting.salesManagement

import CustomRoundedDialog
import org.grr.model.OrderCategory
import org.grr.model.OrderData
import org.grr.model.formatToDisplay
import org.grr.style.MyColor
import org.grr.util.MyFont
import org.grr.widgets.CustomScrollBarUI
import org.grr.widgets.RoundedButton
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class SalesManagementModalDialog(
    parent: JFrame,
    title: String,
    callback: ((Boolean) -> Unit)? = null
) : CustomRoundedDialog(parent, title, 1350, 890, callback) {

    private lateinit var tableModel: DefaultTableModel
    private lateinit var selectedDateLabel: JLabel
    private lateinit var datePickerButton: RoundedButton

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
        val tableScrollPane = createTableScrollPane()

        val tabBarPanel = createTabBarPanel()

        // 매출 요약 정보 패널 1
        val orderTotalLabelPanel = createOrderTotalLabelPanel()

        // 매출 요약 정보 패널 2
        val summaryPanel = createSummaryPanel()

        // 주문 목록 패널 생성
        val orderLabelPanel = createOrderLabelPanel()

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


    // 탭바 생성
    private fun createTabBarPanel(): JPanel {
        return JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT, 10, 0) // 버튼 간의 간격 설정
            background = Color.WHITE

            // 버튼 리스트를 관리하기 위한 리스트
            val buttons = mutableListOf<RoundedButton>()

            // 아이콘 파일 로드
            val resourceUrl = File("src/main/resources/Vector.png").toURI().toURL()
            val imageIcon = ImageIcon(resourceUrl)
            val scaledIcon = ImageIcon(imageIcon.image.getScaledInstance(23, 23, Image.SCALE_SMOOTH))

            // 흰색 아이콘 (선택 시 변경될 아이콘)
            val whiteIconUrl = File("src/main/resources/Vector_white.png").toURI().toURL() // 흰색 아이콘
            val whiteImageIcon = ImageIcon(whiteIconUrl)
            val whiteScaledIcon = ImageIcon(whiteImageIcon.image.getScaledInstance(23, 23, Image.SCALE_SMOOTH))

            // 버튼 색상 초기화 함수
            fun resetButtonColors() {
                buttons.forEach {
                    it.setCustomBackground(MyColor.LIGHT_GREY)
                    it.foreground = Color.GRAY // 기본 글씨 색상
                }
                // 날짜 선택 버튼 아이콘을 원래대로 복원
                datePickerButton.icon = scaledIcon
            }

            val yesterdayButton = RoundedButton("어제").apply {
                preferredSize = Dimension(150, 60)
                font = MyFont.Bold(22f)
                setCustomBackground(MyColor.LIGHT_GREY)
                foreground = Color.GRAY

                addActionListener {
                    resetButtonColors()
                    setCustomBackground(MyColor.LIGHT_BLUE)
                    foreground = Color.WHITE // 선택된 상태 글씨 색상
                    selectedDateLabel.text = "어제"
                    println("어제 버튼 클릭됨")
                }
            }

            val todayButton = RoundedButton("오늘").apply {
                preferredSize = Dimension(150, 60)
                font = MyFont.Bold(22f)
                setCustomBackground(MyColor.LIGHT_GREY)
                foreground = Color.GRAY

                addActionListener {
                    resetButtonColors()
                    setCustomBackground(MyColor.LIGHT_BLUE)
                    foreground = Color.WHITE // 선택된 상태 글씨 색상
                    selectedDateLabel.text = "오늘"
                    println("오늘 버튼 클릭됨")
                }
            }

            // 날짜 선택 버튼
            datePickerButton = RoundedButton("2025-01-24 ~ 2025-01-24").apply {
                preferredSize = Dimension(370, 60)
                font = MyFont.Bold(22f)
                setCustomBackground(MyColor.LIGHT_GREY)
                foreground = Color.GRAY

                icon = scaledIcon

                addActionListener {
                    resetButtonColors().apply {

                    }
                    setCustomBackground(MyColor.LIGHT_BLUE)
                    foreground = Color.WHITE // 선택된 상태 글씨 색상
                    selectedDateLabel.text = "2025-01-24 ~ 2025-01-24"

                    icon = whiteScaledIcon

                    println("날짜 선택 버튼 클릭됨")
                }
            }

            // 버튼 리스트에 추가
            buttons.add(yesterdayButton)
            buttons.add(todayButton)
            buttons.add(datePickerButton)

            // 요소 추가
            add(yesterdayButton)
            add(todayButton)
            add(datePickerButton)
        }
    }

    // 매출 요약 정보 패널 1
    private fun createOrderTotalLabelPanel(): JPanel {
        return JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE // 배경 설정
            isOpaque = true

            val labelPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                background = Color.WHITE
            }

            val orderLabel = JLabel("매출 요약").apply {
                font = MyFont.Bold(24f)

                // 아이콘 설정
                val resourceUrl = File("src/main/resources/bx_receipt.png").toURI().toURL()
                val imageIcon = ImageIcon(resourceUrl)
                val scaledIcon = ImageIcon(
                    imageIcon.image.getScaledInstance(30, 30, Image.SCALE_SMOOTH)
                ) // 이미지 크기 조정
                icon = scaledIcon
                horizontalAlignment = SwingConstants.LEFT // 텍스트와 아이콘의 정렬
                horizontalTextPosition = SwingConstants.RIGHT // 텍스트를 아이콘 오른쪽에 위치
                iconTextGap = 10 // 아이콘과 텍스트 간 간격 설정
            }

            selectedDateLabel = JLabel("전체").apply {
                font = MyFont.Bold(24f)
                foreground = MyColor.LIGHT_BLUE
            }

            val completeLabel = JLabel("완료 기준 : 4건 132,000원").apply {
                font = MyFont.Bold(24f)
            }

            labelPanel.add(orderLabel)
            labelPanel.add(Box.createHorizontalStrut(10))
            labelPanel.add(selectedDateLabel)
            labelPanel.add(Box.createHorizontalStrut(10))
            labelPanel.add(completeLabel)

            val printButton = RoundedButton("인쇄하기").apply {
                preferredSize = Dimension(135, 45)
                font = MyFont.Bold(22f)
                foreground = Color.WHITE
                setCustomBackground(MyColor.DARK_NAVY)
            }

            add(labelPanel, BorderLayout.WEST)
            add(printButton, BorderLayout.EAST)
        }
    }

    //    매출 요약 정보 패널2
    private fun createSummaryPanel(): JPanel {
        return JPanel().apply {
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

                            add(JLabel("1231223").apply {
                                font = MyFont.Bold(20f)
                                foreground = MyColor.LIGHT_BLUE
                            })

                            add(JLabel(" 건").apply {
                                font = MyFont.Medium(15f)
                                foreground = Color.BLACK
                            })

                            add(JLabel("123,111,123").apply {
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

                            add(JLabel("1231223").apply {
                                font = MyFont.Bold(20f)
                                foreground = MyColor.LIGHT_BLUE
                            })

                            add(JLabel(" 건").apply {
                                font = MyFont.Medium(15f)
                                foreground = Color.BLACK
                            })

                            add(JLabel("123,111,123").apply {
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

                            add(JLabel("1231223").apply {
                                font = MyFont.Bold(20f)
                                foreground = MyColor.LIGHT_BLUE
                            })

                            add(JLabel(" 건").apply {
                                font = MyFont.Medium(15f)
                                foreground = Color.BLACK
                            })

                            add(JLabel("123,111,123").apply {
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

                            add(JLabel("1231223").apply {
                                font = MyFont.Bold(20f)
                                foreground = MyColor.LIGHT_BLUE
                            })

                            add(JLabel(" 건").apply {
                                font = MyFont.Medium(15f)
                                foreground = Color.BLACK
                            })

                            add(JLabel("123,111,123").apply {
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

    // 주문 목록 패널 생성
    private fun createOrderLabelPanel(): JPanel {
        return JPanel().apply {
            layout = FlowLayout(FlowLayout.LEFT, 0, 0) // 좌측 정렬
            background = Color.WHITE // 배경 설정
            isOpaque = true

            val orderLabel = JLabel("주문 목록").apply {
                font = MyFont.Bold(24f)

                // 아이콘 설정
                val resourceUrl = File("src/main/resources/bx_receipt.png").toURI().toURL()
                val imageIcon = ImageIcon(resourceUrl)
                val scaledIcon = ImageIcon(
                    imageIcon.image.getScaledInstance(30, 30, Image.SCALE_SMOOTH)
                ) // 이미지 크기 조정
                icon = scaledIcon
                horizontalAlignment = SwingConstants.LEFT // 텍스트와 아이콘의 정렬
                horizontalTextPosition = SwingConstants.RIGHT // 텍스트를 아이콘 오른쪽에 위치
                iconTextGap = 10 // 아이콘과 텍스트 간 간격 설정
            }

            add(orderLabel) // JLabel 추가
        }
    }

    // 테이블 및 스크롤 생성
    private fun createTableScrollPane(): JScrollPane {
        val columnNames = arrayOf("주문일시", "주문번호", "분류", "상태", "금액", "결제방법")
        tableModel = object : DefaultTableModel(columnNames, 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false // 모든 셀을 편집 불가로 설정
            }
        }

        val table = JTable(tableModel).apply {
            font = MyFont.Medium(18f)
            rowHeight = 55
            background = Color.WHITE
            foreground = Color.BLACK
            showVerticalLines = true
            showHorizontalLines = true

            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

            // 셀 편집 비활성화
            setDefaultEditor(Any::class.java, null)

            // 클릭 이벤트 처리
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    val row = rowAtPoint(e.point)
                    val column = columnAtPoint(e.point)

                    if (column == 1) {
//                        주문번호 클릭 시 주문 상세정보 조회 되야함.
                        val cellValue = getValueAt(row, column)
                        println("주문번호 클릭됨: $cellValue")
                    }
                }
            })

            columnModel.getColumn(0).cellEditor = null
            columnModel.getColumn(1).cellEditor = null
            columnModel.getColumn(2).cellEditor = null
            columnModel.getColumn(3).cellEditor = null
            columnModel.getColumn(4).cellEditor = null
            columnModel.getColumn(5).cellEditor = null

            // 테이블 헤더 커스텀 렌더러
            val headerRenderer = object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
                    component.font = MyFont.Medium(20f) // 폰트 설정
                    component.background = MyColor.LIGHT_BLUE_14 // 헤더 배경색 설정
                    component.foreground = MyColor.LIGHT_BLUE // 헤더 텍스트 색상
                    horizontalAlignment = SwingConstants.CENTER // 텍스트 가운데 정렬
                    border = BorderFactory.createLineBorder(Color.GRAY) // 헤더 테두리 추가
                    return component
                }
            }

            // 각 컬럼에 대해 가운데 정렬 렌더러 적용
            for (i in 0 until columnModel.columnCount) {
                columnModel.getColumn(i).headerRenderer = headerRenderer
            }

            tableHeader.reorderingAllowed = false
            tableHeader.preferredSize = Dimension(0, 50) // 헤더 높이를 50으로 설정

            val cellRenderer = DefaultTableCellRenderer().apply {
                horizontalAlignment = SwingConstants.CENTER // 텍스트 가운데 정렬
            }

            for (i in 0 until columnModel.columnCount) {
                columnModel.getColumn(i).cellRenderer = cellRenderer // 각 컬럼에 렌더러 적용
            }

            // 셀 경계 설정
            setShowGrid(true) // 셀 테두리 보이게 설정
            gridColor = Color.GRAY // 셀 테두리 색상

            // 열 비율 조정
            val totalWeight = 2 + 4 + 1 + 1 + 2 + 2
            val totalWidth = 1200 // 테이블 전체 너비 (스크롤 없이 표시되도록 설정)
            columnModel.getColumn(0).preferredWidth = (totalWidth * 2 / totalWeight)
            columnModel.getColumn(1).preferredWidth = (totalWidth * 4 / totalWeight)
            columnModel.getColumn(2).preferredWidth = (totalWidth * 1 / totalWeight)
            columnModel.getColumn(3).preferredWidth = (totalWidth * 1 / totalWeight)
            columnModel.getColumn(4).preferredWidth = (totalWidth * 2 / totalWeight)
            columnModel.getColumn(5).preferredWidth = (totalWidth * 2 / totalWeight)
        }

        return JScrollPane(table).apply {
            preferredSize = Dimension(1270, 380)
            border = BorderFactory.createLineBorder(MyColor.LIGHT_GREY, 1)

            // 커스텀 스크롤바 적용
            verticalScrollBar.ui = CustomScrollBarUI()
            horizontalScrollBar.ui = CustomScrollBarUI()

            // 스크롤바 크기 조정
            verticalScrollBar.preferredSize = Dimension(10, 0) // 세로 스크롤바 너비
            horizontalScrollBar.preferredSize = Dimension(0, 12) // 가로 스크롤바 높이
        }
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

    // 매출 요약 데이터 계산 함수
    private fun calculateSummary(): Map<String, Pair<Int, Int>> {
        val paymentSummary = mutableMapOf(
            "결제완료" to Pair(0, 0), // 건수, 금액
            "후불결제 - 카드" to Pair(0, 0),
            "후불결제 - 현금" to Pair(0, 0)
        )

        // 테이블 데이터 순회
        for (rowIndex in 0 until tableModel.rowCount) {
            val paymentMethod = tableModel.getValueAt(rowIndex, 5).toString() // 결제방법 열
            val amount = tableModel.getValueAt(rowIndex, 4).toString().replace("[^\\d]".toRegex(), "").toInt() // 금액 열

            when (paymentMethod) {
                "카드결제" -> {
                    // 결제완료 또는 후불결제 - 카드 업데이트
                    paymentSummary["결제완료"] = paymentSummary["결제완료"]!!.let { it.first + 1 to it.second + amount }
                    paymentSummary["후불결제 - 카드"] =
                        paymentSummary["후불결제 - 카드"]!!.let { it.first + 1 to it.second + amount }
                }

                "현금결제" -> {
                    // 후불결제 - 현금 업데이트
                    paymentSummary["후불결제 - 현금"] =
                        paymentSummary["후불결제 - 현금"]!!.let { it.first + 1 to it.second + amount }
                }
            }
        }

        return paymentSummary
    }

    // 매출 요약 패널 업데이트 함수
    private fun updateSummaryPanel(summaryPanel: JPanel, summaryData: Map<String, Pair<Int, Int>>) {
        summaryPanel.removeAll() // 기존 컴포넌트 제거

        val items = listOf(
            "결제완료" to summaryData["결제완료"]!!,
            "후불결제 - 카드" to summaryData["후불결제 - 카드"]!!,
            "후불결제 - 현금" to summaryData["후불결제 - 현금"]!!
        )

        items.forEach { (title, data) ->
            summaryPanel.add(JPanel().apply {
                layout = BorderLayout()
                background = Color.WHITE
                border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
                preferredSize = Dimension(585, 140)
                maximumSize = Dimension(585, 140)

                add(JLabel(title, SwingConstants.CENTER).apply {
                    font = MyFont.Bold(18f)
                    foreground = MyColor.DARK_NAVY
                    horizontalAlignment = SwingConstants.CENTER
                }, BorderLayout.NORTH)

                add(JPanel().apply {
                    layout = GridLayout(2, 1)
                    background = Color.WHITE

                    add(JLabel("${data.first} 건", SwingConstants.CENTER).apply {
                        font = MyFont.Regular(16f)
                        foreground = Color.BLACK
                    })
                    add(JLabel("${data.second} 원", SwingConstants.CENTER).apply {
                        font = MyFont.Regular(16f)
                        foreground = Color.BLACK
                    })
                }, BorderLayout.CENTER)
            })
        }

        summaryPanel.revalidate()
        summaryPanel.repaint()
    }
}