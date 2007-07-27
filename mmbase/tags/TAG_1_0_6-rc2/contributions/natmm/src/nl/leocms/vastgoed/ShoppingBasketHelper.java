package nl.leocms.vastgoed;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ShoppingBasketHelper {
   
   // the jsp's accessing the bean in session will have to use this value
   private static final String SHOPPING_BASKET_SESSION_KEY = "vastgoed_shoppingbasket";
   
   public static ShoppingBasket getShoppingBasket(HttpServletRequest request) {
      ShoppingBasket basket = (ShoppingBasket) request.getSession().getAttribute(SHOPPING_BASKET_SESSION_KEY);
      if (basket == null) {
         basket = new ShoppingBasketImpl();
         request.getSession().setAttribute(SHOPPING_BASKET_SESSION_KEY, basket);
      }
      return basket;
   }
   
   public static void removeShoppingBasket(HttpServletRequest request) {
       request.getSession().setAttribute(SHOPPING_BASKET_SESSION_KEY, new ShoppingBasketImpl());
   }
   
}
