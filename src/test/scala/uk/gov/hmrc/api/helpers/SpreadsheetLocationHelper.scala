package uk.gov.hmrc.api.helpers

import uk.gov.hmrc.api.data.globals.ValidationType

/** Simply used to either return a spreadsheet location or the spreadsheets filename */
object SpreadsheetLocationHelper {
  def getFileLocations(validationType: ValidationType, spreadsheetType: String = "GoodData"): String =
    validationType match {
      case ValidationType.GiftAid =>
        getClass.getClassLoader.getResource(s"spreadsheets/GiftAid/Gift-Aid-Schedule-Excel-$spreadsheetType.ods").toString
      case ValidationType.OtherIncome =>
        getClass.getClassLoader.getResource(s"spreadsheets/OtherIncome/other_income_schedule-$spreadsheetType.ods").toString
      case ValidationType.CommunityBuildings =>
        getClass.getClassLoader.getResource(s"spreadsheets/CommunityBuildings/community_buildings_excel-$spreadsheetType.ods").toString
      case ValidationType.ConnectedCharities =>
        getClass.getClassLoader.getResource(s"spreadsheets/ConnectedCharities/connected_charities_schedule__Excel_$spreadsheetType.ods").toString
    }

  def getFilename(validationType: ValidationType, spreadsheetType: String = "GoodData"): String =
    validationType match {
      case ValidationType.GiftAid => s"Gift-Aid-Schedule-Excel-$spreadsheetType.ods"
      case ValidationType.OtherIncome => s"other_income_schedule-$spreadsheetType.ods"
      case ValidationType.CommunityBuildings => s"community_buildings_excel-$spreadsheetType.ods"
      case ValidationType.ConnectedCharities => s"connected_charities_schedule__Excel_$spreadsheetType.ods"
    }
}
