package uk.gov.hmrc.api.utils

import uk.gov.hmrc.api.models.UpdateUploadStatusPayload

object MockUpdateUploadStatusData {
  /** A valid payload */
  def getSuccessfulPayload: UpdateUploadStatusPayload = UpdateUploadStatusPayload(
    fileStatus = "VERIFYING"
  )

  /** A list of invalid payloads
   * - Wrong Status (Any other value)
   * - Empty String */
  def getInvalidFileStatus: UpdateUploadStatusPayload = UpdateUploadStatusPayload(
    fileStatus = "NOT-EXPECTED-VALUE"
  )

  def getEmptyString: UpdateUploadStatusPayload = UpdateUploadStatusPayload(
    fileStatus = ""
  )
}
