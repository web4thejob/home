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

package org.web4thejob.orm.mapping;

import org.hibernate.validator.constraints.NotBlank;
import org.web4thejob.orm.AbstractHibernateEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class Detail extends AbstractHibernateEntity {
    private static final long serialVersionUID = 1L;
    public static final String FLD_ID = "id";
    public static final String FLD_FDATE = "fdate";
    public static final String FLD_FDOUBLE = "fdouble";
    public static final String FLD_FINT = "fint";
    public static final String FLD_FSTRING = "fstring";
    public static final String FLD_FCLASS = "fclass";
    public static final String FLD_FTIMESTAMP = "ftimestamp";

    @NotNull
    private DetailId id;
    @NotNull
    private Date fdate;
    @Min(0)
    private double fdouble;
    @Min(1)
    private int fint;
    @NotBlank
    private String fstring;
    @NotNull
    private Class<?> fclass;
    @NotNull
    private Timestamp ftimestamp;

    public Class<?> getFclass() {
        return fclass;
    }

    public Date getFdate() {
        return fdate;
    }

    public double getFdouble() {
        return fdouble;
    }

    public int getFint() {
        return fint;
    }

    public String getFstring() {
        return fstring;
    }

    public Timestamp getFtimestamp() {
        return ftimestamp;
    }

    public DetailId getId() {
        return id;
    }

    public void setFclass(Class<?> fclass) {
        this.fclass = fclass;
    }

    public void setFdate(Date fdate) {
        this.fdate = fdate;
    }

    public void setFdouble(double fdouble) {
        this.fdouble = fdouble;
    }

    public void setFint(int fint) {
        this.fint = fint;
    }

    public void setFstring(String fstring) {
        this.fstring = fstring;
    }

    public void setFtimestamp(Timestamp ftimestamp) {
        this.ftimestamp = ftimestamp;
    }

    public void setId(DetailId id) {
        this.id = id;
    }

    @Override
    public Serializable getIdentifierValue() {
        return getId();
    }

    @Override
    public void setAsNew() {
        id = null;
    }

}
