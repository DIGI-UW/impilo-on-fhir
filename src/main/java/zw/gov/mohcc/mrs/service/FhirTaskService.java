package zw.gov.mohcc.mrs.service;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.Task;

public interface FhirTaskService {
	
	 Task getById(@Nonnull String id);

}
