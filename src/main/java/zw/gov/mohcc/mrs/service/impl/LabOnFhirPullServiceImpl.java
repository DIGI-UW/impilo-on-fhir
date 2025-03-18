package zw.gov.mohcc.mrs.service.impl;

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

import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.DateClientParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.TaskStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zw.gov.mohcc.mrs.fhir.FhirConstants;
import zw.gov.mohcc.mrs.fhir.api.FhirUtilityService;
import zw.gov.mohcc.mrs.labonfhir.processor.*;
import zw.gov.mohcc.mrs.labonfhir.service.LabOnFhirPullService;
import zw.gov.mohcc.mrs.sync.data.FetchMeta;
import zw.gov.mohcc.mrs.sync.data.repository.FetchMetaRepository;

import java.util.Date;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class LabOnFhirPullServiceImpl implements LabOnFhirPullService {

    protected final IGenericClient shrFhirClient;

    private final FhirUtilityService fhirUtilityService;

    private final CompletedTaskProcessor completedTaskProcessor;

    private final InProgressTaskProcessor inProgressTaskProcessor;

    private final ReadyTaskProcessor readyTaskProcessor;

    private final RejectedTaskProcessor rejectedTaskProcessor;

    private final OnHoldTaskProcessor onHoldTaskProcessor;

    private final CancelledTaskProcessor cancelledTaskProcessor;

    private final ReceivedTaskProcessor receivedTaskProcessor;

    private final EnteredInErrorTaskProcessor enteredInErrorTaskProcessor;

    private final AcceptedTaskProcessor acceptedTaskProcessor;

    private final FailedTaskProcessor failedTaskProcessor;

    private final FetchMetaRepository fetchMetaRepository;

    @Value("${application.hie.shared-health-record.url}")
    String shrUrl;

    @Value("${application.hie.shared-health-record.inner-url}")
    String shrInnerUrl;


    @Override
    public void pullUpdatedTasks() {
        Bundle taskBundle = shrFhirClient.search().forResource(Task.class)
                .where(Task.IDENTIFIER.hasSystemWithAnyCode(fhirUtilityService.getImpiloSiteSystem(FhirConstants.UID)))
                .where(Task.STATUS.exactly().codes(getUpdatedTaskStatusCodes()))
                .sort(new SortSpec("_lastUpdated", SortOrderEnum.DESC))
                .returnBundle(Bundle.class)
                .execute();

        updateTasksInBundle(taskBundle);

    }

    @Override
    public void pullUpdatedTasks(Date lastUpdateTime) {
        Bundle taskBundle = shrFhirClient.search().forResource(Task.class)
                .where(Task.IDENTIFIER.hasSystemWithAnyCode(fhirUtilityService.getImpiloSiteSystem(FhirConstants.UID)))
                .where(Task.STATUS.exactly().codes(getUpdatedTaskStatusCodes()))
                .where(new DateClientParam("_lastUpdated").afterOrEquals().day(lastUpdateTime))
                .sort(new SortSpec("_lastUpdated", SortOrderEnum.DESC))
                .returnBundle(Bundle.class)
                .execute();

        updateTasksInBundle(taskBundle);

    }

    private void updateTasksInBundle(Bundle taskBundle) {
        Consumer<Bundle.BundleEntryComponent> bundleEntryConsumer = bundleEntryComponent -> {
            Task task = (Task) bundleEntryComponent.getResource();
            if (task.getStatus() == Task.TaskStatus.COMPLETED) {
                completedTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.REJECTED) {
                rejectedTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.RECEIVED) {
                receivedTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.ONHOLD) {
                onHoldTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.INPROGRESS) {
                inProgressTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.CANCELLED) {
                cancelledTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.READY) {
                readyTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.ENTEREDINERROR) {
                enteredInErrorTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.ACCEPTED) {
                acceptedTaskProcessor.process(task);
            } else if (task.getStatus() == Task.TaskStatus.FAILED) {
                failedTaskProcessor.process(task);
            }
        };

        processTaskBundle(taskBundle, bundleEntryConsumer);

    }

    private String[] getUpdatedTaskStatusCodes() {
        return new String[]{
                Task.TaskStatus.REJECTED.toCode(),
                TaskStatus.ACCEPTED.toCode(),
                TaskStatus.COMPLETED.toCode(),
                TaskStatus.RECEIVED.toCode(),
                TaskStatus.CANCELLED.toCode(),
                TaskStatus.INPROGRESS.toCode(),
                TaskStatus.ENTEREDINERROR.toCode(),
                TaskStatus.ONHOLD.toCode(),
                TaskStatus.FAILED.toCode(),
                TaskStatus.READY.toCode()
        };
    }

    public void processTaskBundle(Bundle bundle, Consumer<Bundle.BundleEntryComponent> bundleEntryConsumer) {
        //To keep track of the last time you fetched tasks, you can save the lastUpdated timestamp from the response.
        Date lastUpdateTime = bundle.getMeta().getLastUpdated();
        do {
            bundle.getEntry().forEach(bundleEntryConsumer);
            boolean hasNextBundle = bundle.getLink(IBaseBundle.LINK_NEXT) != null && !bundle.getEntry().isEmpty();
            if (hasNextBundle) {
                String innerUrl = formatUrl(shrInnerUrl);
                String url = formatUrl(shrUrl);
                String nextUrl = bundle.getLink(IBaseBundle.LINK_NEXT).getUrl().replace(innerUrl, url);
                bundle = (Bundle) shrFhirClient.search().byUrl(nextUrl).execute();
            } else {
                bundle = null;
            }
        } while (bundle != null && !bundle.getEntry().isEmpty());

        String resourceType = ResourceType.Task.name();
        FetchMeta fetchMeta = fetchMetaRepository.findOne(resourceType);
        fetchMeta = fetchMeta != null ? fetchMeta : new FetchMeta(resourceType, lastUpdateTime);
        fetchMeta.setLastUpdatedTime(lastUpdateTime);
        fetchMetaRepository.save(fetchMeta);

    }

    private String formatUrl(String url) {
        url = url.trim();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
