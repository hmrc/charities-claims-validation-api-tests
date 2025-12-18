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

package uk.gov.hmrc.api.utils

import uk.gov.hmrc.api.models.{CreateOrganisationAuthPayload, OrganisationEnrolmentDetails, OrganisationIdentifiersDetails}

// TODO: CredID should be a random number
class OrganisationAuthData {
  def getOrganisationIdentifierPayload: OrganisationIdentifiersDetails = OrganisationIdentifiersDetails(
    key = "CHARID",
    value = "123456789"
  )

  def getOrganisationEnrolmentPayload: OrganisationEnrolmentDetails = OrganisationEnrolmentDetails(
    key = "HMRC-CHAR-ORG",
    identifiers = Seq(getOrganisationIdentifierPayload),
    state = "Activated"
  )

  def getOrganisationAuthPayload: CreateOrganisationAuthPayload = CreateOrganisationAuthPayload(
    credId = "123456789",
    confidenceLevel = 200,
    credentialStrength = "strong",
    affinityGroup = "Organisation",
    enrolments = Seq(getOrganisationEnrolmentPayload)
  )
}
