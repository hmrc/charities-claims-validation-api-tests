package uk.gov.hmrc.api.models

import play.api.libs.json.{Json, OFormat}

/** The Request Body for Upscan Callback includes a nested object for "uploadDetails" as described below */
case class UploadDetailsUpscanCallback(
                                        fileName: String,
                                        fileMimeType: String,
                                        uploadTimestamp: String,
                                        checksum: String,
                                        size: Long
                                      )

object UploadDetailsUpscanCallback {
  implicit val format: OFormat[UploadDetailsUpscanCallback] = Json.format[UploadDetailsUpscanCallback]
}

/** The main JSON Payload for a SUCCESSFUL CreateUpscanCallback */
case class CreateUpscanCallbackSuccessfulPayload(
                                         reference: String,
                                         downloadUrl: String,
                                         fileStatus: String,
                                         uploadDetails: UploadDetailsUpscanCallback
                                       )

object CreateUpscanCallbackSuccessfulPayload {
  implicit val format: OFormat[CreateUpscanCallbackSuccessfulPayload] = Json.format[CreateUpscanCallbackSuccessfulPayload]
}

/** The Request Body for a failed Upscan Callback includes a nested object for "failureDetails"  */
case class FailureDetailsUpscanCallback(
                                         failureReason: String,
                                         messages: String
                                       )

object FailureDetailsUpscanCallback {
  implicit val format: OFormat[FailureDetailsUpscanCallback] = Json.format[FailureDetailsUpscanCallback]
}

/** The main JSON Payload for an UNSUCCESSFUL CreateUpscanCallback */
case class CreateUpscanCallbackFailedPayload(
                                              reference: String,
                                              fileStatus: String,
                                              failureDetails: FailureDetailsUpscanCallback
                                            )

object CreateUpscanCallbackFailedPayload {
  implicit val format: OFormat[CreateUpscanCallbackFailedPayload] = Json.format[CreateUpscanCallbackFailedPayload]
}