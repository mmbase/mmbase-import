/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import java.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class BasicStringSearchConstraint extends BasicFieldConstraint implements StringSearchConstraint {

    /** The search type. */
    private int searchType = 0;

    /** The match type. */
    private int matchType = 0;

    /** Map storing additional parameters. */
    private Map<String,Object> parameters = new HashMap<String,Object>(3);

    /** List of searchterms. */
    private List<String> searchTerms = null;

    /**
     * Creates a new instance of BasicStringSearchConstraint.
     *
     * @param field The associated field.
     * @param searchType The search type.
     * @param matchType The match type.
     * @param searchTerms the searchterms
     * @throws IllegalArgumentValue when an invalid argument is supplied.
     * @see #getSearchType
     * @see #getMatchType
     */
    public BasicStringSearchConstraint(StepField field, int searchType,
    int matchType, List<String> searchTerms) {
        this(field, searchType, matchType);
        setSearchTerms(searchTerms);
    }

    /**
     * Creates a new instance of BasicStringSearchConstraint.
     *
     * @param field The associated field.
     * @param searchType The search type.
     * @param matchType The match type.
     * @param searchTerms String containing searchterms as words separated
     *        by white space.
     * @throws IllegalArgumentValue when an invalid argument is supplied.
     * @see #getSearchType
     * @see #getMatchType
     */
    public BasicStringSearchConstraint(StepField field, int searchType,
    int matchType, String searchTerms) {
        this(field, searchType, matchType);
        setSearchTerms(searchTerms);
    }

    /**
     * Creates a new instance of BasicStringSearchConstraint.
     * Private, is to be called from all other creators.
     *
     * @param field The associated field.
     * @param searchType The search type.
     * @param matchType The match type.
     * @throws IllegalArgumentValue when an invalid argument is supplied.
     * @see #getSearchType
     * @see #getMatchType
     */
    private BasicStringSearchConstraint(StepField field, int searchType,
    int matchType) {
        super(field);
        if (field.getType() != Field.TYPE_STRING
        && field.getType() != Field.TYPE_XML) {
            throw new IllegalArgumentException(
            "StringSearchConstraint not allowed for this field type: "
            + getField().getType());
        }
        setSearchType(searchType);
        setMatchType(matchType);
    }

    /**
     * Sets the match type.
     *
     * @param matchType The matchtype.
     * @return This <code>BasicStringSearchConstraint</code> instance.
     * @throws IllegalArgumentValue when an invalid argument is supplied.
     * @see #getMatchType
     */
    public BasicStringSearchConstraint setMatchType(int matchType) {
        if (matchType != StringSearchConstraint.MATCH_TYPE_LITERAL
        && matchType != StringSearchConstraint.MATCH_TYPE_FUZZY
        && matchType != StringSearchConstraint.MATCH_TYPE_SYNONYM) {
            throw new IllegalArgumentException(
            "Invalid match type value: " + matchType);
        }
        this.matchType = matchType;
        if (matchType != StringSearchConstraint.MATCH_TYPE_FUZZY) {
            parameters.remove(StringSearchConstraint.PARAM_FUZZINESS);
        }
        return this;
    }

    /**
     * Sets the search type.
     *
     * @param searchType The searchType.
     * @return This <code>BasicStringSearchConstraint</code> instance.
     * @throws IllegalArgumentValue when an invalid argument is supplied.
     * @see #getSearchType
     */
    public BasicStringSearchConstraint setSearchType(int searchType) {
        if (searchType != StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED
        && searchType != StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED
        && searchType != StringSearchConstraint.SEARCH_TYPE_PROXIMITY_ORIENTED) {
            throw new IllegalArgumentException(
            "Invalid search type value: " + searchType);
        }
        this.searchType = searchType;
        if (searchType != StringSearchConstraint.SEARCH_TYPE_PROXIMITY_ORIENTED) {
            parameters.remove(StringSearchConstraint.PARAM_PROXIMITY_LIMIT);
        }
        return this;
    }

    /**
     * Adds searchterm to list of searchterms.
     *
     * @param searchTerm the searchterms
     * @return This <code>BasicStringSearchConstraint</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStringSearchConstraint addSearchTerm(String searchTerm) {
        if (searchTerm.trim().length() == 0) {
            throw new IllegalArgumentException(
            "Invalid search term value: \"" + searchTerm + "\"");
        }
        searchTerms.add(searchTerm);
        return this;
    }

    /**
     * Sets searchterms to elements in specified list.
     *
     * @param searchTerms the searchterms
     * @return This <code>BasicStringSearchConstraint</code> instance.
     */
    public BasicStringSearchConstraint setSearchTerms(List<String> searchTerms) {
        if (searchTerms.size() == 0) {
            throw new IllegalArgumentException(
            "Invalid search terms value: " + searchTerms);
        }
        List<String> newSearchTerms = new ArrayList<String>();
        Iterator<String> iSearchTerms = searchTerms.iterator();
        while (iSearchTerms.hasNext()) {
            String  searchTerm = iSearchTerms.next();
            newSearchTerms.add(searchTerm);
        }
        this.searchTerms = newSearchTerms;
        return this;
    }

    /**
     * Sets searchterms to searchterms in string.
     *
     * @param searchTerms String containing searchterms as words separated
     *        by white space.
     * @return This <code>BasicStringSearchConstraint</code> instance.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     */
    public BasicStringSearchConstraint setSearchTerms(String searchTerms) {
        if (searchTerms.trim().length() == 0) {
            throw new IllegalArgumentException(
            "Invalid search terms value: \"" + searchTerms + "\"");
        }
        List<String> newSearchTerms = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(searchTerms);
        while (st.hasMoreTokens()) {
            newSearchTerms.add(st.nextToken());
        }
        this.searchTerms = newSearchTerms;
        return this;
    }

    /**
     * Sets parameter. Ignored if parameter is not relavant to the present
     * search- and matchtype.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This <code>BasicStringSearchConstraint</code> instance.
     * @throws IllegalArgumentValue when an invalid argument is supplied.
     * @see #getParameters
     */
    public BasicStringSearchConstraint setParameter(String name, Object value) {
        if (name.equals(StringSearchConstraint.PARAM_FUZZINESS)
        && matchType == StringSearchConstraint.MATCH_TYPE_FUZZY) {
            if (!(value instanceof Float)) {
                throw new IllegalArgumentException(
                "Invalid type for parameter \"" + name + "\": "
                + value.getClass().getName());
            }
            float floatValue = ((Float) value).floatValue();
            if (floatValue < 0 || floatValue > 1) {
                throw new IllegalArgumentException(
                "Invalid fuzziness value: " + floatValue);
            }
        } else if (name.equals(StringSearchConstraint.PARAM_PROXIMITY_LIMIT)
        && searchType == StringSearchConstraint.SEARCH_TYPE_PROXIMITY_ORIENTED) {
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException(
                "Invalid type for parameter \"" + name + "\": "
                + value.getClass().getName());
            }
            int intValue = ((Integer) value).intValue();
            if (intValue < 1) {
                throw new IllegalArgumentException(
                "Invalid proximity limit value: " + intValue);
            }
        } else {
            throw new IllegalArgumentException(
            "Invalid parameter name: \"" + name + "\"");
        }
        parameters.put(name, value);
        return this;
    }

    // javadoc is inherited
    public Map<String,Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    // javadoc is inherited
    public int getMatchType() {
        return matchType;
    }

    /**
     * Returns a description of the match type
     */
    public String getMatchTypeDescription() {
        try {
            return StringSearchConstraint.MATCH_TYPE_DESCRIPTIONS[matchType];
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    // javadoc is inherited
    public int getSearchType() {
        return searchType;
    }

    /**
     * Returns a description of the search type
     */
    public String getSearchTypeDescription() {
        try {
            return StringSearchConstraint.SEARCH_TYPE_DESCRIPTIONS[searchType];
        } catch (IndexOutOfBoundsException ioobe) {
            return null;
        }
    }

    // javadoc is inherited
    public List<String> getSearchTerms() {
        return Collections.unmodifiableList(searchTerms);
    }

    // javadoc is inherited
    public int getBasicSupportLevel() {
        // no basic support
        return SearchQueryHandler.SUPPORT_NONE;
    }

    // javadoc is inherited
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicStringSearchConstraint constraint = (BasicStringSearchConstraint) obj;
            return isInverse() == constraint.isInverse()
                && isCaseSensitive() == constraint.isCaseSensitive()
                && getField().getFieldName().equals(constraint.getField().getFieldName())
                && getField().getStep().getAlias().equals(
                    constraint.getField().getStep().getAlias())
                && searchType == constraint.getSearchType()
                && matchType == constraint.getMatchType()
                && parameters.equals(constraint.parameters)
                && searchTerms.equals(constraint.searchTerms);
        } else {
            return false;
        }
    }

    // javadoc is inherited
    public int hashCode() {
        return super.hashCode()
        + 117 * searchType
        + 127 * matchType
        + 131 * parameters.hashCode()
        + 137 + searchTerms.hashCode();
    }

    // javadoc is inherited
    public String toString() {
        StringBuilder sb = new StringBuilder("StringSearchConstraint(inverse:").append(isInverse()).
        append("field:").append(getFieldName()).
        append(", casesensitive:").append(isCaseSensitive()).
        append(", searchtype:").append(getSearchTypeDescription()).
        append(", matchtype:").append(getMatchTypeDescription()).
        append(", parameters:").append(parameters).
        append(", searchterms:").append(searchTerms).
        append(")");
        return sb.toString();
    }
}
