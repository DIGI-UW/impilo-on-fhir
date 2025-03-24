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
import zw.gov.mohcc.mrs.commons.data.view.art.ArtView;

import java.util.List;

public interface ArtRepository extends JpaRepository<Art, String> {

    Art findByPersonId(String personId);

    boolean existsByPersonId(String personId);

    boolean existsByArtNumber(String artNumber);

    Art findByArtNumber(String artNumber);

    Art findByArtId(String artId);

    Art findTop1ByArtNumberStartingWithOrderByArtNumberDesc(String seed);

    /**
     * @param personId This method is a workaround and is not meant to be used for anything useful
     */
    @Deprecated
    int countByPersonId(String personId);

    /**
     * @param personId This method is a workaround and is not meant to be used for anything useful
     */
    @Deprecated
    List<Art> findByPersonIdOrderByDateAsc(String personId);

    @Query("select NEW zw.gov.mohcc.mrs.commons.data.view.art.ArtView(a.artId, a.artNumber, a.dateEnrolled, " +
            "a.dateOfHivTest, a.date, a.enlargedLymphNode, a.pallor, a.jaundice, a.cyanosis, a.mentalStatus," +
            "a.centralNervousSystem, a.tracing, a.followUp, a.hivStatus, a.relation, a.dateOfDisclosure, a.reason, " +
            "a.personId, a.artCohortNumber) from Art a")
    Page<ArtView> viewAll(Pageable pageable);

    @Query("select a.personId from Art a where a.artId=:artId")
    String findPersonId(@Param("artId") String artId);

    @Query("select distinct a from Art a where a.artNumber Like %:artNumber%")
    List<Art> findAllByArtNumber(@Param("artNumber") String artNumber);

    Art findTopByPersonIdOrderByDateDesc(String personId);

}