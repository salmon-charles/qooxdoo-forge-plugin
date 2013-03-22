package org.charless.jboss.forge.plugins.qooxdoo;

import java.util.ArrayList;
import java.util.Arrays;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
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

/**
 * A plugin to use the qooxdoo universal javascript framework within forge
 */
@Alias("qooxdoo")
@RequiresFacet({QooxdooFacet.class,QooxdooRestFacet.class})
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
			// Create facet
			event.fire(new InstallFacets(QooxdooFacet.class));
		} else {
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
