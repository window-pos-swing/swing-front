package org.example.`interface`

import org.example.model.Order

interface OrderEventListener {
    fun onResendOrder(order: Order)
    fun onCompleteOrder(order: Order)
}