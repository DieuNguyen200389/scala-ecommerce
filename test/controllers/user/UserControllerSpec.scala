package controllers.user

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

class UserControllerSpec extends ControllerFixture {


  "UserController#getById(id: Long)" should {

    "get a user successfully" in {

        // mock response data
        val id = 1L

      val user = User(Some(1L), "email", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockUserService.find(ArgumentMatchers.eq(id))).thenReturn(Future.successful(Some(user)))

        // prepare test request
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/users/${id}")
          .withHeaders(HOST -> "localhost:8080")
          .withAuthenticator[JWTEnvironment](identity.loginInfo)

        // Execute test and then extract result
        val result: Future[Result] = route(app, request).get

        // verify result after test
        status(result) mustEqual OK
        val resUser: UserResource = Json.fromJson[UserResource](contentAsJson(result)).get
        verifyUser(resUser, user)
    }
  }

  private def verifyUser(actual: UserResource, expected: User): Unit = {
    actual.id mustEqual expected.id.get
    actual.email mustEqual expected.email
    actual.role mustEqual expected.role
    actual.firstName mustEqual expected.firstName
    actual.lastName mustEqual expected.lastName
    actual.password mustEqual expected.password
    actual.address mustEqual expected.address
    actual.phoneNumber mustEqual expected.phoneNumber
    actual.birthDate mustEqual expected.birthDate
  }

  "UserController#getAll()" should {

    "get all users successfully" in {

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockUserService.listAll()).thenReturn(Future.successful(Users.allUsers))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/users")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual OK
      val resUser: Seq[UserResource] = Json.fromJson[Seq[UserResource]](contentAsJson(result)).get
      verifyAllUsers(resUser, Users.allUsers)
    }
  }

  private def verifyAllUsers(actual: Seq[UserResource], expected: Seq[User]): Unit = {
    actual.size mustEqual expected.size
  }

  "UserController#delete(id: Long)" should {

    "delete a product successfully" in {

      // mock response data
      val id = 1L

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockUserService.delete(ArgumentMatchers.eq(id))).thenReturn(Future.successful(1))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(DELETE, s"/v1/users/${id}")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual OK
    }
  }

  "UserController#update(user)" should {
    "update a user successfully" in {

      // mock response data
      val id = 1L

      val user = User(Some(id), "email", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())

      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      when(mockUserService.update(ArgumentMatchers.eq(user))).thenReturn(Future.successful(user))

      // prepare test request
      val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(PUT, s"/v1/users/${id}")
        .withHeaders(HOST -> "localhost:8080")
        .withAuthenticator[JWTEnvironment](identity.loginInfo)

      // Execute test and then extract result
      val result: Future[Result] = route(app, request).get

      // verify result after test
      status(result) mustEqual BAD_REQUEST
    }
  }
}
