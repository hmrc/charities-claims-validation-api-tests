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

package uk.gov.hmrc.api.helpers

import uk.gov.hmrc.api.data.globals.ValidationType

/** Simply used to either return a spreadsheet location or the spreadsheets filename */
object SpreadsheetLocationHelper {
  def getFileLocations(validationType: ValidationType, spreadsheetType: String = "GoodData"): String =
    validationType match {
      case ValidationType.GiftAid            =>
        getClass.getClassLoader
          .getResource(s"spreadsheets/giftAid/gift-aid-schedule-$spreadsheetType.ods")
          .toString
      case ValidationType.OtherIncome        =>
        getClass.getClassLoader
          .getResource(s"spreadsheets/otherIncome/other_income_schedule-$spreadsheetType.ods")
          .toString
      case ValidationType.CommunityBuildings =>
        getClass.getClassLoader
          .getResource(s"spreadsheets/communityBuildings/community_buildings_excel-$spreadsheetType.ods")
          .toString
      case ValidationType.ConnectedCharities =>
        getClass.getClassLoader
          .getResource(s"spreadsheets/connectedCharities/connected_charities_schedule__Excel_$spreadsheetType.ods")
          .toString
    }

  def getFilename(validationType: ValidationType, spreadsheetType: String = "GoodData"): String =
    validationType match {
      case ValidationType.GiftAid            => s"gift-aid-schedule-$spreadsheetType.ods"
      case ValidationType.OtherIncome        => s"other_income_schedule-$spreadsheetType.ods"
      case ValidationType.CommunityBuildings => s"community_buildings_excel-$spreadsheetType.ods"
      case ValidationType.ConnectedCharities => s"connected_charities_schedule__Excel_$spreadsheetType.ods"
    }
}
