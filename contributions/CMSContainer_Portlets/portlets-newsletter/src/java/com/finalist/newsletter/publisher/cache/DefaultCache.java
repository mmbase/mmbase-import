package com.finalist.newsletter.publisher.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * @author nikko yin
 */
public class DefaultCache implements ICache{

	private static final int FreshTimerIntervalSeconds = 1;    
	private Map<String, CacheInfo> datas;	
    private long time=1800;
	private Timer timer;

	public DefaultCache() {		
		//synchronized cache when instance it
		datas = Collections.synchronizedMap(new HashMap<String, CacheInfo>());
		//flush cache
		TimerTask task = new CacheFreshTask(this);
		timer = new Timer("Cache_Timer", true);		
        //flush when every second
		timer.scheduleAtFixedRate(task, 1000, FreshTimerIntervalSeconds * 1000);	   
	} 
	
     //	implement the interface	
	public DefaultCache(long time) {        
      this();	
		this.time=time;			
	}
	
	//* add
	public void add(Object key, Object value) {	   
		add(key, value,time);		
	}
	/**
	 * add
	 * @param Object , Object and long
	 */
	public void add(Object key, Object value, long slidingExpiration) {	   
		if(slidingExpiration!=0){			
			CacheInfo ci=new CacheInfo(value, slidingExpiration);			
			datas.put((String) key, ci);
		}
	}

	// contains
	public boolean contains(Object key) {	   
		if(datas.containsKey(key))return true;
		return false;
	}
	/**
	 * get
	 * @param Object
	 */
	public Object get(Object key) {	   
		if(datas.containsKey(key)){
			CacheInfo ci=datas.get(key);			
			//cahce'life will refresh when it's invoke ;)
			ci.setSecondsRemain(ci.getTotalSeconds());
			return ci.getObj();
		}
		return null;
	}
	/**
	 * remove
	 * @param Object
	 */
	public void remove(Object key) {	   
		datas.remove(key); 
	}
	/**
	 * removeAll
	 * @param null
	 */
	public void removeAll() {
	}
	/**
	 * getTime
	 * @param null
	 */
	public long getTime() {
		return time;
	}
	/**
	 * setTime
	 * @param time
	 */
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * getDatas
	 * @param null
	 */
	public Map<String, CacheInfo> getDatas() {
		return datas;
	}
}
