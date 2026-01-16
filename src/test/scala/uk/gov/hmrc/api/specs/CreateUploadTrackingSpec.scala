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

package uk.gov.hmrc.api.specs

import org.scalactic.Prettifier.default
import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.data.CreateUploadTrackingData

class CreateUploadTrackingSpec extends BaseSpec with UploadTestDataHelper {
  Feature("Charities - Create Upload Tracking API - E2E") {
    Scenario("Successful Payload - User wants to upload a spreadsheet for charity claim(s)", E2ETest) {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The CreateUploadTracking Endpoint is sent a valid POST Request")
      uploadTestData(authToken)
    }
  }

  Feature("Charities - Create Upload Tracking API - All Test Cases") {
    Scenario("Invalid Payload - User wants to upload a spreadsheet for charity claim(s)") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The CreateUploadTracking Endpoint is sent an invalid POST Request")
      val payload  = CreateUploadTrackingData.getInvalidValidationCreateUploadTrackingPayload
      val response =
        createUploadTrackingService.postAPayloadObject(
          CreateUploadTrackingData.getInvalidClaimId,
          payload,
          authToken
        )

      Then("A 400 as 'validationType' is incorrect status code should be returned")
      checkGenericResponseBodyAndStatusCode(response, responseCode = 400, responseSuccess = false)
    }

    Scenario("Incomplete Payload - User wants to upload a spreadsheet for charity claim(s)") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The CreateUploadTracking Endpoint is sent an incomplete POST Request")
      val response =
        createUploadTrackingService.postInvalidJSON(CreateUploadTrackingData.getValidClaimId, authToken)

      Then("A 400 status code should be returned due to missing required information")
      checkGenericResponseBodyAndStatusCode(response, responseCode = 400, responseSuccess = false)
    }

    Scenario("The 'validationType' is a duplicate for this claimID") {
      Given("There is an Auth Token and it's valid")
      authToken

      When(
        "The CreateUploadTracking Endpoint is sent a valid POST Request" +
          "A 201 response code is returned as the claim is initially added to the DB"
      )
      uploadTestData(authToken)

      Then("A 500 response code is returned as 'validationType' is a duplicate of an existing claim already made")
      uploadTestData(authToken, responseCode = 500, responseSuccess = false)
    }
  }
}
