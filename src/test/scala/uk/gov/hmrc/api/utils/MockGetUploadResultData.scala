package uk.gov.hmrc.api.utils

class MockGetUploadResultData {

  /** Simply a class to hold pre-determined claimIDs and references to test all edge cases to ensure GetUploadResult API
    * works as intended We need to retrieve multiple individual claim(s) so storing all identifiers in one place to
    * reduce human error
    */
  def getAwaitingUploadClaimId: String             = "awaiting-upload-id"
  def getAwaitingUploadReference: String           = "awaiting-upload-reference"
  def getVerifyingClaimId: String                  = "verifying-id"
  def getVerifyingReference: String                = "verifying-reference"
  def getQuarantineClaimId: String                 = "quarantine-id"
  def getQuarantineReference: String               = "quarantine-reference"
  def getRejectedClaimId: String                   = "rejected-id"
  def getRejectedReference: String                 = "rejected-reference"
  def getUnknownClaimId: String                    = "unknown-id"
  def getUnknownReference: String                  = "unknown-reference"
  def getValidatingClaimId: String                 = "validating-id"
  def getValidatingReference: String               = "validating-reference"
  def getDataValidClaimId: String                  = "valid-data-id"
  def getDataValidReference: String                = "valid-data-reference"
  def getInvalidDataClaimId: String                = "invalid-data-id"
  def getInvalidDataReference: String              = "invalid-data-reference"
  def getThisClaimIdDoesNotExist: String           = "this-claim-id-does-not-exist"
  def getThisReferenceDoesNotExist: String         = "this-reference-does-not-exist"
  def getAwaitingUploadHasExpiredClaimId: String   = "expired-id"
  def getAwaitingUploadHasExpiredReference: String = "expired-reference"
}
