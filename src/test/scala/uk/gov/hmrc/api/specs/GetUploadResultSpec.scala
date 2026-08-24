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
import uk.gov.hmrc.api.data.globals.{FailureReason, FileStatus, ValidationType}
import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.data.{CreateUpscanCallbackData, GetUploadResultData, UpdateUploadStatusData}
import uk.gov.hmrc.api.specs.tags.E2ETest

class GetUploadResultSpec extends BaseSpec with UploadTestDataHelper {
  Feature("Charities - Get Upload Result API - All successful response bodies") {
    Scenario("Testing Awaiting Upload Response", E2ETest) {
      authToken

      Then("Upload AwaitingUpload Test Data")

      /** Uploading the data to the DB first */
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getAwaitingUploadClaimId,
        reference = GetUploadResultData.getAwaitingUploadReference
      )

      /** Checking AwaitingUpload response body */
      When("We check that AwaitingClaim returns expected response body")
      val response = getUploadResultService.getUploadResults(
        GetUploadResultData.getAwaitingUploadClaimId,
        GetUploadResultData.getAwaitingUploadReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(response, 200)

      checkCommonResponseBodies(
        response,
        ValidationType.GiftAid,
        FileStatus.AWAITING_UPLOAD,
        isWrappedByUploadsArray = true
      )
    }

    Scenario("Testing Verifying Response", E2ETest) {
      authToken

      /** We have a valid auth token so now upload the test data to the DB */
      Then("Upload Verifying Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getVerifyingClaimId,
        reference = GetUploadResultData.getVerifyingReference
      )

      /** Checking Verifying response body, we need to hit an additional endpoint to change the current "fileStatus" =
        * "AWAITING_UPLOAD" to become "VERIFYING"
        */
      Then("We call the CreateUpdateUpload API to update 'fileStatus' from AWAITING_UPLOAD to VERIFYING")
      updateUploadStatusService.postAPayloadObject(
        GetUploadResultData.getVerifyingClaimId,
        GetUploadResultData.getVerifyingReference,
        UpdateUploadStatusData.getSuccessfulPayload,
        authToken
      )

      Then("We check now that Verifying returns expected response body")
      val response = getUploadResultService.getUploadResults(
        GetUploadResultData.getVerifyingClaimId,
        GetUploadResultData.getVerifyingReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(response, 200)

      checkCommonResponseBodies(
        response,
        ValidationType.GiftAid,
        FileStatus.VERIFYING
      )
    }

    Scenario("Testing VERIFICATION_FAILED response body", E2ETest) {
      authToken

      /** We have the auth token so upload the test data for all types of VERIFICATION_FAILED
        *   - QUARANTINE
        *   - REJECTED
        *   - UNKNOWN
        */
      Then("Upload Quarantine Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getQuarantineClaimId,
        reference = GetUploadResultData.getQuarantineReference
      )

      Then("Upload Rejected Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getRejectedClaimId,
        reference = GetUploadResultData.getRejectedReference
      )

      Then("Upload Unknown Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getUnknownClaimId,
        reference = GetUploadResultData.getUnknownReference
      )

      /** Checking the "fileStatus" = "VERIFICATION_FAILED" which includes
        *   - QUARANTINE
        *   - REJECTED
        *   - UNKNOWN Again need to hit additional endpoints using CreateUpscanCallback to update these details
        */
      Then("We update three payloads to contain each unique version of VERIFICATION_FAILED")
      createUpscanService.postUnsuccessfulPayloadObject(
        GetUploadResultData.getQuarantineClaimId,
        CreateUpscanCallbackData.getQuarantineUpscanCallbackPayload(
          GetUploadResultData.getQuarantineReference
        ),
        authToken
      ) // QUARANTINE

      createUpscanService.postUnsuccessfulPayloadObject(
        GetUploadResultData.getRejectedClaimId,
        CreateUpscanCallbackData.getRejectedUpscanCallbackPayload(GetUploadResultData.getRejectedReference),
        authToken
      ) // REJECTED

      createUpscanService.postUnsuccessfulPayloadObject(
        GetUploadResultData.getUnknownClaimId,
        CreateUpscanCallbackData.getUnknownUpscanCallbackPayload(GetUploadResultData.getUnknownReference),
        authToken
      ) // UNKNOWN

      /** Now calling GetUploadResult for all three claims to check the response body */
      Then("We call GetUploadResult to check QUARANTINE")
      val quarantineResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getQuarantineClaimId,
        GetUploadResultData.getQuarantineReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(quarantineResponse, 200)

      And("The response body is what we expect")
      checkCommonResponseBodies(
        quarantineResponse,
        ValidationType.GiftAid,
        FileStatus.VERIFICATION_FAILED,
        failureReason = FailureReason.QUARANTINE
      )

      Then("We call GetUploadResult to check REJECTED")
      val rejectedResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getRejectedClaimId,
        GetUploadResultData.getRejectedReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(rejectedResponse, 200)

      And("The response body is what we expect")
      checkCommonResponseBodies(
        rejectedResponse,
        ValidationType.GiftAid,
        FileStatus.VERIFICATION_FAILED,
        failureReason = FailureReason.REJECTED
      )

      Then("We call GetUploadResult to check UNKNOWN")
      val unknownResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getUnknownClaimId,
        GetUploadResultData.getUnknownReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(unknownResponse, 200)

      And("The response body is what we expect")
      checkCommonResponseBodies(
        unknownResponse,
        ValidationType.GiftAid,
        FileStatus.VERIFICATION_FAILED,
        failureReason = FailureReason.UNKNOWN
      )
    }
  }

  Feature("Charities - Get Upload Result API - Failed Response Bodies") {
    Scenario("Request reference for given claimID is not found", E2ETest) {
      authToken

      Then("Upload Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getAwaitingUploadClaimId,
        reference = GetUploadResultData.getAwaitingUploadReference
      )

      /** Checking response body by sending in a reference that isn't the one stored in the DB */
      When("We check that invalid reference returns expected response body")
      val response = getUploadResultService.getUploadResults(
        GetUploadResultData.getAwaitingUploadClaimId,
        GetUploadResultData.getThisReferenceDoesNotExist,
        authToken
      )

      checkErrorResponse(response)
    }

    Scenario("Request claimID is not found", E2ETest) {
      authToken

      Then("Upload Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getAwaitingUploadClaimId,
        reference = GetUploadResultData.getAwaitingUploadReference
      )

      /** Checking response body by sending in a claimID that isn't the one stored in the DB */
      When("We check that invalid claimID returns expected response body")
      val response = getUploadResultService.getUploadResults(
        GetUploadResultData.getThisClaimIdDoesNotExist,
        GetUploadResultData.getAwaitingUploadReference,
        authToken
      )

      checkErrorResponse(response)
    }

    Scenario("Request claimID and reference not found", E2ETest) {
      authToken

      Then("Upload Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getAwaitingUploadClaimId,
        reference = GetUploadResultData.getAwaitingUploadReference
      )

      /** Checking response body by sending in a claimID and reference that isn't the one stored in the DB */
      When("We check that invalid claimID and reference returns expected response body")
      val response = getUploadResultService.getUploadResults(
        GetUploadResultData.getThisClaimIdDoesNotExist,
        GetUploadResultData.getThisReferenceDoesNotExist,
        authToken
      )

      checkErrorResponse(response)
    }
  }
}
