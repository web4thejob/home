package org.web4thejob;

import com.thoughtworks.selenium.DefaultSelenium;
import junit.framework.Assert;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.web.composer.IntegrationTestHook;
import org.web4thejob.web.dialog.DefaultSelectPanelDialog;
import org.zkoss.zk.ui.Executions;


/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class IntegrationTest implements IntegrationTestHook {

    @Test
    public void testDialogCreation() throws Exception {

        Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(8080);
        connector.setHost("127.0.0.1");
        server.addConnector(connector);

        WebAppContext wac = new WebAppContext();
        wac.setParentLoaderPriority(true);
        wac.setContextPath("/org.web4thejob.webapp");
        //expanded war or path of war file
        wac.setWar("c:\\Documents and Settings\\benji\\IdeaProjects\\org.web4thejob\\org.web4thejob" +
                ".webapp\\target\\org.web4thejob" + "" +
                ".webapp-0.0.5-SNAPSHOT\\");
        //wac.setWar("c:\\Documents and Settings\\e36132\\IdeaProjects\\org.web4thejob\\org.web4thejob
        // .webapp\\target\\org.web4thejob
        // .webapp-0.0.5-SNAPSHOT\\");
        //wac.setWar("/Users/benji/org.web4thejob/org.web4thejob.webapp/target/org.web4thejob.webapp-0.0.5-SNAPSHOT/");
        server.addHandler(wac);
        server.setStopAtShutdown(true);
        server.start();

        wac.getServletContext().getContext("/org.web4thejob.webapp").setAttribute(IntegrationTestHook.class.getName()
                , this);

        DefaultSelenium selenium = createSeleniumClient("http://localhost:8080/");
        selenium.start();
        selenium.open("http://localhost:8080/org.web4thejob.webapp/index.zul");

        selenium.stop();
        server.stop();
    }


    protected DefaultSelenium createSeleniumClient(String url) throws Exception {
        return new DefaultSelenium("localhost", 4444, "*firefox", url);
    }

    @Override
    public void doHook(Object... args) {
        Assert.assertNotNull(Executions.getCurrent());

//        WebApplicationContext ctx = getRequiredWebApplicationContext(wac.getServletContext());
//        Assert.assertNotNull(ctx);

        Assert.assertNotNull(ContextUtil.getDialog(DefaultSelectPanelDialog.class));

        Assert.assertNotNull(ContextUtil.getSessionContext());
    }
}
