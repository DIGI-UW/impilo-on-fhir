package zw.gov.mohcc.mrs.art.data.repository;

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
import zw.gov.mohcc.mrs.art.data.Art;
import zw.gov.mohcc.mrs.art.data.ArtAppointment;
import zw.gov.mohcc.mrs.commons.data.view.art.ArtAppointmentView;
import zw.gov.mohcc.mrs.terminology.enumeration.BinType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ArtAppointmentRepository extends JpaRepository<ArtAppointment, String> {

	List<ArtAppointment> findByArtArtIdOrderByDateDesc(String artId);

	Page<ArtAppointment> findByFollowupDateBeforeAndAppointmentOutcomeDateIsNull(LocalDate date, Pageable pageable);

	Page<ArtAppointment> findByAppointmentOutcomeDateIsNull(Pageable pageable);

	long countByDate(LocalDate date);

	boolean existsByArtArtIdAndDateAfter(String artId, LocalDate date);

	List<ArtAppointment> findByArtArtIdAndDateAfter(String artId, LocalDate date);

	List<ArtAppointment> findByArtArtIdAndDateGreaterThanEqual(String artId, LocalDate date);

	List<ArtAppointment> findByArtPersonIdAndDateGreaterThanEqualOrderByDate(String artId, LocalDate date);

	List<ArtAppointment> findByDateGreaterThanEqualOrderByDate(LocalDate date);

	Page<ArtAppointment> findByDateGreaterThanOrderByDate(LocalDate date, Pageable pageable);

	@Query("select NEW zw.gov.mohcc.mrs.commons.data.view.art.ArtAppointmentView(a.artAppointmentId, "
			+ "a.date, a.followupReason, a.appointmentOutcome, a.art.artId, a.followupDate, a.appointmentOutcomeDate) from ArtAppointment a")
	Page<ArtAppointmentView> viewAll(Pageable pageable);

	@Query("select a from ArtAppointment a where a.appointmentOutcome.name IS NULL and a.date < :cutOffDate and a.artAppointmentId in (:artAppointmentIds) order by a.date DESC")
	Page<ArtAppointment> lostToFollowUp(@Param("cutOffDate") LocalDate cutOffDate,
			@Param("artAppointmentIds") Set<String> artAppointmentIds, Pageable pageable);

	@Query("select a from ArtAppointment a where a.appointmentOutcome.name IS NULL and a.date < :cutOffDate and a.artAppointmentId in (:artAppointmentIds) order by a.date DESC")
	List<ArtAppointment> lostToFollowUpList(@Param("cutOffDate") LocalDate cutOffDate,
			@Param("artAppointmentIds") Set<String> artAppointmentIds);

	@Query("select a from ArtAppointment a where a.appointmentOutcome.name IS NULL and (a.date between :startDate and :endDate) and a.artAppointmentId in (:artAppointmentIds) order by a.date DESC")
	Page<ArtAppointment> artDefaulters(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("artAppointmentIds") Set<String> artAppointmentIds, Pageable pageable);

	boolean existsByArtArtIdAndDate(String artId, LocalDate date);

	List<ArtAppointment> findByArtArtIdInAndDateBefore(List<String> artIds, LocalDate date);

	Page<ArtAppointment> findByAppointmentOutcomeIdIsNull(Pageable pageable);

	@Query("select a.art.artId from ArtAppointment a where a.appointmentOutcome.name IS NULL")
	Set<String> findDistinctArtIds();

	ArtAppointment findTopByArtArtIdInOrderByDateDesc(String artId);

	Page<ArtAppointment> findByArtAppointmentIdIn(Set<String> artAppointmentIds, Pageable pageable);

	@Query("select a from ArtAppointment a where a.binType = :binType and a.workSpace.id = :workSpaceId")
	List<ArtAppointment> findByBinTypeAndWorkSpaceId(@Param("binType") BinType binType,
			@Param("workSpaceId") String workSpaceId);

	@Query("select a from ArtAppointment a where a.date >= :date and a.binType = :binType and a.workSpace.id = :workspaceId")
	List<ArtAppointment> findByDateGreaterThanEqualAndBinTypeAndWorkSpaceId(@Param("date") LocalDate twentyEightDaysAgo,
			@Param("binType") BinType binType, @Param("workspaceId") String workSpaceId);

	Page<ArtAppointment> findByDateGreaterThanEqualOrderByDate(LocalDate date, Pageable pageable);

	Page<ArtAppointment> findByDateGreaterThanEqualAndAppointmentOutcomeDateIsNullOrderByDate(LocalDate date,
			Pageable pageable);

	ArtAppointment findTopByAppointmentOutcomeIsNullAndArtArtIdInOrderByDateDesc(String artId);

	@Query("SELECT aa FROM ArtAppointment aa WHERE aa.date BETWEEN :missedCeiling AND :missedFloor AND aa.art.artId IN :filteredArtIds")
	Page<ArtAppointment> findMissedAppointments(@Param("missedCeiling") LocalDate missedCeiling,
												@Param("missedFloor") LocalDate missedFloor,
												@Param("filteredArtIds") Set<String> filteredArtIds,
												Pageable pageable);
	@Query("SELECT a FROM ArtAppointment a WHERE a.appointmentOutcome IS NULL AND a.date >= :sevenDaysAgo AND a.art.personId =:personId")
	List<ArtAppointment> findAppointmentsFromSevenDaysAgoAndFuture(@Param("personId") String personId, @Param("sevenDaysAgo") LocalDate sevenDaysAgo);

	Optional<ArtAppointment>findTopByReasonIdAndArtPersonIdAndDate(String reasonId, String personId, LocalDate date);

	@Query("select a from ArtAppointment  a where a.appointmentOutcome.id IN (:outcomeList) and a.art= :art order by a.date desc ")
	List<ArtAppointment>getArtAppointmentByAppointmentOutcomeIn(@Param("outcomeList") List<String>outcomeList, @Param("art") Art art);

	@Query("select a from ArtAppointment  a where a.appointmentOutcome.id IN (:outcomeList) and a.art= :art and a.date >= :date ")
	List<ArtAppointment>getArtAppointmentByAppointmentOutcomeIn2(@Param("outcomeList") List<String>outcomeList, @Param("art") Art art, @Param("date")LocalDate date);
}