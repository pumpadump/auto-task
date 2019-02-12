package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import model.Advert
import model.AdvertFuelEnum
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor._
import com.typesafe.config.ConfigFactory



class AdvertsControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  


  "AdvertsController GET" should {

    "Return an empty list of adverts initially" in {
      val actorSystem = ActorSystem("testActorSystem", ConfigFactory.load())
      val controller = new AdvertsController(actorSystem, stubControllerComponents())
      val adverts = controller.getAdverts(Option.empty).apply(FakeRequest(GET, "/"))
      status(adverts) mustBe OK
      contentType(adverts) mustBe Some("application/json")
      contentAsJson(adverts) mustEqual (Json.parse("[]"))
    }
    
    //TODO test post,put,delete with spec

  }

}