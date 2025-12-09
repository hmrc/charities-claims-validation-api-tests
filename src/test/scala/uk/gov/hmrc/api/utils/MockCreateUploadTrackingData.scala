package uk.gov.hmrc.api.utils

import uk.gov.hmrc.api.models.CreateUploadTrackingPayload

object MockCreateUploadTrackingData {
  /** A valid payload that should be successful */
  def getSuccessfulCreateUploadTrackingPayload: CreateUploadTrackingPayload = CreateUploadTrackingPayload(
    "f5da5578-8393-4cd1-be0e-d8ef1b78d8e7",
    "GiftAid",
    "https://xxxx/upscan-upload-proxy/bucketName",
    "2025-11-31T06:49:19.571Z"
  )

  /** A payload that should fail due to having an invalid "validationType" must be one of the following
   * - GiftAid
   * - OtherIncome
   * - CommunityBuildings
   * - ConnectedCharities */
  def getInvalidValidationCreateUploadTrackingPayload: CreateUploadTrackingPayload = CreateUploadTrackingPayload(
    "f5da5578-8393-4cd1-be0e-d8ef1b78d8e7",
    "Validation",
    "https://xxxx/upscan-upload-proxy/bucketName",
    "2025-11-31T06:49:19.571Z"
  )

  /** A bunch of incomplete payload(s) that should fail due to missing required information  */
}
