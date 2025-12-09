package uk.gov.hmrc.api.utils

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import uk.gov.hmrc.api.service.{CreateDeleteUploadService, CreateUploadTrackingService}

trait BaseSpec
  extends AnyFeatureSpec
  with GivenWhenThen
  with Matchers
  with BeforeAndAfterEach {
  val createUploadTrackingStub: CreateUploadTrackingService = new CreateUploadTrackingService
  val createSingleDeleteStub: CreateDeleteUploadService = new CreateDeleteUploadService
}
