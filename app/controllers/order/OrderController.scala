package controllers.order

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.Logger
import play.api.data.Form
import play.api.data.format.Formats.doubleFormat
import play.api.i18n.Lang
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.{OrderDetailService, OrderService}
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class OrderDetailFormInput(id: Option[Long], productId: Long, price: Double, quantity: Int)

case class OrderFormInput(detail: Seq[OrderDetailFormInput])

/**
 * Takes HTTP requests and produces JSON.
 */
class OrderController @Inject()(cc: ControllerComponents,
                                orderService: OrderService,
                                orderDetailService: OrderDetailService,
                                silhouette: Silhouette[JWTEnvironment])
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  implicit val lang: Lang = supportedLangs.availables.head

  //Input order info
  private val orderDetailForm = {
    import play.api.data.Forms._

    mapping(
      "id" -> optional(longNumber),
      "productId" -> longNumber,
      "price" -> of(doubleFormat),
      "quantity" -> number
    )(OrderDetailFormInput.apply)(OrderDetailFormInput.unapply)
  }

  private val formPost: Form[OrderFormInput] = {
    import play.api.data.Forms._
    Form(
      mapping(
        "detail" -> seq(orderDetailForm)
      )(OrderFormInput.apply)(OrderFormInput.unapply)
    )
  }

  /**
   * Handles get order by id
   * All authenticated Users should be allowed to perform get their own Order.
   * Admin should be allowed to get.
   *
   * @return The result to display.
   */
  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request =>
      logger.trace(s"get an order by id: $id")
      orderService.find(id).map {
        case Some(order) => {
          request.identity.role match {
            case "Admin" => Ok(Json.toJson(order))
            case "User" => {
              if (order.userId == request.identity.id.get) {
                Ok(Json.toJson(order))
              } else {
                Forbidden
              }
            }
            case _ => Forbidden
          }
        }
        case None => NotFound
      }
    }

  /**
   * Handles get all order
   * All authenticated Users should be allowed to perform get their own Orders.
   * Admin should be allowed to get add Orders.
   *
   * @return The result to display.
   */
  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request =>
      logger.trace("get all orders")

      request.identity.role match {
        case "Admin" => orderService.listAll().map { orders =>
          Ok(Json.toJson(orders))
        }
        case "User" => orderService.getAllOrdersForUser(request.identity.id).map { orders =>
          Ok(Json.toJson(orders))
        }
        case _ => Future.successful(Forbidden)
      }
    }

  /**
   * Handles create new order
   *
   * @return The result to display.
   */
  def create: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async {
      implicit request => {
        logger.trace("create new order")
        processJsonOrder(None, request.identity.id.get)
      }
    }

  /**
   * Handles update existing order
   * All authenticated Users should be allowed to perform update their own Order.
   * Admin should be allowed to update for all Orders.
   *
   * @return The result to display.
   */
  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request => {
      request.identity.role match {
        case "Admin" => processJsonOrder(Some(id), request.identity.id.get)
        case "User" =>
          orderService.find(id).flatMap {
            case Some(order) => {
              if (order.userId != request.identity.id.get) {
                Future.successful(Forbidden)
              } else {
                processJsonOrder(Some(id), request.identity.id.get)
              }
            }
            case None => Future.successful(NotFound)
          }
        case _ => Future.successful(Forbidden)
      }
    }
    }

  /**
   * Handles delete existing order
   * All authenticated Users should be allowed to perform delete their own Order.
   * Admin should be allowed to delete for all Orders.
   *
   * @return The result to display.
   */
  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request =>
      logger.trace(s"delete order with: id = $id")

      request.identity.role match {
        case "Admin" =>
          orderService.delete(id).map { results =>
            if (results == 1)
              Ok(JsString(s"delete order with id: $id successfully"))
            else BadRequest(JsString(s"unable to delete order with $id"))
          }
        case "User" =>
          orderService.find(id).map {
            case Some(order) =>
              if (order.userId == request.identity.id.get) {
                orderService.delete(id)
                Ok(JsString(s"delete order with id: $id successfully"))
              } else {
                Forbidden
              }
            case None => NotFound
          }
      }
    }

  /**
   * Process input order info for create and update a order
   *
   * @return The result to display.
   */
  private def processJsonOrder[A](orderId: Option[Long], userId: Long)(implicit request: Request[A]): Future[Result] = {

    def failure(badForm: Form[OrderFormInput]) = {
      Future.successful(BadRequest(JsString(messagesApi("invalid.input"))))
    }

    def success(input: OrderFormInput) = {

      //In case create
      if (orderId.isEmpty) {
        orderService.handleCreatingOrder(orderId, userId, input).map { orderResource =>
          Created(Json.toJson(orderResource))
        }
      } else {//In case update
        orderService.handleUpdatingOrder(orderId, userId, input).map { orderResource =>
          Created(Json.toJson(orderResource))
        }
      }
    }

    formPost.bindFromRequest().fold(failure, success)
  }
}