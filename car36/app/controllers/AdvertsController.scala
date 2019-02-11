package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.collection.immutable.TreeMap
import java.util.UUID
import model.Advert
import play.api.libs.json._

@Singleton
class AdvertsController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  //TODO This is just for mocking. Replace with persistence service
  //TODO Not threadsafe
  var adverts = new TreeMap[String, Advert]()

  def getAdverts(field: Option[String]) = Action { implicit request: Request[AnyContent] =>

    {
      field match {
        case Some(sortby) => Ok(Json.toJson(sortAdvertsBy(sortby)))
        case None         => Ok(Json.toJson(adverts.values.toList))
      }
    }
  }

  def addAdvert = Action(parse.json) { implicit request: Request[JsValue] =>
    {
      val body = request.body.asOpt[Advert]
      body match {
        case Some(advert) => {
          val newAdvert = advert.copy(id = UUID.randomUUID().toString())
          adverts = adverts + (newAdvert.id -> newAdvert)
          Ok(Json.toJson(newAdvert))
        }
        case None => BadRequest("Advert could not be parsed")
      }
    }
  }

  def getAdvert(advertId: String) = Action { implicit request: Request[AnyContent] =>
    {
      val advert = adverts.get(advertId);
      advert match {
        case Some(advert) => Ok(Json.toJson(advert))
        case None         => NotFound("No advert found with id" + advertId)
      }
    }
  }

  def deleteAdvert(advertId: String) = Action { implicit request: Request[AnyContent] =>
    {
      adverts = adverts - advertId
      Ok
    }
  }

  def setAdvert(advertId: String) = Action(parse.json) { implicit request: Request[JsValue] =>
    {
      val body = request.body.asOpt[Advert]
      body match {
        case Some(newAdvert) => {
          val existingAdvert = adverts.get(advertId);
          existingAdvert match {
            case Some(advert) => {
              adverts = adverts + (advertId -> newAdvert)
              Ok
            }
            case None => NotFound("Advert could not be updated, no advert found with id" + advertId)
          }
        }
        case None => BadRequest("Advert could not be parsed")
      }
    }
  }

  def sortAdvertsBy(field: String): Seq[Advert] = {
    //TODO sort by case class field
    adverts.values.toList
  }
  
  def validateAdvert(advert: Advert): Boolean = {
    //TODO
    true
  }

}