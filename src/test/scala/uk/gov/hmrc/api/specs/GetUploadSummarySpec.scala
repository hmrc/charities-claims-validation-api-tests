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

package uk.gov.hmrc.api.specs

import uk.gov.hmrc.api.BaseSpec
import uk.gov.hmrc.api.data.{CreateUpscanCallbackData, GetUploadSummaryData}
import uk.gov.hmrc.api.data.globals.{FileStatus, ValidationType}
import uk.gov.hmrc.api.helpers.{SpreadsheetLocationHelper, UploadTestDataHelper}
import uk.gov.hmrc.api.specs.tags.E2ETest

class GetUploadSummarySpec extends BaseSpec with UploadTestDataHelper {

  /** The E2E test for this will be simply upload a GiftAid claim and provide valid data then check the response is as
    * expected
    */
  Feature("Charities - Get Upload Summary API - E2E") {
    Scenario("Starting a OtherIncome claim and providing valid data to Upscan", E2ETest) {
      uploadTestData(
        authToken,
        claimId = GetUploadSummaryData.getIndividualClaimID(ValidationType.OtherIncome),
        reference = GetUploadSummaryData.getIndividualReference(ValidationType.OtherIncome),
        validationType = ValidationType.OtherIncome
      )

      When("We provide upscan with a valid payload that has a reference to the valid spreadsheet value")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.OtherIncome),
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadSummaryData.getIndividualReference(ValidationType.OtherIncome),
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.OtherIncome),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.OtherIncome)
        ),
        authToken
      )

      Then("We call the GetUploadSummary API")
      val response = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.OtherIncome),
        authToken
      )

      Then("We check the response of the returned Validated Data")
      checkCommonResponseBodies(
        response,
        ValidationType.OtherIncome,
        FileStatus.VALIDATED,
        isWrappedByUploadsArray = true,
        isRaceCondition = true
      )
    }
  }

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
      checkCommonResponseBodies(
        giftAidResponse,
        ValidationType.GiftAid,
        FileStatus.AWAITING_UPLOAD,
        isWrappedByUploadsArray = true
      )

      val otherIncomeResponse = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.OtherIncome),
        authToken
      )

      Then("We check OtherIncome response")
      checkCommonResponseBodies(
        otherIncomeResponse,
        ValidationType.OtherIncome,
        FileStatus.AWAITING_UPLOAD,
        isWrappedByUploadsArray = true
      )

      val communityBuildingsResponse = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.CommunityBuildings),
        authToken
      )

      Then("We check CommunityBuildings response")
      checkCommonResponseBodies(
        communityBuildingsResponse,
        ValidationType.CommunityBuildings,
        FileStatus.AWAITING_UPLOAD,
        isWrappedByUploadsArray = true
      )

      val connectedCharitiesResponse = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.ConnectedCharities),
        authToken
      )

      Then("We check ConnectedCharities response")
      checkCommonResponseBodies(
        connectedCharitiesResponse,
        ValidationType.ConnectedCharities,
        FileStatus.AWAITING_UPLOAD,
        isWrappedByUploadsArray = true
      )
    }
  }
}
