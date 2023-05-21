package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.Product
import httpclient.ExternalProductClient
import play.api.Configuration
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

/**
 * ExternalProductService.
 */
@ImplementedBy(classOf[ExternalProductServiceImpl])
trait ExternalProductService {

  /**
   * Get External Products.
   *
   * @return  external products.
   */
  def getExternalProducts(): Future[Iterable[Product]]

}

/**
 * Handles actions to External Products.
 *
 * @param client  The HTTP Client instance
 * @param ex      The execution context.
 */
@Singleton
class ExternalProductServiceImpl @Inject()(client: ExternalProductClient, config: Configuration)
                                       (implicit ex: ExecutionContext)
  extends ExternalProductService {

  def getProducts: String = config.get[String]("external.products.getProducts")

  override def getExternalProducts(): Future[Iterable[Product]] = client.get[Seq[Product]](getProducts)

}


