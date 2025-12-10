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

  /** A valid Payload that should return a SUCCESS response */
  def getSuccessfulCreateUpscanCallbackPayload: CreateUpscanCallbackSuccessfulPayload = {
    val uploadDetailsUpscanCallback: UploadDetailsUpscanCallback = UploadDetailsUpscanCallback(
      "test.pdf",
      "application/pdf",
      "2018-04-24T09:30:00Z",
      "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
      987
    )

    CreateUpscanCallbackSuccessfulPayload(
      "11370e18-6e24-453e-b45a-76d3e32ea33d",
      "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      "READY",
      uploadDetailsUpscanCallback
    )
  }

  /** Request body payload(s) that should result in FAILURE
    *   - QUARANTINE - The file has failed virus scanning
    *   - REJECTED - The file detected "mimeType" is not allowed for the service or file extension is not allowed
    *   - UNKNOWN - There is another problem with the file
    */
  private def getQuarantinedFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    "QUARANTINE",
    "e.g. This file has a virus"
  )

  private def getRejectedFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    "REJECTED",
    "MIME type $mime is not allowed for service $service-name"
  )

  private def getUnknownFailureDetails: FailureDetailsUpscanCallback = FailureDetailsUpscanCallback(
    "UNKNOWN",
    "Something unknown happened"
  )

  /** The "failureType" refers to "failureDetails", i.e., QUARANTINE (0), REJECTED (1), UNKNOWN (2) */
  def getFailedCreateUpscanCallbackPayload(failureType: Int): CreateUpscanCallbackFailedPayload = {
    val failureDetails = failureType match {
      case 0 => getQuarantinedFailureDetails
      case 1 => getRejectedFailureDetails
      case 2 => getUnknownFailureDetails
    }

    CreateUpscanCallbackFailedPayload(
      "11370e18-6e24-453e-b45a-76d3e32ea33d",
      "FAILED",
      failureDetails
    )
  }
}
