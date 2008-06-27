/*
 * MMBase Lucene module
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.module.lucene.analysis.en;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ISOLatin1AccentFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link
 * LowerCaseFilter}, {@link StopFilter} and {@link ISOLatin1AccentFilter}.
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class StandardCleaningAnalyzer extends Analyzer {
    private Set stopSet;

    /**
     * An array containing some common English words that are usually not useful
     * for searching.
     */
    public static final String[] STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS;

    /** Builds an analyzer. */
    public StandardCleaningAnalyzer() {
        this(STOP_WORDS);
    }

    /** Builds an analyzer with the given stop words. */
    public StandardCleaningAnalyzer(String[] stopWords) {
        stopSet = StopFilter.makeStopSet(stopWords);
    }

    /**
     * Constructs a {@link StandardTokenizer} filtered by a {@link
     * StandardFilter}, a {@link LowerCaseFilter} and a {@link StopFilter}.
     */
    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream result = new StandardTokenizer(reader);
        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopSet);
        // result = new PorterStemFilter(result);
        result = new ISOLatin1AccentFilter(result);
        return result;
    }
}
