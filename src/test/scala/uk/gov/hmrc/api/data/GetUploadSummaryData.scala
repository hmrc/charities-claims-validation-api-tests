package uk.gov.hmrc.api.data

import uk.gov.hmrc.api.data.globals.ValidationType

object GetUploadSummaryData {

  /** Using validation type to define what claim we are referencing, tests will be duplicated for
    *   - GiftAid, OtherIncome, CommunityBuildings and ConnectedCharities
    */
  def getIndividualClaimID(validationType: ValidationType): String = s"${validationType.toString}-claim-id"
  def getGroupOfClaimsID: String                                   = s"group-claim-id"
  def getThisClaimIdDoesNotExist: String                           = "this-claim-id-does-not-exist"
  def getThisClaimHasExpiredID: String                             = "this-claim-has-expired"
}
