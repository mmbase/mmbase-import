/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;
 
import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
 
import javax.servlet.*;
import javax.servlet.http.*;
 
/**
 * WorkerPostHandler handles all the PostInformation 
 *
 * @version 1 oktober 
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Rob Vermeulen
 */
 

public class HttpPost { 
	private String classname = getClass().getName();
	private boolean debug = false;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	/**
     * are the postparameters decoded yet?
     */
    private boolean postDecoded;
 
	/**
     * if postParameters are decoded to disk they have this postid
     */
    private long postid;
 
    /**    
     * Maximum postparamtersize to decode the the parameters into memory
     */
    private int MaximumPostbufferSize=1500000; // 1024 switch to disk needs to be param

	/**
	* post buffer, holds the values ones decoded
	*/
	private Hashtable postValues=new Hashtable();

	/**
	 * maxFileSize for a property
	 */
	private int maxFileSize = 10485760; // 10 Mb
 
    /**
     * Some postparameters are decoded to disk
     */
    private boolean postToDisk=false;

    /**
     */
    private HttpServletRequest req=null;

	/** 	
	 * Initialise WorkerPostHandler
	 */
	public HttpPost(HttpServletRequest req) {
		postid = System.currentTimeMillis();
		this.req = req;
	}

	/**
	 * Destuctor removes the tmp-files 
	 */
	protected void finalize() {
		reset();
	}

	/** 	
	 * resets WorkerPosthandler
	 */
	public void reset() {
	   /* removing postValueFiles */
            if(postToDisk) {
                File f = new File("/tmp/");
                String[] files = f.list();
                for (int i=0;i<files.length;i++) {
                    if(files[i].indexOf("form_"+postid)==0){
                        File  postValueFile = new File("/tmp/"+files[i]);
                        postValueFile.delete();
                    }
                }
            }
            postid=0;           // reset postid value
            postToDisk=false;   // default, write postValues to memory
	} 

    /**
     * @return the maximumsize of the postparametervalues to decode into memory
     */
    /*
    public int getMaximumPostbufferSize() {
        return MaximumPostbufferSize;
    }

	/**
    * This method checks if the parameter is a multivalued one
    * (as returned by a multiple select form) it returns true
    * if so and false if not. It will also return false when
    * the parameter doesn't exist.
    * @see getPostMultiParameter
    * @see getPostParameter
    */
    public boolean checkPostMultiParameter(String name) {
        Object obj;
 
        if (!postDecoded) decodePost(req);
        if ((obj=postValues.get(name))!=null) {
            if (obj instanceof Vector) {
                return(true);
            } else {
                return(false);
            }
        } else {
            return(false);
        }
    }
 
    /**
    * This method returns the Hashtable containing the POST information.
    * Use of this method is discouraged.
    * Instead use getPostMultiParameter, getPostParameter and checkPostMulitparameter
    * @see getPostMultiParameter
    * @see getPostParameter
    * @see checkPostMultiParameter
    */
    public Hashtable getPostParameters() {
        if (!postDecoded) decodePost(req);
        return(postValues);
    }
 
	/**
    * This method returns the value of the postparameter as a String.
    * If it is a parameter with multiple values it returns the first one.
    * @see getPostMultiParameter
    * @see checkPostMultiParameter
    * @exception PostValueToLargeException will be thrown when the postParameterValue
    * is saved on disk instead of memory.
    */
    public byte[] getPostParameterBytes(String name) throws PostValueToLargeException{
        Object obj=null;
        Vector v;
 
        if (!postDecoded) decodePost(req);
        if ((obj=postValues.get(name))!=null) {
            if (obj instanceof String)
            throw new PostValueToLargeException("Use getPostParameterFile");
            if (obj instanceof Vector) {
                v=(Vector)obj;
                return((byte[])v.elementAt(0));
            } else {
                return((byte[])obj);
            }
        } else {
            return(null);
        }
    }
 
    /**
    * This method returns the value of the postparameter as a Vector.
    * In case of a parameter with one value, it returns it as a Vector
    * with one element.
    * it laso converts the byte[] into strings
    * @see checkPostMultiParameter
    */
    public Vector getPostMultiParameter(String name) {
        Object obj=null;
        Vector v;
 
        if (!postDecoded) decodePost(req);
        if ((obj=postValues.get(name))!=null) {
            if (obj instanceof Vector) {
                Vector results= new Vector();
                for (Enumeration t=((Vector)obj).elements();t.hasMoreElements();) {
                    results.addElement(new String((byte[])t.nextElement(),0));
                }
                return(results);
            } else {
                v=new Vector();
                v.addElement(new String((byte[])obj,0));
                return(v);
            }
        } else {
            return(null);
        }
    }
    
