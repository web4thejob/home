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

package org.web4thejob.orm;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.web4thejob.orm.annotation.EmailHolder;
import org.web4thejob.orm.parameter.Parameter;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.security.Identity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.4
 */

public abstract class AbstractIdentityImpl extends AbstractHibernateEntity implements Identity {
    private long id;
    @SuppressWarnings("unused")
    private int version;
    @NotBlank
    private String code;
    @EmailHolder
    @Email
    private String email;
    private Set<Parameter> parameters = new HashSet<Parameter>();
    private Set<PanelDefinition> panels = new HashSet<PanelDefinition>(0);
    private Set<RenderScheme> renderSchemes = new HashSet<RenderScheme>(0);
    private Set<Query> queries = new HashSet<Query>(0);

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }

    public Set<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<Parameter> parameters) {
        this.parameters = parameters;
    }

    public Set<PanelDefinition> getPanels() {
        return panels;
    }

    public void setPanels(Set<PanelDefinition> panels) {
        this.panels = panels;
    }

    public Set<RenderScheme> getRenderSchemes() {
        return renderSchemes;
    }

    public void setRenderSchemes(Set<RenderScheme> renderSchemes) {
        this.renderSchemes = renderSchemes;
    }

    public Set<Query> getQueries() {
        return queries;
    }

    public void setQueries(Set<Query> queries) {
        this.queries = queries;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        if (email != null && email.trim().length() == 0) {
            email = null; //so that unique constraint will work correctly for multiple null values
        }
        this.email = email;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }
}
