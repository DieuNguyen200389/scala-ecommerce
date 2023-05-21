package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.dao.OrderDetailDao
import domain.models.OrderDetail

import scala.concurrent.{ExecutionContext, Future}

/**
 * OrderDetailService.
 */
@ImplementedBy(classOf[OrderDetailServiceImpl])
trait OrderDetailService {

  /**
   * Finds a Order detail by order id.
   *
   * @param id   The Order id to find.
   * @return     The list of Order detail or None if no Order detail for the given order id could be found.
   */
  def findByOrderId(id: Long): Future[Seq[OrderDetail]]

}

@Singleton
class OrderDetailServiceImpl @Inject()(orderDetailDao: OrderDetailDao)(implicit ex: ExecutionContext) extends OrderDetailService {
  override def findByOrderId(id: Long): Future[Seq[OrderDetail]] =
    orderDetailDao.findByOrderId(id)
}

