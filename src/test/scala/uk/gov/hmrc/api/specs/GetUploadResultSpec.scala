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
import uk.gov.hmrc.api.helpers.{FailureReason, FileStatus, UploadTestDataHelper, ValidationType}
import uk.gov.hmrc.api.data.{CreateUpscanCallbackData, GetUploadResultData, UpdateUploadStatusData}

class GetUploadResultSpec extends BaseSpec with UploadTestDataHelper {
  Feature("Charities - Get Upload Result API - All successful response bodies") {
    Scenario("Testing Awaiting Upload Response") {
      authToken

      Then("Upload AwaitingUpload Test Data")

      /** Uploading the data to the DB first */
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getAwaitingUploadClaimId,
        reference = GetUploadResultData().getAwaitingUploadReference
      )

      /** Checking AwaitingUpload response body */
      When("We check that AwaitingClaim returns expected response body")
      val response = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getAwaitingUploadClaimId,
        GetUploadResultData().getAwaitingUploadReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(response, 200)

      checkCommonResponseBodies(
        response,
        GetUploadResultData().getAwaitingUploadReference,
        ValidationType.GiftAid,
        FileStatus.AWAITING_UPLOAD
      )
    }

    Scenario("Testing Verifying Response") {
      authToken

      /** We have a valid auth token so now upload the test data to the DB */
      Then("Upload Verifying Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getVerifyingClaimId,
        reference = GetUploadResultData().getVerifyingReference
      )

      /** Checking Verifying response body, we need to hit an additional endpoint to change the current "fileStatus" =
        * "AWAITING_UPLOAD" to become "VERIFYING"
        */
      Then("We call the CreateUpdateUpload API to update 'fileStatus' from AWAITING_UPLOAD to VERIFYING")
      updateUploadStatusService.postAPayloadObject(
        GetUploadResultData().getVerifyingClaimId,
        GetUploadResultData().getVerifyingReference,
        UpdateUploadStatusData.getSuccessfulPayload,
        authToken
      )

      Then("We check now that Verifying returns expected response body")
      val response = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getVerifyingClaimId,
        GetUploadResultData().getVerifyingReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(response, 200)

      checkCommonResponseBodies(
        response,
        GetUploadResultData().getVerifyingReference,
        ValidationType.GiftAid,
        FileStatus.VERIFYING
      )
    }

    Scenario("Testing VERIFICATION_FAILED response body") {
      authToken

      /** We have the auth token so upload the test data for all types of VERIFICATION_FAILED
        *   - QUARANTINE
        *   - REJECTED
        *   - UNKNOWN
        */
      Then("Upload Quarantine Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getQuarantineClaimId,
        reference = GetUploadResultData().getQuarantineReference
      )

      Then("Upload Rejected Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getRejectedClaimId,
        reference = GetUploadResultData().getRejectedReference
      )

      Then("Upload Unknown Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getUnknownClaimId,
        reference = GetUploadResultData().getUnknownReference
      )

      /** Checking the "fileStatus" = "VERIFICATION_FAILED" which includes
        *   - QUARANTINE
        *   - REJECTED
        *   - UNKNOWN Again need to hit additional endpoints using CreateUpscanCallback to update these details
        */
      Then("We update three payloads to contain each unique version of VERIFICATION_FAILED")
      createUpscanService.postUnsuccessfulPayloadObject(
        GetUploadResultData().getQuarantineClaimId,
        CreateUpscanCallbackData.getQuarantineUpscanCallbackPayload(
          GetUploadResultData().getQuarantineReference
        ),
        authToken
      ) // QUARANTINE

      createUpscanService.postUnsuccessfulPayloadObject(
        GetUploadResultData().getRejectedClaimId,
        CreateUpscanCallbackData.getRejectedUpscanCallbackPayload(GetUploadResultData().getRejectedReference),
        authToken
      ) // REJECTED

      createUpscanService.postUnsuccessfulPayloadObject(
        GetUploadResultData().getUnknownClaimId,
        CreateUpscanCallbackData.getUnknownUpscanCallbackPayload(GetUploadResultData().getUnknownReference),
        authToken
      ) // UNKNOWN

      /** Now calling GetUploadResult for all three claims to check the response body */
      Then("We call GetUploadResult to check QUARANTINE")
      val quarantineResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getQuarantineClaimId,
        GetUploadResultData().getQuarantineReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(quarantineResponse, 200)

      And("The response body is what we expect")
      checkCommonResponseBodies(
        quarantineResponse,
        GetUploadResultData().getQuarantineReference,
        ValidationType.GiftAid,
        FileStatus.VERIFICATION_FAILED,
        failureReason = FailureReason.QUARANTINE
      )

      Then("We call GetUploadResult to check REJECTED")
      val rejectedResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getRejectedClaimId,
        GetUploadResultData().getRejectedReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(rejectedResponse, 200)

      And("The response body is what we expect")
      checkCommonResponseBodies(
        rejectedResponse,
        GetUploadResultData().getRejectedReference,
        ValidationType.GiftAid,
        FileStatus.VERIFICATION_FAILED,
        failureReason = FailureReason.REJECTED
      )

      Then("We call GetUploadResult to check UNKNOWN")
      val unknownResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getUnknownClaimId,
        GetUploadResultData().getUnknownReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(unknownResponse, 200)

      And("The response body is what we expect")
      checkCommonResponseBodies(
        unknownResponse,
        GetUploadResultData().getUnknownReference,
        ValidationType.GiftAid,
        FileStatus.VERIFICATION_FAILED,
        failureReason = FailureReason.UNKNOWN
      )
    }

