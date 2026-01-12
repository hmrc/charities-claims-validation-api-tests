package uk.gov.hmrc.api.specs

import play.api.libs.json.Json
import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUpscanCallbackData, MockGetUploadResultData, MockUpdateUploadStatusData}

class GetUploadResultSpec extends BaseSpec with UploadTestDataHelper {
  // TODO: This doesn't need to be E2E
  // TODO: Can also break this down into individual scenarios for each response
  Feature("Charities - Get Upload Result API - E2E") {
    Scenario("Getting all successful response bodies", E2ETest) {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      /** Need to upload a bunch of test data first for all successful responses so we can actually call GET on them
        */
      Then("Upload AwaitingUpload Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getAwaitingUploadClaimId,
        MockGetUploadResultData().getAwaitingUploadReference
      )

      Then("Upload Verifying Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getVerifyingClaimId,
        MockGetUploadResultData().getVerifyingReference
      )

      Then("Upload Quarantine Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getQuarantineClaimId,
        MockGetUploadResultData().getQuarantineReference
      )

      Then("Upload Rejected Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getRejectedClaimId,
        MockGetUploadResultData().getRejectedReference
      )

      Then("Upload Unknown Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getUnknownClaimId,
        MockGetUploadResultData().getUnknownReference
      )

      Then("Upload Validating Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getValidatingClaimId,
        MockGetUploadResultData().getValidatingReference
      )

      Then("Upload DataValid Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getDataValidClaimId,
        MockGetUploadResultData().getDataValidReference
      )

      Then("Upload InvalidData Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getInvalidDataClaimId,
        MockGetUploadResultData().getInvalidDataReference
      )

      /** All test data is now stored in DB so we can now perform the necessary checks */
      /** Checking AwaitingUpload response body */
      When("We check that AwaitingClaim returns expected response body")
      val awaitingClaimResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getAwaitingUploadClaimId,
        MockGetUploadResultData().getAwaitingUploadReference,
        authToken
      )

      And("Response code should be 200")
      awaitingClaimResponse.status                                              shouldBe 200
      And("The response body is what we expect")
      (Json.parse(awaitingClaimResponse.body) \ "reference")
        .as[String]                                                          shouldEqual MockGetUploadResultData().getAwaitingUploadReference
      (Json.parse(awaitingClaimResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(awaitingClaimResponse.body) \ "fileStatus").as[String]     shouldEqual "AWAITING_UPLOAD"
      (Json.parse(awaitingClaimResponse.body) \ "uploadUrl").asOpt[String]      shouldBe defined

      /** Checking Verifying response body, we need to hit an additional endpoint to change the current "fileStatus" =
        * "AWAITING_UPLOAD" to become "VERIFYING"
        */
      Then("We call the CreateUpdateUpload API to update 'fileStatus' from AWAITING_UPLOAD to VERIFYING")
      updateUploadStatusService.postAPayloadObject(
        MockGetUploadResultData().getVerifyingClaimId,
        MockGetUploadResultData().getVerifyingReference,
        MockUpdateUploadStatusData.getSuccessfulPayload,
        authToken
      )

      Then("We check now that Verifying returns expected response body")
      val verifyingClaimResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getVerifyingClaimId,
        MockGetUploadResultData().getVerifyingReference,
        authToken
      )

      And("Response code should be 200")
      verifyingClaimResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(verifyingClaimResponse.body) \ "reference")
        .as[String]                                                           shouldEqual MockGetUploadResultData().getVerifyingReference
      (Json.parse(verifyingClaimResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(verifyingClaimResponse.body) \ "fileStatus").as[String]     shouldEqual "VERIFYING"

      /** Checking the "fileStatus" = "VERIFICATION_FAILED" which includes
        *   - QUARANTINE
        *   - REJECTED
        *   - UNKNOWN Again need to hit additional endpoints using CreateUpscanCallback to update these details
        */
      Then("We update three payloads to contain each unique version of VERIFICATION_FAILED")
      createUpscanService.postUnsuccessfulPayloadObject(
        MockGetUploadResultData().getQuarantineClaimId,
        MockCreateUpscanCallbackData.getQuarantineUpscanCallbackPayload(
          MockGetUploadResultData().getQuarantineReference
        ),
        authToken
      ) // QUARANTINE

      createUpscanService.postUnsuccessfulPayloadObject(
        MockGetUploadResultData().getRejectedClaimId,
        MockCreateUpscanCallbackData.getRejectedUpscanCallbackPayload(MockGetUploadResultData().getRejectedReference),
        authToken
      ) // REJECTED

      createUpscanService.postUnsuccessfulPayloadObject(
        MockGetUploadResultData().getUnknownClaimId,
        MockCreateUpscanCallbackData.getUnknownUpscanCallbackPayload(MockGetUploadResultData().getUnknownReference),
        authToken
      ) // UNKNOWN

      /** Now calling GetUploadResult for all three claims to check the response body */
      Then("We call GetUploadResult to check QUARANTINE")
      val quarantineResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getQuarantineClaimId,
        MockGetUploadResultData().getQuarantineReference,
        authToken
      )

