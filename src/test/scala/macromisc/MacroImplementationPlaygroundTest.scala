package macromisc

import org.scalatest.FunSuite

class MacroImplementationPlaygroundTest extends FunSuite {

  import MacroImplementationPlaygroundTest._
  import MacroImplementationPlayground._

  test("macro update equivalent to by hand") {

    val updatedBigCoByHand: Company =
      updateEmployeeName(bigCo)

    val updatedBigCoByMacro: Company =
      update(bigCo)((c: Company) => c.organization.name)((name: String) => s"$strToAddToEmployeeName $name")

    assert(updatedBigCoByHand != bigCo)
    assert(updatedBigCoByMacro != bigCo)
    assert(updatedBigCoByHand == updatedBigCoByMacro)
  }

}

object MacroImplementationPlaygroundTest {

  case class Company(name: String, organization: Department)

  case class Department(name: String, staff: Employee)

  case class Employee(name: String, badge: Int)

  val strToAddToEmployeeName = "Mr. "

  def updateEmployeeName(c: Company): Company =
    c.copy(
      organization = {
      val staff = c.organization
      staff.copy(
        name = s"$strToAddToEmployeeName ${staff.name}"
      )
    }
    )

  val bigCo =
    Company(
      name = "big corp.",
      organization =
        Department(
          name = "engineering",
          staff =
            Employee(
              name = "jack",
              badge = 4121
            )
        )
    )

}