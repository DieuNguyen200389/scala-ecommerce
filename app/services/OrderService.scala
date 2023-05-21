package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.order.{OrderFormInput, OrderDetailResource, OrderResource}
import domain.dao.{OrderDao, OrderDetailDao}
import domain.models.{Order, OrderDetail}

import java.time.LocalDateTime
import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * OrderService.
 */
@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {

  /**
   * Finds a Order by id.
   *
   * @param id The Order id to find.
   * @return The found Order or None if no Order for the given id could be found.
   */
  def find(id: Long): Future[Option[OrderResource]]

  /**
   * List all Orders.
   *
   * @return All existing Orders.
   */
  def listAll(): Future[Iterable[OrderResource]]

  /**
   * List all Orders for login user.
   *
   * @return All existing Orders.
   */
  def getAllOrdersForUser(userid: Option[Long]): Future[Iterable[OrderResource]]

  /**
   * Handle data to create order
   *
   * @param orderId   The order ID, in this case is None.
   * @param userId    The ID for User is logged in.
   * @return          The OrderResource.
   */
  def handleCreatingOrder(orderId: Option[Long], userId: Long, input: OrderFormInput): Future[OrderResource]

  /**
   * Handle data to create order
   *
   * @param orderId   The order ID to update.
   * @param userId    The ID for User is logged in.
   * @return          The OrderResource.
   */
  def handleUpdatingOrder(orderId: Option[Long], userId: Long, input: OrderFormInput): Future[OrderResource]

  def delete(id: Long): Future[Int]
}

@Singleton
class OrderServiceImpl @Inject()(orderDao: OrderDao, orderDetailDao: OrderDetailDao,
                                 orderDetailService: OrderDetailService)(implicit ex: ExecutionContext) extends OrderService {
  override def find(id: Long): Future[Option[OrderResource]] = {
    orderDao.find(id).map {
      case Some(order) => Some(this.convertToOrderResource(order))
      case None => None
    }
  }

  override def listAll(): Future[Iterable[OrderResource]] = {
    orderDao
      .listAll().map { orders =>
      orders.map(order => this.convertToOrderResource(order))
    }
  }

  override def getAllOrdersForUser(userId: Option[Long]): Future[Iterable[OrderResource]] = {
    orderDao
      .getAllOrdersForUser(userId: Option[Long]).map { orders =>
      orders.map(order => this.convertToOrderResource(order))
    }
  }

  override def handleCreatingOrder(orderId: Option[Long], userId: Long, input: OrderFormInput): Future[OrderResource] ={

    var totalPrice: BigDecimal = 0;
    val orderDetails = input.detail.map(orderDetail => {
      totalPrice = totalPrice + orderDetail.price * orderDetail.quantity

      OrderDetail(
        None,
        0,
        orderDetail.productId,
        orderDetail.price,
        orderDetail.quantity
      )
    })
    val order = Order(orderId, userId, totalPrice, LocalDateTime.now())
    this.save(order, orderDetails).map { order =>
      this.convertToOrderResource(order)
    }
  }

  override def handleUpdatingOrder(orderId: Option[Long], userId: Long, input: OrderFormInput): Future[OrderResource] ={

    var totalPrice: BigDecimal = 0;
    val orderDetails = input.detail.map(orderDetail => {
      totalPrice = totalPrice + orderDetail.price * orderDetail.quantity

      OrderDetail(
        orderDetail.id,
        orderId.get,
        orderDetail.productId,
        orderDetail.price,
        orderDetail.quantity
      )
    })
    val order = Order(orderId, userId, totalPrice, LocalDateTime.now())
    this.update(order, orderDetails).map { order =>
      this.convertToOrderResource(order)
    }
  }

  override def delete(id: Long): Future[Int] = {
     Await.result(
       orderDetailDao.findByOrderId(id).map(
         details => details.foreach(detail => orderDetailDao.delete(detail.id.get))
       ),
      scala.concurrent.duration.Duration.Inf
    )

    orderDao.delete(id)
  }

  /**
   * Saves an Order.
   *
   * @param order The Order to save.
   * @param orderDetails The list of order detail to save.
   *
   * @return The saved Order.
   */
  private  def save(order: Order, orderDetails: Seq[OrderDetail]): Future[Order] = {
    for {
      savedOrder <- orderDao.save(order)
      savedOrderDetails = orderDetails.map(detail => detail.copy(orderId = savedOrder.id.get))
      _ <- orderDetailDao.saveAll(savedOrderDetails)
    } yield savedOrder
  }

  /**
   * Update an Order.
   *
   * @param order The Order to update.
   * @param orderDetails The list of order detail to update.
   *
   * @return The updated Order.
   */
  private def update(order: Order, orderDetails: Seq[OrderDetail]): Future[Order] ={
    for {
      savedOrder <- orderDao.update(order)
      updatedOrderDetails <- orderDetailDao.updateAll(orderDetails)
    } yield savedOrder
  }

  /**
   * Convert Order to OrderResource.
   *
   * @param order The Order to convert.
   *
   * @return      The OrderResource.
   */
  private def convertToOrderResource(order: Order): OrderResource = {
    val orderDetails = Await.result(
      orderDetailService.findByOrderId(order.id.get),
      scala.concurrent.duration.Duration.Inf
    )
    var orderDetailsResource = Seq[OrderDetailResource]()
    orderDetails.foreach(
      orderDetail =>
        orderDetailsResource = orderDetailsResource :+ OrderDetailResource
          .fromOrderDetail(orderDetail)
    )
    OrderResource.fromOrder(order).copy(orderDetails = orderDetailsResource)
  }
}

