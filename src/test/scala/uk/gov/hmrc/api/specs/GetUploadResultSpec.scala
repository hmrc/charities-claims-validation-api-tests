package uk.gov.hmrc.api.specs

import play.api.libs.json.Json
import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUpscanCallbackData, MockGetUploadResultData, MockUpdateUploadStatusData}

class GetUploadResultSpec extends BaseSpec with UploadTestDataHelper {
  Feature("Charities - Get Upload Result API - All successful response bodies") {
    Scenario("Testing Awaiting Upload Response") {
      authToken

      Then("Upload AwaitingUpload Test Data")
      /** Uploading the data to the DB first */
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getAwaitingUploadClaimId,
        MockGetUploadResultData().getAwaitingUploadReference
      )

      /** Checking AwaitingUpload response body */
      When("We check that AwaitingClaim returns expected response body")
      val awaitingClaimResponse = getUploadResultService.postAPayloadObject(
        MockGetUploadResultData().getAwaitingUploadClaimId,
        MockGetUploadResultData().getAwaitingUploadReference,
        authToken
      )

      And("Response code should be 200")
      awaitingClaimResponse.status shouldBe 200

      And("The response body is what we expect")
      (Json.parse(awaitingClaimResponse.body) \ "reference").as[String] shouldEqual MockGetUploadResultData().getAwaitingUploadReference
      (Json.parse(awaitingClaimResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(awaitingClaimResponse.body) \ "fileStatus").as[String] shouldEqual "AWAITING_UPLOAD"
      (Json.parse(awaitingClaimResponse.body) \ "uploadUrl").asOpt[String] shouldBe defined
    }

    Scenario("Testing Verifying Response") {
      authToken

      /** We have a valid auth token so now upload the test data to the DB */
      Then("Upload Verifying Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getVerifyingClaimId,
        MockGetUploadResultData().getVerifyingReference
      )

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
        .as[String] shouldEqual MockGetUploadResultData().getVerifyingReference
      (Json.parse(verifyingClaimResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(verifyingClaimResponse.body) \ "fileStatus").as[String] shouldEqual "VERIFYING"
    }

    Scenario("Testing VERIFICATION_FAILED response body") {
      authToken

      /** We have the auth token so upload the test data for all types of VERIFICATION_FAILED
       * - QUARANTINE
       * - REJECTED
       * - UNKNOWN*/
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
        .as[String] shouldEqual MockGetUploadResultData().getQuarantineReference
      (Json.parse(quarantineResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(quarantineResponse.body) \ "fileStatus").as[String] shouldEqual "VERIFICATION_FAILED"
      (Json.parse(quarantineResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "QUARANTINE"
      (Json.parse(quarantineResponse.body) \ "failureDetails" \ "message").asOpt[String] shouldBe defined

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
        .as[String] shouldEqual MockGetUploadResultData().getRejectedReference
      (Json.parse(rejectedResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(rejectedResponse.body) \ "fileStatus").as[String] shouldEqual "VERIFICATION_FAILED"
      (Json.parse(rejectedResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "REJECTED"
      (Json.parse(rejectedResponse.body) \ "failureDetails" \ "message").asOpt[String] shouldBe defined

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
        .as[String] shouldEqual MockGetUploadResultData().getUnknownReference
      (Json.parse(unknownResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(unknownResponse.body) \ "fileStatus").as[String] shouldEqual "VERIFICATION_FAILED"
      (Json.parse(unknownResponse.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual "UNKNOWN"
      (Json.parse(unknownResponse.body) \ "failureDetails" \ "message").asOpt[String] shouldBe defined
    }

    Scenario("Testing VALIDATING Response") {
      authToken

      Then("Upload Validating Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getValidatingClaimId,
        MockGetUploadResultData().getValidatingReference
      )

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
        .as[String] shouldEqual MockGetUploadResultData().getValidatingReference
      (Json.parse(validatingClaimResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(validatingClaimResponse.body) \ "fileStatus").as[String] shouldEqual "VALIDATING"
    }

    Scenario("Testing Data Valid Response") {
      authToken

      Then("Upload DataValid Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getDataValidClaimId,
        MockGetUploadResultData().getDataValidReference
      )

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
        .as[String] shouldEqual MockGetUploadResultData().getDataValidReference
      (Json.parse(dataValidResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(dataValidResponse.body) \ "fileStatus").as[String] shouldEqual "VALIDATED"
      (Json.parse(dataValidResponse.body) \ "giftAidScheduleData").asOpt[String] shouldBe defined
    }

    Scenario("Testing Invalid Data Response") {
      authToken

      Then("Upload InvalidData Test Data")
      uploadTestDataCustomIdAndReferenceNoReturn(
        authToken,
        MockGetUploadResultData().getInvalidDataClaimId,
        MockGetUploadResultData().getInvalidDataReference
      )

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
        .as[String] shouldEqual MockGetUploadResultData().getInvalidDataReference
      (Json.parse(invalidDataResponse.body) \ "validationType").asOpt[String] shouldBe defined
      (Json.parse(invalidDataResponse.body) \ "fileStatus").as[String] shouldEqual "VALIDATION_FAILED"
      (Json.parse(invalidDataResponse.body) \ "giftAidScheduleData").asOpt[String] shouldBe defined
      (Json.parse(invalidDataResponse.body) \ "errors").asOpt[String] shouldBe defined
    }
  }

  Feature("Charities - Get Upload Result API - Failed Response Bodies") {
    Scenario("Getting all successful response bodies") {

    }
  }
}