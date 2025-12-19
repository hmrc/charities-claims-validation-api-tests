package uk.gov.hmrc.api.models

import play.api.libs.json.{Json, OFormat}

case class UpdateUploadStatusPayload (
  fileStatus: String
                                     )

object UpdateUploadStatusPayload {
  implicit val format: OFormat[UpdateUploadStatusPayload] = Json.format[UpdateUploadStatusPayload]
}