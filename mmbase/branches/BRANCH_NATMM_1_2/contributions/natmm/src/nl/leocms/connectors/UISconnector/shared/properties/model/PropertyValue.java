package nl.leocms.connectors.UISconnector.shared.properties.model;

public class PropertyValue
{
   private String propertyValueId;
   private String propertyValueDescription;

   public PropertyValue()
   {
   }

   public void setPropertyValueId(String propertyValueId)
   {
      this.propertyValueId = propertyValueId;
   }

   public void setPropertyValueDescription(String propertyValueDescription)
   {
      this.propertyValueDescription = propertyValueDescription;
   }

   public String getPropertyValueId()
   {
      return propertyValueId;
   }

   public String getPropertyValueDescription()
   {
      return propertyValueDescription;
   }
}
