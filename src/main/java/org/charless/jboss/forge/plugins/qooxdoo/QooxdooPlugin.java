package org.charless.jboss.forge.plugins.qooxdoo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.spec.javaee.events.RestGeneratedResources;

/**
 * A plugin to use the qooxdoo universal javascript framework within forge
 */
@Alias("qooxdoo")
@RequiresFacet({QooxdooFacet.class})
public class QooxdooPlugin implements Plugin {
	@Inject
	private Shell shell;

	@Inject
	private ShellPrompt prompt;

	@Inject
	private Event<InstallFacets> event;

	@Inject
	private Project project;
	
	@Inject
	private Configuration configuration;

	@SetupCommand
	@Command(value = "setup", help = "Setup a qooxdoo project")
	public void setup(final PipeOut out) {
		if (!project.hasFacet(QooxdooFacet.class)) {
			event.fire(new InstallFacets(QooxdooFacet.class));
		} 
		if (project.hasFacet(QooxdooFacet.class)) {
			ShellMessages.info(out, "Qooxdoo is installed.");
		}
	}

	@DefaultCommand
	@Command(value = "help", help = "Help with qooxdoo project")
	public void help(PipeOut out) {
		/*ShellMessages.info(out, "Quick start with Qooxdoo \n" +
				" 1. run your generated application and type 'gwt run'\n" +
		"";*/
		

	}
	
	@Command(value = "uninstall", help = "Remove qooxdoo nature of the project")
	public void uninstall(PipeOut out) {
		if (project.getFacet(QooxdooFacet.class).uninstall())
			ShellMessages.info(out, "Qooxdoo removed.");
		
	}
	
	public void postRestCommand(@Observes final RestGeneratedResources event) throws FileNotFoundException
	{
		int i = 0;
		for (JavaResource jr : event.getEntities()) {
			 JavaClass entity = (JavaClass) (jr).getJavaSource();
			 String entityTable = QooxdooHelpers.getEntityTable(entity);
			 String endpointName = entityTable+"Endpoint";
			 JavaResource endpoint = null;
			 for (int j = i; j<event.getEndpoints().size();j++) {
				 if (event.getEndpoints().get(j).getName().startsWith(endpointName)) {
					 endpoint = event.getEndpoints().get(j);
					 break;
				 }
				 j++;
			 }
			 i++;
			 if (endpoint == null)
	         {
	            ShellMessages.error(shell, "Skipped class [" + entity.getQualifiedName() + "]. Could not find associated Java class Endpoint.");
	            continue;
	         } 
			 ((QooxdooFacet)project.getFacet(QooxdooFacet.class)).createOrUpdateRestEndpoint(entity, endpoint );
			 ShellMessages.success(shell, "Generated QOOXDOO REST endpoint for [" + entity.getQualifiedName() + "]");
		}
		
	}
	
	private void executeMvnCommand(final PipeOut out, String command, String... a) {
		final ArrayList<String> args = new ArrayList<String>();
		args.add(command);
		if (a != null) {
			args.addAll(Arrays.asList(a));
		}
		final MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
		maven.executeMaven(out, args.toArray(new String[args.size()]));
	}


}
