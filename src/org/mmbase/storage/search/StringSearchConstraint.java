/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.*;

/**
 * A constraint specifically for advanced types of text searches.
 * <p>
 * In addition to searchterms, a <em>search type</em> and a
 * <em>match type</em> can be specified:
 * <p>
 * The search type specifies how the search is performed:
 * Must be one of:
 * <ul>
 * <li>{@link #SEARCH_TYPE_WORD_ORIENTED SEARCH_TYPE_WORD_ORIENTED}
 *  - searches for the most occurrences of the words in the search terms.
 *    Order and proximity of words is not significant.
 * <li>{@link #SEARCH_TYPE_PHRASE_ORIENTED SEARCH_TYPE_PHRASE_ORIENTED}
 *  - searches for occurrence of the sequence of words in the search terms.
 * <li>{@link #SEARCH_TYPE_PROXIMITY_ORIENTED SEARCH_TYPE_PROXIMITY_ORIENTED}
 *  - searches for the most occurrences of the words within a given
 *    word distance.
 *    Order of words is not important.
 * </ul>
 * <p>
 * The match type specifies how individual words in the
 * search terms are matched with words in the searched text.
 * <ul>
 * <li>{@link #MATCH_TYPE_LITERAL MATCH_TYPE_LITERAL}
 *  - exact match only
 * <li>{@link #MATCH_TYPE_FUZZY MATCH_TYPE_FUZZY}
 *  - fuzzy match: matches words within the specified number of
 *    typo's as well (specified by parameter <code>PARAM_FUZZINESS</code>)
 * <li>{@link #MATCH_TYPE_SYNONYM MATCH_TYPE_SYNONYM}
 *  - matches synonyms as well
 * </ul>
 * <p>
 * The searchterms may containt the following wildchard characters as well:
 * <ul>
 * <li>% for any string
 * <li>_ for a single character
 * </ul>
 * <p>
 * Depending on searchtype and searchmode, the following parameters
 * can be set:
 * <ul>
 * <li>{@link #PARAM_FUZZINESS PARAM_FUZZINESS}
 *  - <code>Float</code>, specifies maximum allowed number of
 *    typo's per word, expressed as fraction of word length.
 *    (E.g. 0,2 means: maximum 2 typo's for 10 letter words.).<br />
 *    This parameter is only relevant when used with match type
 *    <code>MATCH_TYPE_FUZZY</code>.
 *    It can only be set for this match type, and is cleared when
 *    the match type is set to another value.
 * <li>{@link #PARAM_PROXIMITY_LIMIT PARAM_PROXIMITY_LIMIT}
 *  - <code>Integer</code>, specifies maximum distance between
 *    searched words.<br />
 *    This parameter is only relevant when used with search type
 *    <code>SEARCH_TYPE_PROXIMITY_ORIENTED</code>.
 *    It can only be set for this search type, and is cleared when
 *    the search type is set to another value.
 * </ul>
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface StringSearchConstraint extends FieldConstraint {

    /** Search type for <em>word oriented</em> search. */
    public final static int SEARCH_TYPE_WORD_ORIENTED = 1;

    /** Search type for <em>phrase oriented</em> search. */
    public final static int SEARCH_TYPE_PHRASE_ORIENTED = 2;

    /** Search type for <em>proximity oriented</em> search. */
    public final static int SEARCH_TYPE_PROXIMITY_ORIENTED = 3;

    /**
     * Search type descriptions corresponding to the search type values:
     * {@link #SEARCH_TYPE_WORD_ORIENTED}, {@link #SEARCH_TYPE_PHRASE_ORIENTED}, and
     * {@link #SEARCH_TYPE_PROXIMITY_ORIENTED}
     */
     String[] SEARCH_TYPE_DESCRIPTIONS = new String[] {
         null, // not specified
         "word oriented",
         "phrase oriented",
         "proximity oriented"
    };

    /** Match type for <em>literal</em> matching. */
    public final static int MATCH_TYPE_LITERAL = 1;

    /** Match type for <em>fuzzy</em> matching. */
    public final static int MATCH_TYPE_FUZZY = 2;

    /** Match type for <em>synonym</em> matching. */
    public final static int MATCH_TYPE_SYNONYM = 3;

    /**
     * Match type descriptions corresponding to the match type values:
     * {@link #MATCH_TYPE_LITERAL}, {@link #MATCH_TYPE_FUZZY}, and
     * {@link #MATCH_TYPE_SYNONYM}
     */
    public final static String[] MATCH_TYPE_DESCRIPTIONS = new String[] {
         null, // not specified
         "literal",
         "fuzzy",
         "synonym"
    };

    /** Name for parameter specifying <em>fuzziness</em> for fuzzy matching. */
    public final static String PARAM_FUZZINESS = "fuzziness";

    /**
     * Name for parameter specifying <em>proximity limit</em> for
     * proximity oriented search.
     */
    public final static String PARAM_PROXIMITY_LIMIT = "proximityLimit";

    /**
     * Gets the search type, this specifies how the search is performed.
     */
    int getSearchType();

    /**
     * Gets value of additional parameters.
     *
     * @return The parameters, as an unmodifiable Map.
     */
    Map getParameters();

    /**
     * Gets the match type.
     */
    int getMatchType();

    /**
     * Gets the list of searchterms.
     *
     * @return The searchterms, as an unmodifiable List.
     */
    List getSearchTerms();

    /**
     * Returns a string representation of this StringSearchConstraint.
     * The string representation has the form
     * "StringSearchConstraint(inverse:&lt:inverse&gt;, field:&lt;field&gt;,
     *  casesensitive:&lt;casesensitive&gt;, searchtype:&lt;searchtype&gt;,
     *  matchtype:&lt;matchtype&gt;, parameters:&lt;parameters&gt;,
     *  searchterms:&lt;searchterms&gt;)"
     * where
     * <ul>
     * <li><em>&lt;inverse&gt;</em>is the value returned by
     *      {@link #isInverse isInverse()}
     * <li><em>&lt;field&gt;</em> is the field alias returned by
     *     <code>getField().getAlias()</code>
     * <li><em>&lt;casesensitive&gt;</em> is the value returned by
     *     {@link FieldConstraint#isCaseSensitive isCaseSensitive()}
     * <li><em>&lt;searchtype&gt;</em> is the value returned by
     *     {@link StringSearchConstraint#getSearchType getSearchType()}
     * <li><em>&lt;matchtype&gt;</em> is the value returned by
     *     {@link StringSearchConstraint#getMatchType getMatchType()}
     * <li><em>&lt;parameters&gt;</em> is the map returned by
     *     {@link StringSearchConstraint#getParameters getParameters()}
     * <li><em>&lt;searchterms&gt;</em> is the list returned by
     *     {@link StringSearchConstraint#getSearchTerms getParameters()}
     *
     * @return A string representation of this FieldValueConstraint.
     */
    public String toString();

}
