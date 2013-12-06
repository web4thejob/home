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

package org.web4thejob.web.util;

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.*;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageAware;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.BorderedLayoutPanel;
import org.web4thejob.web.panel.HtmlViewPanel;
import org.web4thejob.web.panel.PlaceholderPanel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbar;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class ToolbarRenderer implements CommandRenderer {
    // ------------------------------ FIELDS ------------------------------
    private static final CommandsSorter COMMANDS_SORTER = new CommandsSorter();
    private final Set<CommandAware> commandOwners = new LinkedHashSet<CommandAware>(0);
    private Component container;
    private Toolbar toolbar;
    private String align;
    private boolean supressed;

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public String getAlign() {
        return align;
    }

    @Override
    public void setAlign(String align) {
        this.align = align;
    }

    @Override
    public boolean isSupressed() {
        return supressed;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CommandRenderer ---------------------

    @Override
    public void render() {
        final boolean isEmpty = isEmpty();
        if (toolbar != null && isEmpty) {
            reset();
            return;
        } else if (toolbar != null || isEmpty) {
            return;
        }

        toolbar = new Toolbar();
        toolbar.setAlign(align);
        container.insertBefore(toolbar, container.getFirstChild());

        if (!HtmlViewPanel.class.isInstance(getPrimaryOwner())) {
            toolbar.setStyle("border-width: 0;");
        }

        SortedMap<CommandEnum, List<Command>> map = mergeCommands();

        for (final CommandEnum id : map.keySet()) {
            CommandDecorator commandDecorator = null;
            if (map.get(id).size() == 1) {
                commandDecorator = getDecorator(map.get(id).get(0));
            } else {
                for (Command command : map.get(id)) {
                    if (commandDecorator == null) {
                        commandDecorator = new DefaultDropdownCommandDecorator(command);
                    } else {
                        ((DropdownCommandDecorator) commandDecorator).add(command);
                    }
                }
            }

            if (id.isRequiresStartSeparator() && id != map.firstKey() && !isPreviousSeparator()) {
                addSeparator();
            }

            if (commandDecorator != null) {
                commandDecorator.attach(toolbar);
                commandDecorator.addMessageListener(this);
                commandDecorator.render();
            }

            if (id.isRequiresEndSeparator() && id != map.lastKey()) {
                addSeparator();
            }


            Space space = new Space();
            space.setSpacing("8px");
            space.setParent(toolbar);
        }
    }

    private boolean isPreviousSeparator() {
        Component component = toolbar.getLastChild();
        while (component != null) {
            if (Space.class.isInstance(component)) {
                component = component.getPreviousSibling();
            } else {
                break;
            }
        }

        return Separator.class.isInstance(component);
    }

    private void addSeparator() {
        Separator separator = new Separator("vertical");
        separator.setParent(toolbar);
        separator.setSclass("z-toolbarbutton");
        separator.setStyle("overflow: visible;");
        separator.setBar(true);
    }


    @Override
    public void reset() {
        if (toolbar != null) {
            List<CommandDecorator> decorators = new ArrayList<CommandDecorator>();
            for (Object item : toolbar.getChildren()) {
                CommandDecorator decorator = ((CommandDecorator) ((Component) item).getAttribute(CommandDecorator
                        .ATTRIB_DECORATOR));
                if (decorator != null) {
                    decorators.add(decorator);
                }
            }

            for (CommandDecorator decorator : decorators) {
                decorator.dettach();
            }

            toolbar.detach();
            toolbar = null;
        }
    }

    @Override
    public void setContainer(Object container) {
        if (this.container != null) throw new IllegalStateException("container can only be set once");

        if (container instanceof Panel) {
            container = ((Panel) container).getPanelchildren();
        }
        this.container = (Component) container;
    }

    @Override
    public void supress(boolean supress) {
        if (supressed != supress) {
            supressed = supress;
            reset();
        }
    }

    @Override
    public void addCommandOwner(CommandAware commandAware) {
        commandOwners.add(commandAware);
    }

    @Override
    public void removeCommandOwner(CommandAware commandAware) {
        commandOwners.remove(commandAware);
    }

    @Override
    public Set<CommandAware> getCommandOwners() {
        return Collections.unmodifiableSet(commandOwners);
    }

// --------------------- Interface MessageAware ---------------------


    @Override
    public boolean addMessageListener(MessageAware messageAware) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dispatchMessage(Message message) {
        processMessage(message);
    }

    @Override
    public boolean removeMessageListener(MessageAware messageAware) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<MessageAware> getListeners() {
        throw new UnsupportedOperationException();
    }

    // --------------------- Interface MessageListener ---------------------


    @Override
    public void processMessage(Message message) {
        switch (message.getId()) {
            case AFTER_ADD:
                reset();
                render();
                break;
            case AFTER_REMOVE:
                if (toolbar != null && isEmpty()) {
                    reset();
                }
                break;
            default:
                break;
        }
    }

// -------------------------- OTHER METHODS --------------------------

    private CommandDecorator getDecorator(Command command) {
        if (CommandEnum.QUERY_LOOKUP == command.getId()) {
            return new QueryLookupCommandDecorator(command);
        } else if (CommandEnum.RENDER_SCHEME_LOOKUP == command.getId()) {
            return new RenderSchemeLookupCommandDecorator(command);
        } else if (CommandEnum.RELATED_PANELS == command.getId()) {
            return new DefaultArbitraryDropdownCommandDecorator(command);
        } else if (!command.getId().getSubcommands().isEmpty()) {
            return new DefaultSubcommandsCommandDecorator(command);
        } else {
            return new DefaultToolbarbuttonCommandDecorator(command);
        }
    }

    private boolean isEmpty() {
        CommandAware primaryOwner = getPrimaryOwner();
        if (primaryOwner.hasCommand(CommandEnum.DESIGN) || primaryOwner.hasCommand(CommandEnum.LOCALIZE) ||
                (!primaryOwner.isCommandsSupressed() && !primaryOwner.getCommands().isEmpty())) {
            return false;
        } else if (!primaryOwner.isCommandsSupressed()) {
            for (CommandAware owner : commandOwners) {
                if (owner instanceof CommandMerger) {
                    if (!((CommandMerger) owner).getMergedCommands().isEmpty()) {
                        return false;
                    }
                } else {
                    if (!owner.getCommands().isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public CommandAware getPrimaryOwner() {
        if (!commandOwners.isEmpty()) {
            return commandOwners.iterator().next();
        }
        return null;
    }

    protected boolean isMergable(Command command) {
        boolean isPrimaryOwner = command.getOwner().equals(getPrimaryOwner());

        if (supressed && command.getId() != CommandEnum.DESIGN && command.getId() != CommandEnum.LOCALIZE) {
            // always show settiings and localization commands
            return false;
        } else if ((command.getId() == CommandEnum.DESIGN || command.getId() == CommandEnum.LOCALIZE) &&
                !isPrimaryOwner) {
            // don't integrate settings and localization command for child panels
            return false;
        } else if (command instanceof Subcommand) {
            return false;
        } else if (!isPrimaryOwner && command.getId().isCrud() && command.getOwner() instanceof org.web4thejob.web.panel
                .Panel) {
            // special toolbar rendering hint
            if (getPrimaryOwner() instanceof BorderedLayoutPanel && command.getArg(CommandMerger
                    .ATTRIB_COMMAND_MERGER, CommandMerger.class) != null) {
                CommandMerger commandMerger = command.getArg(CommandMerger.ATTRIB_COMMAND_MERGER, CommandMerger.class);
                if (commandMerger.equals(((BorderedLayoutPanel) getPrimaryOwner()).getNorth())) {
                    return !((BorderedLayoutPanel) getPrimaryOwner()).getSettingValue(SettingEnum
                            .NORTH_EXCLUDE_CRUD_COMMANDS, false);
                } else if (commandMerger.equals(((BorderedLayoutPanel) getPrimaryOwner()).getSouth())) {
                    return !((BorderedLayoutPanel) getPrimaryOwner()).getSettingValue(SettingEnum
                            .SOUTH_EXCLUDE_CRUD_COMMANDS, false);
                } else if (commandMerger.equals(((BorderedLayoutPanel) getPrimaryOwner()).getCenter())) {
                    return !((BorderedLayoutPanel) getPrimaryOwner()).getSettingValue(SettingEnum
                            .CENTER_EXCLUDE_CRUD_COMMANDS, false);
                } else if (commandMerger.equals(((BorderedLayoutPanel) getPrimaryOwner()).getWest())) {
                    return !((BorderedLayoutPanel) getPrimaryOwner()).getSettingValue(SettingEnum
                            .WEST_EXCLUDE_CRUD_COMMANDS, false);
                } else if (commandMerger.equals(((BorderedLayoutPanel) getPrimaryOwner()).getEast())) {
                    return !((BorderedLayoutPanel) getPrimaryOwner()).getSettingValue(SettingEnum
                            .EAST_EXCLUDE_CRUD_COMMANDS, false);
                }
            } else {
                Object val = (((org.web4thejob.web.panel.Panel) command.getOwner()).getAttribute(CommandRenderer
                        .ATTRIB_SUPPRESS_CRUD_COMMANDS));
                if (val instanceof Boolean) {
                    return !((Boolean) val);
                }
            }

        }

        return true;
    }


    private SortedMap<CommandEnum, List<Command>> mergeCommands() {
        SortedMap<CommandEnum, List<Command>> map = new TreeMap<CommandEnum, List<Command>>(COMMANDS_SORTER);

        for (CommandAware owner : commandOwners) {
            if (owner instanceof PlaceholderPanel && !owner.equals(getPrimaryOwner())) {
                continue;
            }

            SortedSet<Command> commands;
            if (owner instanceof CommandMerger) {
                commands = ((CommandMerger) owner).getMergedCommands();
            } else {
                commands = owner.getCommands();
            }
            for (final Command command : commands) {
                if (isMergable(command)) {
                    if (map.containsKey(command.getId())) {
                        if (!map.get(command.getId()).contains(command)) {
                            map.get(command.getId()).add(command);
                        }
                    } else {
                        List<Command> list = new ArrayList<Command>();
                        list.add(command);
                        map.put(command.getId(), list);
                    }
                }
            }

        }

        return map;
    }

    private static class CommandsSorter implements Comparator<CommandEnum> {

        @Override
        public int compare(CommandEnum o1, CommandEnum o2) {
            Integer i1 = (o1.getSubcommands().isEmpty() ? 10000 : 0) + o1.ordinal();
            Integer i2 = (o2.getSubcommands().isEmpty() ? 10000 : 0) + o2.ordinal();
            return i1.compareTo(i2);
        }
    }

}
