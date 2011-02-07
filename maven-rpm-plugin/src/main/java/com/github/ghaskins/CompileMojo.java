package com.github.ghaskins;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineUtils;

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

		SpecFile specFile = new SpecFile(outputDirectory);

		Map params = new HashMap();
		params.put("topdir", outputDirectory);
		params.put("specfile", specFile.file());
		
		CommandLine command = new CommandLine("rpmbuild");
		command.addArgument("--define");
		command.addArgument("_topdir ${topdir}", false);
		command.addArgument("-bs");
		command.addArgument("${specfile}");
		command.setSubstitutionMap(params);

		runCommand(command);

		StringBuilder archive = new StringBuilder();
		archive.append(outputDirectory);
		archive.append("/SRPMS/");
		archive.append(specFile.get("Name"));
		archive.append("-");
		archive.append(specFile.get("Version"));
		archive.append("-");
		archive.append(specFile.get("Release"));
		archive.append(".src.rpm");

		getLog().debug("Assigning " + archive.toString() + " as artifact");

		project.getArtifact().setFile(new File(archive.toString()));
	}

	private void runCommand(CommandLine cmdLine) throws MojoExecutionException {
		
		try	{
			DefaultExecutor executor = new DefaultExecutor();
			
			getLog().debug("Exec: " + cmdLine.toString());
			
			int ret = executor.execute(cmdLine, CommandLineUtils.getSystemEnvVars());
			if (ret != 0) {
				throw new MojoExecutionException("Exec failed: " + Integer.toString(ret));
			}			
		} catch(IOException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

}
