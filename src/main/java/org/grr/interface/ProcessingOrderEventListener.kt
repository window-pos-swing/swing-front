package org.grr.`interface`

import org.grr.model.Order

interface OrderEventListener {
    fun onResendOrder(order: Order)
    fun onCompleteOrder(order: Order)
}