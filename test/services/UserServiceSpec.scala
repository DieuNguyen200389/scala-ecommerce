package services

import controllers.user.UserResource
import domain.dao.UserDao
import domain.models.User
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.specs2.control.Properties.aProperty

import java.time.LocalDate
import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class UserServiceSpec extends ServiceFixture with MockitoSugar with ScalaFutures {

  val mockUserDao: UserDao = mock[UserDao]

  val userService: UserService = new UserServiceImpl(mockUserDao)(global)

  "UserService#find(id: Long)" should {
    "get a user successfully" in {
      val user = User(Some(2L), "email1", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
      when(mockUserDao.find(anyLong())).thenReturn(Future.successful(Some(user)))

      val result = userService.find(1L).futureValue
      result.isEmpty mustBe false
      val actual = result.get
      actual.id.get mustEqual user.id.get
      actual.email mustEqual user.email
      actual.role mustEqual user.role
      actual.firstName mustEqual user.firstName
      actual.lastName mustEqual user.lastName
      actual.password mustEqual user.password
      actual.address mustEqual user.address
      actual.phoneNumber mustEqual user.phoneNumber
      actual.birthDate mustEqual user.birthDate
    }

    "user not found" in {
      when(mockUserDao.find(anyLong())).thenReturn(Future.successful(None))

      val result = userService.find(1L).futureValue
      result.isEmpty mustBe true
    }
  }

  "UserService#listAll()" should {
    "get all users successfully" in {
      val user = User(Some(2L), "email1", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
      when(mockUserDao.listAll()).thenReturn(Future.successful(Users.allUsers))

      val result = userService.listAll().futureValue
      result.isEmpty mustBe false
      result.size mustEqual Users.allUsers.size
    }

    "user not found" in {
      when(mockUserDao.listAll()).thenReturn(Future.successful(Seq[User]()))

      val result = userService.listAll().futureValue
      result.isEmpty mustBe true
    }
  }

  "UserService#delete(id: Long)" should {
    "delete a user successfully" in {

      when(mockUserDao.delete(anyLong())).thenReturn(Future.successful(1))
      val result = userService.delete(1L).futureValue
      result.mustEqual(1)
    }

    "user not found" in {
      when(mockUserDao.find(anyLong())).thenReturn(Future.successful(None))

      val result = userService.find(1L).futureValue
      result.isEmpty mustBe true
    }
  }

  "UserDao#save(user)" should {
    "save a user successfully" in {
      val user5 = User(None, "email5", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
      when(mockUserDao.save(user5)).thenReturn(Future.successful(user5))
      val result = userService.save(user5).futureValue

      result.isEmpty mustBe false
      val user = result
      user.email mustEqual user5.email
      user.role mustEqual user5.role
      user.firstName mustEqual user5.firstName
      user.lastName mustEqual user5.lastName
      user.password mustEqual user5.password
      user.address mustEqual user5.address
      user.phoneNumber mustEqual user5.phoneNumber
      user.birthDate mustEqual user5.birthDate
    }
  }

  "UserDao#update(user)" should {
    "update a user successfully" in {
      val user5 = User(Some(1L), "email5", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
      when(mockUserDao.update(user5)).thenReturn(Future.successful(user5))
      val result = userService.update(user5).futureValue

      result.isEmpty mustBe false
      val user = result
      user.id.get mustEqual 1L
      user.email mustEqual user5.email
      user.role mustEqual user5.role
      user.firstName mustEqual user5.firstName
      user.lastName mustEqual user5.lastName
      user.password mustEqual user5.password
      user.address mustEqual user5.address
      user.phoneNumber mustEqual user5.phoneNumber
      user.birthDate mustEqual user5.birthDate
    }
  }
}
