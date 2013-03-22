package org.charless.jboss.forge.plugins.qooxdoo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

public class QooxdooPluginTest extends AbstractShellTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment()
            .addPackages(true, QooxdooPlugin.class.getPackage());
   }

   @Test
   public void testDefaultCommand() throws Exception
   {
      //getShell().execute("qooxdoo");
   }

}
