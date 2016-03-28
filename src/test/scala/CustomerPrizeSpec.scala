import java.io.{File, PrintWriter}

import domain.Product
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

/**
  * Created by thomas on 28/3/16.
  */
class CustomerPrizeSpec extends FlatSpec with Matchers with GivenWhenThen {

  "parseProductFile" should "parse each Line of the products file into a Product" in {

    Given("A test file with integers")
    val testFilePath= "testfile.csv"
    val writer = new PrintWriter(new File(testFilePath))
    writer.write("1,2,3,4,5,6\n")
    writer.write("7,8,9,10,11,12\n")
    writer.close()

    When("File is parsed")
    def result = CustomerPrize.parseProductFile(testFilePath)

    Then("A list of 2 Products is returned")
    result  shouldBe a [List[Product]]
    result.size shouldBe(2)
    result should contain allOf (
      Product(1,2,6,60),
      Product(7,8,12,990))
  }


}