	/**
    * This method returns the filename containing the postparametervalue
    * If it is a parameter with multiple values it returns the first one.
    * @see getPostMultiParameter
    * @see checkPostMultiParameter
    * @see getMaximumPostbufferSize
    */
    public String getPostParameterFile(String name) {
        Object obj=null;
        Vector v;
 
        if (!postDecoded) decodePost(req);
        if ((obj=postValues.get(name))!=null) {
 
            // convert byte[] into filename
            if (!(obj instanceof String)) {
                postToDisk=true;
                String filename = "/tmp/form_"+postid+"_"+name;
                RandomAccessFile raf=null;
                try {
                    raf = new RandomAccessFile(filename,"rw");
                    if (obj instanceof Vector) {
                        v=(Vector)obj;
                        raf.write((byte[])v.elementAt(0));
			if(raf.length()>=maxFileSize) {
            		throw new FileToLargeException( classname +":"+"getPostParameterFile("+name+"): file too large("+filename+","+raf.length()+")");
			}
                    } else {
                        raf.write((byte[])obj);
                    }
                    raf.close();
				} catch (FileToLargeException f) {
					reset();
                    debug("getPostParameterFile("+name+"): ERROR: File is too large!: "+f);
                } catch (Exception e) {
                    debug("getPostParameterFile("+name+"): ERROR: "+e);
                } 
                return(filename);
            }
 
            if (obj instanceof Vector) {
                v=(Vector)obj;
                return((String)v.elementAt(0));
            } else {
                return((String)obj);
            }
        } else {
            return(null);
        }
    }
 

	 /**
    * This method returns the value of the postparameter as a String.
    * If it is a parameter with multiple values it returns the first one.
    * @see getPostMultiParameter
    * @see checkPostMultiParameter
    */
    public String getPostParameter(String name) {
        Object obj=null;
        Vector v;
 
        if (!postDecoded) decodePost(req);
        if ((obj=postValues.get(name))!=null) {
            try {
                if (obj instanceof String)
                throw new PostValueToLargeException("Use getPostParameterFile");
            // Catch the exception right here, it should be thrown but
            // that's against the Servlet-API Interface
            } catch (Exception e) {
                debug("getPostParameter("+name+"): ERROR: "+e);
            }
            if (obj instanceof Vector) {
                v=(Vector)obj;
                return(new String((byte[])v.elementAt(0),0));
            } else {
                return(new String((byte[])obj,0));
            }
        } else {
            return(null);
        }
    }

	private void decodePost(HttpServletRequest req) {
        postDecoded=true;
		byte[] postbuffer=null;
        if (req.getHeader("Content-length")!=null || req.getHeader("Content-Length")!=null) {
                postbuffer=readContentLength(req);
                String line=(String)req.getHeader("Content-type");
                if (line==null) line=(String)req.getHeader("Content-Type");
                if (line!=null && line.indexOf("application/x-www-form-urlencoded")!=-1) {
                    //System.out.println("Worker -> application/x-www-form-urlencoded");
                    readPostUrlEncoded(postbuffer,postValues);
                } else if (line!=null && line.indexOf("multipart/form-data")!=-1) {
                    //System.out.println("Worker -> multipart/form-data");
                    if(!postToDisk) {
                    	//System.out.println("Worker -> read to memory");
                        readPostFormData(postbuffer,postValues,line);
                    } else {
                        readPostFormData("/tmp/form_"+postid,postValues,line);
                    }
                }
            } else {
				debug("decodePost(): found no 'content-length' tag in post.");
			}
    }
 
  


