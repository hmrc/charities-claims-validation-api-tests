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

import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.data.DeleteUploadData
import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.specs.tags.E2ETest

// Happy Paths

class DeleteSingleUploadSpec extends BaseSpec with UploadTestDataHelper {

  Feature("Delete Single Upload API") {
    Scenario("Delete one upload from a multi-upload claim", E2ETest) {
      Given("There is a valid AUTH Token")
      authToken

      When("A valid claimId and ref is uploaded")
      uploadTestData(
        authToken,
        DeleteUploadData.getValidClaimId,
        DeleteUploadData.getValidReference
      )

      When("I send DELETE request to the Endpoint")
      val response = deleteSingleUploadService.deleteSingleUpload(
        DeleteUploadData.getValidClaimId,
        DeleteUploadData.getValidReference,
        authToken
      )

      Then("We check the response body and status code are as expected")
      checkGenericResponseBodyAndStatusCode(response, 200, true)
    }

    Scenario("Delete the ONLY upload associated to a claim", E2ETest) {
      Given("A valid existing claimId and ref in MongoDB")
      authToken

      When("A valid claimId and ref is uploaded")
      uploadTestData(
        authToken,
        DeleteUploadData.getValidClaimId,
        DeleteUploadData.getValidReference
      )

      When("I delete the reference for the first time")
      val firstDeleteResponse = deleteSingleUploadService.deleteSingleUpload(
        DeleteUploadData.getValidClaimId,
        DeleteUploadData.getValidReference,
        authToken
      )

      Then("We check the response body and status code are as expected")
      checkGenericResponseBodyAndStatusCode(firstDeleteResponse, 200, true)

      //      2nd DELETE Action
      When("I delete the same reference again")
      val secondDeleteResponse = deleteSingleUploadService.deleteSingleUpload(
        DeleteUploadData.getValidClaimId,
        DeleteUploadData.getValidReference,
        authToken
      )

      Then("A 404 status code should be returned")
      checkErrorResponse(secondDeleteResponse)
    }

    Scenario("Delete non-existent ref, but claimId is existent", E2ETest) {
      authToken

      Given("A valid claimId exists, but reference does not")
      uploadTestData(
        authToken,
        DeleteUploadData.getValidClaimId,
        DeleteUploadData.getValidReference
      )

      When("I delete a reference that does not exist")
      val response = deleteSingleUploadService.deleteSingleUpload(
        DeleteUploadData.getValidClaimId,
        DeleteUploadData.getInvalidReference,
        authToken
      )

      Then("A 404 status code should be returned")
      checkErrorResponse(response)
    }
  }
}
