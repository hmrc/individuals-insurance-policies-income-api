/*
 * Copyright 2023 HM Revenue & Customs
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

package v2.services

import common.errors.RuleOutsideAmendmentWindowError
import shared.controllers.EndpointLogContext
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v2.connectors.MockDeleteInsurancePoliciesConnector
import v2.models.request.deleteInsurancePolicies.DeleteInsurancePoliciesRequestData

import scala.concurrent.Future

class DeleteInsurancePoliciesServiceSpec extends ServiceSpec {

  private val nino    = "AA112233A"
  private val taxYear = "2019-20"

  private val requestData = DeleteInsurancePoliciesRequestData(Nino(nino), TaxYear.fromMtd(taxYear))

  trait Test extends MockDeleteInsurancePoliciesConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service: DeleteInsurancePoliciesService =
      new DeleteInsurancePoliciesService(connector = mockDeleteInsurancePoliciesConnector)

  }

  "DeleteInsurancePoliciesService" when {
    "delete" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockDeleteInsurancePoliciesConnector
          .deleteInsurancePoliciesConnector(requestData)
          .returns(Future.successful(outcome))

        await(service.delete(requestData)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockDeleteInsurancePoliciesConnector
              .deleteInsurancePoliciesConnector(requestData)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.delete(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
          "INVALID_TAX_YEAR"          -> TaxYearFormatError,
          "INVALID_CORRELATIONID"     -> InternalError,
          "NO_DATA_FOUND"             -> NotFoundError,
          "SERVER_ERROR"              -> InternalError,
          "SERVICE_UNAVAILABLE"       -> InternalError
        )

        val extraTysErrors = List(
          "INVALID_CORRELATION_ID"    -> InternalError,
          "TAX_YEAR_NOT_SUPPORTED"    -> RuleTaxYearNotSupportedError,
          "OUTSIDE_AMENDMENT_WINDOW"  -> RuleOutsideAmendmentWindowError
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }
  }

}
