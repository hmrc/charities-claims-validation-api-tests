package uk.gov.hmrc.api.specs

import play.api.libs.json.Json
import uk.gov.hmrc.api.utils.BaseSpec

// TODO: Negative paths etc.,
class CreateDeleteUploadSpec extends BaseSpec {
  Feature("Charities - Create Delete Upload API - Single Delete") {
    Scenario("Successful delete a charity claim") {
      When("The CreateDeleteUpload Endpoint is sent a valid DELETE Request")
      val response = createDeleteUploadStub.deleteSingleRequest("claim-123", "ref-456")

      Then("A 204 status code should be returned")
      response.status shouldBe 204

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }
  }

  Feature("Charities - Create Delete Upload API - Multi Delete") {
    Scenario("Successfully delete a charity claim") {
      When("The CreateDeleteUpload(s) Endpoint is sent a valid DELETE Request")
      val response = createDeleteUploadStub.deleteManyRequest("claim-123")

      Then("A 204 status code should be returned")
      response.status shouldBe 204

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }
  }
}