package com.adobe.acs.sample.servlets;

/**
 * Created with IntelliJ IDEA.
 * User: ynaka
 * Date: 1/8/15
 * Time: 10:01 PM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
/**
 * @scr.component immediate="false" metatype="false"
 * @scr.service interface="javax.servlet.Servlet"
 * @scr.property name="sling.servlet.methods" values.0="GET"
 * @scr.property name="sling.servlet.paths" values.0="/apps/servletExample"
 */

public class ServletExample extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(ServletExample.class);
    private PrintWriter writer = null;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            writer = response.getWriter();
            SlingHttpServletRequest sling = request;
            String selector = request.getRequestPathInfo().getSelectorString();
            writer.print("Hello");
        } catch ( Exception e ){
            log.error( "Error", e );
        } finally {
            if( writer != null ){
                writer.close();
            }
        }
    }
}

