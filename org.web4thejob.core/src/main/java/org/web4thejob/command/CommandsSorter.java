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

package org.web4thejob.command;

import java.util.Comparator;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class CommandsSorter implements Comparator<Command> {

    public int compare(Command o1, Command o2) {
        Integer i1 = (o1.getId().getSubcommands().isEmpty() ? 10000 : 0) + o1.getId().ordinal();
        Integer i2 = (o2.getId().getSubcommands().isEmpty() ? 10000 : 0) + o2.getId().ordinal();
        return i1.compareTo(i2);
    }
}
