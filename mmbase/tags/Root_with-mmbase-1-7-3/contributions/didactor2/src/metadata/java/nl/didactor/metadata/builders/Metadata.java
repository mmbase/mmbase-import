/*

  license to be added

*/
package nl.didactor.metadata.builders;

import java.util.*;
import java.text.SimpleDateFormat;

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
 * @version $Id: Metadata.java,v 1.3 2005-03-14 20:35:48 azemskov Exp $
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
     * valueasarray  - returns the related or generated metavalue as an array
     *
     * @param node the node whose fields are queried
     * @param field the fieldname that is requested
     * @return the result of the call, <code>null</code> if no valid functions or virtual fields could be determined.
     */

    public Object getValue(MMObjectNode node, String field)
    {
        if (field.equals("valueasarray")){
         // *** todo ***
        }
        return super.getValue(node, field);
    }

   public boolean commit(MMObjectNode node)
   {
     log.debug("trying to store values to \"value\" field.");
     Vector vector = node.getRelatedNodes("metadefinition");
     if(vector.size() > 0)
     {
        MMObjectNode nodeMetadefition = (MMObjectNode) vector.get(0);
        String sResult = "";
        int iType = nodeMetadefition.getIntValue("type");
        switch (iType)
        {
           case 1:
           {
              sResult = getMetavocabulary(node);
              log.debug("Metavocabulary id=" + node.getNumber());
              break;
           }
           case 2:
           {
              sResult = getMetadate(node);
              log.debug("Metadate id=" + node.getNumber());
              break;
           }
           case 3:
           {
              sResult = getMetalangstrings(node);
              log.debug("Metalangstrings id=" + node.getNumber());
              break;
           }
           case 4:
           {
              sResult = getMetaduration(node);
              log.debug("Metaduration id=" + node.getNumber());
              break;
           }
           default:
           {
              log.debug("Unknown type of node id=" + node.getNumber());
           }
           log.debug("commiting done with value " + sResult);
        }
        node.setValue("value", sResult);
     }
     else log.error("no metadefinition for node " + node.getNumber());
     return super.commit(node);
   }

    /**
     * Gets the value(s) of the related Metavocabulary(s)
     * @param node the metadata object
     * @return void
     */
    private String getMetavocabulary(MMObjectNode node)
    {
       String sResult = "";
       Vector vectMetavocabulary = node.getRelatedNodes("metavocabulary");
       for(Iterator it = vectMetavocabulary.iterator(); it.hasNext(); )
       {
          MMObjectNode nodeVocabulary = (MMObjectNode) it.next();
          sResult += " " + nodeVocabulary.getStringValue("value");
       }
       return sResult;
    }


    /**
     * Gets the value(s) of the related Metadate(s)
     * @param node the metadata object
     * @return void
     */
   private String getMetadate(MMObjectNode node)
   {
      String sResult = "";
      Vector relatedMetadates = node.getRelatedNodes("metadate");
      if(relatedMetadates.size() > 0)
      {
         long iDate = ( (MMObjectNode) relatedMetadates.get(0)).getLongValue("value");
         Date date = new Date(iDate * 1000);
         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         sResult = df.format(date);
      }
      return sResult;
   }

   /**
    * Gets the value(s) of the related Metalangstring(s)
    * @param node the metadata object
    * @return void
    */
   private String getMetalangstrings(MMObjectNode node)
   {
      Vector vectMetavocabulary = node.getRelatedNodes("metalangstring");
      String sResult = "";
      for(Iterator it = vectMetavocabulary.iterator(); it.hasNext(); )
      {
         MMObjectNode nodeVocabulary = (MMObjectNode) it.next();
         sResult += " " + nodeVocabulary.getStringValue("language") + "-" + nodeVocabulary.getStringValue("value");
      }
      return sResult;
   }

   /**
    * Gets the value(s) of the related Metalangstring(s)
    * @param node the metadata object
    * @return void
    */
   private String getMetaduration(MMObjectNode node)
   {
      Vector relatedMetadates = node.getRelatedNodes("metadate");
      String sResult = "";
      if(relatedMetadates.size() > 1)
      {
         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
         long iDateBegin = ( (MMObjectNode) relatedMetadates.get(0)).getLongValue("value");
         Date dateBegin = new Date(iDateBegin * 1000);
         long iDateEnd = ( (MMObjectNode) relatedMetadates.get(1)).getLongValue("value");
         Date dateEnd = new Date(iDateEnd * 1000);
         sResult = df.format(dateBegin) + " " + df.format(dateEnd);
      }
      return sResult;
   }
}
