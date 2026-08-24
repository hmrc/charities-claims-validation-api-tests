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

package uk.gov.hmrc.api

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.data.globals.{FailureReason, FileStatus, ValidationType}
import uk.gov.hmrc.api.data.GetUploadResultData
import uk.gov.hmrc.api.helpers.AuthHelper
import uk.gov.hmrc.api.service.*

trait BaseSpec extends AnyFeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterEach {
  val authHelper: AuthHelper                                   = new AuthHelper
  val authService: AuthService                                 = new AuthService
  val createUploadTrackingService: CreateUploadTrackingService = new CreateUploadTrackingService
  val createUpscanService: CreateUpscanCallbackService         = new CreateUpscanCallbackService
  val updateUploadStatusService: UpdateUploadStatusService     = new UpdateUploadStatusService
  val getUploadResultService: GetUploadResultService           = new GetUploadResultService
  val deleteSingleUploadService                                = new DeleteSingleUploadService
  val deleteUploadsClaimService                                = new DeleteUploadsClaimService
  val getUploadSummaryService: GetUploadSummaryService         = new GetUploadSummaryService

  authHelper.fetchAuthBearerToken()

  protected def authToken: String = {
    When("We request a bearer token")
    val token = authHelper.bearerToken
    token shouldNot include("No Auth Token Found")
    Then("Then token is valid")
    token
  }

  def checkGenericResponseBodyAndStatusCode(
    response: StandaloneWSResponse,
    responseCode: Int,
    responseSuccess: Boolean
  ): Unit = {
    Then(s"A $responseCode status code should be returned from generic response")
    response.status shouldBe responseCode

    And(s"The response body is { success: $responseSuccess }")
    (Json.parse(response.body) \ "success").as[Boolean] shouldBe responseSuccess
  }

  def checkStatusCode(response: StandaloneWSResponse, statusCode: Int): Unit = {
    Then(s"A $statusCode code should be returned from checkStatusCode")
    response.status shouldBe statusCode
  }

  private def extractResponseFromPotentialUploadsArray(json: JsValue, field: String): Seq[String] =
    (json \ field).asOpt[String].toSeq ++ (json \ "uploads")
      .asOpt[Seq[JsValue]]
      .toSeq
      .flatten
      .flatMap(eachClaimInsideUploads => (eachClaimInsideUploads \ field).asOpt[String])

  /** A few of the APIs all return response bodies that share data, breaking out the common functionality here to keep
    * code DRY and spec files more condensed Note: we generally don't care about failureReason as this will only occur
    * during failure at Upscan so we default to success and presume a healthy response, if this value is ever
    * over-ridden then we expect a failure reason, and we can pass this information off to another method
    */
  def checkCommonResponseBodies(
    response: StandaloneWSResponse,
    validationType: ValidationType,
    fileStatus: FileStatus,
    failureReason: FailureReason = FailureReason.SUCCESS,
    isWrappedByUploadsArray: Boolean = false,
    isRaceCondition: Boolean = false
  ): Unit = {
    Then(s"The response body within checkCommonResponseBody is what we expect for a claim with fileStatus $fileStatus")
    extractResponseFromPotentialUploadsArray(Json.parse(response.body), "reference")      should not be empty
    extractResponseFromPotentialUploadsArray(Json.parse(response.body), "validationType") should contain(
      validationType.toString
    )

    if (!isRaceCondition) {
      extractResponseFromPotentialUploadsArray(Json.parse(response.body), "fileStatus") should contain(
        fileStatus.toString
      )
    }

    if (fileStatus == FileStatus.AWAITING_UPLOAD) {
      awaitingUploadExtraBodyInfo(response, isWrappedByUploadsArray)
    }

    if (fileStatus == FileStatus.VERIFICATION_FAILED) {
      failureDetailsExtraBodyInfo(response, failureReason)
    }
  }

  /** Claims that have fileStatus = AWAITING_UPLOAD will have additional fields in the response body */
  private def awaitingUploadExtraBodyInfo(
    response: StandaloneWSResponse,
    isWrappedByUploadsArray: Boolean
  ): Unit = {
    And("FileStatus is 'AWAITING_UPLOAD' so we have more data to check")
    if (!isWrappedByUploadsArray) {

      /** Default behavior as it should always be included, unless coming from GetUploadResult which wraps our response
        * inside an Uploads[] if this is the case we don't actually initiateTimestamp in the response so we just ignore
        */
      (Json.parse(response.body) \ "initiateTimestamp").asOpt[String] shouldBe defined
    }
    extractResponseFromPotentialUploadsArray(Json.parse(response.body), "uploadUrl") should not be empty
  }

  /** Claims that have failed at Upscan will have some failure fields in the response body */
  private def failureDetailsExtraBodyInfo(response: StandaloneWSResponse, failureReason: FailureReason): Unit = {
    And("We have a failure so checking the response body for extra details")
    (Json.parse(response.body) \ "failureDetails" \ "failureReason").as[String] shouldEqual failureReason.toString
    (Json.parse(response.body) \ "failureDetails" \ "message").as[String]       shouldEqual failureReason.getFailureMessage
  }

  /** Check for error usually caused by incorrect claimID and / or reference */
  def checkErrorResponse(response: StandaloneWSResponse, statusCode: Int = 404): Unit = {
    And("We are checking common error response bodies")
    (Json.parse(response.body) \ "error").as[String] shouldEqual "CLAIM_REFERENCE_DOES_NOT_EXIST"
    (Json.parse(response.body) \ "message").as[String]    should include("There is no reference")
    And(s"Response code for error should be $statusCode")
    checkStatusCode(response, statusCode)
  }

  /** Valid and Invalid Data response(s) both share a chunk of common responses / values, populating here to keep code
    * DRY and declutter the scenario(s).
    */
  def checkValidAndInvalidDataResponseBody(
    response: StandaloneWSResponse,
    validationType: ValidationType,
    fileStatus: FileStatus
  ): Unit = {
    val reference  = GetUploadResultData.getCorrectReference(validationType, fileStatus)
    val typeOfData = GetUploadResultData.getCorrectJsonBodyFieldName(validationType)

    Then(s"The response for $validationType and data is $fileStatus is what we expect")
    (Json.parse(response.body) \ "reference").as[String]      shouldEqual reference
    (Json.parse(response.body) \ "validationType").as[String] shouldEqual validationType.toString
    (Json.parse(response.body) \ "fileStatus")
      .as[String]                                                  should (be(FileStatus.VALIDATED.toString) or be(FileStatus.VALIDATION_FAILED.toString))
    (Json.parse(response.body) \ typeOfData).asOpt[String] shouldBe defined

    if (fileStatus == FileStatus.VALIDATION_FAILED) {
      And("We have invalid data so an additional check for errors")
      (Json.parse(response.body) \ "errors").asOpt[String] shouldBe defined
    }

    And("Response code should be 200")
    checkStatusCode(response, 200)
  }
}
