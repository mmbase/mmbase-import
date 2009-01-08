package nl.leocms.vastgoed;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.util.logging.*;

public class ShoppingBasketImpl implements ShoppingBasket {
   
   private  List items;
   private static final Logger log = Logging.getLoggerInstance(ShoppingBasketImpl.class);
   
   public ShoppingBasketImpl() {
      items = new ArrayList();
   }
   
        /*
         * Adds an item to the shopping basket.
         */
   public boolean addItem(Object item){
      items.add(item);
      return true;
   }
   
        /*
         * Gets the item from the shopping basket.
         */
   public Object getItem(String itemIndex){
      try {
         Object object = items.get(Integer.parseInt(itemIndex));
         return object;
      } catch(Exception e) {
         // no item with the passed index or index format wrong
         log.debug("Exception: getItem() no item with index: " + itemIndex);
         return null;
      }
   }
   
        /*
         * Removes the item from the shopping basket.
         */
   public boolean removeItem(String itemIndex) {
      try {
         items.remove(Integer.parseInt(itemIndex));
         return true;
      } catch(Exception e) {
         // error in removing item with passed index
         log.debug("Exception: removeItem() error removing item with index: " + itemIndex);
         return false;
      }
   }
   
   // items list for iterate tag
   public List getItems() {
      return items;
   }
   
   
}
