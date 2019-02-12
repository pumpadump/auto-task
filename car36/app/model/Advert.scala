/**
 * CarScout36
 * A simple RESTful web-service that allows users to place new car adverts and view, modify and delete existing car adverts.
 *
 * Contact: samy@samyateia.de
 *
 */

package model
import play.api.libs.json._
import java.util.Date
import java.util.UUID

case class Advert(
  id:                String,
  title:             String,
  fuel:              Option[AdvertFuelEnum.AdvertFuelEnum],
  price:             Int,
  `new`:             Boolean,
  mileage:           Option[Int],
  firstRegistration: Option[Date])

object Advert {

  implicit val advertReads = Json.reads[Advert]
  
  val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")
  
  implicit val advertWrites = new Writes[Advert] {
    def writes(advert: Advert) = Json.obj(
      "id" -> advert.id,
      "title" -> advert.title,
      "fuel" -> advert.fuel,
      "price" -> advert.price,
      "new" -> advert.`new`,
      "mileage" -> advert.mileage,
      "firstRegistration" -> dateFormat.format(advert.firstRegistration.get))
  }
  
  /**
   * Should return a list of adverts sorted by the given field name in the parameter field.
   * @param field the field name to sort the adverts by, e.g 'title', 'price'
   * @return
   */
  def sortBy(field: String, advertsList: Seq[Advert]): Seq[Advert] = {
    field match {
      case "id"                => advertsList.sortBy(advert => advert.id)
      case "title"             => advertsList.sortBy(advert => advert.title)
      case "fuel"              => advertsList.sortBy(advert => advert.fuel)
      case "price"             => advertsList.sortBy(advert => advert.price)
      case "new"               => advertsList.sortBy(advert => advert.`new`)
      case "mileage"           => advertsList.sortBy(advert => advert.mileage)
      case "firstRegistration" => advertsList.sortBy(advert => advert.firstRegistration match {
        case Some(date) => date.getTime
        case None => 0l
      })
      case _ => {
        println("could not match field "+field)
        advertsList.sortBy(advert => advert.id)
      }
    }
  }

  /**
   * Validates that an advert for a new car doesn't contain mileage or registration information, whereas a used
   * car must contain both.
   * @param advert
   */
  def validate(advert: Advert): Boolean = {
    if (advert.`new`) {
      !(advert.mileage.isDefined || advert.firstRegistration.isDefined)
    } else {
      advert.mileage.isDefined && advert.firstRegistration.isDefined
    }
  }
}

object AdvertFuelEnum extends Enumeration {
  val diesel, gasoline = Value
  type AdvertFuelEnum = Value
  implicit val format: Format[Value] = Format(Reads.enumNameReads(this), Writes.enumNameWrites[AdvertFuelEnum.type])
}
