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

package api.hateoas

import api.models.hateoas.Link
import api.models.hateoas.Method._
import api.models.hateoas.RelType._
import config.AppConfig

//noinspection ScalaStyle
trait HateoasLinks {

  private def insurancePoliciesUri(appConfig: AppConfig, nino: String, taxYear: String) =
    s"/${appConfig.apiGatewayContext}/$nino/$taxYear"

  // Insurance Policies Income
  def amendInsurancePolicies(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = insurancePoliciesUri(appConfig, nino, taxYear),
      method = PUT,
      rel = AMEND_INSURANCE_POLICIES_INCOME
    )

  def retrieveInsurancePolicies(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = insurancePoliciesUri(appConfig, nino, taxYear),
      method = GET,
      rel = SELF
    )

  def deleteInsurancePolicies(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = insurancePoliciesUri(appConfig, nino, taxYear),
      method = DELETE,
      rel = DELETE_INSURANCE_POLICIES_INCOME
    )
}