	/**
    * read a block into a array of ContentLenght size from the users networksocket
    *
    * @param table the hashtable that is used as the source for the mapping process
    * @return byte[] buffer of length defined in the content-length mimeheader
    */
    public byte[] readContentLength(HttpServletRequest req) {
        int len,len2,len3;
        byte buffer[]=null;
		DataInputStream	connect_in=null ;
 
		try {
	 		connect_in=new DataInputStream(req.getInputStream());
		} catch(Exception e) {
		}

        len=req.getContentLength();
        // Maximum postsize
        if (len<MaximumPostbufferSize) {
            if( debug ) debug("readContentLength(): writing to memory.");
            try {
                buffer=new byte[len];
                // can come back before done len !!!!

		// HUGE hack to counter a bug i can't find. for some reason
		// i get a extra \10.
		/*
		int x=connect_in.read();
		if (x==10) {
               		len2=connect_in.read(buffer,0,len);
		} else {
			buffer[0]=(byte)x;
               		len2=connect_in.read(buffer,1,len-1);
		}
		*/
                len2=connect_in.read(buffer,0,len);
                //len2=connect_in.read(buffer,0,64*1024);
                while (len2<len) {
                    if( debug ) debug("readContentLength(): found len2( "+len2+")");
                    len3=connect_in.read(buffer,len2,len-len2);
                    if (len3==-1) {
                        if( debug ) debug("readContentLength(): WARNING: EOF while not Content Length");
                        break;
                    } else {
                        len2+=len3;
                    }
                }
            } catch (Exception e) {
                debug("readContentLength(): ERROR: Can't read post msg from client");
            }
		} else {
            if( debug ) debug("readContentLength(): writing to disk" );
            try {
                postToDisk=true;
                RandomAccessFile raf = new RandomAccessFile("/tmp/form_"+postid,"rw");
                int bufferlength=64000;
                buffer=new byte[bufferlength];
                int index1=0,totallength=0;
 
                index1=connect_in.read(buffer);
                raf.write(buffer,0,index1);
		if(raf.length()>=maxFileSize) {
            		throw new FileToLargeException( classname+":"+"readContentLength(): ERROR: size too large("+raf.length()+") for file(/tmp/form_"+postid+")");
		}
                totallength+=index1;
                if( debug ) debug("readContentLength(): writing to disk: +");
 
                while (totallength<len) {
                    index1=connect_in.read(buffer);
                    raf.write(buffer,0,index1);
		if(raf.length()>=maxFileSize) {
            		throw new FileToLargeException("WorkerPostHandler  (getPostParameterFile) -> "+postid);
		}
                    if (index1==-1) {
                        debug("readContentLength(): ERROR: EOF while not Content Length");
                        break;
                    } else {
                        totallength+=index1;
                        System.out.print("+");
                    }
                }
                debug(" written("+totallength+")");
                raf.close();
			} catch (FileToLargeException f) {
				reset();
                debug("readContentLength(): ERROR: "+f);
            } catch (Exception e) {
                debug("readContentLength(): ERROR: "+e);
            }
        }
        return(buffer);
    }
  
	/**
    * read post info from buffer, must be defined in multipart/form-data format.
    *
    * @param postbuffer buffer with the postbuffer information
    * @param post_header hashtable to put the postbuffer information in
    */ 
    public boolean readPostFormData(byte[] postbuffer,Hashtable post_header, String line) {
            int nentrys=0,i,i2,i3,i4,idx,start,end,start2,end2;
            String templine4=null;
            String r,r2;
            String templine="--"+line.substring(line.indexOf("boundary=")+9);
            byte[] marker = new byte[templine.length()];
            byte[] marker2 = new byte[4];
            byte[] marker3 = new byte[1];
            byte[] marker4 = new byte[1];
            byte[] dest;
            marker2[0]=(byte)'\r';marker2[1]=(byte)'\n';
            marker2[2]=(byte)'\r';marker2[3]=(byte)'\n';
            marker3[0]=(byte)'=';
            marker4[0]=(byte)'\"';
            templine.getBytes(0,templine.length(),marker,0);
 
//          if( debug ) debug("readPostFormData(): entered");
            // find first magic cookie
            start2=indexOf(postbuffer,marker,0)+marker.length;
 
            do {
                // hunt second one
                end2=indexOf(postbuffer,marker,start2);
				if (end2<0) {
					debug("readPostFormData(): postbuffer < 0 !!!! ");
					 break;
				}
 
                // get all the data in between
                i2=indexOf(postbuffer,marker2,start2);
 
                i3=indexOf(postbuffer,marker3,start2+2)+2;
                i4=indexOf(postbuffer,marker4,i3+2);
                r=new String(postbuffer,0,i3,(i4-i3));
                // if( debug ) debug("readPostFormData(): r="+r);
                // copy it to a buffer
                dest = new byte[(end2-i2)-6];
                System.arraycopy(postbuffer, i2+4, dest, 0, (end2-i2)-6);
                // r2=new String(postbuffer,0,i2+4,((end2-i2))-6);
 
                addpostinfo(post_header,r,dest);
                start2=end2+marker.length;
            } while (postbuffer[start2]!='-');
            //if( debug ) debug("readPostFormData(): post handled.");
        return(false);
    }

