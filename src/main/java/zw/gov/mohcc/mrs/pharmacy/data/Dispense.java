package zw.gov.mohcc.mrs.pharmacy.data;

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
import zw.gov.mohcc.mrs.history.data.PersonMedication;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A Dispense.
 */
@Data
@Entity
@Table(catalog = Catalog.CONSULTATION, schema = Catalog.CONSULTATION)
@NoArgsConstructor
@AllArgsConstructor
public class Dispense {

	@Id
	private String dispenseId;

	@NotNull
	private Double quantity;

	@Column(nullable = false)
	private String personId;

	@ManyToOne
	@JoinColumn(name = "batch_issue_id", nullable = true, foreignKey = @ForeignKey(name = "fk_dispense_batch_issue"))
	private MedicineBatchIssue batchIssue;

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "frequency_id")),
			@AttributeOverride(name = "name", column = @Column(name = "frequency")) })
	private Identifiable frequency;

	private String notes;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "person_medication_id", nullable = true, foreignKey = @ForeignKey(name = "fk_dispense_person_medication"))
	private PersonMedication personMedication;

	@ManyToOne
	@JoinColumn(name = "prescription_id", nullable = true, foreignKey = @ForeignKey(name = "fk_dispense_prescription"))
	private Prescription prescription;

}