package controllers.product

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.Product
import httpclient.ExternalServiceException
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.{ExternalProductService, ProductService, UserService}
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class ProductFormInput(productName: String, price: BigDecimal, expDate: LocalDate)

/**
 * Takes HTTP requests and produces JSON.
 */
class ProductController @Inject() (cc: ControllerComponents,
                                   productService: ProductService,
                                   extProductService: ExternalProductService,
                                   userService: UserService,
                                   silhouette: Silhouette[JWTEnvironment])
                                   (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  private val form: Form[ProductFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "productName" -> nonEmptyText(maxLength = 455),
        "price" -> bigDecimal,
        "expDate" -> localDate
      )(ProductFormInput.apply)(ProductFormInput.unapply)
    )
  }

  /**
   * Handles get product by id
   *
   * @return The result to display.
   */
  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator", "User")).async { implicit request =>
      logger.trace(s"getById: $id")
      productService.find(id).map {
        case Some(product) => Ok(Json.toJson(ProductResource.fromProduct(product)))
        case None => NotFound
      }
    }

  /**
   * Handles get all products
   *
   * @return The result to display.
   */
  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator", "User")).async { implicit request =>
      logger.trace("getAll Products")
      productService.listAll().map { products =>
        Ok(Json.toJson(products.map(product => ProductResource.fromProduct(product))))
      }
    }

  /**
   * Handles create new product
   *
   * @return The result to display.
   */
  def create: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace("create Product: ")
      processJsonProduct(None)
    }

  /**
   * Handles update existing product
   *
   * @return The result to display.
   */
  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace(s"update Product id: $id")
      processJsonProduct(Some(id))
    }

  /**
   * Handles delete existing product
   *
   * @return The result to display.
   */
  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace(s"Delete product: id = $id")
      productService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1) Ok(JsString(s"Delete product $id successfully"))
        else BadRequest(JsString(s"Unable to delete product $id"))
      }
    }

  /**
   * Get products by call external service
   *
   * @return The result to display.
   */
  def getAllExternal: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace("getAll External Products")

      // try/catch Future exception with transform
      extProductService.getExternalProducts().transform {
        case Failure(exception) => handleExternalError(exception)
        case Success(products) => Try(Ok(Json.toJson(products.map(product => ProductResource.fromProduct(product)))))
      }
    }

  /**
   * Handle error from external service
   *
   */
  private def handleExternalError(throwable: Throwable): Try[Result] = {
    throwable match {
      case ese: ExternalServiceException =>
        logger.trace(s"An ExternalServiceException occurred: ${ese.getMessage}")
        if (ese.error.isEmpty)
          Try(BadRequest(JsString(s"An ExternalServiceException occurred. statusCode: ${ese.statusCode}")))
        else Try(BadRequest(Json.toJson(ese.error.get)))
      case _ =>
        logger.trace(s"An other exception occurred on getAllExternal: ${throwable.getMessage}")
        Try(BadRequest(JsString("Unable to create an external product")))
    }
  }

  /**
   * Process input product info for create and update a product
   *
   * @return The result to display.
   */
  private def processJsonProduct[A](id: Option[Long])(implicit request: Request[A]): Future[Result] = {

    def failure(badForm: Form[ProductFormInput]) = {
      Future.successful(BadRequest(JsString("Invalid Input")))
    }

    def success(input: ProductFormInput) = {
      // create a product from given form input
      val product = Product(id, input.productName, input.price, input.expDate)

      //In case create
      if (id.isEmpty)
        productService.save(product).map { product =>
          Created(Json.toJson(ProductResource.fromProduct(product)))
      }
      else //In case update
        productService.update(product).map { product =>
          Created(Json.toJson(ProductResource.fromProduct(product)))
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
