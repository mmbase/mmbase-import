package org.mmbase.module.builders.vwms;

import java.util.*;
import java.net.*;
import java.sql.*;


import org.mmbase.module.builders.*;

public interface VwmServiceInterface extends VwmInterface {
	public boolean fileChange(String number,String ctype);
	public boolean fileChange(String service,String subservice,String filename);
}
