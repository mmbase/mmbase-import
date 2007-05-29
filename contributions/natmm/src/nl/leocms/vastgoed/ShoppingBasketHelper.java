package nl.leocms.vastgoed;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;


public class ShoppingBasketHelper {

	private static final String SHOPPING_BASKET_SESSION_KEY = "vastgoed_shoppingbasket";
//	private static  ShoppingBasket basket;

	
	
//	public static ShoppingBasket getShoppingBasket(HttpServletRequest request) {
//		basket = (ShoppingBasket) request.getSession().getAttribute(SHOPPING_BASKET_SESSION_KEY);
//		if (basket == null) {
//			basket = new ShoppingBasketImpl();
//			request.getSession().setAttribute(SHOPPING_BASKET_SESSION_KEY, basket);
//		}
//		return basket;
//	}
	
	public static ArrayList getShoppingBasket(HttpServletRequest request) {
		ArrayList basket = (ArrayList) request.getSession().getAttribute(SHOPPING_BASKET_SESSION_KEY);
		if (basket == null) {
			basket = new ArrayList();
			request.getSession().setAttribute(SHOPPING_BASKET_SESSION_KEY, basket);
		}
		return basket;
}
	
}
