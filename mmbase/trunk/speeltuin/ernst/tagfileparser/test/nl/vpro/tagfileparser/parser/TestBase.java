package nl.vpro.tagfileparser.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.*;

import junit.framework.TestCase;

/**
 * This base class for the tagfileparser junit tests
 * helps you to read tag files from the classpath.
 * @author ebunders
 *
 */
public abstract class TestBase extends TestCase{
	
	public TestBase(){
		super();
	}

	/**
	 * Reads a file as resource from the classpath, and creates a BufferedReader for it.
	 * @param resourceName
	 * @return
	 */
	protected BufferedReader loadFileAsReader(String resourceName){
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourceName);
		if(is == null){
			throw new RuntimeException("resource with name '"+resourceName+"' not found");
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		return r;
	}
	
	
	
	/**
	 * Reads the resource of given name, and creates a line iterator for it.
	 * @param filename
	 * @return
	 */
	protected Iterator<String> createFileIterator(String filename){
		BufferedReader br = loadFileAsReader(filename);
		List<String> result = new ArrayList<String>();
		String l;
		try {
			l = br.readLine();
			while (l != null) {
				result.add(l);
				l = br.readLine();
			}
			return result.iterator();
		} catch (IOException e) {
			throw new RuntimeException("something went wrong reading the resource '"+filename+"' :"+e.getMessage(), e);
		}
	}
}
