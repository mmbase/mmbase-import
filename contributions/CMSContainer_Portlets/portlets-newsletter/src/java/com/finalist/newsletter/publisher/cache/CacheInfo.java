package com.finalist.newsletter.publisher.cache;
/**
 * @author nikko yin
 */
public class CacheInfo {

	private Object obj;
	private long secondsRemain;
	private long cacheSeconds;
	/**
	    * CacheInfoBean
	    * @param bean and time
	    */
	public CacheInfo(Object obj, long cacheSeconds) {
		this.obj = obj;
		this.secondsRemain = cacheSeconds;
		this.cacheSeconds = cacheSeconds;
	}
	/**
	* getObjInfoBean
	* @param null
	*/
	public Object getObj() {
		return obj;
	}
	/**
	* getSecondsRemain
	* @param null
	*/
	public long getSecondsRemain() {
		return secondsRemain;
	}
	/**
	* getTotalSeconds
	* @param null
	*/
	public long getTotalSeconds() {
		return cacheSeconds;
	}
	/**
	* setSecondsRemain
	* @param null
	*/
	public void setSecondsRemain(long secondsRemain) {
		this.secondsRemain = secondsRemain;
	}

}
