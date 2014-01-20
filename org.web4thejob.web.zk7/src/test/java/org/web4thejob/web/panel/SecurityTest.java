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

package org.web4thejob.web.panel;

import org.junit.Test;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.test.AbstractWebApplicationContextTest;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class SecurityTest extends AbstractWebApplicationContextTest {

    @Test
    public void generalTest() {
        for (CommandEnum id : CommandEnum.values()) {
            Command parent = printCommand(id, null);

            for (CommandEnum subid : id.getSubcommands()) {
                printCommand(subid, parent);
            }
        }

        // for (Panel panel :
        // BeanFactoryUtils.beansOfTypeIncludingAncestors(ContextUtil.getSessionContext(),
        // Panel.class).values()) {
        // printPanel(panel);
        // }

    }

    private Command printCommand(CommandEnum id, Command parent) {
        Command command;
        if (parent == null) {
            command = ContextUtil.getDefaultCommand(id, null);
        } else {
            command = ContextUtil.getSubcommand(id, parent);
        }
        System.out.println(command.getSid());

        return command;
    }
}
