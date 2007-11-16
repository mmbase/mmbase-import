package com.finalist.newsletter;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.mail.Message;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

public abstract class NewsletterGenerator {

	private String publicationNumber;
	
	public NewsletterGenerator(String publicationNumber) {
				this.publicationNumber = publicationNumber;
	}

	protected abstract Message generateNewsletterContent(String userName);

	protected String getContent(String userName) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node publicationNode = cloud.getNode(publicationNumber);
		// TODO create URL
		String url = "";
		String content = "";

		try {
			URL u = new URL(url);
			URLConnection uc = u.openConnection();
			HttpURLConnection huc = (HttpURLConnection) uc;
			huc.addRequestProperty("username", userName);
			huc.addRequestProperty("publicationnumber", publicationNumber);

			InputStream raw = huc.getInputStream();
			InputStream buffer = new BufferedInputStream(raw);
			Reader r = new InputStreamReader(buffer);
			StringBuffer contentBuffer = new StringBuffer();
			int c;
			while ((c = r.read()) != -1) {
				contentBuffer.append(c);
			}
			content = contentBuffer.toString();
		} catch (Exception ex) {

		}
		return (content);
	}
}