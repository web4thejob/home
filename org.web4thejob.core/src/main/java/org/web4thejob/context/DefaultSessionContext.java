/*
 * Copyright (c) 2012 Veniamin Isaias.
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

package org.web4thejob.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.security.SecurityContext;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.Attributes;
import org.web4thejob.web.panel.DesktopLayoutPanel;
import org.web4thejob.web.panel.Panel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class DefaultSessionContext extends AbstractRefreshableApplicationContext implements SessionContext {

    private final Map<String, Object> sessionMap = new HashMap<String, Object>(0);

    @Autowired
    private SecurityContext securityContext;

    private DesktopLayoutPanel desktopLayoutPanel;

    @Override
    protected void loadBeanDefinitions(final DefaultListableBeanFactory beanFactory) throws BeansException,
            IOException {

        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        final XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        // initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);

    }

    protected void loadBeanDefinitions(final XmlBeanDefinitionReader reader) throws UnsupportedEncodingException {
        final List<PanelDefinition> panels = ContextUtil.getDRS().getAll(PanelDefinition.class);

        final Set<Resource> resources = new HashSet<Resource>();
        for (final PanelDefinition panel : panels) {
            final Resource resource = new ByteArrayResource(panel.getDefinition().getBytes("UTF-8"), panel.getName());
            resources.add(resource);
        }
        reader.loadBeanDefinitions(resources.toArray(new Resource[resources.size()]));
    }

    @Override
    @Autowired
    @Qualifier(CoreUtil.BEAN_ROOT_CONTEXT)
    public void setParent(final ApplicationContext parent) {
        super.setParent(parent);
    }

    @Override
    public void setAttribute(String name, Object value) {
        sessionMap.put(name, value);
    }

    @Override
    public void clarAttribute(String name) {
        sessionMap.remove(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(String name) {
        return (T) sessionMap.get(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        return sessionMap.containsKey(name);
    }

    @Override
    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    @Override
    public <T extends Panel> List<T> getPanels(Class<T> requiredType) {
        List<T> panels = new ArrayList<T>();
        for (String beanid : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, requiredType)) {
            try {
                panels.add(getBean(beanid, requiredType));
            } catch (BeansException ignore) {
                ignore.printStackTrace();
            }
        }
        Collections.sort(panels, new Comparator<Panel>() {
            @Override
            public int compare(Panel p1, Panel p2) {
                return p1.toString().compareToIgnoreCase(p2.toString());
            }
        });
        return panels;
    }

    @Override
    public boolean hasPanel(String beanid, Class<? extends Panel> requiredType) {
        if (Arrays.asList(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, requiredType)).contains(beanid)) {
            try {
                return getBean(beanid, requiredType) != null;
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public DesktopLayoutPanel getUserDesktop() {
        return getAttribute(Attributes.ATTRIB_DESKTOP);
    }
}
