package controllers.product

import domain.models.Product
import play.api.libs.json.{Format, Json, OFormat}

import java.time.{LocalDate, LocalDateTime}

/**
 * DTO for displaying product information.
 */
case class ProductResource(id: Long,
                           productName: String,
                           price: BigDecimal,
                           expDate: LocalDate)

object ProductResource {

  /**
   * Mapping to read/write a ProductResource out as a JSON value.
   */
  implicit val format: OFormat[ProductResource] = Json.format[ProductResource]

  def fromProduct(product: Product): ProductResource =
    ProductResource(product.id.getOrElse(-1), product.productName, product.price, product.expDate)
}
