package uk.gov.hmrc.api.utils

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import uk.gov.hmrc.api.service.CreateUploadTrackingService

trait BaseSpec
  extends AnyFeatureSpec
  with GivenWhenThen
  with Matchers
  with BeforeAndAfterEach {
  val createUploadTrackingStub: CreateUploadTrackingService = new CreateUploadTrackingService
}
