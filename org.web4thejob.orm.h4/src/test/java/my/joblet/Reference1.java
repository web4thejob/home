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
import org.web4thejob.orm.annotation.InsertTimeHolder;
import org.web4thejob.orm.annotation.UpdateTimeHolder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class Reference1 extends AbstractHibernateEntity {
    public static final String FLD_ID = "id";
    public static final String FLD_NAME = "name";
    public static final String FLD_MASTERS1 = "masters1";
    public static final String FLD_REFERENCE2 = "reference2";
    public static final String FLD_CREATE_TIME = "createTime";
    public static final String FLD_UPDATE_TIME = "updateTime";

    private long id;
    @NotBlank
    private String name;
    @NotNull
    private Reference2 reference2;
    private Set<Master1> masters1 = new HashSet<Master1>(0);
    @InsertTimeHolder
    private Timestamp createTime;
    @UpdateTimeHolder
    private Timestamp updateTime;

    public Reference1() {

    }

    public Reference1(Reference2 reference2, String name) {
        this.reference2 = reference2;
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

    public void setMasters1(Set<Master1> masters1) {
        this.masters1 = masters1;
    }

    public Set<Master1> getMasters1() {
        return masters1;
    }

    public void setReference2(Reference2 reference2) {
        this.reference2 = reference2;
    }

    public Reference2 getReference2() {
        return reference2;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
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
