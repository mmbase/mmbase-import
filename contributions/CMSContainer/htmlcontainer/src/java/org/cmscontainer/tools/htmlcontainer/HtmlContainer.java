package org.cmscontainer.tools.htmlcontainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class HtmlContainer {

	private static final String FILE_SEPERATOR = System
			.getProperty("file.separator");

	private HashMap<String, String> views = new HashMap<String, String>();

	private ArrayList<Layout> layouts = new ArrayList<Layout>();

	public HtmlContainer(String path) {
		init(path);
	}

	private void init(String path) {
		File pathFile = new File(path);
		if (!pathFile.isDirectory()) {
			System.out
					.println("Given path is not a valid directory!\nUsage: HtmlContainer <directory/zipfile>");
			System.exit(1);
		}

		File configPathFile = new File(path + FILE_SEPERATOR + "config");
		if (!configPathFile.isDirectory()) {
			System.out
					.println("Given path does not have a valid config directory!\nUsage: HtmlContainer <directory/zipfile>");
			System.exit(1);
		}

		File layoutPathFile = new File(path + FILE_SEPERATOR + "layout");
		if (!layoutPathFile.isDirectory()) {
			System.out
					.println("Given path does not have a valid layout directory!\nUsage: HtmlContainer <directory/zipfile>");
			System.exit(1);
		}

		File viewPathFile = new File(path + FILE_SEPERATOR + "view");
		if (!viewPathFile.isDirectory()) {
			System.out
					.println("Given path does not have a valid view directory!\nUsage: HtmlContainer <directory/zipfile>");
			System.exit(1);
		}

		loadViews(views, "", viewPathFile);
		loadLayouts(layouts, layoutPathFile, configPathFile);

		System.out.println("Layouts: " + layouts.size());

		System.out.println("Views: " + views.size());
	}

	private void loadLayouts(ArrayList<Layout> layouts, File layoutPathFile,
			File configPathFile) {
		for (File layoutFile : layoutPathFile.listFiles()) {
			if(layoutFile.getName().endsWith(".html")) {
				String html = loadFile(layoutFile);
				File configFile = new File(configPathFile, layoutFile.getName()
						.substring(0, layoutFile.getName().indexOf("."))
						+ ".properties");
				if (configFile == null) {
					System.out.println("No config file found for the layout: "
							+ layoutFile
							+ " \nUsage: HtmlContainer <directory/zipfile>");
					System.exit(1);
				}
				Properties properties = new Properties();
				try {
					properties.load(new FileInputStream(configFile));
				} catch (IOException e) {
					System.out
							.println("Problem loading config file for the layout: "
									+ layoutFile
									+ " \nUsage: HtmlContainer <directory/zipfile>");
					System.exit(1);
				}
				layouts.add(new Layout(layoutFile.getName(), html, properties));
			}
		}

	}

	private void loadViews(HashMap<String, String> views, String extraPath,
			File file) {
		for (File subFile : file.listFiles()) {
			if (subFile.isDirectory()) {
				loadViews(views,
						extraPath + FILE_SEPERATOR + subFile.getName(), subFile);
			} else {
				String view = loadFile(subFile);
				views.put(extraPath + FILE_SEPERATOR + subFile.getName(), view);
			}
		}
	}

	private String loadFile(File file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			StringBuffer output = new StringBuffer();
			char buffer[] = new char[1000];
			int read = 0;

			while ((read = in.read(buffer)) != -1) {
				output.append(buffer,0,read);
			}
			in.close();

			return output.toString();
		} catch (IOException e) {
			// TODO exception
			System.out.println(e);
		}

		return null;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: HtmlContainer <directory/zipfile>");
			System.exit(1);
		}

		String path = args[0];
		if (path.endsWith(".zip")) {
			path = upzip(path, "work");
		}

		HtmlContainer container = new HtmlContainer(path);
		ExampleGenerator generator = new ExampleGenerator(container);
		
		String output = generator.generateRandom();
		try {
			FileOutputStream fos = new FileOutputStream(path+"/examples/test.html");
			fos.write(output.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String upzip(String zipPath, String workPath) {

		// TODO unzip
		return workPath;
	}

	public int getLayoutCount() {
		return layouts.size();
	}

	public Layout getLayout(int i) {
		return layouts.get(i);
	}

	public String getViewHtml(String viewName) {
		return views.get("/"+viewName+".html");
	}

}
