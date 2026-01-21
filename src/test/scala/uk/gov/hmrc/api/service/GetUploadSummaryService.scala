package uk.gov.hmrc.api.service

import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.apitestrunner.http.HttpClient

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class GetUploadSummaryService extends HttpClient {
  val host: String     = TestEnvironment.url("Charities Claims Validation")
  val endpoint: String = "upload-results"

  def getUploadSummaryResults(
    claimId: String,
    token: String
  ): StandaloneWSResponse =
    Await.result(
      mkRequest(s"$host/$claimId/$endpoint")
        .withHttpHeaders(
          "Authorization" -> s"Bearer $token"
        )
        .get(),
      10.seconds
    )
}
