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

package uk.gov.hmrc.api.data

import uk.gov.hmrc.api.data.globals.FailureReason
import uk.gov.hmrc.api.models.{CreateUpscanCallbackFailedPayload, CreateUpscanCallbackSuccessfulPayload, FailureDetailsUpscanCallback, UploadDetailsUpscanCallback}

object CreateUpscanCallbackData {

  /** API_NAME is responsible for appending itself to claimId and reference so we know what documents stored in the DB
    * are associated to an API call that created / or called our services. The next few constants we use for
    * 'fileStatus' and 'failureDetails' storing them here just to eliminate the potential of human error with mismatch
    * spelling etc., If any 'fileStatus' parameters are modified / added / removed simply change them here
    */
  private val API_NAME: String           = "Create-Upscan-Callback"
  private val FILE_STATUS_READY: String  = "READY"
  private val FILE_STATUS_FAILED: String = "FAILED"

  /** The expected file type */
  private val FILE_TYPE: String = "application/vnd.oasis.opendocument.spreadsheet"

  /** Common data that is used for the UpscanCallback for successful and failure types of request(s). These default
    * values will be used for testing purposes
    */
  def commonUploadDetailsUpscanCallback(
    fileName: String = getDefaultFilename,
    fileType: String = FILE_TYPE
  ): UploadDetailsUpscanCallback = UploadDetailsUpscanCallback(
    fileName = fileName,
    fileMimeType = fileType,
    uploadTimestamp = "2018-04-24T09:30:00Z",
    checksum = "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
    size = 987
  )

  /** A valid Payload that should return a SUCCESS response, again providing some default values */
  def getSuccessfulCreateUpscanCallbackPayload(
    downloadUrl: String = getDefaultUrl,
    fileName: String = getDefaultFilename
  ): CreateUpscanCallbackSuccessfulPayload =
    CreateUpscanCallbackSuccessfulPayload(
      reference = CreateUploadTrackingData.getValidReference,
      downloadUrl = downloadUrl,
      fileStatus = FILE_STATUS_READY,
      uploadDetails = commonUploadDetailsUpscanCallback(fileName)
    )

  /** A valid Payload that should return a 400 response as the file type is wrong */
  def getInvalidFileTypeCreateUpscanCallbackPayload: CreateUpscanCallbackSuccessfulPayload = {
    val differentMimeType = commonUploadDetailsUpscanCallback(getDefaultFilename, fileType = "application/pdf")

    CreateUpscanCallbackSuccessfulPayload(
      reference = "f5da5578-8393-4cd1-be0e-d8ef1b78d8e8",
      downloadUrl = "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      fileStatus = FILE_STATUS_READY,
      uploadDetails = differentMimeType
    )
  }

  /** A valid Payload that should return a 404 response as the reference does not exist */
  def getInvalidReferenceCreateUpscanCallbackPayload: CreateUpscanCallbackSuccessfulPayload =
    CreateUpscanCallbackSuccessfulPayload(
      reference = CreateUploadTrackingData.getInvalidReference,
      downloadUrl = "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      fileStatus = FILE_STATUS_READY,
      uploadDetails = commonUploadDetailsUpscanCallback()
    )

  /** Request body payload(s) that should result in FAILURE
    *   - QUARANTINE - The file has failed virus scanning
    *   - REJECTED - The file detected "mimeType" is not allowed for the service or file extension is not allowed
    *   - UNKNOWN - There is another problem with the file
    */
  private def getQuarantinedFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    failureReason = FailureReason.QUARANTINE.toString,
    message = FailureReason.QUARANTINE.getFailureMessage
  )

  private def getRejectedFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    failureReason = FailureReason.REJECTED.toString,
    message = FailureReason.REJECTED.getFailureMessage
  )

  private def getUnknownFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    failureReason = FailureReason.UNKNOWN.toString,
    message = FailureReason.UNKNOWN.getFailureMessage
  )

  /** Quick fix as the method above causes stackoverflow issues when overriding reference */
  def getQuarantineUpscanCallbackPayload(reference: String = getQuarantineRef): CreateUpscanCallbackFailedPayload =
    CreateUpscanCallbackFailedPayload(
      reference = reference,
      fileStatus = FILE_STATUS_FAILED,
      failureDetails = getQuarantinedFailureDetails
    )

  def getRejectedUpscanCallbackPayload(reference: String = getRejectedRef): CreateUpscanCallbackFailedPayload =
    CreateUpscanCallbackFailedPayload(
      reference = reference,
      fileStatus = FILE_STATUS_FAILED,
      failureDetails = getRejectedFailureDetails
    )

  def getUnknownUpscanCallbackPayload(reference: String = getUnknownRef): CreateUpscanCallbackFailedPayload =
    CreateUpscanCallbackFailedPayload(
      reference = reference,
      fileStatus = FILE_STATUS_FAILED,
      failureDetails = getUnknownFailureDetails
    )

  /** Helpful methods for overriding default payloads useful for tests that use custom parameters, or we want to target
    * documents in the database that have been created by different API calls and will have unique id / ref
    */
  def getSuccessfulCreateUpscanCallbackPayloadWithReference(
    reference: String,
    downloadUrl: String = getDefaultUrl,
    fileName: String = getDefaultFilename
  ): CreateUpscanCallbackSuccessfulPayload = {
    val differentReference = getSuccessfulCreateUpscanCallbackPayload(downloadUrl, fileName)
    differentReference.copy(reference = reference)
  }

  /** IDs and references used to update each claim to become each failure case, i.e.,
    *   - Quarantine
    *   - Rejected
    *   - Unknown
    * These documents with the associated id / ref will be stored in the DB awaiting UpscanCallback to change the doc to
    * include each unique FailureType
    */
  def getQuarantineClaimId: String = s"$API_NAME-${FailureReason.QUARANTINE.toString}-claim"
  def getQuarantineRef: String     = s"$API_NAME-${FailureReason.QUARANTINE.toString}-ref"
  def getRejectedClaimId: String   = s"$API_NAME-${FailureReason.REJECTED.toString}-claim"
  def getRejectedRef: String       = s"$API_NAME-${FailureReason.REJECTED.toString}-ref"
  def getUnknownClaimId: String    = s"$API_NAME-${FailureReason.UNKNOWN.toString}-claim"
  def getUnknownRef: String        = s"$API_NAME-${FailureReason.UNKNOWN.toString}-ref"

  /** For these values we don't care about the actual value it just for passing generic Upscan Tests, however for file
    * validation we want to override these with valid data
    */
  def getDefaultUrl: String      = "https://bucketName.s3.eu-west-2.amazonaws.com?1235676"
  def getDefaultFilename: String = "default-filename"
}
