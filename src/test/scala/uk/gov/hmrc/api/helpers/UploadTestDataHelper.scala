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

package uk.gov.hmrc.api.helpers

import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.service.DeleteSingleUploadService
import uk.gov.hmrc.api.data.CreateUploadTrackingData

import java.util.UUID
import scala.collection.mutable.ListBuffer

trait UploadTestDataHelper extends BeforeAndAfterEach { self: BaseSpec =>
//  services used for seeding and cleanup
  val deleteSingleUploadService            = new DeleteSingleUploadService()
  val deleteUploadsClaimService            = new DeleteUploadsClaimService()
//  Store everything created for easy cleanup
  val seeded: ListBuffer[(String, String)] = ListBuffer.empty

  def seedUploadTestData(claimId: String, authToken: String, ref: String = UUID.randomUUID().toString): String = {
    val payload  = CreateUploadTrackingData.successfulPayloadWithReference(ref)
    val response = createUploadTrackingService.postAPayloadObject(claimId, payload, authToken)
    response.status shouldBe 201
    seeded += ((claimId, ref))

    ref
  }

  // TODO: Code is not DRY should revisit and clean up duplicated method
  // TODO: We could keep the response being returned but could add Cucumber here instead of in the spec(s)
  // Like seedUploadTestData however we simply use the default payload
  def uploadDefaultTestData(authToken: String): StandaloneWSResponse = {
    val payload  = CreateUploadTrackingData.getSuccessfulCreateUploadTrackingPayload
    val response =
      createUploadTrackingService.postAPayloadObject(CreateUploadTrackingData.getValidClaimId, payload, authToken)

    // Add the data to seeded to be cleaned up after the test has executed
    seeded += ((CreateUploadTrackingData.getValidClaimId, CreateUploadTrackingData.getValidReference))
    response
  }

  def uploadTestDataCustomIdAndReference(
    authToken: String,
    claimId: String,
    reference: String
  ): StandaloneWSResponse = {
    val payload  = CreateUploadTrackingData.successfulPayloadWithReference(reference)
    val response = createUploadTrackingService.postAPayloadObject(
      claimId,
      payload,
      authToken
    )

    seeded += ((claimId, reference))
    response
  }

  // TODO: This will be removed in refactoring of this class but for now same behaviour as class above
  // but will not be returning a response just to keep the GetUploadResultSpec cleaner
  def uploadTestDataCustomIdAndReferenceNoReturn(
    authToken: String,
    claimId: String,
    reference: String
  ): Unit = {
    val payload  = CreateUploadTrackingData.successfulPayloadWithReference(reference)
    val response = createUploadTrackingService.postAPayloadObject(
      claimId,
      payload,
      authToken
    )

    Then("A 201 status code should be returned from CreateUploadTrackingSpec")
    response.status shouldBe 201

    And("The response body is { success: true }")
    (Json.parse(response.body) \ "success").as[Boolean] shouldBe true

    seeded += ((claimId, reference))
  }

  override protected def afterEach(): Unit = {
    seeded.foreach { case (claimId, ref) =>
      try deleteSingleUploadService.deleteSingleUpload(claimId, ref, authHelper.bearerToken)
      catch { case _: Throwable => () }
    }
    seeded.clear()

    super.afterEach()
  }
}
