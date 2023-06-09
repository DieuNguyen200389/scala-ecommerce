package domain.dao

import domain.models.Order
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDateTime

class OrderDaoSpec extends AbstractDaoTest with MockitoSugar with ScalaFutures {

  val orderDao: OrderDao = get[OrderDao]

  // insert some orders for testing, the Order's id will be generated by DB engine with auto increment
  val order1: Order = Order(None, 1L, 100, LocalDateTime.now())
  val order2: Order = Order(None, 1L, 100, LocalDateTime.now())
  val order3: Order = Order(None, 1L, 100, LocalDateTime.now())
  val order4: Order = Order(None, 1L, 100, LocalDateTime.now())

  override protected def beforeAll(): Unit = {

    // Save prepared data to db
    orderDao.save(order1).futureValue // id = 1
    orderDao.save(order2).futureValue // id = 2
    orderDao.save(order3).futureValue // id = 3
    orderDao.save(order4).futureValue // id = 4
  }

  "OrderDao#find(id: Long)" should {

    "get a order successfully" in {
      val result = orderDao.find(1L).futureValue
      result.isEmpty mustBe false
      val order = result.get
      order.id.get mustEqual 1L
      order.userId mustEqual order1.userId
      order.totalPrice mustEqual order1.totalPrice
      order.orderDate mustEqual order1.orderDate
    }

    "order not found" in {
      val result = orderDao.find(5L).futureValue
      result.isEmpty mustBe true
    }
  }

  "OrdertDao#listAll" should {

    "get all orders successfully" in {
      val result = orderDao.listAll().futureValue
      result.size mustBe 4
      result.map(_.id.get) must contain allOf(1L, 2L, 3L, 4L)
    }
  }

  "OrderDao#save(order)" should {

    "save a order successfully" in {
      val order5 = Order(None, 1L, 100, LocalDateTime.now())
      orderDao.save(order5).futureValue

      val result = orderDao.find(5L).futureValue
      result.isEmpty mustBe false
      val order = result.get
      order.id.get mustEqual 5L // fifth order
      order.userId mustEqual order5.userId
      order.totalPrice mustEqual order5.totalPrice
      order.orderDate mustEqual order5.orderDate
    }
  }

  "OrderDao#update(order)" should {

    "update a order successfully" in {
      val order2: Order = Order(Some(2L), 1L, 100, LocalDateTime.now())
      orderDao.update(order2).futureValue

      val result = orderDao.find(2L).futureValue
      result.isEmpty mustBe false
      val order = result.get
      order.id.get mustEqual order2.id.get
      order.userId mustEqual order2.userId
      order.totalPrice mustEqual order2.totalPrice
      order.orderDate mustEqual order2.orderDate
    }
  }

  "OrderDao#delete(id: Long)" should {

    "delete a order successfully" in {
      orderDao.delete(3L).futureValue

      val result = orderDao.find(3L).futureValue
      result.isEmpty mustBe true // order is no longer exists.

      val resultAll = orderDao.listAll().futureValue
      resultAll.size mustBe 4
      resultAll.map(_.id.get) must contain allOf(1L, 2L, 4L, 5L) // order 5 is inserted in the above test
    }
  }
}
