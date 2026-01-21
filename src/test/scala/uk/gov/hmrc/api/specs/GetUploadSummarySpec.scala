package uk.gov.hmrc.api.specs

import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.data.GetUploadSummaryData
import uk.gov.hmrc.api.data.globals.{FileStatus, ValidationType}
import uk.gov.hmrc.api.helpers.UploadTestDataHelper
import uk.gov.hmrc.api.service.GetUploadSummaryService
import uk.gov.hmrc.api.specs.tags.E2ETest

class GetUploadSummarySpec extends BaseSpec with UploadTestDataHelper {

  /** The E2E test for this will be simply upload a GiftAid claim and provide valid data then check the response is as
    * expected
    */
  //TODO: Blocked until Upscan Refactor
//  Feature("Charities - Get Upload Summary API - E2E") {
//    Scenario("Starting a GiftAid claim and providing valid data to Upscan", E2ETest) {
//      uploadTestData(authToken)
//      Then("We provide upscan with a valid payload that has a reference to the valid spreadsheet value")
//
//    }
//  }

  // TODO: Finish
  Feature("Charities - Get Upload Summary API - Testing all response variations") {
    Scenario("Testing the four variations of 'validationType' where one associate claimID is returned") {
      authToken

      /** Successfully update test data */
      When("We upload the test data")
      uploadDataForAllClaims(authToken)

      /** Check the response(s) */
      val giftAidResponse = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.GiftAid),
        authToken
      )

      Then("We check GiftAid response")
      checkCommonResponseBodies(giftAidResponse, ValidationType.GiftAid, FileStatus.AWAITING_UPLOAD)

      val otherIncomeResponse = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.OtherIncome),
        authToken
      )

      Then("We check OtherIncome response")
      checkCommonResponseBodies(otherIncomeResponse, ValidationType.OtherIncome, FileStatus.AWAITING_UPLOAD)

      val communityBuildingsResponse = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.CommunityBuildings),
        authToken
      )

      Then("We check CommunityBuildings response")
      checkCommonResponseBodies(communityBuildingsResponse, ValidationType.CommunityBuildings, FileStatus.AWAITING_UPLOAD)

      val connectedCharitiesResponse = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.ConnectedCharities),
        authToken
      )

      Then("We check ConnectedCharities response")
      checkCommonResponseBodies(connectedCharitiesResponse, ValidationType.ConnectedCharities, FileStatus.AWAITING_UPLOAD)
    }

    Scenario("Testing the four variations of 'validationType' where all claimIDs are associated to one user") {

      /** Successfully update the test data for one user */
      When("We upload the test data")
      uploadDataForAllClaims(
        authToken,
        giftAidID = GetUploadSummaryData.getGroupOfClaimsID,
        otherIncomeID = GetUploadSummaryData.getGroupOfClaimsID,
        communityBuildingsID = GetUploadSummaryData.getGroupOfClaimsID,
        connectedCharitiesID = GetUploadSummaryData.getGroupOfClaimsID
      )

      /** Check the response */
    }
  }
}
