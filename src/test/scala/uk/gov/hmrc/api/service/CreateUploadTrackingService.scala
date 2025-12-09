package uk.gov.hmrc.api.service

import play.api.libs.json.Json
import play.api.libs.ws.DefaultBodyWritables.*
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.api.models.CreateUploadTrackingPayload
import uk.gov.hmrc.apitestrunner.http.HttpClient

import scala.concurrent.Await
import scala.concurrent.duration.*

class CreateUploadTrackingService extends HttpClient {
  // TODO: URLs need to be configured / changed
  val host: String = TestEnvironment.url("create-upload-tracking-stub")
  val endpoint: String = "/123/create-upload-tracking"

  /** Can be used to pass in a complete payload object
   * This can include correct and incorrect Payloads, i.e., successful or invalid "validationType" which will cause a failure */
  def postAPayloadObject(payload: CreateUploadTrackingPayload): StandaloneWSResponse = {
    Await.result(
      mkRequest(host + endpoint)
        .withHttpHeaders("Content-Type" -> "application/json")
        .post(Json.toJson(payload)),
      10.seconds
    )
  }

  /** Used to cause a failure due to request body missing required parameters */
  def postInvalidJSON: StandaloneWSResponse = {
    Await.result(
      mkRequest(host + endpoint)
        .withHttpHeaders("Content-Type" -> "application/json")
        .post(
          """{
            |  "reference": "f5da5578-8393-4cd1-be0e-d8ef1b78d8e7",
            |  "validationType": "GiftAid"
            |}""".stripMargin
        ),
      10.seconds
    )
  }
}