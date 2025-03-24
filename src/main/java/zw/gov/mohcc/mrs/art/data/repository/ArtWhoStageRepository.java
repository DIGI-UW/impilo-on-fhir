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
import zw.gov.mohcc.mrs.art.data.ArtWhoStage;
import zw.gov.mohcc.mrs.commons.data.view.art.ArtWhoStageView;
import zw.gov.mohcc.mrs.terminology.enumeration.WhoStage;

import java.time.LocalDate;
import java.util.List;

public interface ArtWhoStageRepository extends JpaRepository<ArtWhoStage, String> {

	List<ArtWhoStage> findByArtOrderByDateDesc(Art art);

	ArtWhoStage findTopByArtOrderByDateDesc(Art art);

	ArtWhoStage findTopByArt_PersonIdOrderByDateDesc(String personId);
	
	ArtWhoStage findTopByArtAndDateLessThanEqualOrderByDateDesc(Art art, LocalDate date);

	ArtWhoStage findTopByArt_ArtIdAndDate(String string, LocalDate date);

	boolean existsByArt(Art art);
	boolean existsByArtAndStage(Art art, WhoStage stage);

	boolean existsByArtPersonId(String personId);

	@Query("select NEW zw.gov.mohcc.mrs.commons.data.view.art.ArtWhoStageView(a.artStageId, a.art.artId, a.stage, " +
			"a.followUpStatus, a.art.personId, a.date) from ArtWhoStage a")
    Page<ArtWhoStageView> viewAll(Pageable pageable);

	ArtWhoStage findTopByArtAndStage(Art art, WhoStage stage);
	
	boolean existsByArtAndStageAndDate(Art art, WhoStage stage, LocalDate date);

	@Query("select a from ArtWhoStage  a where a.followUpStatus.id IN (:statusList) and a.art= :art order by a.date desc ")
	List<ArtWhoStage> getArtWhoStageByFollowUpStatusIn(@Param("statusList") List<String>statusList, @Param("art") Art art);

	@Query("select a from ArtWhoStage  a where a.followUpStatus.id IN (:statusList) and a.art= :art and a.date >= :date ")
	List<ArtWhoStage> getArtWhoStageByFollowUpStatusIn2(@Param("statusList") List<String>statusList, @Param("art") Art art, @Param("date")LocalDate date);


}