package domain.tables

import domain.models.Product
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDate

class ProductTable(tag: Tag) extends Table[Product](tag, Some("ecommerce"), "products") {

  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  /** The product name column */
  def productName = column[String]("product_name")

  /** The price column */
  def price = column[BigDecimal]("price")

  /** The date column */
  def expDate = column[LocalDate]("exp_date")

  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the Product object.
   */
  def * = (id, productName, price, expDate) <> ((Product.apply _).tupled, Product.unapply)
}
