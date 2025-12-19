package uk.gov.hmrc.api.specs

import play.api.libs.json.Json
import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUpscanCallbackData}

class CreateUpscanCallbackSpec extends BaseSpec {
  Feature("Charities - Create Upscan Callback API - E2E") {
    Scenario("Successful Payload - Upscan gives us a valid success response", E2ETest) {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent a valid POST Request")
      val payload  = MockCreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayload
      val response = createUpscanStub.postSuccessfulPayloadObject(payload, authHelper.bearerToken)

      Then("A 204 status code should be returned")
      response.status shouldBe 204
    }
  }

  Feature("Charities - Create Upscan Callback API - All Test Cases") {
    Scenario("Receive a Request Body that is QUARANTINE") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(0)
      val response = createUpscanStub.postUnsuccessfulPayloadObject(payload, authHelper.bearerToken)

      Then("A 204 status code should be returned")
      response.status shouldBe 204
    }

    Scenario("Receive a Request Body that is REJECTED") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(1)
      val response = createUpscanStub.postUnsuccessfulPayloadObject(payload, authHelper.bearerToken)

      Then("A 204 status code should be returned")
      response.status shouldBe 204
    }

    Scenario("Receive a Request Body that is UNKNOWN") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(2)
      val response = createUpscanStub.postUnsuccessfulPayloadObject(payload, authHelper.bearerToken)

      Then("A 204 status code should be returned")
      response.status shouldBe 204
    }

    Scenario("Send a successful payload with an invalid file type") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getInvalidFileTypeCreateUpscanCallbackPayload
      val response = createUpscanStub.postSuccessfulPayloadObject(payload, authHelper.bearerToken)

      Then("A 400 status code should be returned")
      response.status shouldBe 400
    }

    Scenario("Send a successful payload with a reference that does not exist") {
      Given("There is an Auth Token and it's valid")
      authHelper.bearerToken shouldNot contain("No Auth Token Found")

      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getInvalidReferenceCreateUpscanCallbackPayload
      val response = createUpscanStub.postSuccessfulPayloadObject(payload, authHelper.bearerToken)

      Then("A 404 status code should be returned")
      response.status shouldBe 404
    }
  }
}
