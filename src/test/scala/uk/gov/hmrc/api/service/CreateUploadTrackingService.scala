package uk.gov.hmrc.api.service

import play.api.libs.json.Json
import play.api.libs.ws.DefaultBodyWritables.*
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.api.models.CreateUploadTrackingPayload
import uk.gov.hmrc.api.utils.MockCreateUploadTrackingData
import uk.gov.hmrc.apitestrunner.http.HttpClient

import scala.concurrent.Await
import scala.concurrent.duration.*

class CreateUploadTrackingService extends HttpClient {
  val host: String = TestEnvironment.url("create-upload-tracking-stub")
  val endpoint: String = "/123/create-upload-tracking"

  def post(payload: CreateUploadTrackingPayload): StandaloneWSResponse = {
    Await.result(
      mkRequest(host + endpoint)
        .withHttpHeaders("Content-Type" -> "application/json")
        .post(Json.toJson(payload)),
      10.seconds
    )
  }
}
