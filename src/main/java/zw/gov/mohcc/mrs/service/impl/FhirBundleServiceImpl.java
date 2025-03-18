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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;
import zw.gov.mohcc.mrs.labonfhir.service.FhirBundleService;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class FhirBundleServiceImpl implements FhirBundleService {

    @Override
    public Bundle toTransactionBundle(Collection<Resource> resources){
        Bundle transactionBundle = new Bundle();
        transactionBundle.setType(Bundle.BundleType.TRANSACTION);
        for (Resource resource : resources) {
            Bundle.BundleEntryComponent component = transactionBundle.addEntry();
            component.setResource(resource);
            component.getRequest().setUrl(getRequestUrl(resource))
                    .setMethod(Bundle.HTTPVerb.PUT);
        }
        return transactionBundle;
    }

    private String getRequestUrl(Resource resource) {
        return resource.fhirType() + "/" + resource.getIdElement().getIdPart();
    }

}
