/* 

  license to be added 

*/
package nl.didactor.metadata.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.metadata.handlers.*;

/**
 * The Metadata builder returns the related or generated metavalue, this can be one of the following:
 * <ul>
 * <li>Metadate</li>
 * <li>Metavocabulary</li>
 * <li>Metalangstring</li>
 * <li>Value returned by handler</li>
 * </ul>
 * @author Gerard van Enk <gvenk@millionpieces.nl>
 * @version $Id: Metadata.java,v 1.1 2004-11-01 12:52:45 jdiepenmaat Exp $
 */
public class Metadata extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(Metadata.class);

    /**
     * Provides additional functionality when obtaining field values.
     * This method is called whenever a Node of the builder's type fails at
     * evaluating a getValue() request (generally when a fieldname is supplied
     * that doesn't exist).
     * <br />
     * The following virtual fields are calculated:
     * <br />
     * value - returns the related or generated metavalue as a String divided by spaces 
     * valueasarray  - returns the related or generated metavalue as an array
     *
     * @param node the node whose fields are queried
     * @param field the fieldname that is requested
     * @return the result of the call, <code>null</code> if no valid functions or virtual fields could be determined.
     */

    public Object getValue(MMObjectNode node, String field) {
        /*if (field.equals("value")) {
            List resultingArray = (ArrayList)getValue(node,"valueasarray");
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < resultingArray.size(); i++) {
                result.append((String)resultingArray.get(i));
                if (i +1 < resultingArray.size()) {
                    //have to change this from spaces into a different separator
                    result.append(" ");
                }
            }
            return result.toString();
            } else if (field.equals("valueasarray")){*/
        if ((field.equals("value")) || (field.equals("valueasarray"))) { 
            //check wheter node has been commited
            int number = node.getIntValue("number");
            if (number != -1) {
                //get related metadefinition
                Vector relatedDefinitions = node.getRelatedNodes("metadefinition");
                if (relatedDefinitions.size() > 0) {
                    //can only be one metadefinition related at a time
                    MMObjectNode metadefinition = (MMObjectNode)relatedDefinitions.get(0);
                    String handler = metadefinition.getStringValue("handler");
                    //check if there's a handler to be loaded, if not check type
                    if (handler == null || handler.equals("")) {
                        log.debug("no handler found, going to check type");
                        int type = metadefinition.getIntValue("type");
                        log.debug("metadefinition type = " + type);
                        int maxvalues = metadefinition.getIntValue("maxvalues");
                        //check related metadefinition type
                        switch (type) {
                            case 0:
                            case 4: return getMetalangstring(node,maxvalues);
                            case 1:
                            case 2: return getMetadate(node,maxvalues);
                            case 3: return getMetavocabulary(node, maxvalues);
                            default: break;
                        }
                        log.error("type " + type + " is not a know type");
                        return new ArrayList();
                    } else {
                        log.debug("trying to load the handler " + handler);
                        int maxvalues = metadefinition.getIntValue("maxvalues");
                        return getValueByHandler(node,metadefinition,maxvalues,handler);
                    }
                } else {
                    log.error("no related metadefinition found");
                    return new ArrayList();
                }
            } else {
                log.debug("this node hasn't been commited yet");
                return new ArrayList();
            }
        }
        return super.getValue(node, field);
    }

    /**
     * Gets the value(s) of the related Metalangstring(s)
     * @param node the metadata object
     * @param maxvalues the maximum number of return values
     * @return A List with String values
     */
    private List getMetalangstring(MMObjectNode node, int maxvalues) {
        log.debug("trying to get related Metalangstring value(s)");
        Vector relatedMetalangstrings = node.getRelatedNodes("metalangstring");
        String metalangstring = "";
        int size = relatedMetalangstrings.size();
        if (size > 0) {
            if (size > maxvalues) {
                size = maxvalues;
            }
            List result = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                metalangstring = (String)((MMObjectNode)relatedMetalangstrings.get(i)).getStringValue("value");
                if (metalangstring != null || !metalangstring.equals("")) {
                    result.add(metalangstring);
                }
            }
            return result;
        } else {
            log.error("no related metalangstrings found");
        }
        //return empty list if no related metalangstrings are found
        return new ArrayList();
    }

    /**
     * Gets the value(s) of the related Metadate(s)
     * @param node the metadata object
     * @param maxvalues the maximum number of return values
     * @return A List with String values
     */
    private List getMetadate(MMObjectNode node, int maxvalues) {
        log.debug("trying to get related Metadate value(s)");
        Vector relatedMetadates = node.getRelatedNodes("metadate");
        String metadate;
        int size = relatedMetadates.size();
        if (size > 0) {
            if (size > maxvalues) {
                size = maxvalues;
            }
            List result = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                metadate = (String)((MMObjectNode)relatedMetadates.get(i)).getStringValue("value");
                if (metadate != null) {
                    result.add(metadate);
                }
            }
            return result;
        } else {
            log.error("no related metadates found");
        }
        //return empty list if no related metadates are found
        return new ArrayList();
    }

    /**
     * Gets the value(s) of the related Metavocabulary(s)
     * @param node the metadata object
     * @param maxvalues the maximum number of return values
     * @return A List with String values
     */
    private List getMetavocabulary(MMObjectNode node, int maxvalues) {
        log.debug("trying to get related Metavocabulary value(s)");
        Vector relatedMetavocabularies = node.getRelatedNodes("metavocabulary");
        String metavocabulary = "";
        int size = relatedMetavocabularies.size();
        if (size > 0) {
            if (size > maxvalues) {
                size = maxvalues;
            }
            List result = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                metavocabulary = (String)((MMObjectNode)relatedMetavocabularies.get(i)).getStringValue("value");
                if (metavocabulary != null || !metavocabulary.equals("")) {
                    result.add(metavocabulary);
                }
            }
            return result;
        } else {
            log.error("no related metavocabularies found");
        }

        //return empty list if no related metavocabularies are found
        return new ArrayList();
    }

    /**
     * Gets the value(s) of the handler
     * @param node the metadata object
     * @param metadefinition the related metadefintion
     * @param maxvalues the maximum number of return values
     * @param handlerClassName the handler which must be used to get the value(s)
     * @return A List with String values
     */
    private List getValueByHandler(MMObjectNode node, MMObjectNode metadefinition, int maxvalues, String handlerClassName) {
        Vector relatedLearnobjects = node.getRelatedNodes("learnobjects");
        if (relatedLearnobjects.size() > 0) {
            //can only be one learnobject related at a time
            MMObjectNode learnobject = (MMObjectNode)relatedLearnobjects.get(0);
            log.debug("trying to load handler: " + handlerClassName);
            try {
                Class handlerClass = Class.forName(handlerClassName);
                MetadataHandler handler = (MetadataHandler)handlerClass.newInstance();
                return (List)handler.getMetadata(learnobject,metadefinition);
            } catch (ClassNotFoundException ex) {
                log.error("couldn't find handler with classname: " + handlerClassName);
                log.error(ex.getMessage());
            } catch (InstantiationException ex) {
                log.error("couldn't instantiate handler with classname: " + handlerClassName);
                log.error(ex.getMessage());
            } catch (Exception ex) {
                log.error("something went wrong with handler");
                log.error(ex.getMessage());
            }
        } else {
            log.error("no related learnobjects are found, so can't get metadata value");
        }
        //return empty list if there's an error occured
        return new ArrayList();
    }



}
