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
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.setting.Setting;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.panel.RenderSchemePanel;

import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultRenderSchemeDialog extends AbstractDialog implements RenderSchemeDialog {
    private final Set<Setting<?>> settings;
    private final SchemeType schemeType;
    private final RenderScheme renderScheme;
    private RenderSchemePanel renderSchemePanel;

    public DefaultRenderSchemeDialog(Set<Setting<?>> settings, SchemeType schemeType, RenderScheme renderScheme) {
        this.settings = settings;
        this.schemeType = schemeType;
        this.renderScheme = renderScheme;
    }

    @Override
    protected Message getOKMessage() {
        Message message = ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, MessageArgEnum.ARG_ITEM,
                renderSchemePanel.getRenderScheme());
        return message;
    }

    @Override
    protected String prepareTitle() {
        return L10nUtil.getMessage(CommandEnum.DESIGN_PANEL.getClass(), CommandEnum.DESIGN_PANEL.name(),
                CommandEnum.DESIGN_PANEL.name());
    }

    @Override
    protected void prepareContent() {
        renderSchemePanel = ContextUtil.getDefaultPanel(RenderSchemePanel.class);
        renderSchemePanel.setSchemeType(schemeType);
        renderSchemePanel.attach(dialogContent.getPanelchildren());
        renderSchemePanel.setSettings(settings);
        renderSchemePanel.supressCommands(false);
        super.prepareContent();
    }

    @Override
    protected void onBeforeShow() {
        renderSchemePanel.setInDesignMode(isInDesignMode());
        renderSchemePanel.setL10nMode(getL10nMode());
        renderSchemePanel.render();
        if (renderScheme != null) {
            renderSchemePanel.setRenderScheme(renderScheme);
        }
    }

}
