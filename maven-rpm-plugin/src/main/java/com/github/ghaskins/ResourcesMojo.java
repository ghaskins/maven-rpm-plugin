package com.github.ghaskins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

/**
 * 
 * @goal resources
 * @phase process-resources
 */
public class ResourcesMojo extends AbstractMojo {
    /**
     * The character encoding scheme to be applied when filtering resources.
     *
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;
	
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

	/**
	 * @parameter default-value="${session}"
	 * @readonly
	 * @required
	 */
	protected MavenSession session;

	/**
	 * 
	 * @component 
	 *            role="org.apache.maven.shared.filtering.MavenResourcesFiltering"
	 *            role-hint="default"
	 * @required
	 */
	protected MavenResourcesFiltering filtering;

	public void execute() throws MojoExecutionException {
		try {
			Resource rpmResource = new Resource();

			// be opinionated about the resources we want to process
			rpmResource.setDirectory("src/main/rpm");
			rpmResource.setFiltering(true);

			List<Resource> resources = new ArrayList<Resource>(
					Arrays.asList(rpmResource));

			MavenResourcesExecution exe = new MavenResourcesExecution(
					resources, outputDirectory, project, encoding,
					Collections.EMPTY_LIST, Collections.EMPTY_LIST, session);

			exe.setOverwrite(true);
			filtering.filterResources(exe);
		} catch (MavenFilteringException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}
}
