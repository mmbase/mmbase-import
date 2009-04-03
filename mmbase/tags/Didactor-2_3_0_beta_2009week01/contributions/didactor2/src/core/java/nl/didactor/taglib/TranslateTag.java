package nl.didactor.taglib;

import org.mmbase.bridge.jsp.taglib.ParamHandler;
import org.mmbase.bridge.jsp.taglib.Writer;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.Casting;

/**
 * Translate tag: it will figure out a translation for a given
 * abstract locale.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class TranslateTag extends ContextReferrerTag implements Writer { //, ParamHandler {
    private static final Logger log = Logging.getLoggerInstance(TranslateTag.class);

    private Map<String, Object> parameters = new HashMap<String, Object>();

    public void addParameter(String key, Object value) throws JspTagException {
        parameters.put(key, value);
    }

    // These parameters are set with the different setXyz() methods
    // they may not be manipulated by this class, because that will
    // mess up in case we have tagpooling enabled.
    private String debug;
    private String key;


    private String sArg0;
    private String sArg1;
    private String sArg2;
    private String sArg3;
    private String sArg4;

    public void setKey(String key) {
        if (log.isDebugEnabled()) {
            log.debug("set key to [" + key + "]");
        }
        this.key = key;
    }

    public void setDebug(String value) {
        this.debug = value;
    }


    public void setArg0(String value) {
       this.sArg0 = value;
    }

    public void setArg1(String value) {
       this.sArg1 = value;
    }

    public void setArg2(String value) {
       this.sArg2 = value;
    }

    public void setArg3(String value) {
       this.sArg3 = value;
    }

    public void setArg4(String value) {
       this.sArg4 = value;
    }

    private String translateDebug  = "";


    protected CharSequence getTranslation() {
        return new CharSequence() {
                protected String get() {
                    try {
                        TranslateTable.init();

                        if (log.isDebugEnabled()) {
                            log.debug("Getting translation table for locale '" + getLocale() + "'");
                        }

                        TranslateTable tt = new TranslateTable(getLocale());
                        String translation = "";

                        if (key != null) {
                            translation = tt.translate(key, sArg0, sArg1, sArg2, sArg3, sArg4);
                            if (log.isDebugEnabled()) {
                                log.debug("Translating '" + key + "' to '" + translation + "' " + tt.translate(key));
                            }
                        } else {
                            return "";
                        }

                        if (translation == null || "".equals(translation)) {
                            if ("true".equals(translateDebug)) {
                                translation = "???[" + key + "]???";
                            }
                        }

                        // Save some debugging information about the translation id's that are
                        // used on this page.
                        if ("true".equals(translateDebug)) {
                            List usedTranslations = (List)pageContext.getAttribute("t_usedtrans", PageContext.REQUEST_SCOPE);
                            if (usedTranslations == null) {
                                usedTranslations = new ArrayList();
                            }
                            if (!usedTranslations.contains(key)) {
                                usedTranslations.add(key);
                                pageContext.setAttribute("t_usedtrans", usedTranslations, PageContext.REQUEST_SCOPE);
                            }
                        }

                        return translation;
                    } catch (JspTagException jte ) {
                        log.warn(jte.getMessage(), jte);
                        return key + "[" + jte.getMessage() + "]";
                    }
                }
                public char charAt(int index) {
                    return get().charAt(index);
                }
                public int length() {
                    return get().length();
                }
                public CharSequence subSequence(int start, int end) {
                    return get().subSequence(start, end);
                }

                public String toString() {
                    // this means that it is written to page by ${_} and that consequently there _must_ be a body.
                    // this is needed when body is not buffered.
                    TranslateTag.this.haveBody();
                    return get();
                }
                public int compareTo(Object o) {
                    return toString().compareTo(Casting.toString(o));
                }

            };
    }


    public int doStartTag() throws JspTagException {
        translateDebug  = "";

        if (debug == null) {
            // if no debug is given in the tag, then we look it up in the page context
            translateDebug = (String)pageContext.getAttribute("t_debug");
            if (translateDebug == null) {
                translateDebug = "";
            }
        } else {
            // if debug is given in the tag, then we put it in the page context
            pageContext.setAttribute("t_debug", debug);
            translateDebug = debug;
        }
        helper.useEscaper(false);
        helper.setValue(getTranslation());
        return EVAL_BODY; // lets try _not_ buffering the body.
    }

    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }
    public void release() {
        key = null;
        debug = null;
    }
}
