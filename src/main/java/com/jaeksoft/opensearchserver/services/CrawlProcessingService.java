/*
 * Copyright 2017-2018 Emmanuel Keller / Jaeksoft
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.jaeksoft.opensearchserver.services;

import com.jaeksoft.opensearchserver.model.CrawlTaskRecord;
import com.jaeksoft.opensearchserver.model.TaskRecord;
import com.qwazr.crawler.common.CrawlDefinition;
import com.qwazr.crawler.common.CrawlStatus;
import com.qwazr.crawler.common.CrawlerServiceInterface;
import com.qwazr.server.client.ErrorWrapper;

public abstract class CrawlProcessingService<T extends CrawlTaskRecord, D extends CrawlDefinition, S extends CrawlStatus<D>>
		implements ProcessingService<T, S> {

	private final CrawlerServiceInterface<D, S> crawlerService;
	protected final IndexesService indexesService;

	protected CrawlProcessingService(final CrawlerServiceInterface<D, S> crawlerService,
			final IndexesService indexesService) {
		this.crawlerService = crawlerService;
		this.indexesService = indexesService;
	}

	@Override
	final public S getStatus(final String taskId) {
		return ErrorWrapper.bypass(() -> crawlerService.getSession(taskId), 404);
	}

	@Override
	final public boolean isRunning(final String taskId) {
		final S crawlStatus = getStatus(taskId);
		return crawlStatus != null && crawlStatus.endTime != null;
	}

	protected abstract D getNewCrawlDefinition(final T taskRecord);

	@Override
	final public void checkIsRunning(final TaskRecord taskRecord) {
		if (isRunning(taskRecord.getTaskId()))
			return;
		final D crawlDefinition = getNewCrawlDefinition(getTaskRecordClass().cast(taskRecord));
		if (crawlDefinition == null) // Nothing to do
			return;
		crawlerService.runSession(taskRecord.getTaskId(), crawlDefinition);
	}
}
