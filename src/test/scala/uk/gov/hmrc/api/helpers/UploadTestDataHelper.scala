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
import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.data.CreateUploadTrackingData
import uk.gov.hmrc.api.data.globals.ValidationType
import scala.collection.mutable.ListBuffer

/** Useful class for uploading data to the database that conveniently deletes the records after each test scenario
  * completes so we can run the test in automation without any interference / extra work / DB cleanup
  */
trait UploadTestDataHelper extends BeforeAndAfterEach { self: BaseSpec =>
  //  Store everything created for easy cleanup
  val seeded: ListBuffer[(String, String)] = ListBuffer.empty

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
      claimId,
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
    checkGenericResponseBodyAndStatusCode(response, responseCode = responseCode, responseSuccess = responseSuccess)
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
