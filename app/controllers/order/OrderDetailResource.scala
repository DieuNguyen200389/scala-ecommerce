package controllers.order

import domain.models.OrderDetail
import play.api.libs.json.{Json, OFormat}

/**
 * DTO for displaying order detail information.
 */
case class OrderDetailResource(id: Long, orderId: Long, productId: Long, price: Double, quantity: Int)

object OrderDetailResource {

  implicit val format: OFormat[OrderDetailResource] =
    Json.format[OrderDetailResource]

  def fromOrderDetail(orderDetail: OrderDetail): OrderDetailResource =
    OrderDetailResource(orderDetail.id.getOrElse(-1),
      orderDetail.orderId,
      orderDetail.productId,
      orderDetail.price,
      orderDetail.quantity
    )
}

