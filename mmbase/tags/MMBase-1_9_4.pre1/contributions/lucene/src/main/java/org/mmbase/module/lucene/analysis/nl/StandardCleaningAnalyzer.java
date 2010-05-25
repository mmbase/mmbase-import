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
package org.mmbase.module.lucene.analysis.nl;

/**
 * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link
 * LowerCaseFilter}, {@link StopFilter} and {@link ISOLatin1AccentFilter}.
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class StandardCleaningAnalyzer extends org.mmbase.module.lucene.analysis.en.StandardCleaningAnalyzer {
    /**
     * List of typical Dutch stopwords.
     */
    public static final String[] DUTCH_STOP_WORDS = { 
        "de", "en", "van", "ik", "te", "dat", "die", "in", "een", "hij", "het",
        "niet", "zijn", "is", "was", "op", "aan", "met", "als", "voor", "had", "er", "maar", "om", "hem", "dan", "zou", "of",
        "wat", "mijn", "men", "dit", "zo", "door", "over", "ze", "zich", "bij", "ook", "tot", "je", "mij", "uit", "der",
        "daar", "haar", "naar", "heb", "hoe", "heeft", "hebben", "deze", "u", "want", "nog", "zal", "me", "zij", "nu", "ge",
        "geen", "omdat", "iets", "worden", "toch", "al", "waren", "veel", "meer", "doen", "toen", "moet", "ben", "zonder",
        "kan", "hun", "dus", "alles", "onder", "ja", "eens", "hier", "wie", "werd", "altijd", "doch", "wordt", "wezen",
        "kunnen", "ons", "zelf", "tegen", "na", "reeds", "wil", "kon", "niets", "uw", "iemand", "geweest", "andere" };
    
    /** 
     * Builds an Dutchyfied analyzer. 
     */
    public StandardCleaningAnalyzer() {
        super(DUTCH_STOP_WORDS);
    }
}
