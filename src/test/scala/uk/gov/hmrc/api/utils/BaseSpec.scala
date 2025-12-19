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

package uk.gov.hmrc.api.utils

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import uk.gov.hmrc.api.helpers.AuthHelper
import uk.gov.hmrc.api.service.{AuthService, CreateUploadTrackingService}

trait BaseSpec extends AnyFeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterEach {
  val authHelper: AuthHelper                                = new AuthHelper
  val authService: AuthService                              = new AuthService
  val createUploadTrackingStub: CreateUploadTrackingService = new CreateUploadTrackingService
//  val createDeleteUploadStub: CreateDeleteUploadService     = new CreateDeleteUploadService
//  val createUpscanStub: CreateUpscanCallbackService         = new CreateUpscanCallbackService

  authHelper.fetchAuthBearerToken()
}
