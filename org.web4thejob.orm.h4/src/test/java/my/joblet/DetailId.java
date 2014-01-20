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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class DetailId implements Serializable {
    public static final String FLD_MASTER1 = "master1";
    public static final String FLD_MASTER2 = "master2";
    public static final String FLD_KEYSTRING = "keyString";
    public static final String FLD_KEYLONG = "keyLong";

    private Master1 master1;
    private Master2 master2;
    private String keyString;
    private long keyLong;

    public DetailId() {
    }

    public DetailId(Master1 master1, Master2 master2, String keyString, long keyLong) {
        this.master1 = master1;
        this.master2 = master2;
        this.keyString = keyString;
        this.keyLong = keyLong;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (this == obj) return true;
        else if (!DetailId.class.isInstance(obj)) return false;

        return new EqualsBuilder().append(getKeyLong(), ((DetailId) obj).getKeyLong()).append(getKeyString(),
                ((DetailId) obj).getKeyString()).append(getMaster1(), ((DetailId) obj).getMaster1()).append
                (getMaster2(), ((DetailId) obj).getMaster2()).isEquals();

    }

    public long getKeyLong() {
        return keyLong;
    }

    public String getKeyString() {
        return keyString;
    }

    public Master1 getMaster1() {
        return master1;
    }

    public Master2 getMaster2() {
        return master2;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(keyLong).append(keyString).append(master1).append(master2).toHashCode();
    }

    public void setKeyLong(long keyLong) {
        this.keyLong = keyLong;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }

    public void setMaster1(Master1 master1) {
        this.master1 = master1;
    }

    public void setMaster2(Master2 master2) {
        this.master2 = master2;
    }

}
