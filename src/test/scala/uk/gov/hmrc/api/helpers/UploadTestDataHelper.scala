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
import uk.gov.hmrc.api.service.{DeleteSingleUploadService, DeleteUploadsClaimService}
import uk.gov.hmrc.api.data.CreateUploadTrackingData
import java.util.UUID
import scala.collection.mutable.ListBuffer

/** Useful class for uploading data to the database that conveniently deletes the records after each test scenario
  * completes so we can run the test in automation without any interference / extra work / DB cleanup
  */
trait UploadTestDataHelper extends BeforeAndAfterEach { self: BaseSpec =>
//  services used for seeding and cleanup
  val deleteSingleUploadService            = new DeleteSingleUploadService()
  val deleteUploadsClaimService            = new DeleteUploadsClaimService()
//  Store everything created for easy cleanup
  val seeded: ListBuffer[(String, String)] = ListBuffer.empty

  /** Upload the data to the DB with a random reference, in this scenario we only care about the ID. We return a random
    * reference that we can use to delete the claim straight after, mainly used for testing deleting endpoints
    */
  def seedUploadTestData(claimId: String, authToken: String, ref: String = UUID.randomUUID().toString): String = {
    val payload  = CreateUploadTrackingData.successfulPayloadWithReference(ref)
    val response = createUploadTrackingService.postAPayloadObject(claimId, payload, authToken)
    response.status shouldBe 201
    seeded += ((claimId, ref))

    ref
  }

  /** Similar to seedUploadTestData(), however we want more functionality over the data getting stored into the
    * database. Making our use cases more flexible as we have more control over claimID, reference and validationType
    * that will be stored in DB allowing us to write flexible specs and even test all endpoints more accurately and
    * easily. We should be able to use this method with some default test data otherwise override as needed
    */
  def uploadTestData(
    authToken: String,
    claimId: String = CreateUploadTrackingData.getValidClaimId,
    reference: String = CreateUploadTrackingData.getValidReference,
    validationType: ValidationType = ValidationType.GiftAid,
    responseCode: Int = 201,
    responseSuccess: Boolean = true
  ): Unit = {
    val payload  = CreateUploadTrackingData.customSuccessfulPayLoad(
      reference,
      validationType.toString
    )
    val response = createUploadTrackingService.postAPayloadObject(
      CreateUploadTrackingData.getValidClaimId,
      payload,
      authToken
    )

    /** Add the data to seeded to be cleaned up after the test has executed by calling delete endpoint with the claimId
      * and reference provided
      */
    seeded += ((claimId, reference))

    /** In most cases the successful upload of test data will result in a 201 status code. It is unnecessary to repeat
      * this check everywhere we upload data as we want to keep code DRY and spec files smaller by doing it here. There
      * are one or two instances where we check if the status code is 500 for example due to claim already existing so
      * we have the ability to assert the expected status code and body for these unique edge cases
      */
    Then(s"A $responseCode status code should be returned from uploadTestData, using: CreateUploadTracking API")
    response.status shouldBe responseCode

    And(s"The response body is { success: $responseSuccess }")
    (Json.parse(response.body) \ "success").as[Boolean] shouldBe responseSuccess
  }

  // TODO: POTENTIALLY REDUNDANT - SLOWLY REFACTORING
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
