package uk.gov.hmrc.api.models

import play.api.libs.json.{Json, OFormat}

case class CreateUploadTrackingPayload (
                                         reference: String,
                                         validationType: String,
                                         uploadUrl: String,
                                         initiateTimestamp: String
                                       )

object CreateUploadTrackingPayload {
  implicit val format: OFormat[CreateUploadTrackingPayload] = Json.format[CreateUploadTrackingPayload]
}