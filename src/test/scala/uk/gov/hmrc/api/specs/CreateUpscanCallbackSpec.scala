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

import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUploadTrackingData, MockCreateUpscanCallbackData}

class CreateUpscanCallbackSpec extends BaseSpec {
  Feature("Charities - Create Upscan Callback API - E2E") {
    Scenario("Successful Payload - Upscan gives us a valid success response", E2ETest) {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent a valid POST Request")
      val payload  = MockCreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayload
      val response = createUpscanService.postSuccessfulPayloadObject(
        MockCreateUploadTrackingData.getValidClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 204 status code should be returned")
      response.status shouldBe 204
    }

    Scenario("Uploading Successful Payloads to turn into failure details") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUploadTracking Endpoint is sent a valid POST Request - Quarantine")
      val quarantinePayload  =
        MockCreateUploadTrackingData.successfulPayloadWithReference(MockCreateUpscanCallbackData.getQuarantineRef)
      val quarantineResponse = createUploadTrackingService.postAPayloadObject(
        MockCreateUpscanCallbackData.getQuarantineClaimId,
        quarantinePayload,
        authHelper.bearerToken
      )

      Then("A 201 status code should be returned")
      quarantineResponse.status shouldBe 201

      And("The CreateUploadTracking Endpoint is sent a valid POST Request - Rejected")
      val rejectedPayload  =
        MockCreateUploadTrackingData.successfulPayloadWithReference(MockCreateUpscanCallbackData.getRejectedRef)
      val rejectedResponse = createUploadTrackingService.postAPayloadObject(
        MockCreateUpscanCallbackData.getRejectedClaimId,
        rejectedPayload,
        authHelper.bearerToken
      )

      Then("A 201 status code should be returned")
      rejectedResponse.status shouldBe 201

      And("The CreateUploadTracking Endpoint is sent a valid POST Request - Unknown")
      val unknownPayload  =
        MockCreateUploadTrackingData.successfulPayloadWithReference(MockCreateUpscanCallbackData.getUnknownRef)
      val unknownResponse = createUploadTrackingService.postAPayloadObject(
        MockCreateUpscanCallbackData.getUnknownClaimId,
        unknownPayload,
        authHelper.bearerToken
      )

      Then("A 201 status code should be returned")
      unknownResponse.status shouldBe 201
    }
  }

  Feature("Charities - Create Upscan Callback API - All Test Cases") {
    Scenario("Receive a Request Body that is QUARANTINE") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      // val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(0)
      val payload  = MockCreateUpscanCallbackData.getQurantineUpscanCallbackPayload()
      val response = createUpscanService.postUnsuccessfulPayloadObject(
        MockCreateUpscanCallbackData.getQuarantineClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 204 status code should be returned")
      response.status shouldBe 204
    }

    Scenario("Receive a Request Body that is REJECTED") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      // val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(1)
      val payload  = MockCreateUpscanCallbackData.getRejectedUpscanCallbackPayload()
      val response = createUpscanService.postUnsuccessfulPayloadObject(
        MockCreateUpscanCallbackData.getRejectedClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 204 status code should be returned")
      response.status shouldBe 204
    }

    Scenario("Receive a Request Body that is UNKNOWN") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      // val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(2)
      val payload  = MockCreateUpscanCallbackData.getUnknownUpscanCallbackPayload()
      val response = createUpscanService.postUnsuccessfulPayloadObject(
        MockCreateUpscanCallbackData.getUnknownClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 204 status code should be returned")
      response.status shouldBe 204
    }

    Scenario("Send a successful payload with an invalid file type") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getInvalidFileTypeCreateUpscanCallbackPayload
      val response = createUpscanService.postSuccessfulPayloadObject(
        MockCreateUploadTrackingData.getValidClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 400 status code should be returned")
      response.status shouldBe 400
    }

    Scenario("Send a successful payload with a reference that does not exist") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getInvalidReferenceCreateUpscanCallbackPayload
      val response = createUpscanService.postSuccessfulPayloadObject(
        MockCreateUploadTrackingData.getValidClaimId,
        payload,
        authHelper.bearerToken
      )

      Then("A 404 status code should be returned")
      response.status shouldBe 404
    }
  }
}
