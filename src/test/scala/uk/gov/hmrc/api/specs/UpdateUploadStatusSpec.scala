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
import uk.gov.hmrc.api.data.UpdateUploadStatusData

class UpdateUploadStatusSpec extends BaseSpec with UploadTestDataHelper {
  Feature("Charities - Update Upload Status API - E2E") {
    Scenario("Successful Payload - A valid claim that is 'AWAITING_UPLOAD' has been updated", E2ETest) {
      Given("There is an Auth Token and it's valid")
      authToken

      When("We have sent the first set of test data for UpdateUploadStatus API to the DB")
      uploadTestData(
        authToken,
        claimId = UpdateUploadStatusData.getValidClaimId,
        reference = UpdateUploadStatusData.getValidReference
      )

      And("The UpdateUploadStatus Endpoint is sent a valid PUT Request and claimID / ref / fileStatus are valid")
      val payload  = UpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        UpdateUploadStatusData.getValidClaimId,
        UpdateUploadStatusData.getValidReference,
        payload,
        authToken
      )

      Then("UpdateUploadStatus check response")
      checkGenericResponseBodyAndStatusCode(response, 200, true)
    }
  }

  Feature("Charities - Update Upload Status API - All Tests") {
    Scenario("Successful Payload - A valid claim that IS NOT 'AWAITING_UPLOAD'") {
      Given("There is an Auth Token and it's valid")
      authToken

      uploadTestData(
        authToken,
        claimId = UpdateUploadStatusData.getValidClaimIdDifferentFileStatus,
        reference = UpdateUploadStatusData.getValidReferenceDifferentFileStatus
      )

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request and claimID / ref / fileStatus are valid")
      val payload  = UpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        UpdateUploadStatusData.getValidClaimIdDifferentFileStatus,
        UpdateUploadStatusData.getValidReferenceDifferentFileStatus,
        payload,
        authToken
      )

      Then("Nothing should be updated but a 200 status code should be returned")
      checkGenericResponseBodyAndStatusCode(response, 200, true)
    }

    Scenario("Successful Payload - claimID doesn't exist") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request and claimID is not valid")
      val payload  = UpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        UpdateUploadStatusData.getInvalidClaimId,
        UpdateUploadStatusData.getValidReference,
        payload,
        authToken
      )

      Then("A 404 status code should be returned")
      checkErrorResponse(response)
    }

    Scenario("Successful Payload - reference doesn't exist") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request and reference is not valid")
      val payload  = UpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        UpdateUploadStatusData.getValidClaimId,
        UpdateUploadStatusData.getInvalidReference,
        payload,
        authToken
      )

      Then("A 404 status code should be returned")
      checkErrorResponse(response)
    }

    Scenario("Successful Payload - claimID and reference do not exist") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The UpdateUploadStatus Endpoint is sent a valid PUT Request where claimID and reference is not valid")
      val payload  = UpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(
        UpdateUploadStatusData.getInvalidClaimId,
        UpdateUploadStatusData.getInvalidReference,
        payload,
        authToken
      )

      Then("A 404 status code should be returned")
      checkErrorResponse(response)
    }

    Scenario("Unsuccessful Payload - fileStatus != 'VERIFYING'") {
      Given("There is an Auth Token and it's valid")
      authToken

      When("The UpdateUploadStatus Endpoint is sent an valid PUT Request where fileStatus is incorrect")
      val payload  = UpdateUploadStatusData.getInvalidFileStatusPayload
      val response = updateUploadStatusService.postAPayloadObject(
        UpdateUploadStatusData.getValidClaimId,
        UpdateUploadStatusData.getValidReference,
        payload,
        authToken
      )

      Then("A 400 status code should be returned")
      checkGenericResponseBodyAndStatusCode(response, 400, false)
    }
  }
}
