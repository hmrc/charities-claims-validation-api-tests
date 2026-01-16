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

package uk.gov.hmrc.api

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import uk.gov.hmrc.api.helpers.AuthHelper
import uk.gov.hmrc.api.service.*

trait BaseSpec extends AnyFeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterEach {
  val authHelper: AuthHelper                                   = new AuthHelper
  val authService: AuthService                                 = new AuthService
  val createUploadTrackingService: CreateUploadTrackingService = new CreateUploadTrackingService
  val createUpscanService: CreateUpscanCallbackService         = new CreateUpscanCallbackService
  val updateUploadStatusService: UpdateUploadStatusService     = new UpdateUploadStatusService
  val getUploadResultService: GetUploadResultService           = new GetUploadResultService

  authHelper.fetchAuthBearerToken()
  protected def authToken: String = {
    val token = authHelper.bearerToken
    token shouldNot include("No Auth Token Found")
    token
  }

  // TODO: Could add generic success here to stop repeating it everywhere?
}
