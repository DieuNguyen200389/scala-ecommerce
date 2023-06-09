package domain.dao

import domain.models.OrderDetail
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDateTime

class OrderDetailDaoSpec extends AbstractDaoTest with MockitoSugar with ScalaFutures {

  val orderDetailDao: OrderDetailDao = get[OrderDetailDao]

  // insert some order Details for testing, the Order's id will be generated by DB engine with auto increment
  val orderDetail1: OrderDetail = OrderDetail(None, 1L, 100, 2, 3)
  val orderDetail2: OrderDetail = OrderDetail(None, 1L, 100, 2, 3)
  val orderDetail3: OrderDetail = OrderDetail(None, 1L, 100, 2, 3)
  val orderDetail4: OrderDetail = OrderDetail(None, 1L, 100, 2, 3)

  override protected def beforeAll(): Unit = {

    // Save prepared data to db
    orderDetailDao.save(orderDetail1).futureValue // id = 1
    orderDetailDao.save(orderDetail2).futureValue // id = 2
    orderDetailDao.save(orderDetail3).futureValue // id = 3
    orderDetailDao.save(orderDetail4).futureValue // id = 4
  }

  "OrderDetailDao#find(id: Long)" should {

    "get a order Detail successfully" in {
      val result = orderDetailDao.find(2L).futureValue
      result.isEmpty mustBe false
      val orderDetail = result.get
      orderDetail.id.get mustEqual 2L
      orderDetail.orderId mustEqual orderDetail2.orderId
      orderDetail.productId mustEqual orderDetail2.productId
      orderDetail.price mustEqual orderDetail2.price
      orderDetail.quantity mustEqual orderDetail2.quantity
    }

    "order Detail not found" in {
      val result = orderDetailDao.find(5L).futureValue
      result.isEmpty mustBe true
    }
  }

  "OrderDetailDao#listAll" should {

    "get all order Details successfully" in {
      val result = orderDetailDao.listAll().futureValue
      result.size mustBe 4
      result.map(_.id.get) must contain allOf(1L, 2L, 3L, 4L)
    }
  }

  "OrderDetailDao#save(order)" should {

    "save a order Detail successfully" in {
      val orderDetail5 = OrderDetail(None, 1L, 100,  2, 3)
      orderDetailDao.save(orderDetail5).futureValue

      val result = orderDetailDao.find(5L).futureValue
      result.isEmpty mustBe false
      val orderDetail = result.get
      orderDetail.id.get mustEqual 5L // fifth order
      orderDetail.orderId mustEqual orderDetail2.orderId
      orderDetail.productId mustEqual orderDetail2.productId
      orderDetail.price mustEqual orderDetail2.price
      orderDetail.quantity mustEqual orderDetail2.quantity
    }
  }

  "OrderDetailDao#update(orderDetail)" should {

    "update a order Detail successfully" in {
      val orderDetail2: OrderDetail = OrderDetail(Some(2L), 1L, 100, 2, 3)
      orderDetailDao.update(orderDetail2).futureValue

      val result = orderDetailDao.find(2L).futureValue
      result.isEmpty mustBe false
      val orderDetail = result.get
      orderDetail.id.get mustEqual orderDetail2.id.get
      orderDetail.orderId mustEqual orderDetail2.orderId
      orderDetail.productId mustEqual orderDetail2.productId
      orderDetail.price mustEqual orderDetail2.price
      orderDetail.quantity mustEqual orderDetail2.quantity
    }
  }

  "OrderDetailDao#delete(id: Long)" should {

    "delete a order Detail successfully" in {
      orderDetailDao.delete(3L).futureValue

      val result = orderDetailDao.find(3L).futureValue
      result.isEmpty mustBe true // order Detail is no longer exists.

      val resultAll = orderDetailDao.listAll().futureValue
      resultAll.size mustBe 4
      resultAll.map(_.id.get) must contain allOf(1L, 2L, 4L, 5L) // order Detail 5 is inserted in the above test
    }
  }
}
