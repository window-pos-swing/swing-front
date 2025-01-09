package org.grr.model

import javax.swing.JPanel


interface OrderState {
    fun handle(order: ReceiveOrderModel)
    fun getUI(order: ReceiveOrderModel): JPanel
}