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
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.helpers.{FileStatus, UploadTestDataHelper, ValidationType}
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
      response.status shouldBe 200

      // TODO: Ref, val, file return in all response bodies, could make DRY?
      And("The response body is what we expect")
      (Json.parse(response.body) \ "reference")
        .as[String]                                                shouldEqual GetUploadResultData().getAwaitingUploadReference
      (Json.parse(response.body) \ "validationType").asOpt[String]    shouldBe defined
      (Json.parse(response.body) \ "fileStatus").as[String]        shouldEqual "AWAITING_UPLOAD"
      (Json.parse(response.body) \ "initiateTimestamp").asOpt[String] shouldBe defined
      (Json.parse(response.body) \ "uploadUrl").asOpt[String]         shouldBe defined
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
      response.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(response.body) \ "reference")
        .as[String]                                             shouldEqual GetUploadResultData().getVerifyingReference
      (Json.parse(response.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(response.body) \ "fileStatus").as[String]     shouldEqual "VERIFYING"
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
      quarantineResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(quarantineResponse.body) \ "reference")
        .as[String]                                                                         shouldEqual GetUploadResultData().getQuarantineReference
      (Json.parse(quarantineResponse.body) \ "validationType").asOpt[String]                   shouldBe defined
      (Json.parse(quarantineResponse.body) \ "fileStatus").as[String]                       shouldEqual "VERIFICATION_FAILED"
      (Json.parse(quarantineResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "QUARANTINE"
      (Json.parse(quarantineResponse.body) \ "failureDetails" \ "message").asOpt[String]       shouldBe defined

      Then("We call GetUploadResult to check REJECTED")
      val rejectedResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getRejectedClaimId,
        GetUploadResultData().getRejectedReference,
        authToken
      )

      And("Response code should be 200")
      rejectedResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(rejectedResponse.body) \ "reference")
        .as[String]                                                                       shouldEqual GetUploadResultData().getRejectedReference
      (Json.parse(rejectedResponse.body) \ "validationType").asOpt[String]                   shouldBe defined
      (Json.parse(rejectedResponse.body) \ "fileStatus").as[String]                       shouldEqual "VERIFICATION_FAILED"
      (Json.parse(rejectedResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "REJECTED"
      (Json.parse(rejectedResponse.body) \ "failureDetails" \ "message").asOpt[String]       shouldBe defined

      Then("We call GetUploadResult to check UNKNOWN")
      val unknownResponse = getUploadResultService.postAPayloadObject(
        GetUploadResultData().getUnknownClaimId,
        GetUploadResultData().getUnknownReference,
        authToken
      )

      And("Response code should be 200")
      unknownResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(unknownResponse.body) \ "reference")
        .as[String]                                                                      shouldEqual GetUploadResultData().getUnknownReference
      (Json.parse(unknownResponse.body) \ "validationType").asOpt[String]                   shouldBe defined
      (Json.parse(unknownResponse.body) \ "fileStatus").as[String]                       shouldEqual "VERIFICATION_FAILED"
      (Json.parse(unknownResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "UNKNOWN"
      (Json.parse(unknownResponse.body) \ "failureDetails" \ "message").asOpt[String]       shouldBe defined
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
      response.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(response.body) \ "reference")
        .as[String]                                             shouldEqual GetUploadResultData().getValidatingReference
      (Json.parse(response.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(response.body) \ "fileStatus").as[String]     shouldEqual "VALIDATING"
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

      checkDataResponse(giftAidResponse, ValidationType.GiftAid, FileStatus.VALIDATED)
      checkDataResponse(otherIncomeResponse, ValidationType.OtherIncome, FileStatus.VALIDATED)
      checkDataResponse(connectedCharitiesResponse, ValidationType.ConnectedCharities, FileStatus.VALIDATED)
      checkDataResponse(communityBuildingResponse, ValidationType.CommunityBuildings, FileStatus.VALIDATED)
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

      checkDataResponse(giftAidResponse, ValidationType.GiftAid, FileStatus.VALIDATION_FAILED)
      checkDataResponse(otherIncomeResponse, ValidationType.OtherIncome, FileStatus.VALIDATION_FAILED)
      checkDataResponse(connectedCharitiesResponse, ValidationType.ConnectedCharities, FileStatus.VALIDATION_FAILED)
      checkDataResponse(communityBuildingResponse, ValidationType.CommunityBuildings, FileStatus.VALIDATION_FAILED)
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

      // TODO: Could also break this down into error response method
      And("Response code should be 400")
      response.status shouldBe 400

      And("The response body is what we expect ")
      (Json.parse(response.body) \ "error").as[String]   shouldEqual "CLAIM_REFERENCE_HAS_EXPIRED"
      (Json.parse(response.body) \ "message").asOpt[String] shouldBe defined
    }
  }

  /** Valid and Invalid Data response both share a chunk of common responses, populating here to keep code DRY and
    * declutter the scenario(s)
    */
  def checkDataResponse(
    response: StandaloneWSResponse,
    validationType: ValidationType,
    fileStatus: FileStatus
  ): Unit = {
    val reference  = GetUploadResultData().getCorrectReference(validationType, fileStatus)
    val typeOfData = GetUploadResultData().getCorrectJsonBodyFieldName(validationType)

    Then(s"The response for $validationType and data is $fileStatus is what we expect")
    (Json.parse(response.body) \ "reference").as[String]      shouldEqual reference
    (Json.parse(response.body) \ "validationType").as[String] shouldEqual validationType.toString
    (Json.parse(response.body) \ "fileStatus")
      .as[String]                                                  should (be(FileStatus.VALIDATED) or be(FileStatus.VALIDATION_FAILED))
    (Json.parse(response.body) \ typeOfData).asOpt[String]       shouldBe defined

    if (fileStatus == FileStatus.VALIDATION_FAILED) {
      And("We have invalid data so an additional check for errors")
      (Json.parse(response.body) \ "errors").asOpt[String] shouldBe defined
    }

    And("Response code should be 200")
    response.status shouldBe 200
  }

  /** Invalid claimID, invalid reference and invalid claimID + reference all have the same response body breaking that
    * up and putting into a method to keep code DRY
    */
  def checkErrorResponse(response: StandaloneWSResponse): Unit = {
    And("Response code should be 404")
    response.status shouldBe 404

    And("The response body is what we expect ")
    (Json.parse(response.body) \ "error").as[String]   shouldEqual "CLAIM_REFERENCE_DOES_NOT_EXIST"
    (Json.parse(response.body) \ "message").asOpt[String] shouldBe defined
  }
}
