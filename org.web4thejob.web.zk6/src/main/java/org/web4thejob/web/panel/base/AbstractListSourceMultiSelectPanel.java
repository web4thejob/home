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

package org.web4thejob.web.panel.base;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.LayoutRegion;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;

import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractListSourceMultiSelectPanel<S, T> extends AbstractMultiSelectPanel<S, T> {
    protected Listbox sourceBox;

    protected AbstractListSourceMultiSelectPanel() {
        super(false, false);
    }

    protected AbstractListSourceMultiSelectPanel(boolean readOnly, boolean excludeUpDown) {
        super(readOnly, excludeUpDown);
    }

    @Override
    protected void buildSourceBox() {
        sourceBox = new Listbox();
        sourceBox.setModel(new ListModelList<S>());
        sourceBox.setAttribute("source", true);
    }

    @Override
    protected void attachSourceBox(LayoutRegion parent) {
        sourceBox.setParent(parent);
        sourceBox.setSpan(true);
        sourceBox.setVflex("true");
        sourceBox.addEventListener(Events.ON_DOUBLE_CLICK, this);
        new Listhead().setParent(sourceBox);
        arrangeSourceHeaders(sourceBox);
    }

    @Override
    protected void deselectFromSourceBox(T target) {
        ((ListModelList<S>) sourceBox.getModel()).add(getSourceFromTarget(target));
    }

    @Override
    protected void resetSourceBox() {
        sourceBox.setModel(new ListModelList<S>());
    }

    @Override
    protected void adjustSourceList() {
        final ListModelList<S> srcmodel = (ListModelList<S>) sourceBox.getModel();
        for (final Object item : (ListModelList<T>) targetBox.getModel()) {
            srcmodel.remove(getSourceFromTarget((T) item));
        }
    }

    @Override
    protected void removeFromourceBox(S source) {
        ((ListModelList<S>) sourceBox.getModel()).remove(source);
    }

    @Override
    public void selectAll() {
        while (sourceBox.getModel().getSize() > 0) {
            select((S) sourceBox.getModel().getElementAt(0));
        }
    }

    abstract protected void arrangeSourceHeaders(Listbox listbox);

    @Override
    public void setSourceList(List<S> list) {
        sourceBox.setModel(new ListModelList<S>(list, true));
    }

    @Override
    protected S getSourceBoxSelectedItem() {
        if (sourceBox.getSelectedIndex() >= 0) {
            return (S) sourceBox.getModel().getElementAt(sourceBox.getSelectedIndex());
        }
        return null;
    }

    @Override
    protected boolean isUninitialized() {
        return targetBox.getModel() == null ||
                sourceBox.getModel() == null ||
                sourceBox.getModel().getSize() == 0 && targetBox.getModel().getSize() == 0;
    }

}
