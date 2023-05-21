package controllers.user

import domain.models.User
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

/**
 * DTO for displaying user information.
 */
case class UserResource(id: Long,
                        email: String,
                        role: String,
                        firstName: String,
                        lastName: String,
                        password: Option[String] = None,
                        address: String,
                        phoneNumber: String,
                        birthDate: LocalDate)

object UserResource {

  /**
   * Mapping to read/write a UserResource out as a JSON value.
   */
  implicit val format: OFormat[UserResource] = Json.format[UserResource]

  def fromUser(user: User): UserResource =
    UserResource(user.id.getOrElse(-1), user.email, user.role, user.firstName,
      user.lastName, user.password, user.address, user.phoneNumber, user.birthDate)
}