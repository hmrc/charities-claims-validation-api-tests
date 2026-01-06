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

///*
// * Copyright 2025 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package uk.gov.hmrc.api.specs
//
//import play.api.libs.json.Json
//import uk.gov.hmrc.api.utils.BaseSpec
//
//// TODO: Negative paths etc.,
//class CreateDeleteUploadSpec extends BaseSpec {
//  Feature("Charities - Create Delete Upload API - Single Delete") {
//    Scenario("Successful delete a charity claim") {
//      When("The CreateDeleteUpload Endpoint is sent a valid DELETE Request")
//      val response = createDeleteUploadStub.deleteSingleRequest("claim-123", "ref-456")
//
//      Then("A 204 status code should be returned")
//      response.status shouldBe 204
//
//      And("The response body is { success: true }")
//      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
//    }
//  }
//
//  Feature("Charities - Create Delete Upload API - Multi Delete") {
//    Scenario("Successfully delete a charity claim") {
//      When("The CreateDeleteUpload(s) Endpoint is sent a valid DELETE Request")
//      val response = createDeleteUploadStub.deleteManyRequest("claim-123")
//
//      Then("A 204 status code should be returned")
//      response.status shouldBe 204
//
//      And("The response body is { success: true }")
//      (Json.parse(response.body) \ "success").as[Boolean] shouldBe true
//    }
//  }
//}
