package org.charless.jboss.forge.plugins.qooxdoo;

import java.io.FileNotFoundException;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.Entity;

import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.events.CommandExecuted;
import org.jboss.forge.shell.events.CommandExecuted.Status;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * The Qooxdoo facet handles the installation of the Qooxdoo frameworks 
 * - Add the qooxdoo-maven-plugin dependency 
 * - Add qooxdoo-sdk dependency 
 * - Add specific qooxdoo properties
 * 
 * @author charless
 */
@RequiresFacet({ DependencyFacet.class, MavenPluginFacet.class, MavenCoreFacet.class })
public class QooxdooFacet extends BaseFacet {
	
	@Inject
	private Shell shell;
	
	private DependencyBuilder qooxdooMavenPluginDependency = DependencyBuilder.create()
					.setGroupId("org.qooxdoo")
					.setArtifactId("qooxdoo-maven-plugin");
	
	private DependencyBuilder qooxdooMavenSdkDependency = DependencyBuilder.create()
			.setGroupId("org.qooxdoo")
			.setArtifactId("qooxdoo-sdk");
	
	private final static String QOOXDOO_VERSION_PROPERTY_NAME = "qooxdoo.sdk.version";
					
	
	@Override
	public boolean install() {
		DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);	   
		
		/** QOOXDOO SDK */
		// Find version
	    List<Dependency> versions = dependencyFacet.resolveAvailableVersions(qooxdooMavenSdkDependency);
	    Dependency dep = shell.promptChoiceTyped(
				"Which version of the qooxdoo sdk do you want to install?",
				versions);
	    // Manage version as a pom property
	    String qooxdooVersion = dep.getVersion();
	    qooxdooMavenSdkDependency.setVersion("${"+QOOXDOO_VERSION_PROPERTY_NAME+"}");
	    // And add it to the POM
	    dependencyFacet.addDirectDependency(qooxdooMavenSdkDependency);
	    
	    
	    /** QOOXDOO PLUGIN */
	    // Find version
	    versions = dependencyFacet.resolveAvailableVersions(qooxdooMavenPluginDependency);
	    dep = shell.promptChoiceTyped(
				"Which version of the qooxdoo plugin do you want to install?",
				versions);
        
		// Create the plugin definition
	    MavenPluginBuilder qxPlugin = MavenPluginBuilder.create()
	    	    .setDependency(qooxdooMavenPluginDependency.setVersion(dep.getVersion()))
	    	    .setExtensions(false)
	    	    .createConfiguration()
	    	    .createConfigurationElement("useEmbeddedJython")
	    	    .setText("false").getParentPluginConfig().getOrigin()
	    	    .addExecution(ExecutionBuilder.create().setId("sdk-unpack").setPhase("initialize").addGoal("sdk-unpack"))
	    	    .addExecution(ExecutionBuilder.create().setId("generate-config").setPhase("generate-sources").addGoal("generate-config"))
	    	    .addExecution(ExecutionBuilder.create().setId("translation").setPhase("generate-resources").addGoal("translation"))
	    	    .addExecution(ExecutionBuilder.create().setId("generate-html").setPhase("process-resources").addGoal("generate-html"))
	    	    .addExecution(ExecutionBuilder.create().setId("compile").setPhase("compile").addGoal("compile"))
	    	    .addExecution(ExecutionBuilder.create().setId("test-compile").setPhase("test-compile").addGoal("test-compile"))
	    	    ;
	    // And add it to the POM
		project.getFacet(MavenPluginFacet.class).addPlugin(qxPlugin);
		
		/** PROPERTIES */
		MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();
		pom.getProperties().put(QOOXDOO_VERSION_PROPERTY_NAME, qooxdooVersion);
		mvnFacet.setPOM(pom);
		
		return true;
	}
	
	@Override
	public boolean isInstalled() {
		MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
		DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);	   
		
		return 		dependencyFacet.hasDirectDependency(qooxdooMavenSdkDependency)
				&& pluginFacet.hasPlugin(qooxdooMavenPluginDependency);
	}
	
	@Override
	public boolean uninstall() {
		// Remove plugin
		MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
		pluginFacet.removePlugin(qooxdooMavenPluginDependency);
		// Remove sdk
		DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);	   
		dependencyFacet.removeDependency(qooxdooMavenSdkDependency);
		// Remove properties
		MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();
		pom.getProperties().remove(QOOXDOO_VERSION_PROPERTY_NAME);
		mvnFacet.setPOM(pom);
		return true;
	}
	
	

}
