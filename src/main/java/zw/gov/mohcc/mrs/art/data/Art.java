package zw.gov.mohcc.mrs.art.data;

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

import lombok.Data;
import lombok.NoArgsConstructor;
import zw.gov.mohcc.mrs.art.enumeration.ReasonOfNotDisclosing;
import zw.gov.mohcc.mrs.commons.data.reception.IPersonInfo;
import zw.gov.mohcc.mrs.config.Catalog;
import zw.gov.mohcc.mrs.terminology.enumeration.IndexClientProfileType;
import zw.gov.mohcc.mrs.terminology.enumeration.Normality;
import zw.gov.mohcc.mrs.terminology.enumeration.RelationshipType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(catalog = Catalog.CONSULTATION, schema = Catalog.CONSULTATION)

public class Art implements IPersonInfo {

    @Id
    private String artId;

    @NotNull
    private String personId;

    private LocalDate date;

    private String artNumber;

    private Boolean enlargedLymphNode;

    private Boolean pallor;

    private Boolean jaundice;

    private Boolean cyanosis;

    private String artCohortNumber;

    @Enumerated(EnumType.STRING)
    private Normality mentalStatus;

    @Enumerated(EnumType.STRING)
    private Normality centralNervousSystem;

    // TODO: replace with reference to HIV investigation
    private LocalDate dateOfHivTest;

    private LocalDate dateEnrolled;

    private Boolean tracing;

    private Boolean followUp;

    private Boolean hivStatus;

    @Enumerated(EnumType.STRING)
    private RelationshipType relation;

    private LocalDate dateOfDisclosure;

    @Enumerated(EnumType.STRING)
    private ReasonOfNotDisclosing reason;

    @Enumerated(EnumType.STRING)
    private IndexClientProfileType indexClientProfile;
    private Boolean consentToIndexTesting;


    public Art(String artId) {
        super();
        this.artId = artId;
    }


}