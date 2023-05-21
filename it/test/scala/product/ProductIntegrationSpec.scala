package product

import controllers.product.ProductResource
import domain.models.{Product, User}
import fixtures.DataFixtures
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.libs.ws._
import play.api.test.Helpers._
import play.api.test._

class ProductIntegrationSpec extends DataFixtures with MockitoSugar with ScalaFutures {

  case class LoginBody(email: String, password: String)
  implicit val format: OFormat[LoginBody] = Json.format[LoginBody]
  val authHeaderKey: String = "X-Auth"

  override protected def beforeAll(): Unit = {
    // insert data before tests
    createUsers(Users.allUsers)
    createProducts(Products.allProducts)
  }

  "GET /v1/external/products" should {

    "get a product successfully" in new WithServer(app) {

      val allProducts: Seq[Product] = Products.allProducts
      val user: User = Users.admin
      val loginBody: LoginBody = LoginBody(user.email, Users.plainPassword)

      // login to get access token
      val loginRes: WSResponse = await(WsTestClient.wsUrl("/signIn").post(Json.toJson(loginBody)))
      val accessToken: Option[String] = loginRes.header(authHeaderKey)
      accessToken.isEmpty mustBe false

      // Execute test and then extract result
      val getProductRes: WSResponse = await(
        WsTestClient.wsUrl("/v1/external/products")
          .addHttpHeaders(authHeaderKey -> accessToken.get)
          .get()
      )

      // verify result after test
      getProductRes.status mustEqual 200
      val actualProduct: Seq[ProductResource] = getProductRes.body[JsValue].as[Seq[ProductResource]]
      verifyProduct(actualProduct, allProducts)
    }
  }

  private def verifyProduct(actual: Seq[ProductResource], expected: Seq[Product]): Unit = {
    actual.size mustEqual expected.size
    actual.map(_.id) must contain allOf(1L, 2L, 3L, 4L)
  }
}
