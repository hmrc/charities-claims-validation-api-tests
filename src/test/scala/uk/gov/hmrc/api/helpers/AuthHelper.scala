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

package uk.gov.hmrc.api.helpers

import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.models.CreateOrganisationAuthPayload
import uk.gov.hmrc.api.service.AuthService
import uk.gov.hmrc.api.utils.OrganisationAuthData

class AuthHelper {
  var bearerToken: String                    = ""
  val authAPI: AuthService                   = new AuthService
  val payload: CreateOrganisationAuthPayload = OrganisationAuthData().getOrganisationAuthPayload

  def fetchAuthBearerToken(): Unit = {
    val authTokenRegex                 = """(?i)Bearer\s+(\S+)""".r
    val response: StandaloneWSResponse = authAPI.postAuthPayload(payload)

    val token: Option[String] = response.header("Authorization").flatMap { header =>
      authTokenRegex.findFirstMatchIn(header).map(_.group(1))
    }
    bearerToken = token.getOrElse(throw new RuntimeException("No Auth Token Found"))
  }
}
