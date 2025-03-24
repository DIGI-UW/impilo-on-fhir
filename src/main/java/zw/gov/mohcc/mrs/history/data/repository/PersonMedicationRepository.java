package zw.gov.mohcc.mrs.history.data.repository;

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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.gov.mohcc.mrs.commons.data.view.patient.PersonMedicationView;
import zw.gov.mohcc.mrs.history.data.PersonMedication;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface PersonMedicationRepository extends JpaRepository<PersonMedication, String> {

	Iterable<PersonMedication> findByPersonId(String personId);

	List<PersonMedication> findByPersonIdAndDate(String personId, LocalDate localDate);

	Set<PersonMedication> findByPersonIdAndMedicineIdInAndDateBetween(String personId,
			Set<String> medicines, LocalDate startDate, LocalDate endDate);

	boolean existsByPersonIdAndDateAndMedicineId(String personId, LocalDate date, String medicineId);

	boolean existsByPersonIdAndDateAndMedicineIdIn(String personId , LocalDate date , Set<String> ids);

	boolean existsByPersonIdAndMedicineIdAndDateIsAfter(String personId, String medicineId, LocalDate date);
	
	boolean existsByPersonIdAndMedicineIdAndDate(String personId, String medicineId, LocalDate date);

	@Query("select NEW zw.gov.mohcc.mrs.commons.data.view.patient.PersonMedicationView(p.personMedicationId, " +
			"p.personId, p.medicine, p.date, p.dateCreated, p.endDate) from PersonMedication p")
	Page<PersonMedicationView> viewAll(Pageable pageable);

	@Query("SELECT pm FROM PersonMedication pm " +
			" join Medicine mn on mn.nameId = pm.medicine.id " +
			" 	where (pm.personId like :personId) and " +
			" ((pm.medicine.name like 'Nifedipine' and mn.strength = '20') or " +
			" (pm.medicine.name like 'Enalapril' and (mn.strength = '5' or mn.strength = '10')) or " +
			" (pm.medicine.name like 'Atenolol' and (mn.strength = '50' or mn.strength = '100')) or " +
			" (pm.medicine.name like 'Amlodipine' and (mn.strength = '5' or mn.strength = '10'))or " +
			" (pm.medicine.name like 'Methyldopa' and (mn.strength = '250')) or " +
			" (pm.medicine.name like 'Hydochlorothiazide' and (mn.strength = '25'))or " +
			" (pm.medicine.name like 'Losartan' and (mn.strength = '50'  or mn.strength = '100')) or " +
			" (pm.medicine.name like 'Urazide') or " +
			" (pm.medicine.name like 'Hydralazine') or " +
			" (pm.medicine.name like 'Frusemide' and (mn.strength = '40'))or " +
			" (pm.medicine.name like 'Captopril' and (mn.strength = '25'))) " +
			" and pm.date >=:startDate")
	List<PersonMedication> findHypertensionMedicationByPersonIdAndDateFrom(@Param("personId") String personId, @Param("startDate") LocalDate startDate);

	@Query("SELECT pm FROM PersonMedication pm " +
		" join Medicine mn on mn.nameId = pm.medicine.id " +
			" 	where (pm.personId like :personId) and " +
			" ((pm.medicine.name like 'VELPATASVIR' and mn.strength = '100') OR " +
			" (pm.medicine.name like 'SOFOSBUVIR' and mn.strength = '400')) and " +
			" pm.date >=:startDate")
	List<PersonMedication> findHepatitisCMedicationByPersonIdAndDateFrom(@Param("personId") String personId, @Param("startDate") LocalDate startDate);

	@Query("SELECT pm FROM PersonMedication pm " +
			" join Medicine mn on mn.nameId = pm.medicine.id " +
			" 	where (pm.personId like :personId) and " +
			" (pm.medicine.name like 'FLUCONAZOLE')  and " +
			" pm.date >=:startDate")
	List<PersonMedication> findPremptiveFluconazoleByPersonIdAndDateFrom(@Param("personId") String personId, @Param("startDate") LocalDate startDate);

}