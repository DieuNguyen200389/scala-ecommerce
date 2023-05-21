package controllers.order

import com.mohiva.play.silhouette.test._
import controllers.ControllerFixture
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.test._
import utils.auth.JWTEnvironment

import java.time.LocalDateTime
import scala.concurrent.Future

class OrderControllerSpec extends ControllerFixture {


  "OrderController#getById(id: Long)" should {

    "get a order successfully" in {

      // mock response data
      val id = 1L

      val orderResource: OrderResource = OrderResource(id, 1L, 100, LocalDateTime.now(), Seq[OrderDetailResource]())
      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockOrderService.find(ArgumentMatchers.eq(id))).thenReturn(Future.successful(Some(orderResource)))

      // prepare test request
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/orders/${id}")
          .withHeaders(HOST -> "localhost:8080")
          .withAuthenticator[JWTEnvironment](identity.loginInfo)

        // Execute test and then extract result
        val result: Future[Result] = route(app, request).get

        // verify result after test
        status(result) mustEqual OK
        val resOrder: OrderResource = Json.fromJson[OrderResource](contentAsJson(result)).get
        verifyOrder(resOrder, orderResource)
    }
  }

  private def verifyOrder(actual: OrderResource, expected: OrderResource): Unit = {
    actual.userId mustEqual expected.userId
    actual.totalPrice mustEqual expected.totalPrice
    actual.orderDate mustEqual expected.orderDate
  }

  "OrderController#getAll()" should {

    "get all users successfully" in {

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockOrderService.listAll()).thenReturn(Future.successful(Orders.allOrders))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/orders")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual OK
      val resOrder: Seq[OrderResource] = Json.fromJson[Seq[OrderResource]](contentAsJson(result)).get
      verifyAllOrders(resOrder, Orders.allOrders)
    }
  }

  private def verifyAllOrders(actual: Seq[OrderResource], expected: Seq[OrderResource]): Unit = {
    actual.size mustEqual expected.size
  }

  "OrderController#delete(id: Long)" should {

    "delete a Order successfully" in {

      // mock response data
      val id = 1L

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockOrderService.delete(ArgumentMatchers.eq(id))).thenReturn(Future.successful(1))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(DELETE, s"/v1/orders/${id}")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual OK
    }
  }

}
