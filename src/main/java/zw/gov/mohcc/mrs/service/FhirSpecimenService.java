package zw.gov.mohcc.mrs.service;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.Specimen;

public interface FhirSpecimenService {
	
	Specimen getById(@Nonnull String id);

}
