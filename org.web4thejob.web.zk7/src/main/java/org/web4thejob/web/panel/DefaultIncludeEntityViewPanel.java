package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.web.panel.base.AbstractMutablePanel;
import org.zkoss.bind.BindComposer;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultIncludeEntityViewPanel extends AbstractMutablePanel implements IncludeEntityViewPanel {
    private BindComposer<Component> bc;


    public DefaultIncludeEntityViewPanel() {
        this(MutableMode.READONLY);
    }

    protected DefaultIncludeEntityViewPanel(MutableMode mutableMode) {
        super(mutableMode);
    }


    @Override
    public Set<CommandEnum> getSupportedCommands() {
        return super.getSupportedCommands();
    }

    @Override
    protected void afterSettingsSet() {
        super.afterSettingsSet();
        arrangeForMutableMode();
    }

    @Override
    public void setTargetEntity(Entity targetEntity) {
        super.setTargetEntity(targetEntity);
        if (bc != null) {
            bc.notifyChange(this, ".");
        }
    }

    @Override
    public void setMasterEntity(Entity masterEntity) {
        super.setMasterEntity(masterEntity);
        if (bc != null) {
            bc.notifyChange(this, ".");
        }
    }

    protected void arrangeForMutableMode() {
        if (bc != null) return;

        ((org.zkoss.zul.Panel) base).getPanelchildren().setStyle("overflow: auto;");
        Component root = Executions.createComponentsDirectly("<include src=\"sec/include.zul\"/>", null,
                ((org.zkoss.zul.Panel) base).getPanelchildren(), null);

        bc = new BindComposer<>();
        bc.setViewModel(this);
        root.setAttribute("vm", this);
        try {
            bc.doBeforeComposeChildren(root);
            bc.doAfterCompose(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
/*
        if (getMutableMode() == MutableMode.READONLY) {
        } else {
        }
*/

    }

    @org.zkoss.bind.annotation.Command
    public void processCommand(@BindingParam("name") String name) {
        Clients.alert("command clicked: " + name);

        for (Command command : getCommands()) {
            if (command.getId().name().equals(name)) {
                command.process();
            }
        }
    }

    @Override
    protected void arrangeForTargetType() {
        super.arrangeForTargetType();
        arrangeForMutableMode();
    }

    @Override
    protected Class<? extends MutablePanel> getMutableType() {
        return IncludeEntityViewPanel.class;
    }

    @Override
    protected void monitorComponents(boolean monitor) {
        //skip
    }
}
