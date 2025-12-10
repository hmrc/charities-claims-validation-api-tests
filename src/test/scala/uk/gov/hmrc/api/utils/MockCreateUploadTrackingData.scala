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

object MockCreateUploadTrackingData {

  /** A valid payload that should be successful */
  def getSuccessfulCreateUploadTrackingPayload: CreateUploadTrackingPayload = CreateUploadTrackingPayload(
    "f5da5578-8393-4cd1-be0e-d8ef1b78d8e7",
    "GiftAid",
    "https://xxxx/upscan-upload-proxy/bucketName",
    "2025-11-31T06:49:19.571Z"
  )

  /** A payload that should fail due to having an invalid "validationType" must be one of the following
    *   - GiftAid
    *   - OtherIncome
    *   - CommunityBuildings
    *   - ConnectedCharities
    */
  def getInvalidValidationCreateUploadTrackingPayload: CreateUploadTrackingPayload = CreateUploadTrackingPayload(
    "f5da5578-8393-4cd1-be0e-d8ef1b78d8e7",
    "Validation",
    "https://xxxx/upscan-upload-proxy/bucketName",
    "2025-11-31T06:49:19.571Z"
  )
}
