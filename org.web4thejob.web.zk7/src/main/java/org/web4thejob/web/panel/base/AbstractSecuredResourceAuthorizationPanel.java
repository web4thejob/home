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

package org.web4thejob.web.panel.base;

import nu.xom.Element;
import org.springframework.util.StringUtils;
import org.web4thejob.security.SecuredResource;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.panel.SecuredResourceAuthorizationPanel;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractSecuredResourceAuthorizationPanel<S extends SecuredResource,
        T extends SecuredResource> extends AbstractListSourceMultiSelectPanel<S,
        T> implements SecuredResourceAuthorizationPanel {
    // ------------------------------ FIELDS ------------------------------

    public static final L10nString L10N_HEADER_GRANTED = new L10nString(AbstractMultiSelectPanel.class,
            "header_granted", "Granted");
    public static final L10nString L10N_HEADER_REVOKED = new L10nString(AbstractMultiSelectPanel.class,
            "header_revoked", "Revoked");

    private final boolean readOnly;
    private List<S> sourceList = Collections.emptyList();

    // --------------------------- CONSTRUCTORS ---------------------------

    protected AbstractSecuredResourceAuthorizationPanel(boolean readOnly) {
        super(readOnly, true);
        this.readOnly = readOnly;
        unregisterSetting(SettingEnum.TARGET_TYPE);
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface Panel ---------------------

    @Override
    public void render() {
        super.render();

        if (isUninitialized()) {
            reset();
        }
    }

    // --------------------- Interface SecuredResourceAuthorizationPanel
    // ---------------------

    @Override
    public String getDefinition() {
        final Element rootElement = new Element(getRootElementName());
        for (T resource : getSelection()) {
            Element element = new Element(SecuredResourceAuthorizationPanel.ELEMENT_RESOURCE);
            element.appendChild(resource.getSid());
            rootElement.appendChild(element);
        }

        String xml = rootElement.toXML();
        return xml;
    }

    @Override
    public void setDefinition(String xml) {
        reset();
        if (!StringUtils.hasText(xml)) return;

        Element rootElement = XMLUtil.getRootElement(xml);
        if (rootElement == null || !rootElement.getLocalName().equals(getRootElementName())) {
            throw new IllegalArgumentException();
        }

        List<S> refList = new ArrayList<S>(sourceList);
        for (int i = 0; i < rootElement.getChildElements().size(); i++) {
            String sid = XMLUtil.getTextualValue(rootElement.getChildElements().get(i));
            for (S source : refList) {
                if (source.getSid().equals(sid)) {
                    select(source);
                }
            }
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected void arrangeForTargetType() {
        // ignore
    }

    @Override
    protected void arrangeSourceHeaders(Listbox listbox) {
        if (listbox.getListhead() == null) {
            new Listhead().setParent(listbox);
        }
        listbox.getListhead().setSizable(true);
        listbox.setSizedByContent(true);
        Listheader listheader = new Listheader(L10N_HEADER_GRANTED.toString());
        listheader.setParent(listbox.getListhead());
        listbox.setItemRenderer(getRenderer());
    }

    @Override
    protected void arrangeTargetHeaders(Listbox listbox) {
        if (listbox.getListhead() == null) {
            new Listhead().setParent(listbox);
        }
        listbox.getListhead().setSizable(true);
        listbox.setSizedByContent(true);
        Listheader listheader = new Listheader(L10N_HEADER_REVOKED.toString());
        listheader.setParent(listbox.getListhead());
        listbox.setItemRenderer(getRenderer());
    }

    protected abstract String getRootElementName();

    protected ListitemRenderer<? extends SecuredResource> getRenderer() {
        return new Renderer();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected S getSourceFromTarget(T target) {
        return (S) target;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T getTargetFromSource(S source) {
        return (T) source;
    }

    @Override
    protected void reset() {
        super.reset();
        sourceList = getSourceList();
        setSourceList(sourceList);
    }

    protected abstract List<S> getSourceList();

    // -------------------------- INNER CLASSES --------------------------

    private class Renderer implements ListitemRenderer<SecuredResource> {
        @Override
        public void render(Listitem item, SecuredResource data, int index) throws Exception {
            item.setLabel(data.toString());
            item.setValue(data);
            item.setStyle("white-space:nowrap;");
        }
    }

}
