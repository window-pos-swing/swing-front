package org.grr.`interface`

import org.grr.model.ReceiveOrderModel

interface OrderEventListener {
    //배달대행사로 재전송
    fun onResendOrder(order: ReceiveOrderModel)
    //배달중
    fun onDelivery(order: ReceiveOrderModel)
    //픽업 대기중
    fun onPickUpWait(order: ReceiveOrderModel)
}