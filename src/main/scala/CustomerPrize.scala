import scala.io.Source
import domain.Product

object CustomerPrize extends App {

  val productsFilename = "products.csv"
  val toteLength = 40
  val toteWidth = 30
  val toteHeight = 35

  val availableVolume = toteLength * toteWidth * toteHeight

  val productList = parseProductFile(productsFilename)
    .filter(p => productFitsIndividuallyInTote(p))
    .sorted
    .reverse
    .take(500) //hack to bypass the OOM errors, we sort by priceVolumeRatio and -weight and test with the 500 optimal products

  val numProducts = productList.size

  val startTime = System.currentTimeMillis

  //Knapsack 0/1 algorithm
  val matrix = Array.ofDim[Int](numProducts + 1, availableVolume + 1)
  val plm = (List((
    for {v <- 0 to availableVolume}
      yield Set[Product]()).toArray) ++
    (
      for {
        n <- 0 until numProducts
        colN = (
          for {v <- 0 to availableVolume}
            yield Set[Product](productList(n))).toArray
      } yield colN
      )
    ).toArray

  1 to numProducts foreach { n =>
    0 to availableVolume foreach { currentVolume =>
      def prod = productList(n - 1)
      def prodVolume = prod.volume
      def prodPrice = prod.price
      if (currentVolume < prodVolume) {
        matrix(n)(currentVolume) = matrix(n - 1)(currentVolume)
        plm(n)(currentVolume) = plm(n - 1)(currentVolume)
      }
      else {
        if (matrix(n - 1)(currentVolume) >= matrix(n - 1)(currentVolume - prodVolume) + prodPrice) {
          matrix(n)(currentVolume) = matrix(n - 1)(currentVolume)
          plm(n)(currentVolume) = plm(n - 1)(currentVolume)
        }
        else {
          matrix(n)(currentVolume) = matrix(n - 1)(currentVolume - prodVolume) + prodPrice
          plm(n)(currentVolume) = plm(n - 1)(currentVolume - prodVolume) + prod
        }
      }
    }
  }

  val elapsedTotal = (System.currentTimeMillis - startTime) / 1000
  val productsFound = plm(numProducts)(availableVolume).toList
  println("Total elapsed time: " + elapsedTotal + " sec" + "\n")
  println("# of products: " + productsFound.size + " of " + productList.size)
  println("Total volume: " + productsFound.map { prod => prod.volume }.sum) //toList to deal with 2 products having the same volume, if we keep a Set, the map will keep only 1 volume out of the 2
  println("Total price: " + matrix(numProducts)(availableVolume))
  println("Total weight: " + productsFound.map { prod => prod.weight }.sum)
  println("Products : ----------------------------------------")
  println(productsFound.foreach(println))
  println("---------------------------------------------------")
  print("All right! Let's send that to : " + productsFound.map { prod => prod.productId }.sum + "@redmart.com")


  def productFitsIndividuallyInTote(p: Product): Boolean = {
    p.length < toteLength && p.width < toteWidth && p.height < toteHeight
  }

  def parseProductFile(url: String): List[Product] = {
    Source
      .fromInputStream(getClass.getResourceAsStream(url))
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
