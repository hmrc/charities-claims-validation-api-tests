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
      val payload = MockUpdateUploadStatusData.getSuccessfulPayload
      val response = updateUploadStatusService.postAPayloadObject(payload, authHelper.bearerToken)

      Then("A 200 status code should be returned")
      response.status shouldBe 200

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }
  }
}
