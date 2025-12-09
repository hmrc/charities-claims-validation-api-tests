package uk.gov.hmrc.api.models

import play.api.libs.json.{Json, OFormat}

/** Case and Object for a successful CreateUploadTracking Payload */
case class CreateUploadTrackingPayload (
                                         reference: String,
                                         validationType: String,
                                         uploadUrl: String,
                                         initiateTimestamp: String
                                       )

object CreateUploadTrackingPayload {
  implicit val format: OFormat[CreateUploadTrackingPayload] = Json.format[CreateUploadTrackingPayload]
}