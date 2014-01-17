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
import org.springframework.stereotype.Component;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.panel.I18nAware;
import org.zkoss.zul.Textbox;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("prototype")
public class DefaultLocalizationDialog extends AbstractDialog implements LocalizationDialog {
    private final I18nAware localizable;

    protected DefaultLocalizationDialog(I18nAware localizable) {
        super();
        this.localizable = localizable;
    }

    @Override
    public void setInDesignMode(boolean designMode) {
        // not designable
    }

    @Override
    protected String prepareTitle() {
        return "Panel localization [" + localizable.toString() + "] - [" + CoreUtil.getUserLocale() + "]";
    }

    @Override
    protected void prepareContent() {
        super.prepareContent();

        final StringBuilder sb = new StringBuilder();
        for (L10nString lstring : L10nUtil.getLocalizableResources(localizable.getClass())) {
            sb.append(lstring.getCode()).append("=").append(lstring.toString()).append("\n");
        }

        Textbox textbox = new Textbox();
        textbox.setParent(this.dialogContent.getPanelchildren());
        textbox.setMultiline(true);
        textbox.setHflex("true");
        textbox.setVflex("true");
        textbox.setText(sb.toString());
    }

    @Override
    protected void prepareBottomToolbar() {
        // no bottom toolbar
    }

    @Override
    protected void prepareButtons() {
        // no buttons
    }

}
