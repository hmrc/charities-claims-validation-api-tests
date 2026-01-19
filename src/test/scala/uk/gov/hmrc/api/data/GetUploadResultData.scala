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

package uk.gov.hmrc.api.data

import uk.gov.hmrc.api.data.globals.{FileStatus, ValidationType}

object GetUploadResultData {

  /** Simply a class to hold pre-determined claimIDs and references to test all edge cases to ensure GetUploadResult API
    * works as intended We need to retrieve multiple individual claim(s) so storing all identifiers in one place to
    * reduce human error
    */
  def getAwaitingUploadClaimId: String                  = "awaiting-upload-id"
  def getAwaitingUploadReference: String                = "awaiting-upload-reference"
  def getVerifyingClaimId: String                       = "verifying-id"
  def getVerifyingReference: String                     = "verifying-reference"
  def getQuarantineClaimId: String                      = "quarantine-id"
  def getQuarantineReference: String                    = "quarantine-reference"
  def getRejectedClaimId: String                        = "rejected-id"
  def getRejectedReference: String                      = "rejected-reference"
  def getUnknownClaimId: String                         = "unknown-id"
  def getUnknownReference: String                       = "unknown-reference"
  def getValidatingClaimId: String                      = "validating-id"
  def getValidatingReference: String                    = "validating-reference"
  def getValidDataClaimIdGiftAid: String                = "valid-data-id-gift-aid"
  def getValidDataReferenceGiftAid: String              = "valid-data-reference-gift-aid"
  def getValidDataClaimIdOtherIncome: String            = "valid-data-id-other-income"
  def getValidDataReferenceOtherIncome: String          = "valid-data-reference-other-income"
  def getValidDataClaimIdConnectedCharities: String     = "valid-data-id-connected-charities"
  def getValidDataReferenceConnectedCharities: String   = "valid-data-reference-connected-charities"
  def getValidDataClaimIdCommunityBuildings: String     = "valid-data-id-community-buildings"
  def getValidDataReferenceCommunityBuildings: String   = "valid-data-reference-community-buildings"
  def getInvalidDataClaimIdGiftAid: String              = "invalid-data-id-gift-aid"
  def getInvalidDataReferenceGiftAid: String            = "invalid-data-reference-gift-aid"
  def getInvalidDataClaimIdOtherIncome: String          = "invalid-data-id-other-income"
  def getInvalidDataReferenceOtherIncome: String        = "invalid-data-reference-other-income"
  def getInvalidDataClaimIdConnectedCharities: String   = "invalid-data-id-connected-charities"
  def getInvalidDataReferenceConnectedCharities: String = "invalid-data-reference-connected-charities"
  def getInvalidDataClaimIdCommunityBuildings: String   = "invalid-data-id-community-buildings"
  def getInvalidDataReferenceCommunityBuildings: String = "invalid-data-reference-community-buildings"
  def getThisClaimIdDoesNotExist: String                = "this-claim-id-does-not-exist"
  def getThisReferenceDoesNotExist: String              = "this-reference-does-not-exist"
  def getAwaitingUploadHasExpiredClaimId: String        = "expired-id"
  def getAwaitingUploadHasExpiredReference: String      = "expired-reference"

  // Useful helper methods to extract the correct claimID, reference and validationType
  def getCorrectReference(validationType: ValidationType, fileStatus: FileStatus): String =
    validationType match
      case ValidationType.GiftAid =>
        fileStatus match
          case FileStatus.VALIDATED         => getValidDataReferenceGiftAid
          case FileStatus.VALIDATION_FAILED => getInvalidDataReferenceGiftAid
          case _                            => ""

      case ValidationType.OtherIncome =>
        fileStatus match
          case FileStatus.VALIDATED         => getValidDataReferenceOtherIncome
          case FileStatus.VALIDATION_FAILED => getInvalidDataReferenceOtherIncome
          case _                            => ""

      case ValidationType.ConnectedCharities =>
        fileStatus match
          case FileStatus.VALIDATED         => getValidDataReferenceConnectedCharities
          case FileStatus.VALIDATION_FAILED => getInvalidDataReferenceConnectedCharities
          case _                            => ""

      case ValidationType.CommunityBuildings =>
        fileStatus match
          case FileStatus.VALIDATED         => getValidDataReferenceCommunityBuildings
          case FileStatus.VALIDATION_FAILED => getInvalidDataReferenceCommunityBuildings
          case _                            => ""

  // Get the name of the field in the response body for Valid and Invalid Data, we only know this at runtime
  def getCorrectJsonBodyFieldName(validationType: ValidationType): String =
    validationType match
      case ValidationType.GiftAid            => "giftAidScheduleData"
      case ValidationType.OtherIncome        => "otherIncomeData"
      case ValidationType.ConnectedCharities => "connectedCharitiesData"
      case ValidationType.CommunityBuildings => "communityBuildingData"
}
