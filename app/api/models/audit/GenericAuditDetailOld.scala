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

package api.models.audit

import api.controllers.{AuditHandlerOld, RequestContext}
import api.models.auth.UserDetails
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, OWrites}

case class GenericAuditDetailOld(userType: String,
                                 agentReferenceNumber: Option[String],
                                 params: Map[String, String],
                                 request: Option[JsValue],
                                 `X-CorrelationId`: String,
                                 response: AuditResponse)

object GenericAuditDetailOld {

  implicit val writes: OWrites[GenericAuditDetailOld] = (
    (JsPath \ "userType").write[String] and
      (JsPath \ "agentReferenceNumber").writeNullable[String] and
      JsPath.write[Map[String, String]] and
      (JsPath \ "request").writeNullable[JsValue] and
      (JsPath \ "X-CorrelationId").write[String] and
      (JsPath \ "response").write[AuditResponse]
  )(unlift(GenericAuditDetailOld.unapply))

  def apply(userDetails: UserDetails,
            params: Map[String, String],
            request: Option[JsValue],
            `X-CorrelationId`: String,
            response: AuditResponse): GenericAuditDetailOld = {

    GenericAuditDetailOld(
      userType = userDetails.userType,
      agentReferenceNumber = userDetails.agentReferenceNumber,
      params = params,
      request = request,
      `X-CorrelationId` = `X-CorrelationId`,
      response = response
    )
  }

  def auditDetailCreator(params: Map[String, String]): AuditHandlerOld.AuditDetailCreator[GenericAuditDetailOld] =
    new AuditHandlerOld.AuditDetailCreator[GenericAuditDetailOld] {

      def createAuditDetail(userDetails: UserDetails, request: Option[JsValue], response: AuditResponse)(implicit
          ctx: RequestContext): GenericAuditDetailOld =
        GenericAuditDetailOld(
          userDetails = userDetails,
          params = params,
          request = request,
          `X-CorrelationId` = ctx.correlationId,
          response = response
        )

    }

}
