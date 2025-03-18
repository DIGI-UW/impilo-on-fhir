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
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import zw.gov.mohcc.mrs.commons.data.reception.Person;
import zw.gov.mohcc.mrs.commons.data.reception.repository.PersonRepository;
import zw.gov.mohcc.mrs.facility.domain.Facility;
import zw.gov.mohcc.mrs.facility.repository.FacilityRepository;
import zw.gov.mohcc.mrs.fhir.api.FhirUtilityService;
import zw.gov.mohcc.mrs.fhir.api.ShallowFhirPatientService;
import zw.gov.mohcc.mrs.fhir.api.translators.*;
import zw.gov.mohcc.mrs.history.data.PersonInvestigation;
import zw.gov.mohcc.mrs.labonfhir.processor.StatusTaskProcessor;
import zw.gov.mohcc.mrs.labonfhir.service.FhirBundleService;
import zw.gov.mohcc.mrs.labonfhir.service.FhirPushService;
import zw.gov.mohcc.mrs.labonfhir.service.LabOnFhirPushService;
import zw.gov.mohcc.mrs.labonfhir.util.PatientPostResponse;
import zw.gov.mohcc.mrs.laboratory.data.LaboratoryInvestigation;
import zw.gov.mohcc.mrs.laboratory.data.LaboratoryRequestOrder;
import zw.gov.mohcc.mrs.laboratory.data.repository.LaboratoryRequestOrderRepository;
import zw.gov.mohcc.mrs.provider.domain.Laboratory;
import zw.gov.mohcc.mrs.provider.repository.LaboratoryRepository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LabOnFhirPushServiceImpl implements LabOnFhirPushService {

    private final IGenericClient shrFhirClient;

    private final PatientTranslator patientTranslator;
    private final LocationTranslator<Facility> facilityLocationTranslator;
    private final LocationTranslator<Laboratory> laboratoryLocationTranslator;

    private final EncounterTranslator<LaboratoryRequestOrder> encounterTranslator;
    private final ServiceRequestTranslator<LaboratoryRequestOrder> serviceRequestTranslator;
    private final SpecimenTranslator<LaboratoryRequestOrder> specimenTranslator;
    private final TaskTranslator<LaboratoryRequestOrder> taskTranslator;

    private final PersonRepository personRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final FacilityRepository facilityRepository;

    private final LaboratoryRequestOrderRepository laboratoryRequestOrderRepository;

    private final FhirUtilityService fhirUtilityService;
    private final OrganizationTranslator<Facility> facilityOrganizationTranslator;

    private final FhirBundleService fhirBundleService;

    private final StatusTaskProcessor statusTaskProcessor;

    private final ShallowFhirPatientService shallowFhirPatientService;

    private final FhirPushService fhirPushService;


    @Override
    public void saveInFhirServer(@Nonnull String laboratoryRequestOrderId) {
        LaboratoryRequestOrder impiloLaboratoryRequestOrder = laboratoryRequestOrderRepository.findOne(laboratoryRequestOrderId);
        if (impiloLaboratoryRequestOrder != null) {
            saveInFhirServer(impiloLaboratoryRequestOrder);
            statusTaskProcessor.process(impiloLaboratoryRequestOrder, "SUBMITTED");
        }
    }

    @Override
    public void saveInFhirServer(@Nonnull LaboratoryRequestOrder impiloLaboratoryRequestOrder) {
        if (isInSHR(impiloLaboratoryRequestOrder.getLaboratoryRequestOrderId())) {
            return;
        }

        LaboratoryInvestigation laboratoryInvestigation = impiloLaboratoryRequestOrder.getLaboratoryInvestigation();
        PersonInvestigation personInvestigation = laboratoryInvestigation.getPersonInvestigation();

        if (impiloLaboratoryRequestOrder.getLaboratory() == null) {
            return;
        }
        String laboratoryId = impiloLaboratoryRequestOrder.getLaboratory().getId();
        String facilityId = impiloLaboratoryRequestOrder.getFacility().getId();
        String personId = personInvestigation.getPersonId();
        String siteId = fhirUtilityService.getSiteId();


        Person impiloPerson = personRepository.findOne(personId);
        Facility impiloFacility = facilityRepository.findOne(facilityId);
        Laboratory impiloLaboratory = laboratoryRepository.findOne(laboratoryId);
        Facility impiloManagingFacility = facilityRepository.findOne(siteId);


        Location fhirLabLocation = laboratoryLocationTranslator.toFhirResource(impiloLaboratory);
        Location fhirFacilityLocation = facilityLocationTranslator.toFhirResource(impiloFacility);
        Organization fhirManagingOrganization = facilityOrganizationTranslator.toFhirResource(impiloManagingFacility);
        Patient fhirPatient = patientTranslator.toFhirResource(impiloPerson);
        Encounter fhirEncounter = encounterTranslator.toFhirResource(impiloLaboratoryRequestOrder);
        Specimen fhirSpecimen = specimenTranslator.toFhirResource(impiloLaboratoryRequestOrder);
        ServiceRequest fhirServiceRequest = serviceRequestTranslator.toFhirResource(impiloLaboratoryRequestOrder);
        Task fhirTask = taskTranslator.toFhirResource(impiloLaboratoryRequestOrder);


        PatientPostResponse patientPostResponse = savePatientToCR(fhirPatient);
        if (patientPostResponse.getCruid() != null) {
            fhirPatient.addIdentifier().setSystem("urn:opencr:cruid").setValue(patientPostResponse.getCruid());
        }

        Patient fhirShallowPatient = shallowFhirPatientService.toShallowPatient(fhirPatient);

        List<Resource> fhirResources = new ArrayList<>();

        fhirResources.add(fhirLabLocation);
        fhirResources.add(fhirFacilityLocation);
        fhirResources.add(fhirManagingOrganization);
        fhirResources.add(fhirShallowPatient);
        fhirResources.add(fhirEncounter);
        fhirResources.add(fhirSpecimen);
        fhirResources.add(fhirServiceRequest);
        fhirResources.add(fhirTask);

        Bundle orderBundle = fhirBundleService.toTransactionBundle(fhirResources);

        saveOrderBundleToSHR(orderBundle);


    }

    private void saveOrderBundleToSHR(Bundle orderBundle) {
        shrFhirClient.transaction().withBundle(orderBundle).execute();
    }

    private PatientPostResponse savePatientToCR(Patient fhirPatient) {
        ResponseEntity<String> response = fhirPushService.postPatientToCr(fhirPatient);
        if (response != null) {
            String locationcruid = response.getHeaders().getFirst("locationcruid");
            String location = response.getHeaders().getFirst("location");
            String cruid = locationcruid != null ? locationcruid.split("/")[1] : null;
            String uid = location != null ? location.split("/")[1] : null;
            return new PatientPostResponse(uid, cruid);
        } else {
            return new PatientPostResponse();
        }
    }

    private boolean isInSHR(String taskId) {
        try {
            shrFhirClient.read()
                    .resource(Task.class)
                    .withId(taskId)
                    .execute();
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }


}
