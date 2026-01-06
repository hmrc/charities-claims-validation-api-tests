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

import play.api.libs.json.Json
import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.utils.{BaseSpec, MockUpdateUploadStatusData}

class UpdateUploadStatusSpec extends BaseSpec {
  Feature("Charities - Update Upload Status API - E2E") {
    Scenario("Successful Payload - A valid claim that is 'AWAITING_UPLOAD' has been updated", E2ETest) {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request and claimID / ref / fileStatus are valid")
      val payload  = MockUpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        MockUpdateUploadStatusData.getValidClaimId,
        MockUpdateUploadStatusData.getValidReference,
        payload,
        authHelper.bearerToken
      )

      Then("A 200 status code should be returned")
      response.status shouldBe 200

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }
  }

  Feature("Charities - Update Upload Status API - All Tests") {
    Scenario("Successful Payload - A valid claim that IS NOT 'AWAITING_UPLOAD'") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request and claimID / ref / fileStatus are valid")
      val payload  = MockUpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        MockUpdateUploadStatusData.getValidClaimId,
        MockUpdateUploadStatusData.getValidReference,
        payload,
        authHelper.bearerToken
      )

      Then("Nothing should be updated but a 200 status code should be returned")
      response.status shouldBe 200

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }

    Scenario("Successful Payload - claimID doesn't exist") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request and claimID is not valid")
      val payload  = MockUpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        MockUpdateUploadStatusData.getInvalidClaimId,
        MockUpdateUploadStatusData.getValidReference,
        payload,
        authHelper.bearerToken
      )

      Then("A 404 status code should be returned")
      response.status shouldBe 404

      And(
        "The response body is { " +
          "error: 'CLAIM_REFERENCE_DOES_NOT_EXIST' " +
          "message: 'There is no reference = ref-123 found for the given claimId = 123'" +
          "}"
      )
      (Json.parse(response.body) \ "error").as[String] shouldEqual "CLAIM_REFERENCE_DOES_NOT_EXIST"
      (Json.parse(response.body) \ "message").as[String]    should include("There is no reference")
    }

    Scenario("Successful Payload - reference doesn't exist") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request and reference is not valid")
      val payload  = MockUpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        MockUpdateUploadStatusData.getValidClaimId,
        MockUpdateUploadStatusData.getInvalidReference,
        payload,
        authHelper.bearerToken
      )

      Then("A 404 status code should be returned")
      response.status shouldBe 404

      And(
        "The response body is { " +
          "error: 'CLAIM_REFERENCE_DOES_NOT_EXIST' " +
          "message: 'There is no reference = ref-123 found for the given claimId = 123'" +
          "}"
      )
      (Json.parse(response.body) \ "error").as[String] shouldEqual "CLAIM_REFERENCE_DOES_NOT_EXIST"
      (Json.parse(response.body) \ "message").as[String]    should include("There is no reference")
    }

    Scenario("Successful Payload - claimID and reference do not exist") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request where claimID and reference is not valid")
      val payload  = MockUpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        MockUpdateUploadStatusData.getInvalidClaimId,
        MockUpdateUploadStatusData.getInvalidReference,
        payload,
        authHelper.bearerToken
      )

      Then("A 404 status code should be returned")
      response.status shouldBe 404

      And(
        "The response body is { " +
          "error: 'CLAIM_REFERENCE_DOES_NOT_EXIST' " +
          "message: 'There is no reference = ref-123 found for the given claimId = 123'" +
          "}"
      )
      (Json.parse(response.body) \ "error").as[String] shouldEqual "CLAIM_REFERENCE_DOES_NOT_EXIST"
      (Json.parse(response.body) \ "message").as[String]    should include("There is no reference")
    }

    Scenario("Unsuccessful Payload - fileStatus != 'VERIFYING'") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The UpdateUploadStatus Endpoint is sent an valid PUT Request where fileStatus is incorrect")
      val payload  = MockUpdateUploadStatusData.getInvalidFileStatusPayload
      val response = updateUploadStatusService.postAPayloadObject(
        MockUpdateUploadStatusData.getValidClaimId,
        MockUpdateUploadStatusData.getValidReference,
        payload,
        authHelper.bearerToken
      )

      Then("A 400 status code should be returned")
      response.status shouldBe 400

      And("The response body is { success: false }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe false
    }
  }
}
