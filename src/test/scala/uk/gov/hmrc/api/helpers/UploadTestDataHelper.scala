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
import uk.gov.hmrc.api.service.{CreateUploadTrackingService, DeleteSingleUploadService}
import uk.gov.hmrc.api.utils.{BaseSpec, MockCreateUploadTrackingData}

import java.util.UUID
import scala.collection.mutable.ListBuffer


trait UploadTestDataHelper extends BeforeAndAfterEach { self: BaseSpec =>
//  services used for seeding and cleanup
  val createUploadTrackingService = new CreateUploadTrackingService()
  val deleteSingleUploadService = new DeleteSingleUploadService()
//  Store everything created for easy cleanup
  val seeded: ListBuffer[(String, String)] = ListBuffer.empty 

  def seedUploadTestData(claimId: String, authToken: String, ref: String = UUID.randomUUID().toString): String = {
    val payload = MockCreateUploadTrackingData.successfulPayloadWithReference(ref)
    val response = createUploadTrackingService.postAPayloadObject(claimId, payload, authToken)
    response.status shouldBe 201
    seeded += ((claimId, ref))
    
    ref
  }

  override protected def afterEach(): Unit = {
    seeded.foreach {case (claimId, ref) =>
    try {deleteSingleUploadService.deleteSingleUpload(claimId, ref, authHelper.bearerToken)}
     catch{ case _: Throwable => ()}
    }
    seeded.clear()
    
    super.afterEach()
  }
}
