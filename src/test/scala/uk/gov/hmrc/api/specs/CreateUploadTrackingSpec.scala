package uk.gov.hmrc.api.specs

import play.api.libs.json.Json
import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUploadTrackingData}

class CreateUploadTrackingSpec extends BaseSpec {
  Feature("Charities - Create Upload Tracking API") {
    Scenario("Successful Payload - User wants to upload a spreadsheet for charity claim(s)") {
      When("The CreateUploadTracking Endpoint is sent a valid POST Request")
      val payload = MockCreateUploadTrackingData.getSuccessfulCreateUploadTrackingPayload
      val response = createUploadTrackingStub.postAPayloadObject(payload)

      Then("A 200 status code should be returned")
      response.status shouldBe 200

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }

    Scenario("Invalid Payload - User wants to upload a spreadsheet for charity claim(s)") {
      When("The CreateUploadTracking Endpoint is sent an invalid POST Request")
      val payload = MockCreateUploadTrackingData.getInvalidValidationCreateUploadTrackingPayload
      val response = createUploadTrackingStub.postAPayloadObject(payload)

      Then("A 422 as 'validationType' is incorrect status code should be returned")
      response.status shouldBe 422

      And("The response body is { success: false }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe false
    }

    Scenario("Incomplete Payload - User wants to upload a spreadsheet for charity claim(s)") {
      When("The CreateUploadTracking Endpoint is sent an incomplete POST Request")
      val response = createUploadTrackingStub.postInvalidJSON

      Then("A 400 status code should be returned due to missing required information")
      response.status shouldBe 400

      And("The response body is { success: false }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe false
    }
  }
}