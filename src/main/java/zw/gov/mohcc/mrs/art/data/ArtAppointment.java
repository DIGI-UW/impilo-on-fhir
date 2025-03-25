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
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.gov.mohcc.mrs.commons.data.FollowedUpAppointment;
import zw.gov.mohcc.mrs.commons.data.GeneralAppointment;
import zw.gov.mohcc.mrs.commons.data.Identifiable;
import zw.gov.mohcc.mrs.config.Catalog;
import zw.gov.mohcc.mrs.terminology.enumeration.BinType;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(catalog = Catalog.CONSULTATION, schema = Catalog.CONSULTATION)
public class ArtAppointment implements GeneralAppointment, FollowedUpAppointment {

	@Id
	private String artAppointmentId;

	@ManyToOne
	@JoinColumn(name = "art_id", nullable = false, foreignKey = @ForeignKey(name = "fk_art_appointment_art"))
	private Art art;

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "reason_id")),
			@AttributeOverride(name = "name", column = @Column(name = "reason")) })
	private Identifiable reason;

	private LocalDate date;

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "followup_reason_id")),
			@AttributeOverride(name = "name", column = @Column(name = "followup_reason")) })
	private Identifiable followupReason;

	private LocalDate followupDate;

	private LocalDate appointmentOutcomeDate;

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "appointment_outcome_id")),
		@AttributeOverride(name = "name", column = @Column(name = "appointment_outcome")) })
	private Identifiable appointmentOutcome;

	private LocalDateTime visitDate;

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "work_space_id")),
			@AttributeOverride(name = "name", column = @Column(name = "work_space_name")) })
	private Identifiable workSpace;

	private String reasonForDefaulting;

	private String nameOfHcw;

	private String nameOfCbw;

	@Enumerated(EnumType.STRING)
	private BinType binType;

	public ArtAppointment(String artAppointmentId) {
		super();
		this.artAppointmentId = artAppointmentId;
	}

	public ArtAppointment(String artAppointmentId, Art art, Identifiable reason, LocalDate date,
			Identifiable followupReason, LocalDate followupDate, LocalDate appointmentOutcomeDate,
			Identifiable appointmentOutcome, LocalDateTime visitDate) {
		super();
		this.artAppointmentId = artAppointmentId;
		this.art = art;
		this.reason = reason;
		this.date = date;
		this.followupReason = followupReason;
		this.followupDate = followupDate;
		this.appointmentOutcomeDate = appointmentOutcomeDate;
		this.appointmentOutcome = appointmentOutcome;
		this.visitDate = visitDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ArtAppointment artAppointment = (ArtAppointment) o;
		return Objects.equal(getArtAppointmentId(), artAppointment.getArtAppointmentId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getArtAppointmentId()) * 17;
	}

	@Override
	public Identifiable followUpOutCome() {
		return this.appointmentOutcome;
	}
}