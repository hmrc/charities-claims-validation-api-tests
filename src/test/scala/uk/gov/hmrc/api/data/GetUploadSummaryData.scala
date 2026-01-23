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
}
