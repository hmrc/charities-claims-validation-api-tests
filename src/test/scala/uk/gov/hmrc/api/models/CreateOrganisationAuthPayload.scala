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

package uk.gov.hmrc.api.models

import play.api.libs.json.{Json, OFormat}

/** The Charities Service Frontend requires some extra identifiers for saving a user journey Value can be a unique value
  */
case class OrganisationIdentifiersDetails(
  key: String,
  value: String
)

object OrganisationIdentifiersDetails {
  implicit val format: OFormat[OrganisationIdentifiersDetails] = Json.format[OrganisationIdentifiersDetails]
}

/** The request body for auth login requires an enrollment nested object as described below */
case class OrganisationEnrolmentDetails(
  key: String,
  identifiers: Seq[OrganisationIdentifiersDetails],
  state: String
)

object OrganisationEnrolmentDetails {
  implicit val format: OFormat[OrganisationEnrolmentDetails] = Json.format[OrganisationEnrolmentDetails]
}

/** The main request body for auth login */
case class CreateOrganisationAuthPayload(
  credId: String,
  confidenceLevel: Int,
  credentialStrength: String,
  affinityGroup: String,
  enrolments: Seq[OrganisationEnrolmentDetails]
)

object CreateOrganisationAuthPayload {
  implicit val format: OFormat[CreateOrganisationAuthPayload] = Json.format[CreateOrganisationAuthPayload]
}
