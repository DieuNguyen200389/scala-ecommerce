package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.OrderDetail
import domain.tables.OrderDetailTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

@ImplementedBy(classOf[OrderDetailDaoImpl])
trait OrderDetailDao {
  def find(id: Long): Future[Option[OrderDetail]]
  def findByOrderId(id: Long): Future[Seq[OrderDetail]]
  def listAll(): Future[Iterable[OrderDetail]]
  def save(orderDetail: OrderDetail): Future[OrderDetail]
  def saveAll(list: Seq[OrderDetail]): Future[Seq[OrderDetail]]
  def update(orderDetail: OrderDetail): Future[OrderDetail]
  def updateAll(list: Seq[OrderDetail]): Future[Seq[OrderDetail]]
  def delete(id: Long): Future[Int]
}
@Singleton
class OrderDetailDaoImpl @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext)
  extends OrderDetailDao {

  private val orderDetails = TableQuery[OrderDetailTable]

  override def find(id: Long): Future[Option[OrderDetail]] = daoRunner.run {
    orderDetails.filter(_.id === id).result.headOption
  }

  override def findByOrderId(orderId: Long): Future[Seq[OrderDetail]] =
    daoRunner.run {
      orderDetails.filter(_.orderId === orderId).result
    }

  override def listAll(): Future[Iterable[OrderDetail]] = daoRunner.run {
    orderDetails.result
  }

  override def save(orderDetail: OrderDetail): Future[OrderDetail] = daoRunner.run {
    orderDetails returning orderDetails += orderDetail
  }

  override def saveAll(list: Seq[OrderDetail]): Future[Seq[OrderDetail]] = daoRunner.run {
    (orderDetails ++= list).map(_ => list)
  }

  override def update(orderDetail: OrderDetail): Future[OrderDetail] = daoRunner.run {
    orderDetails.filter(_.id === orderDetail.id).update(orderDetail).map(_ => orderDetail)
  }

  override def updateAll(list: Seq[OrderDetail]): Future[Seq[OrderDetail]] = daoRunner.run {
    val updateActions = list.map { detail =>
      orderDetails.filter(_.id === detail.id).update(detail)
    }
    DBIO.sequence(updateActions).map(_ => list)
  }

  override def delete(id: Long): Future[Int] = daoRunner.run {
    orderDetails.filter(_.id === id).delete
  }
}