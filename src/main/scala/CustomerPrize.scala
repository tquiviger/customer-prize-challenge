import scala.io.Source
import domain.Product

object CustomerPrize extends App {

  val volumeAvailable = 40 * 30 * 35
  val productsFilename = "products.csv"

  val productList = parseProductFile(productsFilename)
    .filter(p => productFitsIndividuallyInTote(p))
    .sorted
    .reverse
    .map(p => (p.productId, p.volume, p.price))
    .take(800) //hack to bypass the OOM errors, we sort by priceVolumeRatio and test with the XXX optimal products

  val s = System.currentTimeMillis
  val productsSize = productList.size

  val m = Array.ofDim[Int](productsSize + 1, volumeAvailable + 1)
  val plm = (List((
    for {v <- 0 to volumeAvailable}
      yield Set[(Int, Int, Int)]()).toArray) ++
    (
      for {
        n <- 0 until productsSize
        colN = (
          for {v <- 0 to volumeAvailable}
            yield Set[(Int, Int, Int)](productList(n))).toArray
      } yield colN
      )
    ).toArray

  val s1 = System.currentTimeMillis
  val elapsedBuilding = (s1 - s) / 1000
  println("Matrix building elapsed time: " + elapsedBuilding + " sec" + "\n")

  1 to productsSize foreach { n =>
    println("N: " + n)
    0 to volumeAvailable foreach { currentVolume =>
      def prod = productList(n - 1)
      def prodVolume = prod._2
      def prodPrice = prod._3
      if (currentVolume < prodVolume) {
        m(n)(currentVolume) = m(n - 1)(currentVolume)
        plm(n)(currentVolume) = plm(n - 1)(currentVolume)
      }
      else {
        if (m(n - 1)(currentVolume) >= m(n - 1)(currentVolume - prodVolume) + prodPrice) {
          m(n)(currentVolume) = m(n - 1)(currentVolume)
          plm(n)(currentVolume) = plm(n - 1)(currentVolume)
        }
        else {
          m(n)(currentVolume) = m(n - 1)(currentVolume - prodVolume) + prodPrice
          plm(n)(currentVolume) = plm(n - 1)(currentVolume - prodVolume) + prod
        }
      }
    }
  }

  val elapsedTotal = (System.currentTimeMillis - s1) / 1000
  println("# of products: " + plm(productsSize)(volumeAvailable).size + " of " + productList.size)
  println("Total volume: " + (0 /: plm(productsSize)(volumeAvailable).map { prod => prod._2 }) (_ + _))
  println("Total price: " + m(productsSize)(volumeAvailable))
  println("Sum of the product Ids: " + (0 /: plm(productsSize)(volumeAvailable).map { prod => prod._1 }) (_ + _))
  println("Total elapsed time: " + elapsedTotal + " sec" + "\n")


  def productFitsIndividuallyInTote(p: Product): Boolean = {
    p.length < 45 && p.width < 30 && p.height < 35
  }

  def parseProductFile(path: String): List[Product] = {
    Source
      .fromURL(getClass.getResource(path))
      .getLines()
      .map(s => s.split(","))
      .map(strings =>
        new Product(
          strings(0).toInt,
          strings(1).toInt,
          strings(2).toInt,
          strings(3).toInt,
          strings(4).toInt,
          strings(5).toInt)
      ).toList
  }

}
