/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  val host: String     = TestEnvironment.url("Charities Claims Validation")
  val endpoint: String = "/create-upload-tracking"

  /** Can be used to pass in a complete payload object This can include correct and incorrect Payloads, i.e., successful
    * or invalid "validationType" which will cause a failure
    */
  def postAPayloadObject(claimId: String, payload: CreateUploadTrackingPayload, token: String): StandaloneWSResponse =
    Await.result(
      mkRequest(s"$host/$claimId$endpoint")
        .withHttpHeaders(
          "Content-Type"  -> "application/json",
          "Authorization" -> s"Bearer $token"
        )
        .post(Json.toJson(payload)),
      10.seconds
    )

  /** Used to cause a failure due to request body missing required parameters */
  def postInvalidJSON(claimId: String, token: String): StandaloneWSResponse =
    Await.result(
      mkRequest(s"$host/$claimId$endpoint")
        .withHttpHeaders(
          "Content-Type"  -> "application/json",
          "Authorization" -> s"Bearer $token"
        )
        .post(
          """{
            |  "reference": "f5da5578-8393-4cd1-be0e-d8ef1b78d8e7",
            |  "validationType": "GiftAid"
            |}""".stripMargin
        ),
      10.seconds
    )
}
