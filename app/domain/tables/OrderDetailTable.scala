package domain.tables

import domain.models.OrderDetail
import slick.jdbc.PostgresProfile.api._

class OrderDetailTable(tag: Tag) extends Table[OrderDetail](tag, Some("ecommerce"), "order_details") {

  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  /** The orderId column */
  def orderId = column[Long]("order_id")

  /** The productId column */
  def productId = column[Long]("product_id")

  /** The quantity column */
  def quantity = column[Int]("quantity")

  /** The totalPrice column */
  def price = column[Double]("price")

  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the User object.
   * In this case, we are simply passing the id, name, email and password parameters to the User case classes
   * apply and unapply methods.
   */
  def * =
    (id, orderId, productId,price, quantity) <> ((OrderDetail.apply _).tupled, OrderDetail.unapply)
}