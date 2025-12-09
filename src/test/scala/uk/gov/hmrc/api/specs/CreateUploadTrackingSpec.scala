package uk.gov.hmrc.api.specs

import play.api.libs.json.Json
import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUploadTrackingData}

class CreateUploadTrackingSpec extends BaseSpec {
  Feature("Charities - Create Upload Tracking API") {
    Scenario("Successful Path - User wants to upload a spreadsheet for charity claim(s)") {
      When("The CreateUploadTracking Endpoint is sent a valid POST Request")
      val payload = MockCreateUploadTrackingData.getSuccessfulCreateUploadTrackingPayload
      val response = createUploadTrackingStub.post(payload)

      Then("A 200 status code should be returned")
      response.status shouldBe 200

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }

    Scenario("Failed Path - User wants to upload a spreadsheet for charity claim(s)") {
      When("The CreateUploadTracking Endpoint is sent an invalid POST Request")
      val payload = MockCreateUploadTrackingData.getInvalidValidationCreateUploadTrackingPayload
      val response = createUploadTrackingStub.post(payload)

      Then("A 403 status code should be returned")
      response.status shouldBe 403

      And("The response body is { success: false }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe false
    }
  }
}