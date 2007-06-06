package nl.leocms.vastgoed;

public interface ShoppingBasket {
   
   public boolean addItem(Object item);
   public Object getItem(String itemIndex);
   public boolean removeItem(String itemIndex);
}
