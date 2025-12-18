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
import play.api.libs.ws.DefaultBodyWritables.{writeableOf_String, writeableOf_urlEncodedSimpleForm}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.api.models.CreateOrganisationAuthPayload
import uk.gov.hmrc.apitestrunner.http.HttpClient
import scala.concurrent.Future

class AuthService extends HttpClient {
  val host: String     = TestEnvironment.url("auth")
  val endpoint: String = "/government-gateway/session/login"

  def postAuthPayload(payload: CreateOrganisationAuthPayload): Future[String] =
    mkRequest(host + endpoint)
      .post(Json.toJson(payload))
      .flatMap { response =>
        extractBearerToken(response) match {
          case Some(token) => Future.successful(token)
          case None        => Future.failed(new RuntimeException("Authorization bearer token not found"))
        }
      }

  def extractBearerToken(response: StandaloneWSRequest#Response): Option[String] =
    // Get all values of the "Authorization" header as a sequence
    response.headers.get("Authorization").flatMap { values =>
      values.collectFirst {
        case headerValue if headerValue.contains("Bearer ") =>
          // Split on "Bearer " and take what's after it
          headerValue.split("Bearer ", 2)(1).trim
      }
    }
}
