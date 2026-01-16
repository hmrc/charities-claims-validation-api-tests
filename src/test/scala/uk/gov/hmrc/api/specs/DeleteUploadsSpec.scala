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

import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.specs.tags.E2ETest
import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.data.DeleteUploadData

class DeleteUploadsSpec extends BaseSpec with UploadTestDataHelper {

  Feature("Delete Upload Claim API") {
    Scenario("Delete a claim", E2ETest) {
      Given("There is a valid AUTH Token")
      authToken

      When("We upload the test data")
      uploadTestData(authToken, DeleteUploadData.getValidClaimId)

      When("I send DELETE request to the Endpoint")
      val response = deleteUploadsClaimService.deleteUploads(DeleteUploadData.getValidClaimId, authToken)

      Then("A 200 status code should be returned with a { success: true } body")
      checkGenericResponseBodyAndStatusCode(response, 200, true)
    }
  }
}
