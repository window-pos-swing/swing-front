package org.grr.`interface`

import org.grr.model.ReceiveOrderModel

interface OrderEventListener {
    fun onResendOrder(order: ReceiveOrderModel)
    fun onCompleteOrder(order: ReceiveOrderModel)
}