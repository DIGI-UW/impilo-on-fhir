package zw.gov.mohcc.mrs.consultation.data.repository;

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

import org.springframework.data.jpa.repository.JpaRepository;
import zw.gov.mohcc.mrs.commons.data.Identifiable;
import zw.gov.mohcc.mrs.consultation.data.PatientClientProfile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface PatientClientProfileRepository extends JpaRepository<PatientClientProfile, String> {

	Optional<PatientClientProfile> findTopByPersonIdAndClientProfileAndDateLessThanEqualOrderByDateDesc(String personId, Identifiable clientProfile, LocalDateTime date);


	Optional<PatientClientProfile> findTopByPersonIdAndDateLessThanEqualOrderByDateDesc(String personId, LocalDateTime timeStamp);
	
	boolean existsByPersonIdAndClientProfileNameIn(String personId, Set<String> clientProfiles);

	boolean existsByPersonId(String personId);


	PatientClientProfile findTopByPersonIdOrderByDateDesc(String personId);


}