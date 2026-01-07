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

import uk.gov.hmrc.api.models.CreateUploadTrackingPayload

// The only valid types we are expecting for validationType in our payload
enum ValidationType:
  case GiftAid, OtherIncome, CommunityBuildings, ConnectedCharities

object MockCreateUploadTrackingData {
  // Used to differentiate documents in the DB to understand which API call created them
  private val API_NAME: String = "Create-Upload-Tracking"

  /** A valid payload that should be successful */
  def getSuccessfulCreateUploadTrackingPayload: CreateUploadTrackingPayload = CreateUploadTrackingPayload(
    reference = getValidReference,
    validationType = ValidationType.GiftAid.toString,
    uploadUrl = "https://xxxx/upscan-upload-proxy/bucketName",
    initiateTimestamp = "2025-11-30T06:49:19.571Z"
  )

  /** A payload that should fail due to having an invalid "validationType" must be one of the following
    *   - GiftAid
    *   - OtherIncome
    *   - CommunityBuildings
    *   - ConnectedCharities
    */
  def getInvalidValidationCreateUploadTrackingPayload: CreateUploadTrackingPayload = CreateUploadTrackingPayload(
    reference = getInvalidReference,
    validationType = "Validation",
    uploadUrl = "https://xxxx/upscan-upload-proxy/bucketName",
    initiateTimestamp = "2025-11-30T06:49:19.571Z"
  )

  //  Some helpful methods to allow us to override the default payload for more customizable DB data uploads
  def successfulPayloadWithReference(reference: String): CreateUploadTrackingPayload =
    getSuccessfulCreateUploadTrackingPayload.copy(reference = reference)

  def successfulPayloadWithValidationType(validationType: String): CreateUploadTrackingPayload =
    getSuccessfulCreateUploadTrackingPayload.copy(validationType = validationType)

  def customSuccessfulPayLoad(reference: String, validationType: String): CreateUploadTrackingPayload =
    getSuccessfulCreateUploadTrackingPayload.copy(reference = reference, validationType = validationType)

  // Default claimID that will be used for CreateUploadTracking Spec and will be associated documents stored in the DB
  def getValidClaimId: String   = s"$API_NAME-claim"
  def getValidReference: String = s"$API_NAME-ref"

  // For testing failures using some default failure details
  def getInvalidClaimId: String   = s"$API_NAME-invalid-claim"
  def getInvalidReference: String = s"$API_NAME-invalid-ref"
}
