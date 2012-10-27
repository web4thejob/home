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

package org.web4thejob.command;

import org.springframework.context.annotation.Scope;
import org.web4thejob.security.SecuredResource;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultSubcommand extends AbstractCommand implements Subcommand {
// ------------------------------ FIELDS ------------------------------

    private final Command parent;
    private final CommandListener alternateListener;

// --------------------------- CONSTRUCTORS ---------------------------

    protected DefaultSubcommand(CommandEnum id, Command parent) {
        this(id, parent, null);
    }

    @Override
    public void process() throws CommandProcessingException {
        if (isActive()) {
            if (alternateListener == null) {
                super.process();
            } else {
                alternateListener.process(this);
            }
        }
    }

    protected DefaultSubcommand(CommandEnum id, Command parent, CommandListener alternateListener) {
        super(id, parent.getOwner());
        this.parent = parent;
        this.alternateListener = alternateListener;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public Command getParent() {
        return parent;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getId() == null ? 0 : getId().hashCode());
        result = prime * result + (parent == null ? 0 : parent.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(parent.toString()).append("\\");
        sb.append(getName());
        return sb.toString();
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface SecuredResource ---------------------

    @Override
    public String getSid() {
        StringBuilder sb = new StringBuilder();
        sb.append(parent.getSid());
        sb.append(SecuredResource.SECURITY_PATH_DELIM);
        sb.append(Command.class.getCanonicalName()).append(".").append(getId().name());
        return sb.toString();
    }
}
