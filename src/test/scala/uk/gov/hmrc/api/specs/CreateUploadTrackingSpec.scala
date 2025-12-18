/*
 * Copyright 2025 HM Revenue & Customs
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

import org.scalactic.Prettifier.default
import play.api.libs.json.Json
import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUploadTrackingData}

class CreateUploadTrackingSpec extends BaseSpec {
  Feature("Charities - Create Upload Tracking API") {
    Scenario("Successful Payload - User wants to upload a spreadsheet for charity claim(s)") {
      Given("There is an AUTH Token")
      val authToken: String = authHelper.bearerToken

      When("The CreateUploadTracking Endpoint is sent a valid POST Request")
      val payload  = MockCreateUploadTrackingData.getSuccessfulCreateUploadTrackingPayload
      val response = createUploadTrackingStub.postAPayloadObject(payload, authToken)

      Then("A 201 status code should be returned")
      response.status shouldBe 201

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }

    Scenario("Invalid Payload - User wants to upload a spreadsheet for charity claim(s)") {
      Given("There is an AUTH Token")
      val authToken: String = authHelper.bearerToken

      When("The CreateUploadTracking Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUploadTrackingData.getInvalidValidationCreateUploadTrackingPayload
      val response = createUploadTrackingStub.postAPayloadObject(payload, authToken)

      Then("A 400 as 'validationType' is incorrect status code should be returned")
      response.status shouldBe 400

      And("The response body is { success: false }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe false
    }

    Scenario("Incomplete Payload - User wants to upload a spreadsheet for charity claim(s)") {
      Given("There is an AUTH Token")
      val authToken: String = authHelper.bearerToken

      When("The CreateUploadTracking Endpoint is sent an incomplete POST Request")
      val response = createUploadTrackingStub.postInvalidJSON(authToken)

      Then("A 400 status code should be returned due to missing required information")
      response.status shouldBe 400

      And("The response body is { success: false }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe false
    }
  }
}
