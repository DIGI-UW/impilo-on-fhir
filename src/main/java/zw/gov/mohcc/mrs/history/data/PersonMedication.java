package zw.gov.mohcc.mrs.history.data;

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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.gov.mohcc.mrs.commons.data.Identifiable;
import zw.gov.mohcc.mrs.config.Catalog;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(catalog = Catalog.CONSULTATION, schema = Catalog.CONSULTATION)
public class PersonMedication {

	@Id
	private String personMedicationId;

	@Column(nullable = false)
	private String personId;

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "medicine_id")),
			@AttributeOverride(name = "name", column = @Column(name = "medicine")) })
	private Identifiable medicine;

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "option_id")),
			@AttributeOverride(name = "name", column = @Column(name = "`option`")) })
	private Identifiable option;

	private LocalDate date;

	private String note;

	private LocalDate dateCreated;

	private LocalDate endDate;

	private Integer doses;
}