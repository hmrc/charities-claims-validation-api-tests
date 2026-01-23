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
import uk.gov.hmrc.api.helpers.{SpreadsheetLocationHelper, UploadTestDataHelper}
import uk.gov.hmrc.api.data.{CreateUpscanCallbackData, GetUploadResultData, UpdateUploadStatusData}

class GetUploadResultSpec extends BaseSpec with UploadTestDataHelper {
  Feature("Charities - Get Upload Result API - All successful response bodies") {
    Scenario("Testing Awaiting Upload Response") {
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

    Scenario("Testing Verifying Response") {
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

    // TODO: Investigate, don't think we can have it as VALIDATING anymore
//    Scenario("Testing VALIDATING Response") {
//      authToken
//
//      Then("Upload Validating Test Data")
//      uploadTestData(
//        authToken,
//        claimId = GetUploadResultData.getValidatingClaimId,
//        reference = GetUploadResultData.getValidatingReference
//      )
//
//      /** Checking Validating response body, we need to hit an additional endpoint to change the current "fileStatus" =
//        * "AWAITING_UPLOAD" to become "VALIDATING"
//        */
//      Then("We call the CreateUpscanCallback API to update 'fileStatus' from AWAITING_UPLOAD to VALIDATING")
//      createUpscanService.postSuccessfulPayloadObject(
//        GetUploadResultData.getValidatingClaimId,
//        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
//          GetUploadResultData.getValidatingReference
//        ),
//        authToken
//      )
//
//      Then("We check now that Validating returns expected response body")
//      val response = getUploadResultService.getUploadResults(
//        GetUploadResultData.getValidatingClaimId,
//        GetUploadResultData.getValidatingReference,
//        authToken
//      )
//
//      And("Response code should be 200")
//      checkStatusCode(response, 200)
//
//      And("The response body is what we expect")
//      checkCommonResponseBodies(
//        response,
//        ValidationType.GiftAid,
//        FileStatus.VALIDATING
//      )
//    }

    Scenario("Testing Data Valid Response") {
      authToken

      Then("Upload GiftAid DataValid Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getValidDataClaimIdGiftAid,
        reference = GetUploadResultData.getValidDataReferenceGiftAid,
        validationType = ValidationType.GiftAid
      )

      Then("Upload OtherIncome DataValid Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getValidDataClaimIdOtherIncome,
        reference = GetUploadResultData.getValidDataReferenceOtherIncome,
        validationType = ValidationType.OtherIncome
      )

      Then("Upload ConnectedCharities DataValid Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getValidDataClaimIdConnectedCharities,
        reference = GetUploadResultData.getValidDataReferenceConnectedCharities,
        validationType = ValidationType.ConnectedCharities
      )

      Then("Upload CommunityBuildings DataValid Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getValidDataClaimIdCommunityBuildings,
        reference = GetUploadResultData.getValidDataReferenceCommunityBuildings,
        validationType = ValidationType.CommunityBuildings
      )

      /** We need to hit an additional endpoint to change the current "fileStatus" = "AWAITING_UPLOAD" to become
        * "VALIDATED" Doing this for GiftAid, OtherIncome, CommunityBuildings and ConnectedCharities
        */
      Then("We validate a spreadsheet to update 'fileStatus' from AWAITING_UPLOAD to VALIDATED")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData.getValidDataClaimIdGiftAid,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadResultData.getValidDataReferenceGiftAid,
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.GiftAid),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.GiftAid)
        ),
        authToken
      )

      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData.getValidDataClaimIdOtherIncome,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadResultData.getValidDataReferenceOtherIncome,
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.OtherIncome),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.OtherIncome)
        ),
        authToken
      )

      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData.getValidDataClaimIdCommunityBuildings,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadResultData.getValidDataReferenceCommunityBuildings,
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.CommunityBuildings),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.CommunityBuildings)
        ),
        authToken
      )

      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData.getValidDataClaimIdConnectedCharities,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadResultData.getValidDataReferenceConnectedCharities,
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.ConnectedCharities),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.ConnectedCharities)
        ),
        authToken
      )

      /** Checking data valid - GiftAid, OtherIncome, ConnectedCharities and CommunityBuildings */
      Then("We check now that Data Valid returns expected response body")
      // TODO: UNCOMMENT ONCE GIFTAID IS IMPLEMENTED
