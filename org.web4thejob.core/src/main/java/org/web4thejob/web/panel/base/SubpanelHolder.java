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

package org.web4thejob.web.panel.base;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.web.panel.Panel;
import org.web4thejob.web.panel.ParentCapable;
import org.web4thejob.web.panel.Subpanels;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class SubpanelHolder extends AbstractCollection<Panel> implements Subpanels {
    private static class ChildrenComparator implements Comparator<Panel> {
        @Override
        public int compare(Panel o1, Panel o2) {
            return Integer.valueOf(o1.getIndex()).compareTo(o2.getIndex());
        }
    }

    private static final ChildrenComparator comparator = new ChildrenComparator();
    private final ParentCapable owner;
    private final List<Panel> children = new ArrayList<Panel>(3);

    public SubpanelHolder(ParentCapable owner) {
        this.owner = owner;
    }

    @Override
    public boolean add(Panel panel) {
        if (!contains(panel) && owner.accepts(panel)) {
            dispatchBeforeAdd(panel);
            children.add(panel);
            if (!owner.equals(panel.getParent())) {
                panel.setParent(owner);
            }
            dispatchAfterAdd(panel);
            if (panel instanceof MessageAware) {
                ((MessageAware) panel).addMessageListener(this);
            }
            Collections.sort(children, comparator);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Panel> panels) {
        boolean result = true;
        for (final Panel panel : panels) {
            result &= add(panel);
        }
        return result;
    }

    @Override
    public boolean addMessageListener(MessageAware messageAware) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        for (Panel panel : new ArrayList<Panel>(children)) /*resolve ConcurrentModificationException*/ {
            remove(panel);
        }
    }

    protected void dispatchAfterAdd(Panel panel) {
        final Message message = ContextUtil.getMessage(MessageEnum.AFTER_ADD, this, MessageArgEnum.ARG_ITEM, panel);
        owner.processMessage(message);
    }

    protected void dispatchAfterRemove(Panel panel) {
        final Message message = ContextUtil.getMessage(MessageEnum.AFTER_REMOVE, this, MessageArgEnum.ARG_ITEM, panel);
        owner.processMessage(message);
    }

    protected void dispatchAfterReplace(Panel oldItem, Panel newItem) {
        final Map<MessageArgEnum, Object> args = new HashMap<MessageArgEnum, Object>();
        args.put(MessageArgEnum.ARG_OLD_ITEM, oldItem);
        args.put(MessageArgEnum.ARG_NEW_ITEM, newItem);
        final Message message = ContextUtil.getMessage(MessageEnum.AFTER_REPLACE, this, args);
        owner.processMessage(message);
    }

    protected void dispatchBeforeAdd(Panel panel) {
        final Message message = ContextUtil.getMessage(MessageEnum.BEFORE_ADD, this, MessageArgEnum.ARG_ITEM, panel);
        owner.processMessage(message);
    }

    protected void dispatchBeforeRemove(Panel panel) {
        final Message message = ContextUtil.getMessage(MessageEnum.BEFORE_REMOVE, this, MessageArgEnum.ARG_ITEM, panel);
        owner.processMessage(message);
    }

    protected void dispatchBeforeReplace(Panel oldItem, Panel newItem) {
        final Map<MessageArgEnum, Object> args = new HashMap<MessageArgEnum, Object>();
        args.put(MessageArgEnum.ARG_OLD_ITEM, oldItem);
        args.put(MessageArgEnum.ARG_NEW_ITEM, newItem);
        final Message message = ContextUtil.getMessage(MessageEnum.BEFORE_REPLACE, this, args);
        owner.processMessage(message);
    }


    @Override
    public void dispatchMessage(Message message) {
        owner.dispatchMessage(message);
    }

    @Override
    public Panel first() {
        if (!isEmpty()) return children.get(0);
        return null;
    }

    @Override
    public Panel get(int index) {
        return children.get(index);
    }

    @Override
    public Iterator<Panel> iterator() {
        return Collections.unmodifiableList(children).iterator();
    }

    @Override
    public void processMessage(Message message) {
        for (final Panel panel : this) {
            if (panel instanceof MessageAware) {
                ((MessageAware) panel).processMessage(message);
            }
        }

    }

    @Override
    public boolean remove(Object object) {
        if (contains(object)) {
            final Panel panel = (Panel) object;
            dispatchBeforeRemove(panel);
            children.remove(panel);
            panel.setParent(null);
            panel.detach();
            if (panel instanceof MessageAware) {
                ((MessageAware) panel).removeMessageListener(this);
            }
            dispatchAfterRemove(panel);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> panels) {
        boolean result = true;
        for (final Object object : panels) {
            result &= remove(object);
        }
        return result;
    }

    @Override
    public boolean removeMessageListener(MessageAware messageAware) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(Panel oldItem, Panel newItem) {
        if (contains(oldItem) && !contains(newItem)) {
            dispatchBeforeReplace(oldItem, newItem);
            remove(oldItem);
            add(newItem);
            dispatchAfterReplace(oldItem, newItem);
            return true;
        }
        return false;
    }

    @Override
    public void sort() {
        Collections.sort(children, comparator);
    }

    @Override
    public boolean retainAll(Collection<?> panels) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public Set<MessageAware> getListeners() {
        throw new UnsupportedOperationException();
    }

}
