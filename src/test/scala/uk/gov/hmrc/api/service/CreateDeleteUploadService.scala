package uk.gov.hmrc.api.service

import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.apitestrunner.http.HttpClient
import scala.concurrent.Await
import scala.concurrent.duration.*

class CreateDeleteUploadService extends HttpClient {
  // TODO: URLs need to be configured / changed
  val host: String = TestEnvironment.url("create-delete-upload-single-stub")
  val endpoint: String = "/upload-results"

  def deleteSingleRequest(claimID: String, claimRef: String): StandaloneWSResponse = {
    Await.result(
      mkRequest(host + s"/$claimID" + endpoint + s"/$claimRef")
        .withHttpHeaders("Content-Type" -> "application/json")
        .delete(),
      10.seconds
    )
  }

  def deleteManyRequest(claimID: String): StandaloneWSResponse = {
    Await.result(
      mkRequest(host + s"/$claimID" + endpoint)
        .withHttpHeaders("Content-Type" -> "application/json")
        .delete(),
      10.seconds
    )
  }
}