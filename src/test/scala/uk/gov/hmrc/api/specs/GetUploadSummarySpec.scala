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
    Scenario("Starting a GiftAid claim and providing valid data to Upscan", E2ETest) {
      uploadTestData(
        authToken,
        claimId = GetUploadSummaryData.getIndividualClaimID(ValidationType.GiftAid),
        reference = GetUploadSummaryData.getIndividualReference(ValidationType.GiftAid),
        validationType = ValidationType.GiftAid
      )

      When("We provide upscan with a valid payload that has a reference to the valid spreadsheet value")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.GiftAid),
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadSummaryData.getIndividualReference(ValidationType.GiftAid),
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.GiftAid),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.GiftAid)
        ),
        authToken
      )

      Then("We call the GetUploadSummary API")
      val response = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.GiftAid),
        authToken
      )

      Then("We check the response of the returned Validated Data")
      checkCommonResponseBodies(
        response,
        ValidationType.GiftAid,
        FileStatus.VALIDATED,
        isWrappedByUploadsArray = true,
        isRaceCondition = true
      )
    }

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

    Scenario("Starting a CommunityBuildings claim and providing valid data to Upscan", E2ETest) {
      uploadTestData(
        authToken,
        claimId = GetUploadSummaryData.getIndividualClaimID(ValidationType.CommunityBuildings),
        reference = GetUploadSummaryData.getIndividualReference(ValidationType.CommunityBuildings),
        validationType = ValidationType.CommunityBuildings
      )

      When("We provide upscan with a valid payload that has a reference to the valid spreadsheet value")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.CommunityBuildings),
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadSummaryData.getIndividualReference(ValidationType.CommunityBuildings),
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.CommunityBuildings),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.CommunityBuildings)
        ),
        authToken
      )

      Then("We call the GetUploadSummary API")
      val response = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.CommunityBuildings),
        authToken
      )

      Then("We check the response of the returned Validated Data")
      checkCommonResponseBodies(
        response,
        ValidationType.CommunityBuildings,
        FileStatus.VALIDATED,
        isWrappedByUploadsArray = true,
        isRaceCondition = true
      )
    }

    Scenario("Starting a ConnectedCharities claim and providing valid data to Upscan", E2ETest) {
      uploadTestData(
        authToken,
        claimId = GetUploadSummaryData.getIndividualClaimID(ValidationType.ConnectedCharities),
        reference = GetUploadSummaryData.getIndividualReference(ValidationType.ConnectedCharities),
        validationType = ValidationType.ConnectedCharities
      )

      When("We provide upscan with a valid payload that has a reference to the valid spreadsheet value")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.ConnectedCharities),
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadSummaryData.getIndividualReference(ValidationType.ConnectedCharities),
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.ConnectedCharities),
          fileName = SpreadsheetLocationHelper.getFilename(ValidationType.ConnectedCharities)
        ),
        authToken
      )

      Then("We call the GetUploadSummary API")
      val response = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.ConnectedCharities),
        authToken
      )

      Then("We check the response of the returned Validated Data")
      checkCommonResponseBodies(
        response,
        ValidationType.ConnectedCharities,
        FileStatus.VALIDATED,
        isWrappedByUploadsArray = true,
        isRaceCondition = true
      )
    }

// invalid scenarios

    Scenario("Starting a GiftAid claim and providing invalid data to Upscan", E2ETest) {
      uploadTestData(
        authToken,
        claimId = GetUploadSummaryData.getIndividualClaimID(ValidationType.GiftAid),
        reference = GetUploadSummaryData.getIndividualReference(ValidationType.GiftAid),
        validationType = ValidationType.GiftAid
      )

      When("We provide upscan with a valid payload that has a reference to the valid spreadsheet value")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.GiftAid),
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadSummaryData.getIndividualReference(ValidationType.GiftAid),
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.GiftAid),
          fileName = SpreadsheetLocationHelper.getFilenameInvalid(ValidationType.GiftAid)
        ),
        authToken
      )

      Then("We call the GetUploadSummary API")
      val response = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.GiftAid),
        authToken
      )

      Then("We check the response of the returned Validated Data")
      checkCommonResponseBodies(
        response,
        ValidationType.GiftAid,
        FileStatus.VALIDATION_FAILED,
        isWrappedByUploadsArray = true,
        isRaceCondition = true
      )
    }

    Scenario("Starting a OtherIncome claim and providing invalid data to Upscan", E2ETest) {
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
          fileName = SpreadsheetLocationHelper.getFilenameInvalid(ValidationType.OtherIncome)
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
        FileStatus.VALIDATION_FAILED,
        isWrappedByUploadsArray = true,
        isRaceCondition = true
      )
    }

    Scenario("Starting a CommunityBuildings claim and providing invalid data to Upscan", E2ETest) {
      uploadTestData(
        authToken,
        claimId = GetUploadSummaryData.getIndividualClaimID(ValidationType.CommunityBuildings),
        reference = GetUploadSummaryData.getIndividualReference(ValidationType.CommunityBuildings),
        validationType = ValidationType.CommunityBuildings
      )

      When("We provide upscan with a valid payload that has a reference to the valid spreadsheet value")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.CommunityBuildings),
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadSummaryData.getIndividualReference(ValidationType.CommunityBuildings),
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.CommunityBuildings),
          fileName = SpreadsheetLocationHelper.getFilenameInvalid(ValidationType.CommunityBuildings)
        ),
        authToken
      )

      Then("We call the GetUploadSummary API")
      val response = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.CommunityBuildings),
        authToken
      )

      Then("We check the response of the returned Validated Data")
      checkCommonResponseBodies(
        response,
        ValidationType.CommunityBuildings,
        FileStatus.VALIDATION_FAILED,
        isWrappedByUploadsArray = true,
        isRaceCondition = true
      )
    }

    Scenario("Starting a ConnectedCharities claim and providing invalid data to Upscan", E2ETest) {
      uploadTestData(
        authToken,
        claimId = GetUploadSummaryData.getIndividualClaimID(ValidationType.ConnectedCharities),
        reference = GetUploadSummaryData.getIndividualReference(ValidationType.ConnectedCharities),
        validationType = ValidationType.ConnectedCharities
      )

      When("We provide upscan with a valid payload that has a reference to the valid spreadsheet value")
      createUpscanService.postSuccessfulPayloadObject(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.ConnectedCharities),
        CreateUpscanCallbackData.getSuccessfulCreateUpscanCallbackPayloadWithReference(
          reference = GetUploadSummaryData.getIndividualReference(ValidationType.ConnectedCharities),
          downloadUrl = SpreadsheetLocationHelper.getFileLocations(ValidationType.ConnectedCharities),
          fileName = SpreadsheetLocationHelper.getFilenameInvalid(ValidationType.ConnectedCharities)
        ),
        authToken
      )

      Then("We call the GetUploadSummary API")
      val response = getUploadSummaryService.getUploadSummaryResults(
        GetUploadSummaryData.getIndividualClaimID(ValidationType.ConnectedCharities),
        authToken
      )

      Then("We check the response of the returned Validated Data")
      checkCommonResponseBodies(
        response,
        ValidationType.ConnectedCharities,
        FileStatus.VALIDATION_FAILED,
        isWrappedByUploadsArray = true,
        isRaceCondition = true
      )
    }
  }

  Feature("Charities - Get Upload Summary API - Testing all response variations") {
    Scenario("Testing the four variations of 'validationType' where one associate claimID is returned", E2ETest) {
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
