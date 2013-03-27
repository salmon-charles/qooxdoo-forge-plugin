package org.charless.jboss.forge.plugins.qooxdoo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.java.util.Strings;

import freemarker.template.DefaultObjectWrapper;


public class QooxdooHelpers {
	
	private static freemarker.template.Configuration freemarkerConfig;
	
	public static freemarker.template.Configuration getFreemarkerConfig() {
		if (freemarkerConfig == null) {
			freemarkerConfig = new freemarker.template.Configuration();
	        freemarkerConfig.setClassForTemplateLoading(QooxdooHelpers.class, "/");
	        freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
		}
		return freemarkerConfig;
	}
	
	public static String resolveIdType(JavaClass entity)
	   {
	      for (Member<JavaClass, ?> member : entity.getMembers())
	      {
	         if (member.hasAnnotation(Id.class))
	         {
	            if (member instanceof Method)
	            {
	               return ((Method<?>) member).getReturnType();
	            }
	            if (member instanceof Field)
	            {
	               return ((Field<?>) member).getType();
	            }
	         }
	      }
	      return "Object";
	   }
	
	public static String resolveIdSetterName(JavaClass entity)
	   {
	      String result = null;

	      for (Member<JavaClass, ?> member : entity.getMembers())
	      {
	         if (member.hasAnnotation(Id.class))
	         {
	            String name = member.getName();
	            String type = null;
	            if (member instanceof Method)
	            {
	               type = ((Method<?>) member).getReturnType();
	               if (name.startsWith("get"))
	               {
	                  name = name.substring(2);
	               }
	            }
	            else if (member instanceof Field)
	            {
	               type = ((Field<?>) member).getType();
	            }

	            if (type != null)
	            {
	               for (Method<JavaClass> method : entity.getMethods())
	               {
	                  // It's a setter
	                  if (method.getParameters().size() == 1 && method.getReturnType() == null)
	                  {
	                     Parameter<JavaClass> param = method.getParameters().get(0);

	                     // The type matches ID field's type
	                     if (type.equals(param.getType()))
	                     {
	                        if (method.getName().toLowerCase().contains(name.toLowerCase()))
	                        {
	                           result = method.getName() + "(id)";
	                           break;
	                        }
	                     }
	                  }
	               }
	            }

	            if (result != null)
	            {
	               break;
	            }
	            else if (type != null && member.isPublic())
	            {
	               String memberName = member.getName();
	               // Cheat a little if the member is public
	               if (member instanceof Method && memberName.startsWith("get"))
	               {
	                  memberName = memberName.substring(3);
	                  memberName = Strings.uncapitalize(memberName);
	               }
	               result = memberName + " = id";
	            }
	         }
	      }

	      if (result == null)
	      {
	         throw new RuntimeException("Could not determine @Id field and setter method for @Entity ["
	                  + entity.getQualifiedName()
	                  + "]. Aborting.");
	      }

	      return result;
	   }

	   public static String resolveIdGetterName(JavaClass entity)
	   {
	      String result = null;

	      for (Member<JavaClass, ?> member : entity.getMembers())
	      {
	         if (member.hasAnnotation(Id.class))
	         {
	            String name = member.getName();
	            String type = null;
	            if (member instanceof Method)
	            {
	               type = ((Method<?>) member).getReturnType();
	               if (name.startsWith("get"))
	               {
	                  name = name.substring(2);
	               }
	            }
	            else if (member instanceof Field)
	            {
	               type = ((Field<?>) member).getType();
	            }

	            if (type != null)
	            {
	               for (Method<JavaClass> method : entity.getMethods())
	               {
	                  // It's a getter
	                  if (method.getParameters().size() == 0 && type.equals(method.getReturnType()))
	                  {
	                     if (method.getName().toLowerCase().contains(name.toLowerCase()))
	                     {
	                        result = method.getName() + "()";
	                        break;
	                     }
	                  }
	               }
	            }

	            if (result != null)
	            {
	               break;
	            }
	            else if (type != null && member.isPublic())
	            {
	               String memberName = member.getName();
	               // Cheat a little if the member is public
	               if (member instanceof Method && memberName.startsWith("get"))
	               {
	                  memberName = memberName.substring(3);
	                  memberName = Strings.uncapitalize(memberName);
	               }
	               result = memberName;
	            }
	         }
	      }

	      if (result == null)
	      {
	         throw new RuntimeException("Could not determine @Id field and getter method for @Entity ["
	                  + entity.getQualifiedName()
	                  + "]. Aborting.");
	      }

	      return result;
	   }
	   
	   public static String getEntityTable(final JavaClass entity)
	   {
	      String table = entity.getName();
	      if (entity.hasAnnotation(Entity.class))
	      {
	         Annotation<JavaClass> a = entity.getAnnotation(Entity.class);
	         if (!Strings.isNullOrEmpty(a.getStringValue("name")))
	         {
	            table = a.getStringValue("name");
	         }
	         else if (!Strings.isNullOrEmpty(a.getStringValue()))
	         {
	            table = a.getStringValue();
	         }
	      }
	      return table;
	   }
	   
	   
}
