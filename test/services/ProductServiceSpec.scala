package services

import domain.dao.ProductDao
import domain.models.Product
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import java.time.LocalDate
import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class ProductServiceSpec extends ServiceFixture with MockitoSugar with ScalaFutures {

  val mockProductDao: ProductDao = mock[ProductDao]

  val productService: ProductService = new ProductServiceImpl(mockProductDao)(global)

  "ProductService#find(id: Long)" should {
    "get a product successfully" in {
      val product = Product(Some(2L), "Samsung1", 100, LocalDate.now())
      when(mockProductDao.find(anyLong())).thenReturn(Future.successful(Some(product)))

      val result = productService.find(1L).futureValue
      result.isEmpty mustBe false
      val actual = result.get
      actual.id.get mustEqual product.id.get
      actual.productName mustEqual product.productName
      actual.price mustEqual product.price
      actual.expDate mustEqual product.expDate
    }

    "product not found" in {
      when(mockProductDao.find(anyLong())).thenReturn(Future.successful(None))

      val result = productService.find(1L).futureValue
      result.isEmpty mustBe true
    }
  }

  "ProductService#listAll()" should {
    "get all products successfully" in {
      when(mockProductDao.listAll()).thenReturn(Future.successful(Products.allProducts))

      val result = productService.listAll().futureValue
      result.isEmpty mustBe false
      result.size mustEqual Products.allProducts.size
    }

    "product not found" in {
      when(mockProductDao.listAll()).thenReturn(Future.successful(Seq[Product]()))

      val result = productService.listAll().futureValue
      result.isEmpty mustBe true
    }
  }

  "ProductService#delete(id: Long)" should {
    "delete a Product successfully" in {

      when(mockProductDao.delete(anyLong())).thenReturn(Future.successful(1))
      val result = productService.delete(1L).futureValue
      result.mustEqual(1)
    }

    "user not found" in {
      when(mockProductDao.find(anyLong())).thenReturn(Future.successful(None))

      val result = productService.find(1L).futureValue
      result.isEmpty mustBe true
    }
  }

  "ProductDao#save(product)" should {
    "save a Product successfully" in {
      val product1: Product = Product(Some(1L), "product 1", 10.0, LocalDate.now())
      when(mockProductDao.save(product1)).thenReturn(Future.successful(product1))
      val result = productService.save(product1).futureValue

      result.productName mustEqual product1.productName
      result.price mustEqual product1.price
      result.expDate mustEqual product1.expDate
    }
  }

  "ProductDao#update(product)" should {
    "update a user successfully" in {
      val product1: Product = Product(Some(1L), "product 1", 10.0, LocalDate.now())
      when(mockProductDao.update(product1)).thenReturn(Future.successful(product1))
      val result = productService.update(product1).futureValue

      result.id.get mustEqual 1L
      result.productName mustEqual product1.productName
      result.price mustEqual product1.price
      result.expDate mustEqual product1.expDate
    }
  }
}
