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

package my.joblet;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.web4thejob.orm.AbstractHibernateEntity;
import org.web4thejob.orm.validation.ValidatingGroup;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class Master2 extends AbstractHibernateEntity implements ValidatingGroup {
    public static final String FLD_KEY = "key";
    public static final String FLD_NAME = "name";
    public static final String FLD_DETAILS = "details";

    @NotBlank
    private String key;
    @NotBlank
    @Length(min = 1, max = 1, groups = {MustHaveDetailValidation.class})
    private String name;
    @NotEmpty(groups = {MustHaveDetailValidation.class})
    private Set<Detail> details = new HashSet<Detail>(0);

    public Set<Detail> getDetails() {
        return details;
    }

    @Override
    public Class<?>[] getGroupNames() {
        if (name == null) {
            return null;
        } else {
            return new Class<?>[]{MustHaveDetailValidation.class};
        }
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setDetails(Set<Detail> details) {
        this.details = details;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Serializable getIdentifierValue() {
        return getKey();
    }

    @Override
    public void setAsNew() {
        key = null;
    }
}
