package com.github.ghaskins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.dependency.CopyDependenciesMojo;

/**
 * 
 * @author ghaskins
 *
 * @extendsPlugin maven-dependency-plugin
 * @goal copy-dependencies
 * @phase process-sources
 */
public class CopyDepsMojo extends CopyDependenciesMojo {

	/**
	 * The output directory into which to copy the resources.
	 * 
	 * @parameter default-value="${project.build.directory}/SOURCES"
	 * @required
	 */
	private File resourceDirectory;

	public void execute() throws MojoExecutionException {
		outputDirectory = resourceDirectory;
		super.execute();
	}

}
