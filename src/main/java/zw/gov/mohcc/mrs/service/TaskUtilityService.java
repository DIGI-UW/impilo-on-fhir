package zw.gov.mohcc.mrs.service;

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

import org.hl7.fhir.r4.model.*;
import zw.gov.mohcc.mrs.laboratory.data.LaboratoryRequestOrder;

import java.util.Optional;

public interface TaskUtilityService {

    Optional<Specimen> getFirstSpecimen(Bundle servideRequestBundle);

    Optional<ServiceRequest> getFirstServiceRequest(Bundle servideRequestBundle);

    Optional<Observation> getFirstObservation(Bundle diagnosticReportBundle);

    Optional<LaboratoryRequestOrder> toLaboratoryRequestOrder(Task task);

    Optional<Specimen> getFirstSpecimen(Task task);

    Optional<ServiceRequest> getFirstServiceRequest(Task task);

    Optional<String> getFirstResultValue(Bundle diagnosticReportBundle);

    Optional<String> getFirstResultValue(Task task);

    Optional<DiagnosticReport> getFirstDiagnosticReport(Bundle diagnosticReportBundle);


}
