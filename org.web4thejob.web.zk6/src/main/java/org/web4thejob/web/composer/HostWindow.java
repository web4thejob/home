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

package org.web4thejob.web.composer;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.DesktopLayoutPanel;
import org.web4thejob.web.panel.SessionInfoPanel;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Window;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class HostWindow implements Composer<Window>, EventListener<ClientInfoEvent> {

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        Window hostWindow = comp;
        hostWindow.getRoot().addEventListener(Events.ON_CLIENT_INFO, this);

        try {
//            Module coreModule = ContextUtil.getModules().get(0);
//            hostWindow.setTitle(coreModule.getProjectName() + " v" + coreModule.getVersion());

            Executions.getCurrent().getSession().setAttribute(Attributes.PREFERRED_LOCALE, CoreUtil.getUserLocale());

            DesktopLayoutPanel desktopLayoutPanel = getUserDesktop();
            desktopLayoutPanel.attach(hostWindow);
            desktopLayoutPanel.render();

            // TODO parse request params here...
        } catch (Exception e) {
            e.printStackTrace();

            Executions.sendRedirect("sec/sm.zul");
        }


        checkIntegrationTestHook();
    }

    private void checkIntegrationTestHook() {
        IntegrationTestHook hook = (IntegrationTestHook) Executions.getCurrent().getDesktop().getWebApp()
                .getServletContext().getAttribute(IntegrationTestHook.class.getName());
        if (hook != null) {
            hook.doHook();
        }
    }

    @Override
    public void onEvent(ClientInfoEvent event) throws Exception {
        ContextUtil.getSessionContext().setAttribute(SessionInfoPanel.ATTRIB_CLIENT_INFO, event);
    }

    public DesktopLayoutPanel getUserDesktop() {
        DesktopLayoutPanel desktopLayoutPanel = null;

        PanelDefinition desktop = ORMUtil.getUserDesktop();
        if (desktop != null) {
            desktopLayoutPanel = (DesktopLayoutPanel) ContextUtil.getPanelSafe(desktop.getBeanId());
        }
        if (desktopLayoutPanel == null) {
            //if error, fall back to default desktop
            desktopLayoutPanel = ContextUtil.getBean(DesktopLayoutPanel.class);
        }

        ContextUtil.getSessionContext().setAttribute(org.web4thejob.web.panel.Attributes.ATTRIB_DESKTOP,
                desktopLayoutPanel);

        return desktopLayoutPanel;
    }
}
