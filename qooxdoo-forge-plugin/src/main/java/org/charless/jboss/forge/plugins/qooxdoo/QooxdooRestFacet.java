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
public class QooxdooRestFacet extends BaseFacet {
	
	@Inject
	private Shell shell;
	
   @Inject
   @Current
   private Resource<?> currentResource;
	
	@Override
	public boolean install() {
		return true;
	}
	
	@Override
	public boolean isInstalled() {
		return true;
	}
	
	@Override
	public boolean uninstall() {
		return true;
	}
	
	void postRestCommand(@Observes final CommandExecuted event) throws FileNotFoundException
	{
		if (	"rest".equals(event.getCommand().getParent().getName())
				&& "endpoint-from-entity".equals(event.getCommand().getName())
				&& event.getStatus() == Status.SUCCESS) 
		{
					// Successfully ran command: rest endpoint-from-entity ...
					// Get back args
					String contentType =  (String)event.getParameters()[1];
					ShellMessages.error(shell, "QOOXDOO-REST: contentType="+contentType);
					
					ShellMessages.info(shell, Integer.toString(event.getCommand().getResourceScopes().size()));
					
					JavaResource[] targets;
					if (event.getParameters().length > 2) {
						for (Resource<?> r : (Resource[])event.getParameters()[2])
					      {
					         if (r instanceof JavaResource)
					         {
					            JavaSource<?> entity = ((JavaResource) r).getJavaSource();
					            if (entity instanceof JavaClass)
					            {
					               if (entity.hasAnnotation(Entity.class))
					               {
					            	   ShellMessages.info(shell, "OK");
					               }
					            }
					         }
					      }
						
					} else {
						// No domain specified, use currentResource
						// Here, we know for sure the currentRessource is of type JavaResource, otherwise the
						// command status wont be SUCCESS
						targets = new JavaResource[] { (JavaResource) currentResource };
					}
					
	      }
	}

}
