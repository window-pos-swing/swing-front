package org.example.model

import javax.swing.JPanel


interface OrderState {
    fun handle(order: Order)
    fun getUI(order: Order): JPanel
}