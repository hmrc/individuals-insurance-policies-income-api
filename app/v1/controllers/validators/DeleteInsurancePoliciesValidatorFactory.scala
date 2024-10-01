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

package v1.controllers.validators

import cats.data.Validated
import cats.implicits._
import config.InsuranceAppConfig
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinimum}
import shared.models.errors.MtdError
import v1.models.request.deleteInsurancePolicies.DeleteInsurancePoliciesRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class DeleteInsurancePoliciesValidatorFactory @Inject()(appConfig: InsuranceAppConfig) {

  private lazy val minimumTaxYear = appConfig.minimumPermittedTaxYear
  private lazy val resolveTaxYear = ResolveTaxYearMinimum(minimumTaxYear)

  def validator(nino: String, taxYear: String): Validator[DeleteInsurancePoliciesRequestData] =
    new Validator[DeleteInsurancePoliciesRequestData] {

      def validate: Validated[Seq[MtdError], DeleteInsurancePoliciesRequestData] =
        (
          ResolveNino(nino),
          resolveTaxYear(taxYear)
        ).mapN(DeleteInsurancePoliciesRequestData)

    }

}
