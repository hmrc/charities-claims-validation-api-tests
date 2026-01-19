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

package uk.gov.hmrc.api.data.globals

/** Upscan call back can provide three known responses for a VALIDATION_FAILED state, these are
  *   - QUARANTINE, REJECTED, UNKNOWN. Success is used for a method in BaseSpec for us to use as a default value, i.e.,
  *     don't get the failureMessage
  */
enum FailureReason:
  case QUARANTINE, REJECTED, UNKNOWN, SUCCESS

  def getFailureMessage: String = this match {
    case QUARANTINE => "e.g. This file has a virus"
    case REJECTED   => "MIME type $mime is not allowed for service $service-name"
    case UNKNOWN    => "Something unknown happened"
    case SUCCESS    => ""
  }
