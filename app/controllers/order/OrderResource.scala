package controllers.order

import domain.models.Order
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

/**
 * DTO for displaying order information.
 */
case class OrderResource(id: Long, userId: Long, totalPrice: BigDecimal, orderDate: LocalDateTime,  orderDetails: Seq[OrderDetailResource])

object OrderResource {

  implicit val format: OFormat[OrderResource] =
    Json.format[OrderResource]

  def fromOrder(order: Order): OrderResource =
    OrderResource(order.id.getOrElse(-1),
      order.userId,
      order.totalPrice,
      order.orderDate,
      List[OrderDetailResource]()
    )
}

