package domain


class Product(productIdIn: Int, priceIn: Int, lengthIn: Int, widthIn: Int, heightIn: Int, weightIn: Int) extends Ordered[Product] {

  val productId = productIdIn
  val price = priceIn
  val length = lengthIn
  val width = widthIn
  val height = heightIn
  val weight = weightIn
  val volume = length * width * height
  val priceVolumeRatio = price.toFloat / volume

  override def compare(that: Product): Int = {
    if ((priceVolumeRatio compare that.priceVolumeRatio) == 0) {
      -weight compare -that.weight
    }
    else priceVolumeRatio compare that.priceVolumeRatio
  }

  override def toString: String = "product #" + productId + " / " + price + "$ / " + volume + "cm^3 / " + weight + "g / ratio: " + priceVolumeRatio
}



