package com.github.ghaskins;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
/**
 * 
 * @author ghaskins
 * @goal compile
 * @phase compile
 */
public class CompileMojo extends AbstractMojo {

	/**
	 * The output directory into which to copy the resources.
	 * 
	 * @parameter default-value="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;
	
	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {

		File specFile = findSpecFile();
		String name = parseSpec("Name");
		String release = parseSpec("Release");
		
		StringBuilder command = new StringBuilder("rpmbuild");
		command.append(" --define \"_topdir ");
		command.append(outputDirectory);
		command.append("\" -bs ");
		command.append(specFile);
		
		runCommand(command.toString());
		
		StringBuilder archive = new StringBuilder();
		archive.append(outputDirectory);
		archive.append("/SRPMS/");
		archive.append(name);
		archive.append("-");
		archive.append(project.getVersion());
		archive.append("-");
		archive.append(release);
		archive.append(".src.rpm");
		
		getLog().debug("Assigning " + archive.toString() + " as artifact");
		
		project.getArtifact().setFile(new File(archive.toString()));
	}
	
	private void runCommand(String command) throws MojoExecutionException {
		getLog().info("Exec: " + command.toString());
		
		try	{
			Process process = Runtime.getRuntime().exec(command.toString());
			
			process.waitFor();
			int ret = process.exitValue();
			
			if (ret != 0)
				throw new MojoExecutionException("Exec failed");
			
		} catch(IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (InterruptedException e) {
			throw new MojoExecutionException(e.getMessage());
		}

	}
	
	private String parseSpec(String key) {
		return "";
	}
	
	private File findSpecFile() throws MojoExecutionException {

		String[] exts = new String[]{"spec"};
		Collection<File> specs = FileUtils.listFiles(outputDirectory, exts, true);
		
		switch (specs.size()) {
		case 1:
			return specs.iterator().next();
		case 0:
			throw new MojoExecutionException("no spec file found");
		default:
			throw new MojoExecutionException("multiple spec files found");
		}
	}

}