	  /**
    * read post info from buffer, must be defined in multipart/form-data format.
    * Deze methode gaat in het beginsel alleen werken voor het uploaden van 1 bestand
    * De te vinden markers kunnen anders op de scheiding liggen van 2 blokken
    * Het kan dus voorkomen dat de marker op de scheiding ligt van 2 blokken ook dan
    * zal deze methode falen.
    *
    * @param postbuffer buffer with the postbuffer information
    * @param post_header hashtable to put the fromFile information in
    */
    public boolean readPostFormData(String formFile, Hashtable post_header, String line) {
            //if( debug ) debug("readPostFormData(): New readPostFormDate toDisk");
            FileInputStream fis=null;
            RandomAccessFile raf=null;
            try {
                fis = new FileInputStream(formFile);
            } catch (Exception e) {
                System.out.println("WorkerPostHandler -> File "+formFile +" not exist");
            }
            int nentrys=0,i,i2,i3,i4,idx,start,end,start2,end2;
            String templine4=null;
            String r,r2;
            String templine="--"+line.substring(line.indexOf("boundary=")+9);
            byte[] marker = new byte[templine.length()];
            byte[] marker2 = new byte[4];
            byte[] marker3 = new byte[1];
            byte[] marker4 = new byte[1];
            byte[] dest;
            marker2[0]=(byte)'\r';marker2[1]=(byte)'\n';
            marker2[2]=(byte)'\r';marker2[3]=(byte)'\n';
            marker3[0]=(byte)'=';
            marker4[0]=(byte)'\"';
            templine.getBytes(0,templine.length(),marker,0);
            debug("readPostFormData(): begin");
 
            int offset=0;
            int temp=0;
            int len=64000;
            byte postbuffer[] = new byte[len];
            try {
                // Lees eerst stuk van het bestand.
                len = fis.read(postbuffer);
 
                    // find first magic cookie
                    start2=indexOf(postbuffer,marker,0)+marker.length;
                do {
                    // Get keyword
                    i3=indexOf(postbuffer,marker3,start2+2)+2;
                    i4=indexOf(postbuffer,marker4,i3+2);
                    r=new String(postbuffer,0,i3,(i4-i3));
                    //if( debug ) debug("readPostFormData(): postName="+r);
 
                    // hunt second one
                    end2=indexOf(postbuffer,marker,start2);
                    i2=indexOf(postbuffer,marker2,start2);
 
                    if(end2==-1) {
                        // Sjeetje dit moet het bestand zijn het is immers zoooo groot
                            debug("readPostFormData(): writing to postValue: ");
                            raf = new RandomAccessFile("/tmp/form_"+postid+"_"+r,"rw");
                            addpostinfo(post_header,r,"/tmp/form_"+postid+"_"+r);
                            try {
                                raf.write(postbuffer,i2+4,len-(i2+4));
                            } catch (Exception e) {
                                debug("readPostFormData(): ERROR: Cannot write into file(1)"+e);
                            }
                            offset=len-i2+4;
                        do {
                            try {
                                temp = fis.read(postbuffer);
                            } catch (Exception e) {
                                debug("readPostFormData(): ERROR: while reading: "+e);
                            }
 
                            end2=indexOf(postbuffer,marker,0);
                            try {
                                if(end2==-1) {
                                    raf.write(postbuffer);
                                } else {
                                    raf.write(postbuffer,0,end2-2);
                                }
                            offset+=len;
                            } catch (Exception e) {
                                debug("readPostFormData(): ERROR: Cannot write into file (2)"+e);
                            }
//                            System.out.print("-");
                            } while (end2==-1);
                        start2=end2+marker.length;
                        System.out.println();
                        raf.close();
                    } else {
                        dest = new byte[(end2-i2)-6];
                        System.arraycopy(postbuffer, i2+4, dest, 0, (end2-i2)-6);
 
                        addpostinfo(post_header,r,dest);
                        start2=end2+marker.length;
                    }
		if(raf.length()>=maxFileSize) {
            		throw new FileToLargeException("WorkerPostHandler  (getPostParameterFile) -> "+postid);
		}
                } while (postbuffer[start2]!='-');
			} catch (FileToLargeException f) {
				reset();
                debug("readPostFormData(): ERROR: "+f);
            } catch (Exception e) {
                debug("readPostFormData(): Reached end of file: "+e);
            }
        return(false);
    }
 
