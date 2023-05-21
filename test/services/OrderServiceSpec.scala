package services

import domain.dao.{OrderDao, OrderDetailDao}
import domain.models.Order
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class OrderServiceSpec extends ServiceFixture with MockitoSugar with ScalaFutures {

  val mockOrderDao: OrderDao = mock[OrderDao]
  val mockOrderDetailDao: OrderDetailDao = mock[OrderDetailDao]
  val mockOrderDetailService: OrderDetailService = mock[OrderDetailService]
  val orderService: OrderService = new OrderServiceImpl(mockOrderDao, mockOrderDetailDao, mockOrderDetailService)(global)

  "OrderService#find(id: Long)" should {
    "get a order successfully" in {
      val order = Order(Some(2L), 1L, 100, LocalDateTime.now())
      when(mockOrderDao.find(anyLong())).thenReturn(Future.successful(Some(order)))

      orderService.find(1L)
    }

    "order not found" in {
      when(mockOrderDao.find(anyLong())).thenReturn(Future.successful(None))

      val result = orderService.find(1L).futureValue
      result.isEmpty mustBe true
    }
  }

  "OrderService#listAll()" should {
    "get all orders successfully" in {

      when(mockOrderDao.listAll()).thenReturn(Future.successful(Orders.allOrders))

      orderService.listAll()
    }

    "order not found" in {
      when(mockOrderDao.listAll()).thenReturn(Future.successful(Seq[Order]()))

      val result = orderService.listAll().futureValue
      result.isEmpty mustBe true
    }
  }

}
