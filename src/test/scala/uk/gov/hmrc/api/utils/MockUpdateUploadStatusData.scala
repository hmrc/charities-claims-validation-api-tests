/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.api.utils

import uk.gov.hmrc.api.models.UpdateUploadStatusPayload

object MockUpdateUploadStatusData {

  /** A valid payload */
  def getSuccessfulPayload: UpdateUploadStatusPayload = UpdateUploadStatusPayload(
    fileStatus = "VERIFYING"
  )

  /** A list of invalid payloads
    *   - Wrong Status (Any other value)
    *   - Empty String
    */
  def getInvalidFileStatusPayload: UpdateUploadStatusPayload = UpdateUploadStatusPayload(
    fileStatus = "NOT-EXPECTED-VALUE"
  )

  def getEmptyString: UpdateUploadStatusPayload = UpdateUploadStatusPayload(
    fileStatus = ""
  )

  def getValidClaimId: String     = "claim-123"
  def getValidReference: String   = "ref-001"
  def getInvalidClaimId: String   = "invalid-claim-123"
  def getInvalidReference: String = "invalid-ref-001"
}
