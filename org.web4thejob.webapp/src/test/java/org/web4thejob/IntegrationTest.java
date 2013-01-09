/*
 * Copyright (c) 2012-2013 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */

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