      And("Response code should be 200")
      quarantineResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(quarantineResponse.body) \ "reference")
        .as[String]                                                                         shouldEqual MockGetUploadResultData().getQuarantineReference
      (Json.parse(quarantineResponse.body) \ "validationType").asOpt[String]                   shouldBe defined
      (Json.parse(quarantineResponse.body) \ "fileStatus").as[String]                       shouldEqual "VERIFICATION_FAILED"
      (Json.parse(quarantineResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "QUARANTINE"
      (Json.parse(quarantineResponse.body) \ "failureDetails" \ "message").asOpt[String]       shouldBe defined

      Then("We call GetUploadResult to check REJECTED")
      val rejectedResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getRejectedClaimId,
        MockGetUploadResultData().getRejectedReference,
        authToken
      )

      And("Response code should be 200")
      rejectedResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(rejectedResponse.body) \ "reference")
        .as[String]                                                                       shouldEqual MockGetUploadResultData().getRejectedReference
      (Json.parse(rejectedResponse.body) \ "validationType").asOpt[String]                   shouldBe defined
      (Json.parse(rejectedResponse.body) \ "fileStatus").as[String]                       shouldEqual "VERIFICATION_FAILED"
      (Json.parse(rejectedResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "REJECTED"
      (Json.parse(rejectedResponse.body) \ "failureDetails" \ "message").asOpt[String]       shouldBe defined

      Then("We call GetUploadResult to check UNKNOWN")
      val unknownResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getUnknownClaimId,
        MockGetUploadResultData().getUnknownReference,
        authToken
      )

      And("Response code should be 200")
      unknownResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(unknownResponse.body) \ "reference")
        .as[String]                                                                      shouldEqual MockGetUploadResultData().getUnknownReference
      (Json.parse(unknownResponse.body) \ "validationType").asOpt[String]                   shouldBe defined
      (Json.parse(unknownResponse.body) \ "fileStatus").as[String]                       shouldEqual "VERIFICATION_FAILED"
      (Json.parse(unknownResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "UNKNOWN"
      (Json.parse(unknownResponse.body) \ "failureDetails" \ "message").asOpt[String]       shouldBe defined

      /** Checking Validating response body, we need to hit an additional endpoint to change the current "fileStatus" =
        * "AWAITING_UPLOAD" to become "VALIDATING"
        */
      Then("We call the CreateUpscanCallback API to update 'fileStatus' from AWAITING_UPLOAD to VALIDATING")
      createUpscanService.postSuccessfulPayloadObject(
        MockGetUploadResultData().getValidatingClaimId,
        MockCreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          MockGetUploadResultData().getValidatingReference
        ),
        authToken
      )

      Then("We check now that Validating returns expected response body")
      val validatingClaimResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getValidatingClaimId,
        MockGetUploadResultData().getValidatingReference,
        authToken
      )

      And("Response code should be 200")
      validatingClaimResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(validatingClaimResponse.body) \ "reference")
        .as[String]                                                            shouldEqual MockGetUploadResultData().getValidatingReference
      (Json.parse(validatingClaimResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(validatingClaimResponse.body) \ "fileStatus").as[String]     shouldEqual "VALIDATING"

      /** Checking data valid - GiftAid TODO: When we add more types of claims we would check all of them and add a
        * getter that gets the type
        */
      Then("We check now that Data Valid returns expected response body")
      val dataValidResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getDataValidClaimId,
        MockGetUploadResultData().getDataValidReference,
        authToken
      )

      And("Response code should be 200")
      dataValidResponse.status shouldBe 200

      // TODO: This could become a method making testing all 4 DRY, could also check a few more fields
      And("The response body is what we expect")
      (Json.parse(dataValidResponse.body) \ "reference")
        .as[String]                                                           shouldEqual MockGetUploadResultData().getDataValidReference
      (Json.parse(dataValidResponse.body) \ "validationType").asOpt[String]      shouldBe defined
      (Json.parse(dataValidResponse.body) \ "fileStatus").as[String]          shouldEqual "VALIDATED"
      (Json.parse(dataValidResponse.body) \ "giftAidScheduleData").asOpt[String] shouldBe defined

      /** Checking invalid data - GiftAid */
      Then("We check now that Invalid Data returns expected response body")
      val invalidDataResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getInvalidDataClaimId,
        MockGetUploadResultData().getInvalidDataReference,
        authToken
      )

      And("Response code should be 200")
      invalidDataResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(invalidDataResponse.body) \ "reference")
        .as[String]                                                             shouldEqual MockGetUploadResultData().getInvalidDataReference
      (Json.parse(invalidDataResponse.body) \ "validationType").asOpt[String]      shouldBe defined
      (Json.parse(invalidDataResponse.body) \ "fileStatus").as[String]          shouldEqual "VALIDATION_FAILED"
      (Json.parse(invalidDataResponse.body) \ "giftAidScheduleData").asOpt[String] shouldBe defined
      (Json.parse(invalidDataResponse.body) \ "errors").asOpt[String]              shouldBe defined
    }
  }
}
