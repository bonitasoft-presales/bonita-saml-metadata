/**
 * Copyright (C) 2019 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.saml.metadata

import lombok.Data

@Data
class SamlModel {
    String binding
    URL assertionEndPoint
    URL logoutEndpoint

    boolean wantAuthnRequestSigned

    String entityId
    String nameIdPolicyFormat
    String signingCerts

    URL idpSingleSignOnServiceBindingUrl

    SamlModel(String xmlContent, String endPoint) {


        def rootNode = new XmlSlurper().parseText(xmlContent)

        def sp = rootNode.SP
        this.entityId = sp.@entityID
        this.assertionEndPoint = new URL("${endPoint}/saml")
        this.logoutEndpoint = new URL("${endPoint}/samlLogout")

        this.idpSingleSignOnServiceBindingUrl = new URL(sp.IDP.SingleSignOnService.@bindingUrl as String)


        this.nameIdPolicyFormat = sp.PrincipalNameMapping.@attribute
    }
}
