package model

import org.scalatestplus.play._

import scala.collection.mutable
import java.util.Date
import play.api.libs.json._


class AdvertSpec extends PlaySpec {

  "An advert for a new car" should {
    "not be valid with mileage and registration" in {
      val newCar = Advert("1", "test", Option(AdvertFuelEnum.gasoline), 2000, true, Option(2000), Option(Advert.dateFormat.parse("2017-01-01")))
      Advert.validate(newCar) mustBe false
    }
    "not be valid with firstRegistration" in {
      val newCar = Advert("1", "test", Option(AdvertFuelEnum.gasoline), 2000, true, None, Option(Advert.dateFormat.parse("2017-01-01")))
      Advert.validate(newCar) mustBe false
    }
    "be valid without mileage or firstRegistration" in {
      val newCar = Advert("1", "test", Option(AdvertFuelEnum.gasoline), 2000, true, None, None)
      Advert.validate(newCar) mustBe true
    }
  }
  "An advert for an old car" should {
    "be valid with mileage and registration" in {
      val oldCar = Advert("1", "test", Option(AdvertFuelEnum.gasoline), 2000, false, Option(2000), Option(Advert.dateFormat.parse("2017-01-01")))
      Advert.validate(oldCar) mustBe true
    }
     "not be valid without mileage or firstRegistration" in {
      val oldCar = Advert("1", "test", Option(AdvertFuelEnum.gasoline), 2000, false, None, None)
      Advert.validate(oldCar) mustBe false
    }
  }
  "Multiple adverts" should {
    
    val advert1 = Advert("1", "first", Option(AdvertFuelEnum.gasoline), 2000, false, Option(3000), Option(Advert.dateFormat.parse("2017-01-01")))
    val advert2 = Advert("2", "second", Option(AdvertFuelEnum.gasoline), 1000, false, Option(6000), Option(Advert.dateFormat.parse("2017-01-02")))
    val advert3 = Advert("3", "third", Option(AdvertFuelEnum.diesel), 3000, true, None, Option(Advert.dateFormat.parse("2019-01-01")))

    val adverts = List(advert2,advert1,advert3)
    
    "be sorted correctly by id" in {
      val sortedById = Advert.sortBy("id", adverts)
      sortedById.head.id mustBe advert1.id
      sortedById.last.id mustBe advert3.id
    }
    "be sorted correctly by title" in {
      val sortedByTitle = Advert.sortBy("title", adverts)
      sortedByTitle.head.title mustBe advert1.title
      sortedByTitle.last.title mustBe advert3.title
    }
    "be sorted correctly by fuel" in {
      val sortedByFuel = Advert.sortBy("fuel", adverts)
      sortedByFuel.head.fuel mustBe advert3.fuel
      sortedByFuel.last.fuel mustBe advert1.fuel
    }
    "be sorted correctly by price" in {
      val sortedByPrice = Advert.sortBy("price", adverts)
      sortedByPrice.head.price mustBe advert2.price
      sortedByPrice.last.price mustBe advert3.price
    }
    "be sorted correctly by newness" in {
      val sortedByNew = Advert.sortBy("new", adverts)
      sortedByNew.head.`new` mustBe advert1.`new`
      sortedByNew.last.`new` mustBe advert3.`new`
    }
    "be sorted correctly by mileage" in {
      val sortedByMileage = Advert.sortBy("mileage", adverts)
      sortedByMileage.head.mileage mustBe advert3.mileage
      sortedByMileage.last.mileage mustBe advert2.mileage
    }
    "be sorted correctly by registration date" in {
      val sortedByRegistration = Advert.sortBy("firstRegistration", adverts)
      sortedByRegistration.head.firstRegistration mustBe advert1.firstRegistration
      sortedByRegistration.last.firstRegistration mustBe advert3.firstRegistration
    }
  }

}