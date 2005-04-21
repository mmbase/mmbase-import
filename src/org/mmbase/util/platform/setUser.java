package org.mmbase.util.platform;

public class setUser {
boolean needSetUser;
String osname;

	public setUser() {
		//System.out.println("java.library.path="+System.getProperty("java.library.path"));
		needSetUser=needSetUser();
		if (needSetUser) {
			System.loadLibrary("setUser");
		}
	}

	private boolean needSetUser() {
		boolean rtn=true;
		/* Figure out if need setuid/setgid */
		osname=System.getProperty("os.name");
		System.out.println("OS "+osname);
		if (osname.equals("Windows 95") || osname.equals("Windows NT")) {
			rtn=false;
		}
		return(rtn);
	}

	public boolean setUserGroup(String user,String group) {
		if (needSetUser) {
			return(setUserGroupNative(user,group));
		} else {
			return(true);
		}
	}

	private native boolean setUserGroupNative(String user,String group);

}
