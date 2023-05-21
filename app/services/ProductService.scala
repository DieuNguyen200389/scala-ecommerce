package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.dao.ProductDao
import domain.models.Product

import scala.concurrent.{ExecutionContext, Future}

/**
 * ProductService.
 */
@ImplementedBy(classOf[ProductServiceImpl])
trait ProductService {

  /**
   * Finds a Product by id.
   *
   * @param id The Product id to find.
   * @return The found Product or None if no Product for the given id could be found.
   */
  def find(id: Long): Future[Option[Product]]

  /**
   * List all Products.
   *
   * @return All existing Products.
   */
  def listAll(): Future[Iterable[Product]]

  /**
   * Saves a Product.
   *
   * @param product The Product to save.
   * @return The saved Product.
   */
  def save(product: Product): Future[Product]

  /**
   * Updates a Product.
   *
   * @param product The Product to update.
   * @return The updated Product.
   */
  def update(product: Product): Future[Product]

  /**
   * Deletes a Product
   * @param id The Product's id to delete.
   * @return The deleted Product.
   */
  def delete(id: Long): Future[Int]
}

/**
 * Handles actions to Products.
 *
 * @param productDao The Product DAO implementation.
 * @param ex      The execution context.
 */
@Singleton
class ProductServiceImpl @Inject() (productDao: ProductDao)(implicit ex: ExecutionContext) extends ProductService {

  override def find(id: Long): Future[Option[Product]] = productDao.find(id)

  override def listAll(): Future[Iterable[Product]] = productDao.listAll()

  override def save(product: Product): Future[Product] = productDao.save(product)

  override def update(product: Product): Future[Product] = productDao.update(product)

  override def delete(id: Long): Future[Int] = productDao.delete(id)
}


