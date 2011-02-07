package com.github.ghaskins;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

public class SpecFile extends Object {

	private File m_file;
	private Properties m_properties = new Properties();
	
	public SpecFile(File searchRoot) throws MojoExecutionException {
		String[] exts = new String[] { "spec" };
		Collection<File> specs = FileUtils.listFiles(searchRoot, exts, true);

		switch (specs.size()) {
		case 1:
			m_file = specs.iterator().next();
			break;
		case 0:
			throw new MojoExecutionException("no spec file found");
		default:
			throw new MojoExecutionException("multiple spec files found");
		}
		
		parse();
	}
	
	private void parse() throws MojoExecutionException {
		
		try {
			FileReader reader = new FileReader(m_file.getAbsolutePath());
			Scanner scanner = new Scanner(reader);
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String delims = ":[ ]+";
				String[] tokens = line.split(delims);
				
				if (tokens.length == 2)
					m_properties.setProperty(tokens[0], tokens[1]);	
			}

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}
	
	public String get(String key) throws MojoExecutionException {
		String value = m_properties.getProperty(key);
		if (value == null)
			throw new MojoExecutionException("Property \"" + key + "\" not found in spec file");
		
		return value;
	}
	
	public File file() {
		return m_file;
	}
}
