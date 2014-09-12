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

import nu.xom.Attribute;
import nu.xom.Element;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.security.SecuredResource;
import org.web4thejob.web.panel.Panel;
import org.web4thejob.web.panel.ParentCapable;

import java.util.UUID;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractPanel implements Panel {
    protected AbstractPanel() {
        base = initBaseComponent();
    }

    protected static final String BEANS_NAMESPACE = "http://www.springframework.org/schema/beans";
    protected final Object base;
    private ParentCapable parent;
    private String beanId;
    private boolean beanInitialized;

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getClass()).append(base).toHashCode();
    }

    public String getSid() {
        if (isPersisted()) {
            return getBeanName();
        } else {
            return getClass().getCanonicalName();
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (this == obj) return true;
        else if (!Panel.class.isInstance(obj)) return false;

        return hashCode() == obj.hashCode();

    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name, T defaultWhenNull) {
        final T value = (T) getAttribute(name);
        return value != null ? value : defaultWhenNull;
    }

    public String getBeanName() {
        return beanId;
    }

    public void setBeanName(String name) {
        if (StringUtils.hasText(beanId)) throw new IllegalStateException("bean name cannot be set again");

        beanId = name;
    }

    public ParentCapable getParent() {
        return parent;
    }

    public void setParent(ParentCapable parent) {
        if (parent == null) {
            if (this.parent != null) {
                final ParentCapable ref = this.parent;
                this.parent = null;
                ref.getSubpanels().remove(this);
            }
        } else {
            if (this.parent == null) {
                if (!parent.getSubpanels().contains(this)) {
                    parent.getSubpanels().add(this);
                }
                this.parent = parent;
            } else {
                if (!this.parent.equals(parent)) {
                    setParent(null);
                    setParent(parent);
                }
            }
        }
    }

    protected abstract Object initBaseComponent();

    public void render() {
        if (!beanInitialized) {
            throw new IllegalStateException("cannot call prior bean initialization");
        }
    }

    protected void reset() {

    }

    public String toSpringXml() {

        beforePersistencePhase();

        final Element bean = new Element("bean", BEANS_NAMESPACE);
        if (!isPersisted()) {
            bean.addAttribute(new Attribute("id", getSid() + SecuredResource.SECURITY_PATH_DELIM + "panel_" + UUID
                    .randomUUID().toString()));
        } else {
            bean.addAttribute(new Attribute("id", getBeanName()));
        }
        bean.addAttribute(new Attribute("class", getClass().getName()));
        bean.addAttribute(new Attribute("scope", "prototype"));

        return bean.toXML();
    }

    public String getImage() {
        if (isPersisted()) {
            PanelDefinition panelDefinition = ORMUtil.getPanelDefinition(beanId);
            if (StringUtils.hasText(panelDefinition.getImage())) {
                return panelDefinition.getImage();
            }
            return "img/PANEL.png";
        } else {
            return "img/PANEL_NEW.png";
        }
    }

    protected void beforePersistencePhase() {
        // override
    }

    public void afterPropertiesSet() throws Exception {
        if (beanInitialized) {
            throw new IllegalStateException("Illegal to call more than once");
        }

        beanInitialized = true;
    }

    protected boolean isInitialized() {
        return beanInitialized;
    }

    protected void updateBeanName(String name) {
        beanId = name;
    }

    abstract protected void displayMessage(String message, boolean error);
}
