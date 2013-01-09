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

package org.web4thejob.web.dialog;

import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.util.L10nMessages;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.Calendar;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultAboutDialog extends AbstractDialog implements AboutDialog {
    @Override
    protected void prepareButtons() {
        btnOK = new Button(L10N_BUTTON_OK.toString());
        btnOK.setParent(dialogButtongs);
        btnOK.setMold("trendy");
        btnOK.setWidth("100px");
        btnOK.setAutodisable("self");
        btnOK.addEventListener(Events.ON_CLICK, this);
    }

    @Override
    protected void prepareContentLayout() {
        super.prepareContentLayout();
        window.setMaximizable(false);
        window.setHeight("550px");
        window.setWidth("600px");
    }

    @Override
    protected String prepareTitle() {
        return null;
    }

    @Override
    protected void prepareContent() {
        dialogContent.getPanelchildren().setStyle("overflow: auto;");

        Vbox vbox = new Vbox();
        vbox.setParent(dialogContent.getPanelchildren());
        vbox.setHflex("true");
        vbox.setVflex("true");
        vbox.setAlign("center");
        vbox.setPack("center");
        vbox.setSpacing("10px");

        Separator separator = new Separator();
        separator.setParent(vbox);

        A image = new A();
        image.setParent(vbox);
        image.setImage("img/w4tj_logo_vertical_full.png");
        image.setHref("http://web4thejob.org");
        image.setTarget("_blank");

        Label label = new Label();
        label.setParent(vbox);
        label.setStyle("font-size:16pt;");
        label.setValue("version " + ContextUtil.getModules().get(0).getVersion());

        Html html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;");
        html.setContent("Copyright &copy; 2012" +
                (java.util.Calendar.getInstance().get(Calendar.YEAR) > 2012 ? "-" + java.util.Calendar.getInstance()
                        .get(Calendar.YEAR) : "") + " Veniamin Isaias");

        Space space = new Space();
        space.setSpacing("20px");
        space.setParent(vbox);

        html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;color:#0097D9;");
        html.setContent(L10nMessages.L10N_DEVELOPER_SIGNATURE.toString());

        html = new Html();
        html.setParent(vbox);
        html.setZclass("z-label");
        html.setStyle("font-size:12pt;color:#0097D9;");
        html.setContent(L10nMessages.L10N_ICONS_SIGNATURE.toString());

        btnOK.setFocus(true);
    }
}
