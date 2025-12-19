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

import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.apitestrunner.http.HttpClient
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.StandaloneWSResponse
import play.api.libs.ws.DefaultBodyReadables._

import scala.concurrent.Await
import scala.concurrent.duration.*

class GovGatewayAuthService extends HttpClient {
  val host: String = TestEnvironment.url("government-gateway")
  val url          = s"$host/government-gateway/session/login"

  def authorizationHeader(jsonBody: String): String = {
    val response: StandaloneWSResponse =
      Await.result(
        mkRequest(url)
          .withHttpHeaders(
            "Content-Type" -> "application/json",
            "Accept"       -> "application/json"
          )
          .post(Json.parse(jsonBody)),
        10.seconds
      )
    val authHeaderOpt: Option[String]  =
      response.headers.get("Authorization").flatMap(_.headOption)
    authHeaderOpt.getOrElse("Authorisation header missing")
  }
}
