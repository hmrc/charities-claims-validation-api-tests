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
  private val API_NAME: String = "Update-Upload-Status"

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

  /** The two valid claimIDs will be used to refer to data actually stored in the DB and will be used to check various
    * edge cases, we need two different IDs as we want a claim to have the incorrect "fileStatus" to ensure behavior is
    * what we expect
    */
  def getValidClaimId: String            = s"$API_NAME-claim"
  def getValidClaimIdDifferentFileStatus = s"$API_NAME-claim-123"

  /** As we will have two valid claims stored in the DB we should give them two different references */
  def getValidReference: String            = s"$API_NAME-ref"
  def getValidReferenceDifferentFileStatus = s"$API_NAME-ref-123"

  /** Some test cases require us to pass in invalid data */
  def getInvalidClaimId: String   = s"$API_NAME-invalid-claim"
  def getInvalidReference: String = s"$API_NAME-invalid-ref"
}
