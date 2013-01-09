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

package org.web4thejob.context;

import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ConfigurableApplicationContext;
import org.web4thejob.security.SecurityContext;
import org.web4thejob.web.panel.DesktopLayoutPanel;
import org.web4thejob.web.panel.Panel;

import java.util.List;

/**
 * <p>This interface describes the application context that is bound to the session scope.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface SessionContext extends ConfigurableApplicationContext {

    public void setAttribute(String name, Object value);

    public void clarAttribute(String name);

    public <T> T getAttribute(String name);

    public boolean hasAttribute(String name);

    public SecurityContext getSecurityContext();

    @Cacheable(value = "session-cache", key = "new org.apache.commons.lang.builder.HashCodeBuilder().append(#root" +
            ".target).append(#requiredType).toHashCode()")
    public <T extends Panel> List<T> getPanels(Class<T> requiredType);

    @Cacheable(value = "session-cache", key = "new org.apache.commons.lang.builder.HashCodeBuilder().append(#root" +
            ".target).append(#requiredType).append(#beanid).toHashCode()")
    public boolean hasPanel(String beanid, Class<? extends Panel> requiredType);

    @Override
    @CacheEvict(value = "session-cache", allEntries = true)
    public void refresh() throws BeansException, IllegalStateException;

    public DesktopLayoutPanel getUserDesktop();
}



