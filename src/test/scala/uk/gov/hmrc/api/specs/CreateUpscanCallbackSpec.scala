/*
 * Copyright 2026 HM Revenue & Customs
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

import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.data.{CreateUploadTrackingData, CreateUpscanCallbackData}

class CreateUpscanCallbackSpec extends BaseSpec with UploadTestDataHelper {
  Feature("Charities - Create Upscan Callback API - E2E") {
    Scenario("Successful Payload - Upscan gives us a valid success response", E2ETest) {
      Given("There is an Auth Token and it's valid")
      authToken

      // First need to send a document to the DB
      When("The CreateUploadTracking Endpoint is sent a valid POST Request")
      uploadTestData(authToken)

      Then("The CreateUpscanCallback Endpoint is sent a valid POST Request")
      val payload  = CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayload
      val response = createUpscanService.postSuccessfulPayloadObject(
        CreateUploadTrackingData.getValidClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 204 status code should be returned from UpscanCallback API")
      checkStatusCode(response, 204)
    }
  }

  Feature("Charities - Create Upscan Callback API - All Test Cases") {
    Scenario("Receive a Request Body that is QUARANTINE") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The CreateUploadTracking Endpoint is sent a valid POST Request")
      uploadTestData(
        authToken,
        claimId = CreateUpscanCallbackData.getQuarantineClaimId,
        reference = CreateUpscanCallbackData.getQuarantineRef
      )

      And("The CreateUpscanCallback Endpoint is sent an invalid POST Request using the same details just created")
      // val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(0)
      val payload  = CreateUpscanCallbackData.getQuarantineUpscanCallbackPayload()
      val response = createUpscanService.postUnsuccessfulPayloadObject(
        CreateUpscanCallbackData.getQuarantineClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 204 status code should be returned")
      checkStatusCode(response, 204)
    }

    Scenario("Receive a Request Body that is REJECTED") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The CreateUploadTracking Endpoint is sent a valid POST Request")
      uploadTestData(
        authToken,
        claimId = CreateUpscanCallbackData.getRejectedClaimId,
        reference = CreateUpscanCallbackData.getRejectedRef
      )

      And("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      // val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(1)
      val payload  = CreateUpscanCallbackData.getRejectedUpscanCallbackPayload()
      val response = createUpscanService.postUnsuccessfulPayloadObject(
        CreateUpscanCallbackData.getRejectedClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 204 status code should be returned")
      checkStatusCode(response, 204)
    }

    Scenario("Receive a Request Body that is UNKNOWN") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The CreateUploadTracking Endpoint is sent a valid POST Request")
      uploadTestData(
        authToken,
        claimId = CreateUpscanCallbackData.getUnknownClaimId,
        reference = CreateUpscanCallbackData.getUnknownRef
      )

      And("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      // val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(2)
      val payload  = CreateUpscanCallbackData.getUnknownUpscanCallbackPayload()
      val response = createUpscanService.postUnsuccessfulPayloadObject(
        CreateUpscanCallbackData.getUnknownClaimId,
        payload,
        authHelper.bearerToken
      )

      checkStatusCode(response, 204)
    }

    Scenario("Send a successful payload with an invalid file type") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = CreateUpscanCallbackData.getInvalidFileTypeCreateUpscanCallbackPayload
      val response = createUpscanService.postSuccessfulPayloadObject(
        CreateUploadTrackingData.getValidClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 400 status code should be returned")
      checkStatusCode(response, 400)
    }

    Scenario("Send a successful payload with a reference that does not exist") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = CreateUpscanCallbackData.getInvalidReferenceCreateUpscanCallbackPayload
      val response = createUpscanService.postSuccessfulPayloadObject(
        CreateUploadTrackingData.getValidClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 404 status code should be returned")
      checkStatusCode(response, 404)
    }
  }
}
