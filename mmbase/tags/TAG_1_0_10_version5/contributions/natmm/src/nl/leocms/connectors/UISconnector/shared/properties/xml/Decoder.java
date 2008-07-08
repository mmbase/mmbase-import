package nl.leocms.connectors.UISconnector.shared.properties.xml;

import java.util.ArrayList;
import org.w3c.dom.*;
import nl.leocms.connectors.UISconnector.shared.properties.model.*;



/**
 * <p>Title: PropertyDecoder</p>
 *
 * <p>Description: </p>
 * Converts a list of properties to object model
 *
 * @version 1.0
 */


public class Decoder
{
   public static ArrayList decode(Node node){

      ArrayList arliProperties = new ArrayList();

      NodeList nlPropertiesNodes = node.getChildNodes();

      for (int i = 0; i < nlPropertiesNodes.getLength(); i++)
      {
         Node nodeProperty = nlPropertiesNodes.item(i);

         if ("property".equals(nodeProperty.getNodeName()))
         {
            Property property = new Property();
            arliProperties.add(property);
            ArrayList arliPropertyValues = new ArrayList();
            property.setPropertyValues(arliPropertyValues);

            NodeList nlPropertyInnerNodes = nodeProperty.getChildNodes();
            for (int j = 0; j < nlPropertyInnerNodes.getLength(); j++)
            {
               Node nodePropertyInnerNode = nlPropertyInnerNodes.item(j);

               if ("propertyId".equals(nodePropertyInnerNode.getNodeName()))
               {
                  property.setPropertyId(nodePropertyInnerNode.getFirstChild().getNodeValue());
               }
               if ("propertyDescription".equals(nodePropertyInnerNode.getNodeName()))
               {
                  property.setPropertyDescription(nodePropertyInnerNode.getFirstChild().getNodeValue());
               }
               if ("propertyValue".equals(nodePropertyInnerNode.getNodeName()))
               {
                  PropertyValue propertyValue = new PropertyValue();
                  arliPropertyValues.add(propertyValue);

                  NodeList nlPropertyValueInnerNodes = nodePropertyInnerNode.getChildNodes();
                  for (int k = 0; k < nlPropertyValueInnerNodes.getLength(); k++)
                  {
                     Node nodePropertyValueInner = nlPropertyValueInnerNodes.item(k);

                     if ("propertyValueId".equals(nodePropertyValueInner.getNodeName()))
                     {
                        propertyValue.setPropertyValueId(nodePropertyValueInner.getFirstChild().getNodeValue());
                     }
                     if ("propertyValueDescription".equals(nodePropertyValueInner.getNodeName()))
                     {
                        propertyValue.setPropertyValueDescription(nodePropertyValueInner.getFirstChild().getNodeValue());
                     }
                  }
               }
            }
         }
      }

      return arliProperties;
   }
}
