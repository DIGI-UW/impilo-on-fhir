package zw.gov.mohcc.mrs.service.impl;

/*-
 * ========================LICENSE_START=================================
 * Medical Records System (MRS)
 * ---------------------------------------------------------------------
 * Copyright (C) 2017 - 2025 Ministry of Health and Child Care (Zimbabwe)
 * ---------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ========================LICENSE_END=================================
 */

import ca.uhn.fhir.context.FhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import zw.gov.mohcc.mrs.labonfhir.service.FhirPushService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FhirPushServiceImpl implements FhirPushService {



    private static final String FORWARD_SLASH = "/";

    @Value("${application.hie.client-registry.url}")
    private String crUrl;
    @Value("${application.hie.username}")
    private String hieUsername;
    @Value("${application.hie.password}")
    private String hiePassword;

    public ResponseEntity<String> postPatientToCr(Patient patient){
        return this.postFhirResource(patient, getPatientUrl(), hieUsername, hiePassword);
    }


    protected ResponseEntity<String> postFhirResource(IBaseResource theResource, String url, String username, String password) {
        String theResourceInString = FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(theResource);

        RestTemplate restTemplate = new RestTemplate();
        String plainCreds = username + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.add("Content-Type", "application/fhir+json; charset=UTF-8");

        HttpEntity<String> request = new HttpEntity<>(theResourceInString, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    private String getPatientUrl() {
        String patientUrl = crUrl.trim();
        if (!patientUrl.endsWith(FORWARD_SLASH)) {
            patientUrl += FORWARD_SLASH;
        }
        return patientUrl + "Patient";
    }

}
