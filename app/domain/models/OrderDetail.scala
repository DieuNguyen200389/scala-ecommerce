package domain.models

import play.api.libs.json.{Json, OFormat}

/**
 * The OrderDetail class
 */
case class OrderDetail(id: Option[Long], orderId: Long, productId: Long, price: Double, quantity: Int)

object OrderDetail {

  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[OrderDetail] = Json.format[OrderDetail]
}



