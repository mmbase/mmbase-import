package org.mmbase.util.transformers;

import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.List;


/**
 * Can be used to strip tags and attributes from HTML. Also, if markup remains, it can be made
 * 'locally' well formed XML (the 'escapeamps' parameter suffices then), by which I mean that if you
 * put it in a div, that div is then well formed.
 *
 * http://javafaq.nu/java-example-code-618.html
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.4
 */
public class TagStripperFactory implements ParameterizedTransformerFactory<CharTransformer>  {

    private static final Logger log = Logging.getLoggerInstance(TagStripperFactory.class);


    private static final String NL_TOKEN = "XXXX_NL_XXXX";

    public static final Parameter<String> TAGS         =
        new Parameter<String>("tags", String.class, "");  // allowed tags, default no tags are permitted.

    public static final Parameter<Boolean> ADD_BRS     =
        new Parameter<Boolean>("addbrs", Boolean.class, Boolean.FALSE);

    public static final Parameter<Boolean> CONSERVE_NEWLINES =
            new Parameter<Boolean>("conservenewlines", Boolean.class, Boolean.FALSE);

    public static final Parameter<Boolean> ESCAPE_AMPS =
        new Parameter<Boolean>("escapeamps", Boolean.class, Boolean.FALSE);

    public static final Parameter<Boolean> ADD_NEWLINES     =
        new Parameter<Boolean>("addnewlines", Boolean.class, Boolean.FALSE);


    protected static final Parameter[] PARAMS = new Parameter[] { TAGS, ADD_BRS, ESCAPE_AMPS, ADD_NEWLINES, CONSERVE_NEWLINES};

    @Override
    public Parameters createParameters() {
        return new Parameters(PARAMS);
    }



    /**
     * Creates a parameterized transformer.
     */
    @Override
    public CharTransformer createTransformer(final Parameters parameters) {

        parameters.checkRequiredParameters();
        if (log.isDebugEnabled()) {
            log.debug("Creating transformer, with " + parameters);
        }

        final List<TagStripper.Tag> tagList;
        String tags = parameters.getString(TAGS).toUpperCase();
        if (tags.equals("XSS")) {
            tagList = TagStripper.XSS;
        } else if (tags.equals("")) {
            tagList = TagStripper.NONE;
        } else if (tags.equals("NONE")) {
            tagList = TagStripper.NONE;
        } else {
            throw new RuntimeException("Unknown value for 'tags' parameter '" + tags + "'. Known are 'XSS': strip only cross-site scripting, and '': strip all tags.");
        }



        TagStripper trans = new TagStripper(tagList);
        trans.addBrs(parameters.get(ADD_BRS));
        trans.escapeAmps(parameters.get(ESCAPE_AMPS));
        trans.addNewlines(parameters.get(ADD_NEWLINES));
        trans.conserveNewlines(parameters.get(CONSERVE_NEWLINES));

        if (log.isDebugEnabled()) {
            log.debug("Created " + trans);
        }
        return trans;
    }

}