    Scenario("Testing VALIDATING Response") {
      authToken

      Then("Upload Validating Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getValidatingClaimId,
        reference = GetUploadResultData().getValidatingReference
      )

      /** Checking Validating response body, we need to hit an additional endpoint to change the current "fileStatus" =
        * "AWAITING_UPLOAD" to become "VALIDATING"
        */
      Then("We call the CreateUpscanCallback API to update 'fileStatus' from AWAITING_UPLOAD to VALIDATING")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData().getValidatingClaimId,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          GetUploadResultData().getValidatingReference
        ),
        authToken
      )

      Then("We check now that Validating returns expected response body")
      val response = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getValidatingClaimId,
        GetUploadResultData().getValidatingReference,
        authToken
      )

      And("Response code should be 200")
      checkStatusCode(response, 200)

      And("The response body is what we expect")
      checkCommonResponseBodies(
        response,
        GetUploadResultData().getValidatingReference,
        ValidationType.GiftAid,
        FileStatus.VALIDATING
      )
    }

    Scenario("Testing Data Valid Response") {
      authToken

      Then("Upload GiftAid DataValid Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getValidDataClaimIdGiftAid,
        reference = GetUploadResultData().getValidDataReferenceGiftAid
      )

      Then("Upload OtherIncome DataValid Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getValidDataClaimIdOtherIncome,
        reference = GetUploadResultData().getValidDataReferenceOtherIncome
      )

      Then("Upload ConnectedCharities DataValid Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getValidDataClaimIdConnectedCharities,
        reference = GetUploadResultData().getValidDataReferenceConnectedCharities
      )

      Then("Upload CommunityBuildings DataValid Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getValidDataClaimIdCommunityBuildings,
        reference = GetUploadResultData().getValidDataReferenceCommunityBuildings
      )

      /** We need to hit an additional endpoint to change the current "fileStatus" = "AWAITING_UPLOAD" to become
        * "VALIDATED" TODO: For this we need to have the file validated (can't do this right now) Blocked by DTR-2169
        */
      Then("We validate a spreadsheet to update 'fileStatus' from AWAITING_UPLOAD to VALIDATED")

      /** Checking data valid - GiftAid, OtherIncome, ConnectedCharities and CommunityBuildings */
      Then("We check now that Data Valid returns expected response body")
      val giftAidResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getValidDataClaimIdGiftAid,
        GetUploadResultData().getValidDataReferenceGiftAid,
        authToken
      )

      val otherIncomeResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getValidDataClaimIdOtherIncome,
        GetUploadResultData().getValidDataReferenceOtherIncome,
        authToken
      )

      val connectedCharitiesResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getValidDataClaimIdConnectedCharities,
        GetUploadResultData().getValidDataReferenceConnectedCharities,
        authToken
      )

      val communityBuildingResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getValidDataClaimIdCommunityBuildings,
        GetUploadResultData().getValidDataReferenceCommunityBuildings,
        authToken
      )

      checkValidAndInvalidDataResponseBody(giftAidResponse, ValidationType.GiftAid, FileStatus.VALIDATED)
      checkValidAndInvalidDataResponseBody(otherIncomeResponse, ValidationType.OtherIncome, FileStatus.VALIDATED)
      checkValidAndInvalidDataResponseBody(
        connectedCharitiesResponse,
        ValidationType.ConnectedCharities,
        FileStatus.VALIDATED
      )
      checkValidAndInvalidDataResponseBody(
        communityBuildingResponse,
        ValidationType.CommunityBuildings,
        FileStatus.VALIDATED
      )
    }

    Scenario("Testing Invalid Data Response") {
      authToken

      Then("Upload GiftAid InvalidData Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getInvalidDataClaimIdGiftAid,
        reference = GetUploadResultData().getInvalidDataReferenceGiftAid
      )

      Then("Upload OtherIncome InvalidData Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getInvalidDataClaimIdOtherIncome,
        reference = GetUploadResultData().getInvalidDataReferenceOtherIncome
      )

      Then("Upload ConnectedCharities InvalidData Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getInvalidDataClaimIdConnectedCharities,
        reference = GetUploadResultData().getInvalidDataReferenceConnectedCharities
      )

      Then("Upload CommunityBuilding InvalidData Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getInvalidDataClaimIdCommunityBuildings,
        reference = GetUploadResultData().getInvalidDataReferenceCommunityBuildings
      )

      // Again TODO: we can't update to VERIFICATION_FAILED just yet --- Blocked by DTR-2169

      val giftAidResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getInvalidDataClaimIdGiftAid,
        GetUploadResultData().getInvalidDataReferenceGiftAid,
        authToken
      )

      val otherIncomeResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getInvalidDataClaimIdOtherIncome,
        GetUploadResultData().getInvalidDataReferenceOtherIncome,
        authToken
      )

      val connectedCharitiesResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getInvalidDataClaimIdConnectedCharities,
        GetUploadResultData().getInvalidDataReferenceConnectedCharities,
        authToken
      )

      val communityBuildingResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getInvalidDataClaimIdCommunityBuildings,
        GetUploadResultData().getInvalidDataReferenceCommunityBuildings,
        authToken
      )

      checkValidAndInvalidDataResponseBody(giftAidResponse, ValidationType.GiftAid, FileStatus.VALIDATION_FAILED)
      checkValidAndInvalidDataResponseBody(
        otherIncomeResponse,
        ValidationType.OtherIncome,
        FileStatus.VALIDATION_FAILED
      )
      checkValidAndInvalidDataResponseBody(
        connectedCharitiesResponse,
        ValidationType.ConnectedCharities,
        FileStatus.VALIDATION_FAILED
      )
      checkValidAndInvalidDataResponseBody(
        communityBuildingResponse,
        ValidationType.CommunityBuildings,
        FileStatus.VALIDATION_FAILED
      )
    }
  }

  Feature("Charities - Get Upload Result API - Failed Response Bodies") {
    Scenario("Request reference for given claimID is not found") {
      authToken

      Then("Upload Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getAwaitingUploadClaimId,
        reference = GetUploadResultData().getAwaitingUploadReference
      )

      /** Checking response body by sending in a reference that isn't the one stored in the DB */
      When("We check that invalid reference returns expected response body")
      val response = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getAwaitingUploadClaimId,
        GetUploadResultData().getThisReferenceDoesNotExist,
        authToken
      )

      checkErrorResponse(response)
    }

    Scenario("Request claimID is not found") {
      authToken

      Then("Upload Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getAwaitingUploadClaimId,
        reference = GetUploadResultData().getAwaitingUploadReference
      )

      /** Checking response body by sending in a claimID that isn't the one stored in the DB */
      When("We check that invalid claimID returns expected response body")
      val response = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getThisClaimIdDoesNotExist,
        GetUploadResultData().getAwaitingUploadReference,
        authToken
      )

      checkErrorResponse(response)
    }

    Scenario("Request claimID and reference not found") {
      authToken

      Then("Upload Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getAwaitingUploadClaimId,
        reference = GetUploadResultData().getAwaitingUploadReference
      )

      /** Checking response body by sending in a claimID and reference that isn't the one stored in the DB */
      When("We check that invalid claimID and reference returns expected response body")
      val response = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getThisClaimIdDoesNotExist,
        GetUploadResultData().getThisReferenceDoesNotExist,
        authToken
      )

      checkErrorResponse(response)
    }

    Scenario("Claim has expired") {
      authToken

      Then("Upload Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData().getAwaitingUploadHasExpiredClaimId,
        reference = GetUploadResultData().getAwaitingUploadHasExpiredClaimId
      )

      When("We check a claim that has passed its 7 days expiry")
      val response = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getAwaitingUploadHasExpiredClaimId,
        GetUploadResultData().getAwaitingUploadHasExpiredClaimId,
        authToken
      )

      And("The response body and status code is what we expect")
      checkErrorResponse(response, 400)
    }
  }
}
