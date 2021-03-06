/*
 * License Agreement for OpenSearchServer
 * <p>
 * Copyright (C) 2008-2017 Emmanuel Keller / Jaeksoft
 * <p>
 * http://www.open-search-server.com
 * <p>
 * This file is part of OpenSearchServer.
 * <p>
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with OpenSearchServer.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaeksoft.searchlib.index;

import com.jaeksoft.searchlib.SearchLibException;
import com.jaeksoft.searchlib.analysis.PerFieldAnalyzer;
import com.jaeksoft.searchlib.filter.FilterAbstract;
import com.jaeksoft.searchlib.filter.FilterHits;
import com.jaeksoft.searchlib.function.expression.SyntaxError;
import com.jaeksoft.searchlib.query.ParseException;
import com.jaeksoft.searchlib.request.AbstractLocalSearchRequest;
import com.jaeksoft.searchlib.request.AbstractRequest;
import com.jaeksoft.searchlib.result.AbstractResult;
import com.jaeksoft.searchlib.schema.FieldValue;
import com.jaeksoft.searchlib.schema.SchemaField;
import com.jaeksoft.searchlib.util.Timer;
import com.qwazr.utils.FunctionUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similar.MoreLikeThis;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public interface ReaderInterface {

	boolean sameIndex(ReaderInterface reader);

	void close() throws IOException;

	Collection<?> getFieldNames() throws SearchLibException;

	int getDocFreq(Term term) throws SearchLibException;

	void termEnum(FunctionUtils.ConsumerEx<TermEnum, IOException> termEnumConsumer)
			throws IOException, SearchLibException;

	void termEnum(Term term, FunctionUtils.ConsumerEx2<TermEnum, IOException, SearchLibException> termEnumConsumer)
			throws IOException, SearchLibException;

	void termDocs(Term term, FunctionUtils.ConsumerEx<TermDocs, IOException> termDocsConsumer) throws IOException,
			SearchLibException;

	LinkedHashMap<String, FieldValue> getDocumentFields(final int docId, final LinkedHashSet<String> fieldNameSet,
			final Timer timer) throws IOException, ParseException, SyntaxError, SearchLibException;

	LinkedHashMap<String, FieldValue> getDocumentStoredField(final int docId) throws IOException, SearchLibException;

	TermFreqVector getTermFreqVector(final int docId, final String field) throws IOException, SearchLibException;

	void termPositions(FunctionUtils.ConsumerEx<TermPositions, IOException> TermPositions) throws IOException,
			SearchLibException;

	FieldCacheIndex getStringIndex(final String fieldName) throws IOException, SearchLibException;

	String[] getDocTerms(String field) throws IOException, SearchLibException;

	FilterHits getFilterHits(SchemaField defaultField, PerFieldAnalyzer analyzer, AbstractLocalSearchRequest request,
			FilterAbstract<?> filter, Timer timer) throws ParseException, IOException, SearchLibException, SyntaxError;

	DocSetHits searchDocSet(AbstractLocalSearchRequest searchRequest, Timer timer)
			throws IOException, ParseException, SyntaxError, SearchLibException;

	void putTermVectors(final int[] docIds, final String field, final Collection<String[]> termVectors)
			throws IOException, SearchLibException;

	Query rewrite(Query query) throws SearchLibException;

	MoreLikeThis getMoreLikeThis() throws SearchLibException;

	AbstractResult<?> request(AbstractRequest request) throws SearchLibException;

	String explain(AbstractRequest request, int docId, boolean bHtml) throws SearchLibException;

	IndexStatistics getStatistics() throws IOException, SearchLibException;

	long getVersion() throws SearchLibException;

}