//      val giftAidResponse = getUploadResultService.getUploadResults(
//        GetUploadResultData.getValidDataClaimIdGiftAid,
//        GetUploadResultData.getValidDataReferenceGiftAid,
//        authToken
//      )

      val otherIncomeResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getValidDataClaimIdOtherIncome,
        GetUploadResultData.getValidDataReferenceOtherIncome,
        authToken
      )

      val connectedCharitiesResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getValidDataClaimIdConnectedCharities,
        GetUploadResultData.getValidDataReferenceConnectedCharities,
        authToken
      )

      val communityBuildingResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getValidDataClaimIdCommunityBuildings,
        GetUploadResultData.getValidDataReferenceCommunityBuildings,
        authToken
      )

      // TODO:UNCOMMENT ONCE GIFT AID IS IMPLEMENTED
      // checkValidAndInvalidDataResponseBody(giftAidResponse, ValidationType.GiftAid, FileStatus.VALIDATED)
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
        claimId = GetUploadResultData.getInvalidDataClaimIdGiftAid,
        reference = GetUploadResultData.getInvalidDataReferenceGiftAid,
        validationType = ValidationType.GiftAid
      )

      Then("Upload OtherIncome InvalidData Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getInvalidDataClaimIdOtherIncome,
        reference = GetUploadResultData.getInvalidDataReferenceOtherIncome,
        validationType = ValidationType.OtherIncome
      )

      Then("Upload ConnectedCharities InvalidData Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getInvalidDataClaimIdConnectedCharities,
        reference = GetUploadResultData.getInvalidDataReferenceConnectedCharities,
        validationType = ValidationType.ConnectedCharities
      )

      Then("Upload CommunityBuilding InvalidData Test Data")
      uploadTestData(
        authToken,
        claimId = GetUploadResultData.getInvalidDataClaimIdCommunityBuildings,
        reference = GetUploadResultData.getInvalidDataReferenceCommunityBuildings,
        validationType = ValidationType.CommunityBuildings
      )

      /** Now we hit upscan with invalid spreadsheets to turn the fileStatus = "VALIDATION_FAILED" */
      Then("We validate a spreadsheet to update 'fileStatus' from AWAITING_UPLOAD to VALIDATED")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData.getInvalidDataClaimIdGiftAid,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadResultData.getInvalidDataReferenceGiftAid,
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.GiftAid, "BadData"),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.GiftAid, "BadData")
        ),
        authToken
      )

      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData.getInvalidDataClaimIdOtherIncome,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadResultData.getInvalidDataReferenceOtherIncome,
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.OtherIncome, "BadData"),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.OtherIncome, "BadData")
        ),
        authToken
      )

      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData.getInvalidDataClaimIdCommunityBuildings,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadResultData.getInvalidDataReferenceCommunityBuildings,
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.CommunityBuildings, "BadData"),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.CommunityBuildings, "BadData")
        ),
        authToken
      )

      createUpscanService.postSuccessfulPayloadObject(
        GetUploadResultData.getInvalidDataClaimIdConnectedCharities,
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadResultData.getInvalidDataReferenceConnectedCharities,
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.ConnectedCharities, "BadData"),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.ConnectedCharities, "BadData")
        ),
        authToken
      )

      val giftAidResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getInvalidDataClaimIdGiftAid,
        GetUploadResultData.getInvalidDataReferenceGiftAid,
        authToken
      )

      val otherIncomeResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getInvalidDataClaimIdOtherIncome,
        GetUploadResultData.getInvalidDataReferenceOtherIncome,
        authToken
      )

      val connectedCharitiesResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getInvalidDataClaimIdConnectedCharities,
        GetUploadResultData.getInvalidDataReferenceConnectedCharities,
        authToken
      )

      val communityBuildingResponse = getUploadResultService.getUploadResults(
        GetUploadResultData.getInvalidDataClaimIdCommunityBuildings,
        GetUploadResultData.getInvalidDataReferenceCommunityBuildings,
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

    Scenario("Request claimID is not found") {
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

    Scenario("Request claimID and reference not found") {
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
