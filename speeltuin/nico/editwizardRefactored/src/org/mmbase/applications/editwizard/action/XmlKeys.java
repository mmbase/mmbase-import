/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.action;


/**
 * Keys for elements name and attributes in <Wizard> and <list> DOM.
 * @todo javadoc
 * 
 * @author caicai
 * @created 2005-7-31
 * @version $Id: XmlKeys.java,v 1.1 2005-11-28 10:09:27 nklasens Exp $
 */
public class XmlKeys {
    
    public static final String ELEM_ACTION          ="action";
    public static final String ELEM_COMMAND         ="command";
    public static final String ELEM_CURFORM         ="curform";
    public static final String ELEM_DESCRIPTION     ="description";
    public static final String ELEM_FIELD           ="field";
    public static final String ELEM_FIELDSET        ="fieldset";    
    public static final String ELEM_FORM            ="form";
    public static final String ELEM_ITEM            ="item";
    public static final String ELEM_LIST            ="list";
    public static final String ELEM_NAME            ="name";
    public static final String ELEM_NEXTFORM        ="nextform";
    public static final String ELEM_OPTION          ="option";
    public static final String ELEM_OPTIONLIST      ="optionlist";
    public static final String ELEM_PATH            ="path";
    public static final String ELEM_PREVFORM        ="prevform";
    public static final String ELEM_PROMPT          ="prompt";
    public static final String ELEM_RELATION        ="relation";
    public static final String ELEM_SEARCHFIELDS    ="search-fields";
    public static final String ELEM_SEARCHFILTER    ="search-filter";
    public static final String ELEM_STEP            ="step";
    public static final String ELEM_STEPSVALIDATOR  ="steps-validator";
    public static final String ELEM_SUBTITLE        ="subtitle";    
    public static final String ELEM_TITLE           ="title";
    public static final String ELEM_UPLOAD          ="upload";
    public static final String ELEM_VALIDATOR       ="validator";
    public static final String ELEM_VALUE           ="value";
    public static final String ELEM_WIZARD          ="wizard";    
    
    public static final String ATTR_ALLOWSAVE       ="allowsave";
    public static final String ATTR_CMD             ="cmd";
    public static final String ATTR_DESTINATION     ="destination";
    public static final String ATTR_DESTINATIONTYPE ="destinationtype"; //copy from schema?
    public static final String ATTR_DID             ="did";
    public static final String ATTR_DTMAXLENGTH     ="dtmaxlength";
    public static final String ATTR_DTMINLENGTH     ="dtminlength";
    public static final String ATTR_DTREQUIRED      ="dtrequired";
    public static final String ATTR_DTTYPE          ="dttype";
    public static final String ATTR_EXTENDS         ="extends";
    public static final String ATTR_FDATAPATH       ="fdatapath";
    public static final String ATTR_FID             ="fid";
    public static final String ATTR_FIELDNAME       ="fieldname";
    public static final String ATTR_FIELDS          ="fields";
    public static final String ATTR_FIRSTITEM       ="firstitem";
    public static final String ATTR_FPARENTDATAPATH ="fparentdatapath"; //copy from schema?
    public static final String ATTR_FTYPE           ="ftype";
    public static final String ATTR_HIDECOMMAND     ="hidecommand";     //copy from schema?
    public static final String ATTR_HREF            ="href";
    public static final String ATTR_ID              ="id";
    public static final String ATTR_INCLUDE         ="include";
    public static final String ATTR_INLINE          ="inline";
    public static final String ATTR_INSTANCE        ="instance";        //copy from schema?
    public static final String ATTR_INVALIDLIST     ="invalidlist";
    public static final String ATTR_ITEMDESCRIPTION ="itemdescription"; //created tag, and never be used in demo?
    public static final String ATTR_ITEMTITLE       ="itemtitle";       //created tag, and never be used in demo?
    public static final String ATTR_LANG            ="xml:lang";
    public static final String ATTR_LASTITEM        ="lastitem";
    public static final String ATTR_MAXOCCURS       ="maxoccurs";
    public static final String ATTR_MAYDELETE       ="maydelete";
    public static final String ATTR_MAYWRITE        ="maywrite";
    public static final String ATTR_MINOCCURS       ="minoccurs";
    public static final String ATTR_NAME            ="name";
    public static final String ATTR_NODEPATH        ="nodepath";
    public static final String ATTR_NUMBER          ="number";
    public static final String ATTR_OBJECTNUMBER    ="objectnumber";
    public static final String ATTR_ORDERBY         ="orderby";
    public static final String ATTR_ORDERTYPE       ="ordertype";
    public static final String ATTR_ORIGIN          ="origin";
    public static final String ATTR_ROLE            ="role";
    public static final String ATTR_ROWS            ="rows";
    public static final String ATTR_SEARCHDIR       ="searchdir";       //copy from schema?
    public static final String ATTR_SIZE            ="size";
    public static final String ATTR_SOURCE          ="source";
    public static final String ATTR_STATUS          ="status";
    public static final String ATTR_TYPE            ="type";
    public static final String ATTR_UPLOADED        ="uploaded";
    public static final String ATTR_VALUE           ="value";
    public static final String ATTR_WIZARDNAME      ="wizardname";
    

    public static final String SEARCHTYPE_LIKE = "like";
    public static final String SEARCHTYPE_EQUALS = "equals";
    public static final String SEARCHTYPE_NOTEQUALS = "notequals";
    public static final String SEARCHTYPE_GREATERTHAN = "greaterthan";
    public static final String SEARCHTYPE_LESSTHAN = "lessthan";
    public static final String SEARCHTYPE_NOTGREATERTHAN = "notgreaterthan";
    public static final String SEARCHTYPE_NOTLESSTHAN = "notlessthan";
    public static final String SEARCHTYPE_STRING = "string";

    public static final String SEARCHDIR_SOURCE = "source";
    public static final String SEARCHDIR_DESTINATION = "destination";
    public static final String SEARCHDIR_BOTH = "both";
    public static final String SEARCHDIR_ALL = "all";
    public static final String SEARCHDIR_EITHER = "either";

    public static final String CREATEDIR_SOURCE = "source";
    public static final String CREATEDIR_DESTINATION = "destination";
    public static final String CREATEDIR_EITHER = "either";
    
}
