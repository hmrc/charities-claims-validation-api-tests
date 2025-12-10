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

import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUpscanCallbackData}

class CreateUpscanCallbackSpec extends BaseSpec {
  Feature("Charities - Create Upscan Callback API") {
    Scenario("Receive a Request Body that is SUCCESSFUL") {
      When("The CreateUpscanCallback Endpoint is sent a valid POST Request")
      val payload  = MockCreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayload
      val response = createUpscanStub.postSuccessfulPayloadObject(payload)

      Then("A 200 status code should be returned")
      response.status shouldBe 200
    }

    Feature("Charities - Create Upscan Callback API - FAILURE Responses") {
      Scenario("Receive a Request Body that is QUARANTINE") {
        When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
        val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(0)
        val response = createUpscanStub.postUnsuccessfulPayloadObject(payload)

        Then("A 400 status code should be returned")
        response.status shouldBe 400
      }
    }

    Scenario("Receive a Request Body that is REJECTED") {
      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(1)
      val response = createUpscanStub.postUnsuccessfulPayloadObject(payload)

      Then("A 400 status code should be returned")
      response.status shouldBe 400
    }

    Scenario("Receive a Request Body that is UNKNOWN") {
      When("The CreateUpscanCallback Endpoint is sent an invalid POST Request")
      val payload  = MockCreateUpscanCallbackData.getFailedCreateUpscanCallbackPayload(2)
      val response = createUpscanStub.postUnsuccessfulPayloadObject(payload)

      Then("A 400 status code should be returned")
      response.status shouldBe 400
    }
  }
}
