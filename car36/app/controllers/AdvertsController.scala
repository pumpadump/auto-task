package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.collection.immutable.TreeMap
import java.util.UUID
import model.Advert
import play.api.libs.json._
import actors.AdvertsListActor
import actors.AdvertsListActor._
import akka.actor._
import akka.util.Timeout
import scala.util.Success
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.None
import play.api.Logger


@Singleton
class AdvertsController @Inject() (system: ActorSystem, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val listActor = system.actorOf(AdvertsListActor.props, "adverts-list-actor")

  import scala.concurrent.duration._
  import akka.pattern.ask
  implicit val timeout: Timeout = 600.seconds

  def getAdverts(field: Option[String]) = Action.async { implicit request: Request[AnyContent] =>
    {
      (listActor ? GetAdverts(field)).mapTo[Seq[Advert]].map {
        list => {
          Ok(Json.toJson(list))
        }
      }
    }
  }

  def addAdvert = Action(parse.json).async { implicit request: Request[JsValue] =>
    {
      val body = request.body.asOpt[Advert]
      body match {
        case Some(advert) if Advert.validate(advert) => {
          val newAdvert = advert.copy(id = UUID.randomUUID().toString());
          (listActor ? AddAdvert(newAdvert)).mapTo[Advert].map {
            addedAdvert => Ok(Json.toJson(addedAdvert))
          }
        }
        case _ => Future(BadRequest("Advert could not be parsed"))
      }
    }
  }

  def getAdvert(advertId: String) = Action.async { implicit request: Request[AnyContent] =>
    {
      (listActor ? GetAdvert(advertId)).mapTo[Response].map {
        response =>
          response match {
            case Succeeded(advert) => {
              Ok(Json.toJson(advert))
            }
            case AdvertNotFound()  => NotFound
          }
      }
    }
  }

  def deleteAdvert(advertId: String) = Action.async { implicit request: Request[AnyContent] =>
    {
      (listActor ? DeleteAdvert(advertId)).mapTo[Response].map {
        response =>
          response match {
            case Succeeded(_)  => Ok
            case AdvertNotFound() => NotFound
          }
      }
    }
  }

  def setAdvert(advertId: String) = Action(parse.json).async { implicit request: Request[JsValue] =>
    {
      val body = request.body.asOpt[Advert]
      body match {
        case Some(newAdvert) if Advert.validate(newAdvert) => {
          (listActor ? UpdateAdvert(advertId, newAdvert)).mapTo[Response].map {
            response =>
              response match {
                case Succeeded(_)  => Ok
                case AdvertNotFound() => NotFound
              }
          }
        }
        case _ => Future(BadRequest("Advert could not be parsed"))
      }
    }
  }

}