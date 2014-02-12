/*
 * Copyright (c) 2012-2014 Veniamin Isaias.
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

package org.web4thejob.web.zbox;

import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Toolbarbutton;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractBox<T> extends HtmlMacroComponent implements RawValueBox<T>, EventListener<Event> {
    // ------------------------------ FIELDS ------------------------------

    public static final L10nString L10N_BUTTON_CLICK_FOR_VALUE = new L10nString(AbstractBox.class,
            "link_click_to_set_value", "Click to set value");
    public static final L10nString L10N_BUTTON_EDIT = new L10nString(AbstractBox.class, "link_edit_value", "Edit");
    public static final L10nString L10N_BUTTON_CLEAR = new L10nString(AbstractBox.class, "link_clear_value", "Clear");

    private static final long serialVersionUID = 1L;
    private static final String NOVALUE_STYLE = "font-style: italic; color: #0099cc;";
    protected Toolbarbutton _clearLink;
    protected Component _valueBox;
    protected Toolbarbutton _novalueLink;
    protected Toolbarbutton _editLink;
    private int tooltipLimit = PropertyBox.TOOLTIP_LIMIT;
    private boolean hideClearLink;
    @Wire
    protected Hbox hbox;

    public void setHideClearLink(boolean hideClearLink) {
        this.hideClearLink = hideClearLink;
    }

    public int getTooltipLimit() {
        return tooltipLimit;
    }

    public void setTooltipLimit(int tooltipLimit) {
        this.tooltipLimit = tooltipLimit;
        if (_valueBox instanceof PropertyBox) {
            ((PropertyBox) _valueBox).setTooltipLimit(tooltipLimit);
        }
    }


    // --------------------------- CONSTRUCTORS ---------------------------

    protected AbstractBox() {
        compose();
    }

    @Override
    protected void compose() {
        setMacroURI("/WEB-INF/zbox.zul");
        super.compose();
        this.addEventListener("onEdit", this);
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface EventListener ---------------------

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getName().equals(Events.ON_CLICK) && (event.getTarget().hasAttribute("edit") || event.getTarget()
                .hasAttribute("novalue"))) {
            Clients.showBusy(null);
            Events.echoEvent("onEdit", this, null);
        } else if (event.getName().equals(Events.ON_CLICK) && event.getTarget().hasAttribute("clear")) {
            onClear();
        } else if (event.getName().equals("onEdit")) {
            Clients.clearBusy();
            onEdit();
        }
    }

    // --------------------- Interface RawValueBox ---------------------

    @Override
    public T getRawValue() {
        if (!isEmpty()) {
            return unmarshallToRawValue();
        } else {
            return null;
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    protected boolean isEmpty() {
        return _valueBox == null || _valueBox.getParent() == null;
    }

    protected void marshallEmptyValue() {
        if (_valueBox != null) {
            _valueBox.detach();
            _valueBox = null;
            _editLink.detach();
            _editLink = null;
            if (_clearLink != null) {
                _clearLink.detach();
                _clearLink = null;
            }
        }
        if (_novalueLink == null) {
            _novalueLink = new Toolbarbutton();
            if (ContextUtil.resourceExists("img/FIND.png")) {
                _novalueLink.setImage("img/FIND.png");
            } else {
                _novalueLink.setLabel(L10N_BUTTON_CLICK_FOR_VALUE.toString());
            }
            _novalueLink.setTooltiptext(L10N_BUTTON_CLICK_FOR_VALUE.toString());
            _novalueLink.setAttribute("novalue", true);
            _novalueLink.addEventListener(Events.ON_CLICK, this);
            _novalueLink.setStyle(NOVALUE_STYLE);
            _novalueLink.setTabindex(1);
        }
        _novalueLink.setParent(this);
    }

    protected Component getValueBox() {
        PropertyBox propertyBox = new PropertyBox();
        propertyBox.setParent(this);
        propertyBox.setTooltipLimit(getTooltipLimit());
        return propertyBox;
    }


    protected Toolbarbutton getEditLink() {
        Toolbarbutton editLink = new Toolbarbutton();
        editLink.setParent(this);
        String image = CoreUtil.getCommandImage(CommandEnum.UPDATE, null);
        if (image != null) {
            editLink.setImage(image);
        } else {
            editLink.setLabel(L10N_BUTTON_EDIT.toString());
        }
        editLink.setTooltiptext(L10N_BUTTON_EDIT.toString());
        editLink.setAttribute("edit", true);
        editLink.addEventListener(Events.ON_CLICK, this);
        editLink.setTabindex(2);

        return editLink;
    }

    protected Toolbarbutton getClearLink() {
        Toolbarbutton clearLink = new Toolbarbutton();
        clearLink.setParent(this);
        String image = CoreUtil.getCommandImage(CommandEnum.CLEAR, null);
        if (image != null) {
            clearLink.setImage(image);
        } else {
            clearLink.setLabel(L10N_BUTTON_CLEAR.toString());
        }
        clearLink.setTooltiptext(L10N_BUTTON_CLEAR.toString());
        clearLink.addEventListener(Events.ON_CLICK, this);
        clearLink.setAttribute("clear", true);
        clearLink.setTabindex(3);

        return clearLink;
    }


    protected void marshallToString(T value) {
        if (_novalueLink != null) {
            _novalueLink.detach();
        }

        if (_valueBox == null) {
            _valueBox = getValueBox();
            _editLink = getEditLink();

            if (!hideClearLink) {
                _clearLink = getClearLink();
            }
        }

        if (_valueBox instanceof PropertyBox) {
            ((PropertyBox) _valueBox).setValue(value);
        }
    }

    protected void onClear() {
        setRawValue(null);
    }

    @Override
    public void setRawValue(T value) {
        if (!Objects.equals(value, getRawValue())) {
            if (value != null) {
                marshallToString(value);
            } else {
                marshallEmptyValue();
            }

            Events.sendEvent(Events.ON_CHANGE, this, null);
        }
    }

    abstract protected void onEdit();

    @Override
    public void setStyle(String style) {
        if (_valueBox instanceof HtmlBasedComponent) {
            ((HtmlBasedComponent) _valueBox).setStyle(style);
        }
    }

    @Override
    public String getStyle() {
        if (_valueBox instanceof HtmlBasedComponent) {
            return ((HtmlBasedComponent) _valueBox).getStyle();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected T unmarshallToRawValue() {
        return (T) ((PropertyBox) _valueBox).getContent();
    }

    public void setTooltip(String tooltip) {
        hbox.setTooltip(tooltip);
        if (_valueBox instanceof HtmlBasedComponent) {
            ((HtmlBasedComponent) _valueBox).setTooltiptext(tooltip);
        }
    }
}
