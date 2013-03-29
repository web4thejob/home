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

package my.joblet;

import org.hibernate.validator.constraints.NotBlank;
import org.web4thejob.orm.AbstractHibernateEntity;
import org.web4thejob.orm.annotation.StatusHolder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class Reference2 extends AbstractHibernateEntity {
    public static final String FLD_ID = "id";
    public static final String FLD_NAME = "name";
    public static final String FLD_STATUS1 = "status1";
    public static final String FLD_STATUS2 = "status2";
    public static final String FLD_REFERENCES1 = "references1";

    private long id;
    @NotBlank
    private String name;
    @StatusHolder(InactiveWhen = true)
    private boolean status1 = false;
    @StatusHolder(InactiveWhen = false)
    private boolean status2 = true;
    private Set<Reference1> references1 = new HashSet<Reference1>(0);

    public Reference2() {

    }

    public Reference2(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setReferences1(Set<Reference1> references1) {
        this.references1 = references1;
    }

    public Set<Reference1> getReferences1() {
        return references1;
    }

    public void setStatus1(boolean status1) {
        this.status1 = status1;
    }

    public boolean isStatus1() {
        return status1;
    }

    public void setStatus2(boolean status2) {
        this.status2 = status2;
    }

    public boolean isStatus2() {
        return status2;
    }

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }

}
