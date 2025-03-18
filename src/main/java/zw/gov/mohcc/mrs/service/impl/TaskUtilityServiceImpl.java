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
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;
import zw.gov.mohcc.mrs.fhir.api.FhirUtilityService;
import zw.gov.mohcc.mrs.labonfhir.service.BundleUtilityService;
import zw.gov.mohcc.mrs.labonfhir.service.TaskEnricherService;
import zw.gov.mohcc.mrs.labonfhir.service.TaskUtilityService;
import zw.gov.mohcc.mrs.laboratory.data.LaboratoryRequestOrder;
import zw.gov.mohcc.mrs.laboratory.data.repository.LaboratoryRequestOrderRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskUtilityServiceImpl implements TaskUtilityService {


    private final BundleUtilityService bundleUtilityService;
    private final FhirUtilityService fhirUtilityService;
    private final LaboratoryRequestOrderRepository laboratoryRequestOrderRepository;
    private final TaskEnricherService taskEnricherService;


    public Optional<Observation> getFirstObservation(Bundle diagnosticReportBundle) {
        return bundleUtilityService.getFirstResource(ResourceType.Observation, diagnosticReportBundle)
                .map(r -> (Observation) r);
    }

    public Optional<DiagnosticReport> getFirstDiagnosticReport(Bundle diagnosticReportBundle) {
        return bundleUtilityService.getFirstResource(ResourceType.DiagnosticReport, diagnosticReportBundle)
                .map(r -> (DiagnosticReport) r);
    }

    public Optional<String> getFirstResultValue(Bundle diagnosticReportBundle) {
        return getFirstObservation(diagnosticReportBundle).map(this::getResult);
    }

    public Optional<String> getFirstResultValue(Task task) {
        Bundle diagnosticReportBundle = taskEnricherService.getFirstDiagnosticReportBundle(task).orElse(null);
        if (diagnosticReportBundle != null) {
            return getFirstResultValue(diagnosticReportBundle);
        }
        return Optional.empty();
    }


    @Override
    public Optional<Specimen> getFirstSpecimen(Bundle servideRequestBundle) {
        return bundleUtilityService.getFirstResource(ResourceType.Specimen, servideRequestBundle)
                .map(r -> (Specimen) r);
    }

    @Override
    public Optional<ServiceRequest> getFirstServiceRequest(Bundle serviceRequestBundle) {
        return bundleUtilityService.getFirstResource(ResourceType.ServiceRequest, serviceRequestBundle)
                .map(r -> (ServiceRequest) r);
    }

    public Optional<Specimen> getFirstSpecimen(Task task) {
        Bundle serviceRequestBundle = taskEnricherService.getFirstServiceRequestBundle(task).orElse(null);
        if (serviceRequestBundle != null) {
            return this.getFirstSpecimen(serviceRequestBundle);
        }
        return Optional.empty();
    }

    public Optional<ServiceRequest> getFirstServiceRequest(Task task) {
        Bundle serviceRequestBundle = taskEnricherService.getFirstServiceRequestBundle(task).orElse(null);
        if (serviceRequestBundle != null) {
            return this.getFirstServiceRequest(serviceRequestBundle);
        }
        return Optional.empty();
    }

    public Optional<LaboratoryRequestOrder> toLaboratoryRequestOrder(Task task) {
        String laboratoryRequestOrderId = fhirUtilityService.getLaboratoryRequestOrderId(task);
        if (laboratoryRequestOrderId != null) {
            LaboratoryRequestOrder laboratoryRequestOrder = laboratoryRequestOrderRepository
                    .findOne(laboratoryRequestOrderId);
            if (laboratoryRequestOrder != null) {
                return Optional.of(laboratoryRequestOrder);
            }
        }
        return Optional.empty();
    }

    private String getResult(Observation observation) {
        if (!observation.hasValue()) {
            return null;
        }

        if (observation.hasValueQuantity()) {
            return observation.getValueQuantity().getValue().toString();
        } else if (observation.hasValueStringType()) {
            return observation.getValueStringType().getValue();
        } else if (observation.hasValueIntegerType()) {
            return observation.getValueIntegerType().getValue().toString();
        } else if (observation.hasValueBooleanType()) {
            return observation.getValueBooleanType().getValue().toString();
        } else if (observation.hasValuePeriod()) {
            return observation.getValuePeriod().getStart() + "-" + observation.getValuePeriod().getEnd();
        } else if (observation.hasValueRatio()) {
            return observation.getValueRatio().getNumerator().getValue() + ":" + observation.getValueRatio().getDenominator().getValue();
        } else if (observation.hasValueDateTimeType()) {
            return observation.getValueDateTimeType().getValue().toString();
        } else if (observation.hasValueTimeType()) {
            return observation.getValueTimeType().getValue().toString();
        } else if (observation.hasCode()) {
            return observation.getCode().getCoding().stream().map(Coding::getCode).findFirst().orElse(null);
        }else {
            throw new IllegalArgumentException(observation.getValue().fhirType() + " not supported");
        }
    }


}
