package controllers

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.test._
import controllers.order.{OrderDetailResource, OrderResource}
import domain.dao._
import domain.models._
import fixtures.TestApplication
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Suite
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import services._
import utils.auth.JWTEnvironment

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.ExecutionContext.Implicits.global

class ControllerFixture extends PlaySpec with Suite with GuiceOneAppPerSuite with MockitoSugar with ScalaFutures {
  val mockOrderService: OrderService = mock[OrderService]
  val mockProductService: ProductService = mock[ProductService]
  val mockExtProductService: ExternalProductService = mock[ExternalProductService]
  val mockUserService: UserService = mock[UserService]
  val mockDaoRunner: DaoRunner = mock[DaoRunner]
  val mockUserDao: UserDao = mock[UserDao]
  val mockProductDao: ProductDao = mock[ProductDao]
  val mockOrderDao: OrderDao = mock[OrderDao]

  object Orders {

    val order1: OrderResource = OrderResource(1L,  1L, 100, LocalDateTime.now(), Seq[OrderDetailResource]())
    val order2: OrderResource = OrderResource(2L,  1L, 100, LocalDateTime.now(), Seq[OrderDetailResource]())
    val order3: OrderResource = OrderResource(3L,  1L, 100, LocalDateTime.now(), Seq[OrderDetailResource]())
    val order4: OrderResource = OrderResource(4L,  1L, 100, LocalDateTime.now(), Seq[OrderDetailResource]())
    val allOrders: Seq[OrderResource] = Seq(order1, order2, order3, order4)
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

  val password: String = new BCryptPasswordHasher().hash("fakeP@ssw0rd").password
  val identity: User = User(Some(1L), "email@gmail.com", "Admin", "firstname", "lastname", Some("password"), "address", "phone", LocalDate.now())
  implicit val env: Environment[JWTEnvironment] = new FakeEnvironment[JWTEnvironment](Seq(identity.loginInfo -> identity))

  class FakeServiceModule extends AbstractModule with ScalaModule {
    override def configure(): Unit = {
      bind[Environment[JWTEnvironment]].toInstance(env)
      bind[OrderService].toInstance(mockOrderService)
      bind[ProductService].toInstance(mockProductService)
      bind[ExternalProductService].toInstance(mockExtProductService)
      bind[UserService].toInstance(mockUserService)
      bind[DaoRunner].toInstance(mockDaoRunner)
      bind[UserDao].toInstance(mockUserDao)
      bind[ProductDao].toInstance(mockProductDao)
      bind[OrderDao].toInstance(mockOrderDao)
    }
  }

  implicit override lazy val app: Application = TestApplication.appWithOverridesModule(module = new FakeServiceModule())
}
