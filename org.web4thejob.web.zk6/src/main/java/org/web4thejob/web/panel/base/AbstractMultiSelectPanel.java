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

package org.web4thejob.web.panel.base;

//import org.web4thejob.orm.Entity;

import org.web4thejob.util.L10nString;
import org.web4thejob.web.panel.MultiSelectPanel;
import org.web4thejob.web.panel.PanelState;
import org.web4thejob.web.panel.base.zk.AbstractZkTargetTypeAwarePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@SuppressWarnings("unchecked")
public abstract class AbstractMultiSelectPanel<S, T> extends AbstractZkTargetTypeAwarePanel implements
        MultiSelectPanel<S, T>, EventListener<Event> {
    public static final L10nString L10N_BUTTON_UP = new L10nString(AbstractMultiSelectPanel.class, "button_up", "Up");
    public static final L10nString L10N_BUTTON_DOWN = new L10nString(AbstractMultiSelectPanel.class, "button_down",
            "Down");
    public static final L10nString L10N_BUTTON_ADD = new L10nString(AbstractMultiSelectPanel.class, "button_add", ">");
    public static final L10nString L10N_BUTTON_ADD_ALL = new L10nString(AbstractMultiSelectPanel.class,
            "button_addAll", ">>");
    public static final L10nString L10N_BUTTON_REMOVE = new L10nString(AbstractMultiSelectPanel.class,
            "button_remove", "<");
    public static final L10nString L10N_BUTTON_REMOVE_ALL = new L10nString(AbstractMultiSelectPanel.class,
            "button_removeAll", "<<");

    protected final Borderlayout outerBorderlayout = new Borderlayout();
    protected final Listbox targetBox = new Listbox();

    protected AbstractMultiSelectPanel() {
        this(false, false);
    }

    abstract protected void buildSourceBox();

    abstract protected void attachSourceBox(LayoutRegion parent);

    protected AbstractMultiSelectPanel(boolean readOnly, boolean excludeUpDown) {
        ZkUtil.setParentOfChild((Component) base, outerBorderlayout);
        final Borderlayout innerBorderlayout = ZkUtil.buildMultiSelectLayout(outerBorderlayout, readOnly,
                excludeUpDown, this);

        buildSourceBox();
        if (!readOnly) {
            attachSourceBox(innerBorderlayout.getWest());
        }

        targetBox.setModel(new ListModelList<T>());
        targetBox.setParent(innerBorderlayout.getEast());
        targetBox.setSpan(true);
        targetBox.setVflex("true");
        targetBox.addEventListener(Events.ON_DOUBLE_CLICK, this);
        new Listhead().setParent(targetBox);
        arrangeTargetHeaders(targetBox);

    }


    abstract protected void arrangeTargetHeaders(Listbox listbox);

    abstract protected void deselectFromSourceBox(T target);

    @Override
    public void deselect(T target) {
        deselectFromSourceBox(target);
        ((ListModelList<T>) targetBox.getModel()).remove(target);
    }

    protected abstract S getSourceFromTarget(T target);

    protected abstract T getTargetFromSource(S source);

    abstract protected void resetSourceBox();

    @Override
    protected void reset() {
        resetSourceBox();
        targetBox.setModel(new ListModelList<T>());
        arrangeForState(PanelState.UNDEFINED);
    }

    protected void moveDown() {
        final int index = targetBox.getSelectedIndex();
        if (index < 0 || index == targetBox.getModel().getSize() - 1) {
            return;
        }

        final T item = (T) targetBox.getModel().getElementAt(index);
        ((ListModelList<T>) targetBox.getModel()).remove(index);
        ((ListModelList<T>) targetBox.getModel()).add(index + 1, item);
        targetBox.setSelectedIndex(index + 1);

    }

    protected void moveUp() {
        final int index = targetBox.getSelectedIndex();
        if (index <= 0 || targetBox.getModel().getSize() == 1) {
            return;
        }

        final T item = (T) targetBox.getModel().getElementAt(index);
        ((ListModelList<T>) targetBox.getModel()).remove(index);
        ((ListModelList<T>) targetBox.getModel()).add(index - 1, item);
        targetBox.setSelectedIndex(index - 1);
    }

    abstract protected void adjustSourceList();

    @Override
    public void deselectAll() {
        for (T item : getSelection()) {
            deselect(item);
        }
    }


    @Override
    public List<T> getSelection() {
        final List<T> selection = new ArrayList<T>();
        for (final Object item : (ListModelList<T>) targetBox.getModel()) {
            selection.add((T) item);
        }

        return selection;
    }

    abstract protected void removeFromourceBox(S source);

    @Override
    public T select(S source) {
        T target = getTargetFromSource(source);
        ((ListModelList<T>) targetBox.getModel()).add(target);
        removeFromourceBox(source);
        return target;
    }

    @Override
    abstract public void selectAll();


    @Override
    public void setTargetList(List<T> list) {
        targetBox.setModel(new ListModelList<T>(list, true));
        adjustSourceList();
    }


    abstract protected S getSourceBoxSelectedItem();


    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getTarget() instanceof Toolbarbutton && event.getName().equals(Events.ON_CLICK)) {
            if (event.getTarget().hasAttribute("add") && getSourceBoxSelectedItem() != null) {
                select(getSourceBoxSelectedItem());
            } else if (event.getTarget().hasAttribute("remove") && targetBox.getSelectedIndex() >= 0) {
                deselect((T) targetBox.getModel().getElementAt(targetBox.getSelectedIndex()));
            } else if (event.getTarget().hasAttribute("addall")) {
                selectAll();
            } else if (event.getTarget().hasAttribute("removeall")) {
                deselectAll();
            } else if (event.getTarget().hasAttribute("up")) {
                moveUp();
            } else if (event.getTarget().hasAttribute("down")) {
                moveDown();
            }
        } else if (event.getTarget() instanceof Listbox && event.getName().equals(Events.ON_DOUBLE_CLICK)) {
            final Listbox lbox = (Listbox) event.getTarget();
            final int index = lbox.getSelectedIndex();
            if (index >= 0) {
                final Object item = lbox.getModel().getElementAt(index);
                if (lbox.hasAttribute("source")) {
                    select((S) item);
                } else {
                    deselect((T) item);
                }
            }
        }
    }

    abstract protected boolean isUninitialized();
}
