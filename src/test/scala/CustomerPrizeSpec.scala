import CustomerPrize._
import domain.Product
import org.scalatest._

class CustomerPrizeSpec extends FlatSpec with Matchers {

  val testFile = "testfile.csv"
  val testFilePath = "src/test/resources/" + testFile

  "parseProductFile" should "parse each Line of the products file into a Product" in {
    def result = parseProductFile(testFile)

    result shouldBe a[List[Product]]
    result.size shouldBe 2
  }

  it should "compute the volume and priceVolumeRatio for each product" in {
    def result = parseProductFile(testFile)

    result.head.volume shouldBe 60
    result.head.priceVolumeRatio shouldBe 0.033333335f
  }

  "productFitsIndividuallyInTote" should "return false if length > tote length" in {
    val product = new Product(1, 2, toteLength + 1, 1, 1, 6)
    def result = productFitsIndividuallyInTote(product)

    result shouldBe false
  }

  it should "return false if width > tote width" in {
    val product = new Product(1, 2, 1, toteWidth + 1, 1, 6)
    def result = productFitsIndividuallyInTote(product)

    result shouldBe false
  }

  it should "return false if height > tote height" in {
    val product = new Product(1, 2, 1, 1, toteHeight + 1, 6)
    def result = productFitsIndividuallyInTote(product)

    result shouldBe false
  }

  it should "return true if length, width and height each are <= tote dimensions" in {
    val product = new Product(1, 2, toteLength, toteWidth, toteHeight, 6)
    def result = productFitsIndividuallyInTote(product)

    result shouldBe false
  }


}
