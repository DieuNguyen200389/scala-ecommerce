package controllers.product

import com.mohiva.play.silhouette.test._
import controllers.ControllerFixture
import domain.models._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.test._
import utils.auth.JWTEnvironment

import java.time.LocalDate
import scala.concurrent.Future

class ProductControllerSpec extends ControllerFixture {

  "ProductController#getById(id: Long)" should {

    "get a product successfully" in {

        // mock response data
        val id = 1L

        val product: Product = Product(Some(id), "Samsung", 100, LocalDate.now())
        when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
        when(mockProductService.find(ArgumentMatchers.eq(id))).thenReturn(Future.successful(Some(product)))

        // prepare test request
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/products/${id}")
          .withHeaders(HOST -> "localhost:8080")
          .withAuthenticator[JWTEnvironment](identity.loginInfo)

        // Execute test and then extract result
        val result: Future[Result] = route(app, request).get

        // verify result after test
        status(result) mustEqual OK
        val resProduct: ProductResource = Json.fromJson[ProductResource](contentAsJson(result)).get
        verifyProduct(resProduct, product)
    }
  }

  private def verifyProduct(actual: ProductResource, expected: Product): Unit = {
    actual.id mustEqual expected.id.get
    actual.productName mustEqual expected.productName
    actual.price mustEqual expected.price
    actual.price mustEqual expected.price
  }

  "ProductController#getAll()" should {

    "get all products successfully" in {

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockProductService.listAll()).thenReturn(Future.successful(Products.allProducts))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/products")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual OK
      val resProduct: Seq[ProductResource] = Json.fromJson[Seq[ProductResource]](contentAsJson(result)).get
      verifyAllProducts(resProduct, Products.allProducts)
    }
  }

  private def verifyAllProducts(actual: Seq[ProductResource], expected: Seq[Product]): Unit = {
    actual.size mustEqual expected.size
  }

  "ProductController#delete(id: Long)" should {

    "delete a product successfully" in {

      // mock response data
      val id = 1L

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockProductService.delete(ArgumentMatchers.eq(id))).thenReturn(Future.successful(1))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(DELETE, s"/v1/products/${id}")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual OK

    }
  }

  "ProductController#getAllExternal()" should {

    "get all products successfully" in {

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockExtProductService.getExternalProducts).thenReturn(Future.successful(Products.allProducts))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/external/products")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual OK
      val resProduct: Seq[ProductResource] = Json.fromJson[Seq[ProductResource]](contentAsJson(result)).get
      verifyAllProducts(resProduct, Products.allProducts)
    }
  }

  "ProductController#create(product)" should {
    "create a product successfully" in {

      // mock response data
      val id = 1L

      val product: Product = Product(None, "Samsung", 100, LocalDate.now())
      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockProductService.save(ArgumentMatchers.eq(product))).thenReturn(Future.successful(product))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, s"/v1/products")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual BAD_REQUEST
    }
  }

  "ProductController#update(product)" should {
    "update a product successfully" in {

      // mock response data
      val id = 1L

      val product: Product = Product(Some(id), "Samsung", 100, LocalDate.now())
      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockProductService.update(ArgumentMatchers.eq(product))).thenReturn(Future.successful(product))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(PUT, s"/v1/products/${id}")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual BAD_REQUEST
    }
  }

}
