package uk.gov.hmrc.api.service

import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.apitestrunner.http.HttpClient
import uk.gov.hmrc.api.models.{CreateUpscanCallbackFailedPayload, CreateUpscanCallbackSuccessfulPayload}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class CreateUpscanCallbackService extends HttpClient {
  // TODO: URLs need to be configured / changed
  val host: String = TestEnvironment.url("create-upscan-callback-stub")
  val endpoint: String = "/123/upscan-callback"
  
  def postSuccessfulPayloadObject(payload: CreateUpscanCallbackSuccessfulPayload): StandaloneWSResponse = {
    Await.result(
      mkRequest(host + endpoint)
        .withHttpHeaders("Content-Type" -> "application/json")
        .post(Json.toJson(payload)),
      10.seconds
    )
  }

  def postUnsuccessfulPayloadObject(payload: CreateUpscanCallbackFailedPayload): StandaloneWSResponse = {
    Await.result(
      mkRequest(host + endpoint)
        .withHttpHeaders("Content-Type" -> "application/json")
        .post(Json.toJson(payload)),
      10.seconds
    )
  }
}
