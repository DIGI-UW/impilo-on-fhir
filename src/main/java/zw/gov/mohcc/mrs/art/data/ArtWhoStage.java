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

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.gov.mohcc.mrs.commons.data.Identifiable;
import zw.gov.mohcc.mrs.config.Catalog;
import zw.gov.mohcc.mrs.terminology.enumeration.WhoStage;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A PersonArvStatus.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(catalog = Catalog.CONSULTATION, schema = Catalog.CONSULTATION)
public class ArtWhoStage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String artStageId;

	@NotNull
	private LocalDate date;

	@ManyToOne(optional = false)
	@JoinColumn(name = "art_id", nullable = false, foreignKey = @ForeignKey(name = "fk_art_status_art"))
	private Art art;

	@Enumerated(EnumType.STRING)
	private WhoStage stage;

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "follow_up_status_id")),
			@AttributeOverride(name = "name", column = @Column(name = "follow_up_status")) })
	private Identifiable followUpStatus;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ArtWhoStage artWhoStage = (ArtWhoStage) o;
		return Objects.equal(getArtStageId(), artWhoStage.getArtStageId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getArtStageId()) * 17;
	}
}