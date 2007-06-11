package nl.leocms.vastgoed;

import java.util.List;

public interface ShoppingBasket {
   
   public boolean addItem(Object item);
   public Object getItem(String itemIndex);
   public boolean removeItem(String itemIndex);
   public List getItems();
}