	private final void addpostinfo(Hashtable postinfo,String name,Object value) {
        Object obj;
        Vector v;
 
        if (postinfo.containsKey(name)) {
            obj=postinfo.get(name);
            if (obj instanceof byte[]) {
                v=new Vector();
                v.addElement((byte[])obj); // rico also add the first one ?
                v.addElement(value);
                postinfo.put(name,v);
            } else if (obj instanceof Vector) {
                v=(Vector)obj;
                v.addElement(value);
            } else {
                debug("addpostinfo("+name+","+value+"): mirror mirror on the wall, who has been fiddling with my Vector?");
            }
        } else {
            postinfo.put(name,value);
        }
    }
 
 
    private final void addpostinfo2(Hashtable postinfo,String name,String values) {
        Object obj;
        Vector v;
 
        byte[] value = new byte[values.length()];
        values.getBytes(0,values.length(),value,0);
 
        if (postinfo.containsKey(name)) {
            obj=postinfo.get(name);
            if (obj instanceof byte[]) {
                v=new Vector();
                v.addElement((byte[])obj); // rico also add the first one ?
                v.addElement(value);
                postinfo.put(name,v);
            } else if (obj instanceof Vector) {
                v=(Vector)obj;
                v.addElement(value);
            } else {
                debug("addpostinfo2("+name+","+value+"): mirror mirror on the wall, who has been fiddling with my Vector?");
            }
        } else {
            postinfo.put(name,value);
        }
    }
 

	/**
    * read post info from buffer, must be defined in UrlEncode format.
    *
    * @param postbuffer buffer with the postbuffer information
    * @param post_header hashtable to put the postbuffer information in
    */
    private boolean readPostUrlEncoded(byte[] postbuffer,Hashtable post_header) {
        String mimestr="";
        int nentrys=0,i=0,idx;
        char letter;
 
        String buffer = new String(postbuffer,0);
        buffer=buffer.replace('+',' ');
        StringTokenizer tok = new StringTokenizer(buffer,"&");
        while (tok.hasMoreTokens()) {
            mimestr=tok.nextToken();
            if ((idx=mimestr.indexOf('='))!=-1) {
                while ((i=mimestr.indexOf('%',i))!=-1) {
                    // Unescape the 'invalids' in the buffer (%xx) form
                    try {
                        letter=(char)Integer.parseInt(mimestr.substring(i+1,i+3),16);
                        mimestr=mimestr.substring(0,i)+letter+mimestr.substring(i+3);
                    } catch (Exception e) {
                    }
                    i++;
                }
                addpostinfo2(post_header,mimestr.substring(0,idx),mimestr.substring(idx+1));
            } else {
                addpostinfo2(post_header,mimestr,"");
            }
        }
        return(true);
    }
 	

	/**
     * gives the index of a bytearray in a bytearray
     *
     * @param v1[] The bytearray the search in.
     * @param v2[] The bytearray to find.
     * @param fromindex An index ti search from.
     * @return The index of v2[] in v1[], else -1
     */
    private int indexOf(byte v1[], byte v2[], int fromIndex) {
 
    int max = (v1.length - v2.length);

		// Yikes !!! Bij gebruik van continue test wordt de variable i (in de for) niet
	  	// opnieuw gedeclareerd. continue test kan gezien worden als ga verder met de for.
		// test is dus zeker GEEN label. 
      test:
        for (int i = ((fromIndex < 0) ? 0 : fromIndex); i <= max ; i++) {
        int n = v2.length;
        int j = i;
        int k = 0;
        while (n-- != 0) {
            if (v1[j++] != v2[k++]) {
                continue test;
            }
        }
        return i;
    }
    return -1;
    }
}
