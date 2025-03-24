package zw.gov.mohcc.mrs.pharmacy.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.gov.mohcc.mrs.commons.data.view.pharmacy.DispenseView;
import zw.gov.mohcc.mrs.pharmacy.data.Dispense;
import zw.gov.mohcc.mrs.pharmacy.data.MedicineBatchIssue;
import zw.gov.mohcc.mrs.pharmacy.data.Prescription;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

/**
 * Spring Data JPA repository for the Dispense entity.
 */
@SuppressWarnings("unused")
public interface DispenseRepository extends JpaRepository<Dispense, String> {

	@Query("select sum(m.quantity) from Dispense m where m.batchIssue=:batchIssue")
	Double findTotalQuantityDispensed(@Param("batchIssue") MedicineBatchIssue batchIssue);

	Iterable<Dispense> findByPrescription(Prescription prescription);
	Dispense findTopByPrescription(Prescription prescription);

	List<Dispense> findByPersonId(String personId);

	Iterable<Dispense> findByDispenseIdIn(Set<String> dispenses);

	boolean existsByPersonMedication_Medicine_IdIn(Collection<String> dispenses);

	long countByPrescription(Prescription prescription);
	
	boolean existsByPrescription(Prescription prescription);

	@Query("select NEW zw.gov.mohcc.mrs.commons.data.view.pharmacy.DispenseView(d.dispenseId, d.quantity, " +
			"d.personId, medicine.id, d.frequency, d.notes, medication.option.id, medication.dateCreated, medication.endDate, " +
			"prescription.prescriptionId, issue.batchIssueId) " +
			"from Dispense d " +
			"left join d.batchIssue issue " +
			"left join d.batchIssue.batch.medicine medicine " +
			"left join d.personMedication medication " +
			"left join d.prescription prescription")
	Page<DispenseView> viewAll(Pageable pageable);
	
	boolean existsByPersonIdAndPersonMedicationDateAndPersonMedicationMedicineId(String personId, LocalDate date, String medicineId);

	boolean existsByPersonIdAndPersonMedication_Medicine_IdIn(String personId, List<String> list);
}