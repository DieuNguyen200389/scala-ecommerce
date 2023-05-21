package services

import domain.dao.OrderDetailDao
import domain.models.OrderDetail
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class OrderDetailServiceSpec extends ServiceFixture with MockitoSugar with ScalaFutures{

  val mockOrderDetailDao: OrderDetailDao = mock[OrderDetailDao]

  val orderDetailService: OrderDetailService = new OrderDetailServiceImpl(mockOrderDetailDao)(global)

  "OrderDetailService#find(id: Long)" should {
    "get a orderDetail successfully" in {
      val orderDetail = OrderDetail(Some(2L), 1L, 100, 2, 3)

      when(mockOrderDetailDao.findByOrderId(1L)).thenReturn(Future.successful(OrderDetails.allOrderDetails))

      val result = orderDetailService.findByOrderId(1L).futureValue
      result.isEmpty mustBe false
      result.size mustEqual OrderDetails.allOrderDetails.size
    }

    "orderDetail not found" in {
      when(mockOrderDetailDao.findByOrderId(1L)).thenReturn(Future.successful(Seq[OrderDetail]()))

      val result = orderDetailService.findByOrderId(1L).futureValue
      result.isEmpty mustBe true
    }
  }

  "OrderDetailService#listAll()" should {
    "get all orderDetails by order id successfully" in {
      when(mockOrderDetailDao.findByOrderId(1L)).thenReturn(Future.successful(OrderDetails.allOrderDetails))

      val result = orderDetailService.findByOrderId(1L).futureValue
      result.isEmpty mustBe false
      result.size mustEqual OrderDetails.allOrderDetails.size
    }

    "orderDetail not found" in {
      when(mockOrderDetailDao.findByOrderId(1L)).thenReturn(Future.successful(Seq[OrderDetail]()))

      val result = orderDetailService.findByOrderId(1L).futureValue
      result.isEmpty mustBe true
    }
  }
}
