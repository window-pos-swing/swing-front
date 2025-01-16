package org.grr.model

import org.grr.enum.OrderReceiveType
import org.grr.enum.PosOrderStatus
import org.grr.enum.ServerOrderStatus

data class OrderFilter(
    val posOrderStatus: PosOrderStatus? = null,
    val serverOrderStatus: ServerOrderStatus? = null,
    val orderReceiveType: OrderReceiveType? = null,
)
