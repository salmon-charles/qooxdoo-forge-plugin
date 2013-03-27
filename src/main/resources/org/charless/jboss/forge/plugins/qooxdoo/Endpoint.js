/* ************************************************************************
      DO NOT MODIFY THIS FILE AS IT IS AUTOMATICALLY GENERATED !!
************************************************************************ */

/**
 * This class is generated automatically, and is tied to the Java class ${javaEndpoint}
 */
qx.Class.define("${package}",
{
  extend: qx.io.rest.Resource,

  /**
   * Build a new REST Resource to manage the ${entityTable} resources 
   */
  construct: function()
  {
    this.base(arguments, 
    	{
	    	"create": { method: "POST", url: "/${resourcePath}" },
	    	"deleteById": { method: "DELETE", url: "/${resourcePath}/{id}", check: { id: /\d+/ } },
	    	"findById": { method: "GET", url: "/${resourcePath}/{id}", check: { id: /\d+/ } },
	    	"listAll": { method: "GET", url: "/${resourcePath}" },
	    	"update": { method: "POST", url: "/${resourcePath}", check: { id: /\d+/ } }
    	}
    );
  }
   
});