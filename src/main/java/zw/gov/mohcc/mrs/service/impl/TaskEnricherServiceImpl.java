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

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;
import zw.gov.mohcc.mrs.labonfhir.service.TaskEnricherService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEnricherServiceImpl implements TaskEnricherService {

    private final IGenericClient shrFhirClient;

    @Override
    public Optional<Bundle> getFirstDiagnosticReportBundle(Task task) {
        List<Task.TaskOutputComponent> output = task.getOutput();
        if (!output.isEmpty()) {
            Task.TaskOutputComponent outputRef = output.get(0);
            String diagnosticReportUuid = ((Reference) outputRef.getValue()).getReferenceElement()
                    .getIdPart();
            Bundle diagnosticReportBundle = shrFhirClient.search().forResource(DiagnosticReport.class)
                    .where(new TokenClientParam("_id").exactly().code(diagnosticReportUuid))
                    .include(DiagnosticReport.INCLUDE_RESULT).include(DiagnosticReport.INCLUDE_SUBJECT)
                    .returnBundle(Bundle.class).execute();
            return Optional.of(diagnosticReportBundle);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Bundle> getFirstServiceRequestBundle(Task task) {
        if (!task.getBasedOn().isEmpty()) {
            Reference reference = task.getBasedOn().get(0);
            String serviceRequestUuid = reference.getReferenceElement()
                    .getIdPart();
            Bundle servideRequestBundle = shrFhirClient.search().forResource(ServiceRequest.class)
                    .where(new TokenClientParam("_id").exactly().code(serviceRequestUuid))
                    .include(ServiceRequest.INCLUDE_SPECIMEN).include(DiagnosticReport.INCLUDE_SUBJECT)
                    .returnBundle(Bundle.class).execute();
            return Optional.of(servideRequestBundle);
        }
        return Optional.empty();
    }
}
