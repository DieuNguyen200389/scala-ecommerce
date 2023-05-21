package controllers.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import domain.models.User
import play.api.Configuration
import play.api.i18n.Lang
import play.api.libs.json.{JsString, Json, OFormat}
import play.api.mvc.{Action, AnyContent, Request}
import utils.auth.WithRole

import scala.concurrent.{ExecutionContext, Future}

class ChangePasswordController @Inject()(components: SilhouetteControllerComponents)
                                        (implicit ex: ExecutionContext)
  extends SilhouetteController(components) {

    case class ChangePasswordModel(oldPassword: String, newPassword: String)

    implicit val changePasswordFormat = Json.format[ChangePasswordModel]

    /**
     * Changes the password.
     */
      def changePassword = SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator", "User")).async { implicit request =>
        implicit val lang: Lang = supportedLangs.availables.head
        request.body.asJson.flatMap(_.asOpt[ChangePasswordModel]) match {
          case Some(changePasswordModel) =>
            val credentials = Credentials(request.identity.email, changePasswordModel.oldPassword)
            credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
              val newHashedPassword = passwordHasherRegistry.current.hash(changePasswordModel.newPassword)
              authInfoRepository.update(loginInfo, newHashedPassword).map(_ => Ok)
            }.recover {
              case _: ProviderException => BadRequest(JsString(messagesApi("invalid.old.password")))
            }
          case None => Future.successful(BadRequest(JsString(messagesApi("invalid.body"))))
        }
    }
}
