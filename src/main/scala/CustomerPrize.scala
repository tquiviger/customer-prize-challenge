import scala.io.Source
import domain.Product

object CustomerPrize extends App {

  val productsFilename = "products.csv"
  val productList = parseProductFile(productsFilename)
  println(productList.size + " products have been found")




  def parseProductFile(path:String): List[Product] = {
    Source
      .fromFile(path)
      .getLines()
      .map(s=>
        s.split(",")
      )
      .map(strings=>
        new Product(
          strings(0).toInt,
          strings(1).toInt,
          strings(5).toInt,
          strings(2).toInt * strings(3).toInt * strings(4).toInt)
      ).toList
  }

}
