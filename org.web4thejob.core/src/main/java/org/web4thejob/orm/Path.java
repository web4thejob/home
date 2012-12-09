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

package org.web4thejob.orm;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */
public class Path {
    public static final String DELIMITER = "/";

    private final boolean reverse;
    private StringBuilder nodes = new StringBuilder();

    public Path() {
        reverse = false;
    }

    public Path(boolean reverse) {
        this.reverse = reverse;
    }

    public Path(PropertyMetadata node) {
        reverse = false;
        append(node);
    }

    public Path(String node) {
        reverse = false;
        append(node);
    }


    public Path append(PropertyMetadata propertyMetadata) {
        String prop = "";
        if (propertyMetadata.isSubclassType()) {
            prop = "(" + propertyMetadata.getAssociatedEntityMetadata().getName() + ")";
        }
        prop += propertyMetadata.getName();
        return append(prop);
    }

    public Path append(String node) {
        if (nodes.length() > 0) {
            if (!reverse) {
                nodes.append(DELIMITER);
            } else {
                nodes.insert(0, DELIMITER);
            }
        }

        if (!reverse) {
            nodes.append(node);
        } else {
            nodes.insert(0, node);
        }

        return this;
    }

    @Override
    public String toString() {
        return nodes.toString();
    }
}
