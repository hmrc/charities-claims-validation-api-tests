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

package uk.gov.hmrc.api.utils

import uk.gov.hmrc.api.models.{CreateUpscanCallbackFailedPayload, CreateUpscanCallbackSuccessfulPayload, FailureDetailsUpscanCallback, UploadDetailsUpscanCallback}

object MockCreateUpscanCallbackData {
  private val API_NAME: String   = "Create-Upscan-Callback"
  private val QUARANTINE: String = "QUARANTINE"
  private val REJECTED: String   = "REJECTED"
  private val UNKNOWN: String    = "UNKNOWN"
  private val FAILED: String     = "FAILED"

  /** Common data */
  private val commonUploadDetailsUpscanCallback: UploadDetailsUpscanCallback = UploadDetailsUpscanCallback(
    fileName = "test.pdf",
    fileMimeType = "application/vnd.oasis.opendocument.spreadsheet",
    uploadTimestamp = "2018-04-24T09:30:00Z",
    checksum = "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
    size = 987
  )

  /** A valid Payload that should return a SUCCESS response */
  def getSuccessfulCreateUpscanCallbackPayload: CreateUpscanCallbackSuccessfulPayload =
    CreateUpscanCallbackSuccessfulPayload(
      reference = MockCreateUploadTrackingData.getValidReference,
      downloadUrl = "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      fileStatus = "READY",
      uploadDetails = commonUploadDetailsUpscanCallback
    )

  /** A valid Payload that should return a 400 response as the file type is wrong */
  def getInvalidFileTypeCreateUpscanCallbackPayload: CreateUpscanCallbackSuccessfulPayload = {
    val differentMimeType = commonUploadDetailsUpscanCallback.copy(fileMimeType = "application/pdf")

    CreateUpscanCallbackSuccessfulPayload(
      reference = "f5da5578-8393-4cd1-be0e-d8ef1b78d8e8",
      downloadUrl = "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      fileStatus = "READY",
      uploadDetails = differentMimeType
    )
  }

  /** A valid Payload that should return a 404 response as the reference does not exist */
  def getInvalidReferenceCreateUpscanCallbackPayload: CreateUpscanCallbackSuccessfulPayload =
    CreateUpscanCallbackSuccessfulPayload(
      reference = MockCreateUploadTrackingData.getInvalidReference,
      downloadUrl = "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      fileStatus = "READY",
      uploadDetails = commonUploadDetailsUpscanCallback
    )

  /** Request body payload(s) that should result in FAILURE
    *   - QUARANTINE - The file has failed virus scanning
    *   - REJECTED - The file detected "mimeType" is not allowed for the service or file extension is not allowed
    *   - UNKNOWN - There is another problem with the file
    */
  private def getQuarantinedFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    failureReason = QUARANTINE,
    message = "e.g. This file has a virus"
  )

  private def getRejectedFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    failureReason = REJECTED,
    message = "MIME type $mime is not allowed for service $service-name"
  )

  private def getUnknownFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    failureReason = UNKNOWN,
    message = "Something unknown happened"
  )

  // No longer works if we want unique reference, come back and fix but for now doing the easy way
  /** The "failureType" refers to "failureDetails", i.e., QUARANTINE (0), REJECTED (1), UNKNOWN (2) */
//  def getFailedCreateUpscanCallbackPayload(failureType: Int): CreateUpscanCallbackFailedPayload = {
//    val failureDetails = failureType match {
//      case 0 => getQuarantinedFailureDetails
//      case 1 => getRejectedFailureDetails
//      case 2 => getUnknownFailureDetails
//    }
//
//    CreateUpscanCallbackFailedPayload(
//      reference = "referece",
//      fileStatus = "FAILED",
//      failureDetails = failureDetails
//    )
//  }

  /** Quick fix as the method above causes stackoverflow issues when overriding reference */
  def getQurantineUpscanCallbackPayload(): CreateUpscanCallbackFailedPayload =
    CreateUpscanCallbackFailedPayload(
      reference = getQuarantineRef,
      fileStatus = FAILED,
      failureDetails = getQuarantinedFailureDetails
    )

  def getRejectedUpscanCallbackPayload(): CreateUpscanCallbackFailedPayload =
    CreateUpscanCallbackFailedPayload(
      reference = getRejectedRef,
      fileStatus = FAILED,
      failureDetails = getRejectedFailureDetails
    )

  def getUnknownUpscanCallbackPayload(): CreateUpscanCallbackFailedPayload =
    CreateUpscanCallbackFailedPayload(
      reference = getUnknownRef,
      fileStatus = FAILED,
      failureDetails = getUnknownFailureDetails
    )

  /** Helpful methods for overriding default payloads useful for tests that use custom parameters */
  def getSuccessfulCreateUpscanCallbackPayloadWithReference(reference: String): CreateUpscanCallbackSuccessfulPayload =
    getSuccessfulCreateUpscanCallbackPayload.copy(reference = reference)

  /** IDs and references used to update each claim to become each failure case, i.e.,
    *   - Quarantine
    *   - Rejected
    *   - Unknown
    */
  def getQuarantineClaimId: String = s"$API_NAME-$QUARANTINE-claim"
  def getQuarantineRef: String     = s"$API_NAME-$QUARANTINE-ref"
  def getRejectedClaimId: String   = s"$API_NAME-$REJECTED-claim"
  def getRejectedRef: String       = s"$API_NAME-$REJECTED-ref"
  def getUnknownClaimId: String    = s"$API_NAME-$UNKNOWN-claim"
  def getUnknownRef: String        = s"$API_NAME-$UNKNOWN-ref"
}
