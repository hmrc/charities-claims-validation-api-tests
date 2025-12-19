package uk.gov.hmrc.api.service

import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.api.models.UpdateUploadStatusPayload
import uk.gov.hmrc.apitestrunner.http.HttpClient

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class UpdateUploadStatusService extends HttpClient {
  val host: String = TestEnvironment.url("Charities Claims Validation")
  val endpoint: String = "/claim-123/upload-results/ref-001"

  def postAPayloadObject(payload: UpdateUploadStatusPayload, token: String): StandaloneWSResponse =
    Await.result(
      mkRequest(host + endpoint)
        .withHttpHeaders(
          "Authorization" -> s"Bearer $token"
        )
        .put(Json.toJson(payload)),
      10.seconds
    )
}
