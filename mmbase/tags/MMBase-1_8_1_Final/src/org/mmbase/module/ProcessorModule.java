/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;
import javax.servlet.http.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;


/**
 * The Processor Module extends the basic module to the Processor
 * interface so it can perform for servscan (pagelets).
 *
 * @author Daniel Ockeloen
 * @todo   Should be abstract, deprecated?
 */
public class ProcessorModule extends Module implements ProcessorInterface {
    private static final Logger log = Logging.getLoggerInstance(ProcessorModule.class);
    /**
     * {@inheritDoc}
     **/
    public MMObjectBuilder getListBuilder(String command, Map params) {
        return new VirtualBuilder(null);
    }

    protected static final Parameter[] PARAMS_PAGEINFO = new Parameter[] {Parameter.REQUEST, Parameter.RESPONSE, Parameter.CLOUD};
    protected static final Parameter.Wrapper PARAM_PAGEINFO = new Parameter.Wrapper(PARAMS_PAGEINFO);


    /**
     * Used by function wrappers.
     * @since MMBase-1.8
     */
    private static PageInfo getPageInfo(Parameters arguments) {
        PageInfo pageInfo = null;
        if (arguments.indexOfParameter(Parameter.REQUEST)> -1) {
            HttpServletRequest req  = (HttpServletRequest) arguments.get(Parameter.REQUEST);
            HttpServletResponse res = (HttpServletResponse) arguments.get(Parameter.RESPONSE);
            Cloud cloud = (Cloud) arguments.get(Parameter.CLOUD);
            pageInfo = new PageInfo(req, res, cloud);
        }
        return pageInfo;
    }
    /**
     * Used by function wrappers.
     * @since MMBase-1.8
     */
    private static String getCommand(String functionName, Parameters arguments) {
        StringBuffer buf = new StringBuffer(functionName);
        Iterator i = arguments.iterator();
        while (i.hasNext()) {
            Object argument = i.next();
            if (argument instanceof String && ! "".equals(argument)) {
                buf.append('-').append(argument);
            }
        }
        return buf.toString();
    }
    /**
     * Function implementation around {@link #getNodeList(Object, String, Map)}. See in MMAdmin for an example on how to use.
     * @since MMBase-1.8
     */
    protected class GetNodeListFunction extends AbstractFunction {
        public GetNodeListFunction(String name, Parameter[] params) {
            super(name, params, ReturnType.NODELIST);
        }
        public Object getFunctionValue(Parameters arguments) {
            return getNodeList(getPageInfo(arguments), getCommand(getName(), arguments), arguments.toMap());
        }
    }
    /**
     * Function implementation around {@link #replace(PageInfo, String)}. See in MMAdmin for an example on how to use.
     * @since MMBase-1.8
     */
    protected class ReplaceFunction extends AbstractFunction {
        public ReplaceFunction(String name, Parameter[] params) {
            super(name, params, ReturnType.STRING);
        }
        public Object getFunctionValue(Parameters arguments) {
            return replace(getPageInfo(arguments), getCommand(getName(), arguments));
        }
    }
    /**
     * Function implementation around {@link #process(PageInfo, Hashtable, Hashtable)}. See in
     * MMAdmin for an example on how to use.  It does not support multipible commands, so the first
     * Hashtable always contains precisely one entry. The value of the entry is the value of the
     * first string parameter or the empty string. All parameters are added to the second Hashtable
     * parameter ('vars'), and this is also returned (because sometimes also results are put in it).
     * @since MMBase-1.8
     */
    protected class ProcessFunction extends AbstractFunction {
        public ProcessFunction(String name, Parameter[] params) {
            super(name, params, ReturnType.MAP);
        }

        public Object getFunctionValue(Parameters arguments) {
            Hashtable cmds = new Hashtable();
            Hashtable vars = new Hashtable();
            Parameter[] def = arguments.getDefinition();
            for (int i = 0; i < def.length; i++) {
                Parameter param = def[i];
                Object value = arguments.get(param);
                if (String.class.isAssignableFrom(param.getTypeAsClass()) && cmds.size() == 0) {
                    cmds.put(getName(), value);
                }
                vars.put(param.getName(), value);
            }
            if (cmds.size() == 0) cmds.put(getName(), "");
            boolean ok = process(getPageInfo(arguments), cmds, vars);
            return vars;
        }
    }

    /**
     * This method is a wrapper around {@link #getList(PageInfo, StringTagger, String)}
     * @param context The PageInfo object. It beats me why it is Object and not PageInfo. I think it's silly.
     * @param command The command to execute
     * @param params  Parameters, they will be added to the StringTagger.
     **/
    public Vector getNodeList(Object context, String command, Map params) {
        StringTagger tagger=null;
        if (params instanceof StringTagger) {
            tagger = (StringTagger)params;
        } else {
            tagger = new StringTagger("");
            if (params != null) {
                for (Iterator entries = params.entrySet().iterator(); entries.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    String key=(String) entry.getKey();
                    Object o = entry.getValue();
                    if (o instanceof Vector) {
                        tagger.setValues(key, (Vector)o);
                    } else {
                        tagger.setValue(key, "" + o);
                    }
                }
            }
        }
        PageInfo sp = null;
        if (context instanceof PageInfo) {
            sp = (PageInfo)context;
        }
        Vector v = getList(sp, tagger, command);
        int items = 1;
        try { items = Integer.parseInt(tagger.Value("ITEMS")); } catch (NumberFormatException e) {}
        Vector fieldlist = tagger.Values("FIELDS");
        Vector res = new Vector(v.size() / items);
        MMObjectBuilder bul = getListBuilder(command, params);
        for(int i= 0; i < v.size(); i+=items) {
            VirtualNode node = new VirtualNode(bul);
            for(int j= 0; (j<items) && (j<v.size()); j++) {
                if ((fieldlist!=null) && (j<fieldlist.size())) {
                    node.setValue((String)fieldlist.get(j),v.get(i+j));
                } else {
                    node.setValue("item"+(j+1),v.get(i+j));
                }
            }
            res.add(node);
        }
        return res;
    }

    /**
     * @javadoc
     **/
    public Vector  getList(PageInfo sp,StringTagger params, String command) {
        throw new UnsupportedOperationException("Module " + this.getClass().getName() + " does not implement LIST");
    }

    /**
     * {@inheritDoc}
     */
    public boolean process(PageInfo sp, Hashtable cmds, Hashtable vars) {
        return false;
    }

    /**
     * {@inheritDoc}
     **/
    public String replace (PageInfo sp, String command) {
        return "This module doesn't implement this processor call";
    }

    /**
     * {@inheritDoc}
     * who the hell uses this (daniel)
     **/
    public String replace (PageInfo sp, StringTagger command) {
        return "This module doesn't implement this processor call";
    }

    /**
     * {@inheritDoc}
     */
    public boolean cacheCheck(PageInfo sp, String cmd) {
        return false;
    }


    /**
     * What should this do, when is this called?
     * @deprecated called by nothing
     * @javadoc
     */

     public void reload() {
     }


    /**
     * What should this do, when is this called?
     * @deprecated called by nothing
     * @javadoc
     */
    public void unload() {
    }

    /**
     * {@inheritDoc}
     * @scope abstract
     */
    public void init() {
    }

    /**
     * {@inheritDoc}
     * @scope abstract
     */
    public void onload() {
    }


}
