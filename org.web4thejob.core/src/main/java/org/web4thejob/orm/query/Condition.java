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

package org.web4thejob.orm.query;

import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.annotation.ControllerHolder;
import org.web4thejob.orm.annotation.EntityTypeHolder;
import org.web4thejob.util.L10nUtil;

import java.util.*;

/**
 * <p>Class for defining and caching valid conditions used on {@link Criterion} instances.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class Condition {

    public static final Condition EQ = new Condition("EQU");
    public static final Condition NE = new Condition("NE");
    public static final Condition GTE = new Condition("GTE");
    public static final Condition GT = new Condition("GT");
    public static final Condition LTE = new Condition("LTE");
    public static final Condition LT = new Condition("LT");

    public static final Condition SW = new Condition("SW");
    public static final Condition NSW = new Condition("NSW");
    public static final Condition CN = new Condition("CN");
    public static final Condition NCN = new Condition("NCN");
    public static final Condition EW = new Condition("EW");
    public static final Condition NEW = new Condition("NEW");

    public static final Condition IN = new Condition("IN");
    public static final Condition NIN = new Condition("NIN");

    public static final Condition NL = new Condition("NULL", 0);
    public static final Condition NNL = new Condition("NOTNULL", 0);

    public static final Condition EX = new Condition("EXIST", 0);
    public static final Condition NEX = new Condition("NOTEXIST", 0);

    private static final Map<String, List<Condition>> typeCache = new HashMap<String, List<Condition>>();
    private static final Map<String, Condition> keyCache = new HashMap<String, Condition>();
    private final String key;
    private final int operandsNo;

    static {
        keyCache.put(EQ.key, EQ);
        keyCache.put(NE.key, NE);
        keyCache.put(GTE.key, GTE);
        keyCache.put(GT.key, GT);
        keyCache.put(LTE.key, LTE);
        keyCache.put(LT.key, LT);
        keyCache.put(SW.key, SW);
        keyCache.put(NSW.key, NSW);
        keyCache.put(CN.key, CN);
        keyCache.put(NCN.key, NCN);
        keyCache.put(EW.key, EW);
        keyCache.put(NEW.key, NEW);
        keyCache.put(IN.key, IN);
        keyCache.put(NIN.key, NIN);
        keyCache.put(NL.key, NL);
        keyCache.put(NNL.key, NNL);
        keyCache.put(EX.key, EX);
        keyCache.put(NEX.key, NEX);
    }


    private Condition(String key, int operandsNo) {
        this.key = key;
        this.operandsNo = operandsNo;
    }

    private Condition(String key) {
        this(key, 1);
    }

    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public int getOperandsNo() {
        return operandsNo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else return Condition.class.isInstance(obj) && key.equals(((Condition) obj).getKey());
    }

    @Override
    public String toString() {
        return L10nUtil.getMessage(Condition.class, key, key);
    }

    public static Condition fromKey(String key) {
        return keyCache.get(key);
    }

    public static List<Condition> getApplicableToType(PropertyMetadata propertyMetadata) {
        Class<?> javaType = propertyMetadata.getJavaType();
        List<Condition> conditions = new ArrayList<Condition>();

        if (propertyMetadata.isAnnotatedWith(ControllerHolder.class) || propertyMetadata.isAnnotatedWith
                (EntityTypeHolder.class)) {
            conditions.add(EQ);
            conditions.add(NE);
            conditions.add(IN);
            conditions.add(NIN);
            conditions.add(NL);
            conditions.add(NNL);
        } else if (Number.class.isAssignableFrom(javaType) || Integer.class.isAssignableFrom(javaType) || Date.class
                .isAssignableFrom(javaType)) {
            conditions.add(EQ);
            conditions.add(NE);
            conditions.add(GT);
            conditions.add(GTE);
            conditions.add(LT);
            conditions.add(LTE);
            conditions.add(IN);
            conditions.add(NIN);
            conditions.add(NL);
            conditions.add(NNL);
        } else if (Entity.class.isAssignableFrom(javaType)) {
            conditions.add(EQ);
            conditions.add(NE);
            conditions.add(IN);
            conditions.add(NIN);
            conditions.add(NL);
            conditions.add(NNL);
        } else if (Locale.class.isAssignableFrom(javaType)) {
            conditions.add(EQ);
            conditions.add(NE);
            conditions.add(IN);
            conditions.add(NIN);
            conditions.add(NL);
            conditions.add(NNL);
        } else if (Boolean.class.isAssignableFrom(javaType)) {
            conditions.add(EQ);
            conditions.add(NE);
            conditions.add(NL);
            conditions.add(NNL);
        } else if (Collection.class.isAssignableFrom(javaType)) {
            conditions.add(EX);
            conditions.add(NEX);
        } else {
            conditions.add(EQ);
            conditions.add(NE);
            conditions.add(SW);
            conditions.add(NSW);
            conditions.add(CN);
            conditions.add(NCN);
            conditions.add(EW);
            conditions.add(NEW);
            conditions.add(GT);
            conditions.add(GTE);
            conditions.add(LT);
            conditions.add(LTE);
            conditions.add(IN);
            conditions.add(NIN);
            conditions.add(NL);
            conditions.add(NNL);
        }
        return conditions;
    }

    public static Collection<Condition> getConditions() {
        return Collections.unmodifiableCollection(keyCache.values());
    }

}
