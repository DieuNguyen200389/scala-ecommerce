package services

import domain.models._
import org.scalatest.Suite
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import java.time.{LocalDate, LocalDateTime}

class ServiceFixture extends PlaySpec with Suite with GuiceOneAppPerSuite with MockitoSugar with ScalaFutures {

  object OrderDetails {

    val orderDetail1: OrderDetail = OrderDetail(Some(1L), 1L, 100, 2, 3)
    val orderDetail2: OrderDetail = OrderDetail(Some(2L), 1L, 100, 2, 3)
    val orderDetail3: OrderDetail = OrderDetail(Some(3L), 1L, 100, 2, 3)
    val orderDetail4: OrderDetail = OrderDetail(Some(4L), 1L, 100, 2, 3)
    val allOrderDetails: Seq[OrderDetail] = Seq(orderDetail1, orderDetail2, orderDetail3, orderDetail4)

  }

  object Orders {

    val order1: Order = Order(Some(1L),  1L, 100, LocalDateTime.now())
    val order2: Order = Order(Some(2L),  1L, 100, LocalDateTime.now())
    val order3: Order = Order(Some(3L),  1L, 100, LocalDateTime.now())
    val order4: Order = Order(Some(4L),  1L, 100, LocalDateTime.now())
    val allOrders: Seq[Order] = Seq(order1, order2, order3, order4)
  }

  object Products {

    val product1: Product = Product(Some(1L), "product 1", 10.0, LocalDate.now())
    val product2: Product = Product(Some(2L), "product 2", 10.0, LocalDate.now())
    val product3: Product = Product(Some(3L), "product 3", 10.0, LocalDate.now())
    val product4: Product = Product(Some(4L), "product 4", 10.0, LocalDate.now())
    val allProducts: Seq[Product] = Seq(product1, product2, product3, product4)
  }

  object Users {

    val user1: User = User(Some(1L), "email", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
    val user2: User = User(Some(2L), "email", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
    val user3: User = User(Some(3L), "email", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
    val user4: User = User(Some(4L), "email", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
    val allUsers: Seq[User] = Seq(user1, user2, user3, user4)
  }
}
