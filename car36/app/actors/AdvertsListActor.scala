package actors

import akka.actor._
import model.Advert
import scala.collection.immutable.TreeMap
import java.util.UUID
import play.api.Logger


object AdvertsListActor {
  def props = Props[AdvertsListActor]

  //Receives
  case class GetAdverts(field: Option[String])
  case class AddAdvert(advert: Advert)
  case class UpdateAdvert(advertId: String, advert: Advert)
  case class DeleteAdvert(advertId: String)
  case class GetAdvert(advertId: String)

  //Sends
  //Advert
  //Seq[Advert]
  sealed trait Response
  case class Succeeded(advert: Option[Advert]) extends Response
  case class AdvertNotFound() extends Response
}

class AdvertsListActor extends Actor {
  import AdvertsListActor._

  //TODO This is just for mocking. Should be replaced with a persistence service
  var adverts = new TreeMap[String, Advert]()

  def receive = {
    case GetAdverts(field: Option[String]) => {
      field match {
        case Some(sortBy) => sender() ! Advert.sortBy(sortBy, adverts.values.toList)
        case None         => sender() ! adverts.values.toList
      }
    }
    case AddAdvert(advert: Advert) => {
      val newAdvert = advert.copy(id = UUID.randomUUID().toString())
      adverts = adverts + (newAdvert.id -> newAdvert)
      sender() ! newAdvert
    }
    case UpdateAdvert(advertId: String, newAdvert: Advert) => {
      val existingAdvert = adverts.get(advertId);
      existingAdvert match {
        case Some(advert) => {
          adverts = adverts + (advertId -> newAdvert)
          sender() ! Succeeded(None)
        }
        case None => sender() ! AdvertNotFound()
      }
    }
    case DeleteAdvert(advertId: String) => {
      adverts = adverts - advertId
      sender() ! Succeeded(None)
    }
    case GetAdvert(advertId: String) => {
      val advert = adverts.get(advertId);
      advert match {
        case Some(existingAdvert) => sender() ! Succeeded(Option(existingAdvert))
        case None                 => sender() ! AdvertNotFound()
      }

    }
  }

}