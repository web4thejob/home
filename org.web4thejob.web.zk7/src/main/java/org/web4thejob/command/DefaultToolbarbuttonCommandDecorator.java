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

package org.web4thejob.command;

import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class DefaultToolbarbuttonCommandDecorator extends AbstractClickableCommandDecorator implements
        ToolbarbuttonCommandDecorator {

    public DefaultToolbarbuttonCommandDecorator(Command command) {
        super(command);
    }

    @Override
    public boolean isDisabled() {
        return ((Toolbarbutton) clickable).isDisabled();
    }

    @Override
    public void setDisabled(boolean disabled) {
        ((Toolbarbutton) clickable).setDisabled(disabled);
    }

    @Override
    protected LabelImageElement getClickable() {
        return new Toolbarbutton();
    }

    @Override
    public void setHref(String href) {
        ((Toolbarbutton) clickable).setHref(href);
    }

    @Override
    public String getHref() {
        return ((Toolbarbutton) clickable).getHref();
    }

    @Override
    public void setTarget(String target) {
        ((Toolbarbutton) clickable).setTarget(target);
    }

    @Override
    public String getTarget() {
        return ((Toolbarbutton) clickable).getTarget();
    }
}
