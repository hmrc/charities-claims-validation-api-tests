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

import play.api.libs.json.Json
import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.utils.BaseSpec

// Happy Paths

class DeleteSingleUploadSpec extends BaseSpec with UploadTestDataHelper {

  Feature("Delete Single Upload API") {

    Scenario("Delete one upload from a multi-upload claim", E2ETest) {
      Given("There is a valid AUTH Token")
      val token = authToken

      When("A valid claimId and ref")
      val claimId = "claim-456"
      val ref     = seedUploadTestData(claimId, authToken)

      When("I send DELETE request to the Endpoint")
      val response =
        deleteSingleUploadService.deleteSingleUpload(claimId = claimId, ref = ref, authorizationHeaderValue = authToken)

      Then("A 200 status code should be returned")
      response.status shouldBe 200

      And("The response body is { success: true }")
      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
    }

    Scenario("Delete the ONLY upload associated to a claim", E2ETest) {
      Given("A valid existing claimId and ref in MongoDB")
      val token = authToken

      val claimId = "claim-457"
      val ref     = seedUploadTestData(claimId, authToken)

      When("I delete the reference for the first time")
      val firstDeleteResponse =
        deleteSingleUploadService.deleteSingleUpload(claimId = claimId, ref = ref, authorizationHeaderValue = authToken)

      Then("A 200 status code should be returned")
      firstDeleteResponse.status shouldBe 200

      And("The response body is { success: true }")
      (Json.parse(firstDeleteResponse.body) \ "success").as[Boolean] shouldBe true

//      2nd DELETE Action
      When("I delete the same reference again")
      val secondDeleteResponse =
        deleteSingleUploadService.deleteSingleUpload(claimId = claimId, ref = ref, authorizationHeaderValue = authToken)

      Then("A 404 status code should be returned")
      secondDeleteResponse.status shouldBe 404

      And("The response body should contain CLAIM_REFERENCE_DOES_NOT_EXIST")
      val json = Json.parse(secondDeleteResponse.body)
      (json \ "error").as[String] shouldBe "CLAIM_REFERENCE_DOES_NOT_EXIST"
    }

    Scenario("Delete non-existent ref, but claimId is existent", E2ETest) {
      val token = authToken

      Given("A valid claimId exists, but reference does not")
      val claimId        = "claim-890"
      val existingRef    = seedUploadTestData(claimId, authToken)
      val nonExistentRef = java.util.UUID.randomUUID().toString
      nonExistentRef should not be existingRef

      When("I delete a reference that does not exist")
      val response = deleteSingleUploadService.deleteSingleUpload(claimId, nonExistentRef, authToken)

      Then("A 404 status code should be returned")
      response.status shouldBe 404

      And("The response body should contain CLAIM_REFERENCE_DOES_NOT_EXIST")
      val json = Json.parse(response.body)
      (json \ "error").as[String] shouldBe "CLAIM_REFERENCE_DOES_NOT_EXIST"
    }

  }

//  Feature("Charities - Create Delete Upload API - Multi Delete") {
//    Scenario("Successfully delete a charity claim") {
//      When("The DeleteSingleUpload(s) Endpoint is sent a valid DELETE Request")
//      val response = deleteSingleUploadService.deleteManyRequest("claim-123")
//
//      Then("A 204 status code should be returned")
//      response.status shouldBe 204
//
//      And("The response body is { success: true }")
//      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
//    }
//  }
}
