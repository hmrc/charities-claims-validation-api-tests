package uk.gov.hmrc.api.data

import uk.gov.hmrc.api.data.globals.ValidationType

object GetUploadSummaryData {

  /** Using validation type to define what claim we are referencing, tests will be duplicated for
    *   - GiftAid, OtherIncome, CommunityBuildings and ConnectedCharities
    */
  def getIndividualClaimID(validationType: ValidationType): String   = s"${validationType.toString}-claim-id"
  def getIndividualReference(validationType: ValidationType): String = s"${validationType.toString}-reference"
  def getGroupOfClaimsID: String                                     = s"group-claim-id"
  def getThisClaimIdDoesNotExist: String                             = "this-claim-id-does-not-exist"
  def getThisClaimHasExpiredID: String                               = "this-claim-has-expired"

  /** File paths to the local successful spreadsheet uploads */
  def getSuccessfulFileLocations(validationType: ValidationType, fileLocation: Boolean = true): String =
    validationType match {
      case ValidationType.OtherIncome =>
        if (fileLocation) {
          "file:/home/kyle/documents/charities-claims-validation-api-tests/src/test/resources/spreadsheets/OtherIncome/other_income_schedule-GoodData.ods"
        } else {
          "other_income_schedule-GoodData.ods"
        }
    }
}
