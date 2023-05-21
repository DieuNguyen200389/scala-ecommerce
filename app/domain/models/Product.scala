package domain.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class Product(id: Option[Long],
                   productName: String,
                   price: BigDecimal,
                   expDate: LocalDate = LocalDate.now())

object Product {

  /**
   * Mapping to read/write a ProductResource out as a JSON value.
   */
  implicit val format: OFormat[Product] = Json.format[Product]
}


