package zw.gov.mohcc.mrs.service;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.ServiceRequest;

public interface FhirServiceRequestService {
	
	ServiceRequest getById(@Nonnull String id);

}
