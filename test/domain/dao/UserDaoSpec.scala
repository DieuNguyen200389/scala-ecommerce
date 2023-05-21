package domain.dao

import domain.models.User
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDate

class UserDaoSpec extends AbstractDaoTest with MockitoSugar with ScalaFutures {

  val userDao: UserDao = get[UserDao]

  // insert some users for testing, the User's id will be generated by DB engine with auto increment
  val user1: User = User(None, "email1", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
  val user2: User = User(None, "email2", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
  val user3: User = User(None, "email3", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
  val user4: User = User(None, "email4", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())

  override protected def beforeAll(): Unit = {

    // Save prepared data to db
    userDao.save(user1).futureValue // id = 1
    userDao.save(user2).futureValue // id = 2
    userDao.save(user3).futureValue // id = 3
    userDao.save(user4).futureValue // id = 4
  }

  "UserDao#find(id: Long)" should {

    "get a user successfully" in {
      val result = userDao.find(1L).futureValue
      result.isEmpty mustBe false
      val user = result.get
      user.id.get mustEqual 1L
      user.email mustEqual user1.email
      user.role mustEqual user1.role
      user.firstName mustEqual user1.firstName
      user.lastName mustEqual user1.lastName
      user.password mustEqual user1.password
      user.address mustEqual user1.address
      user.phoneNumber mustEqual user1.phoneNumber
      user.birthDate mustEqual user1.birthDate
    }

    "user not found" in {
      val result = userDao.find(5L).futureValue
      result.isEmpty mustBe true
    }
  }

  "UserDao#listAll" should {

    "get all users successfully" in {
      val result = userDao.listAll().futureValue
      result.size mustBe 4
      result.map(_.id.get) must contain allOf(1L, 2L, 3L, 4L)
    }
  }

  "UserDao#save(user)" should {

    "save a user successfully" in {
      val user5 = User(None, "email5", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
      userDao.save(user5).futureValue

      val result = userDao.find(5L).futureValue
      result.isEmpty mustBe false
      val user = result.get
      user.id.get mustEqual 5L // fifth user
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
      val user2 = User(Some(2L), "email2", "Admin", "firstname", "lastname", Option("password"), "address", "phone", LocalDate.now())
      userDao.update(user2).futureValue

      val result = userDao.find(2L).futureValue
      result.isEmpty mustBe false
      val user = result.get
      user.id.get mustEqual user2.id.get
      user.email mustEqual user2.email
      user.role mustEqual user2.role
      user.firstName mustEqual user2.firstName
      user.lastName mustEqual user2.lastName
      user.password mustEqual user2.password
      user.address mustEqual user2.address
      user.phoneNumber mustEqual user2.phoneNumber
      user.birthDate mustEqual user2.birthDate
    }
  }

  "UserDao#delete(id: Long)" should {

    "delete a user successfully" in {
      userDao.delete(3L).futureValue

      val result = userDao.find(3L).futureValue
      result.isEmpty mustBe true // user is no longer exists.

      val resultAll = userDao.listAll().futureValue
      resultAll.size mustBe 4
      resultAll.map(_.id.get) must contain allOf(1L, 2L, 4L, 5L) // user 5 is inserted in the above test
    }
  }
}