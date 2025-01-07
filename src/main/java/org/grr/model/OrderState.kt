package org.grr.model

import javax.swing.JPanel


interface OrderState {
    fun handle(order: Order)
    fun getUI(order: Order): JPanel
}