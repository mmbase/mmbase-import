package nl.leocms.connectors.UISconnector.shared.properties.model;

import java.util.*;

public class Property
{
   private String propertyId;
   private String propertyDescription;
   private ArrayList propertyValues;

   public Property()
   {
   }

   public void setPropertyId(String propertyId)
   {
      this.propertyId = propertyId;
   }

   public void setPropertyDescription(String propertyDescription)
   {
      this.propertyDescription = propertyDescription;
   }

   public void setPropertyValues(ArrayList propertyValues)
   {
      this.propertyValues = propertyValues;
   }

   public String getPropertyId()
   {
      return propertyId;
   }

   public String getPropertyDescription()
   {
      return propertyDescription;
   }

   public ArrayList getPropertyValues()
   {
      return propertyValues;
   }
}
