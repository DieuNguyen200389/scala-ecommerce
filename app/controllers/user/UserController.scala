package controllers.user

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.User
import play.api.Logger
import play.api.data.Form
import play.api.i18n.Lang
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.UserService
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class UserFormInput(email: String, role: String, firstName: String, lastName: String, password: Option[String] = None,
                         address: String, phoneNumber: String, birthDate: LocalDate)
/**
 * Takes HTTP requests and produces JSON.
 */
class UserController @Inject() (cc: ControllerComponents,
                                passwordHasherRegistry: PasswordHasherRegistry,
                                userService: UserService,
                                silhouette: Silhouette[JWTEnvironment])
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  implicit val lang: Lang = supportedLangs.availables.head

  //Form input for user info
  private val form: Form[UserFormInput] = {
    import play.api.data.Forms._
    Form(
      mapping(
        "email" -> nonEmptyText(maxLength = 128),
        "role" -> nonEmptyText(maxLength = 25),
        "firstName" -> nonEmptyText(maxLength = 255),
        "lastName" -> nonEmptyText(maxLength = 255),
        "password" -> optional(text(maxLength = 128)),
        "address" -> nonEmptyText(maxLength = 455),
        "phoneNumber" -> nonEmptyText(maxLength = 25),
        "birthDate" -> localDate("yyyy-MM-dd")
      )(UserFormInput.apply)(UserFormInput.unapply)
    )
  }

  /**
   * Handles get user by id
   *
   * @return The result to display.
   */
  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace(s"getById: $id")
      userService.find(id).map {
        case Some(user) => Ok(Json.toJson(UserResource.fromUser(user)))
        case None => NotFound
      }
    }

  /**
   * Handles get all users
   *
   * @return The result to display.
   */
  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace("getAll Users")
      userService.listAll().map { users =>
        Ok(Json.toJson(users.map(user => UserResource.fromUser(user))))
      }
    }

  /**
   * Handles create new user
   *
   * @return The result to display.
   */
  def create: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace("create User: ")
      processJsonUser(None)
    }

  /**
   * Handles update existing user
   *
   * @return The result to display.
   */
  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace(s"update User id: $id")
      processJsonUser(Some(id))
    }

  /**
   * Handles delete existing user
   *
   * @return The result to display.
   */
  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace(s"Delete user: id = $id")
      userService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1) Ok(JsString(s"Delete user $id successfully"))
        else BadRequest(JsString(s"Unable to delete user $id"))
      }
    }

  /**
   * Process input user info for create and update an user
   *
   * @return The result to display.
   */
  private def processJsonUser[A](id: Option[Long])(implicit request: Request[A]): Future[Result] = {

    def failure(badForm: Form[UserFormInput]) = {
      Future.successful(BadRequest(JsString(messagesApi("invalid.input"))))
    }

    def success(input: UserFormInput) = {
      // create a user from given form input
      val user = User(id, input.email, input.role, input.firstName,
        input.lastName, Some(passwordHasherRegistry.current.hash(input.password.get).password),
        input.address, input.phoneNumber, input.birthDate)

      //In case create
      if (id.isEmpty) {
        userService.save(user).map { user =>
          Created(Json.toJson(UserResource.fromUser(user)))
        }
      } else { //In case update
        userService.update(user).map { user =>
          Created(Json.toJson(UserResource.fromUser(user)))
        }
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
