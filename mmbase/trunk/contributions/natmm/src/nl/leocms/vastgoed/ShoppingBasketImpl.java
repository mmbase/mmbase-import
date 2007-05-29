package nl.leocms.vastgoed;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class ShoppingBasketImpl implements ShoppingBasket, Iterator {

	private  List items;
	private  Iterator iterator;
	
//	static {
//		items = new ArrayList();
//		iterator = items.iterator();
//	}
	
	public ShoppingBasketImpl() {
		items = new ArrayList();
		iterator = items.iterator();
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
			 //LOG HERE
			 // no item with the passed index or index format wrong
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
			// LOG HERE
			// error in removing item with passed index
			return false;
		}
	}
	
	// struts iterate?
	
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
    public Object next() {
    	return iterator.next();
    }
    
    public void remove() {
    	iterator.remove();
    }

//	public Iterator getIterator() {
//		return iterator;
//	}
//
//	public void setIterator(Iterator iterator) {
//		this.iterator = iterator;
//	}

//public Iterator iterator() {
//	return iterator;
//}
	
	
	
	
	
//	public Object get(int index) {
//		return this.getItem(String.valueOf(index));
//	}
//	
//	//
//	public int size() {
//		return items.size();
//	}
//	
//	//
//	public Iterator iterator() {
//		return items.iterator();
//	}
//	
//	public ListIterator listIterator() {
//		return items.listIterator();
//	}
	
	
	
	
}
