package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.RenderSchemeUtil;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.web.panel.base.AbstractMutablePanel;
import org.zkoss.bind.BindComposer;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.metainfo.Annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultIncludeEntityViewPanel extends AbstractMutablePanel implements IncludeEntityViewPanel {
    protected BindComposer<Component> bc;
    protected RenderScheme renderScheme;
    protected Component root;
    protected Map<String, Component> bindings = new HashMap<>();

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


        Map<String, String> args = new HashMap<>();
        args.put("mutableMode", getMutableMode().name());
        ((org.zkoss.zul.Panel) base).getPanelchildren().setStyle("overflow: auto;");
        root = Executions.createComponentsDirectly("<include src=\"sec/include.zul\"/>", null,
                ((org.zkoss.zul.Panel) base).getPanelchildren(), args);

        bc = new BindComposer<>();
        bc.setViewModel(this);
        root.setAttribute("vm", this);
        try {
            bc.doBeforeComposeChildren(root);
            bc.doAfterCompose(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        discoverBindings(root);

        //((org.zkoss.zul.Textbox)root.getFirstChild()).getAnnotation("value","load");
    }


    protected void discoverBindings(Component parent) {
        for (Component child : parent.getChildren()) {
            if (bc.getBinder().equals(BinderUtil.getBinder(child))) {
                if (child instanceof AbstractComponent) {
                    parseAnnotation((AbstractComponent) child, "bind");
                    parseAnnotation((AbstractComponent) child, "save");
                }
            }

            discoverBindings(child);
        }
    }

    protected void parseAnnotation(AbstractComponent comp, String name) {
        for (String property : comp.getAnnotatedPropertiesBy(name)) {
            Annotation annotation = comp.getAnnotation(property, name);
            String[] paths = annotation.getAttributeValues(property);
            for (String path : paths) {
                if (path.startsWith("vm.targetEntity.")) {
                    bindings.put(path.split("\\.")[2], comp);
                }
            }
        }
    }

    @org.zkoss.bind.annotation.Command
    public void processCommand(@BindingParam("name") String name) {
        //Clients.alert("command clicked: " + name);

        for (Command command : getCommands()) {
            if (command.getId().name().equals(name)) {
                command.process();
            }
        }
    }

    @Override
    protected Component getBoundComponent(PathMetadata matchPath) {
        if (matchPath == null) {
            return null;
        }

        for (String path : bindings.keySet()) {
            if (path.equals(matchPath.getPath())) {
                return bindings.get(path);
            }
        }

        return null;
    }

    public boolean hasCommand(String name) {
        for (Command command : getCommands()) {
            if (command.getId().name().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void arrangeForNullTargetType() {
        super.arrangeForNullTargetType();
        //bc = null;
        renderScheme = null;
    }

    public RenderElement getRenderElement(String name) {
        if (renderScheme != null) {
            for (RenderElement element : renderScheme.getElements()) {
                if (element.getFlatPropertyPath().equals(name)) {
                    return element;
                }
            }
        }
        return null;
    }

    @Override
    protected void arrangeForTargetType() {
        super.arrangeForTargetType();
        renderScheme = RenderSchemeUtil.getDefaultRenderScheme(getTargetType(), SchemeType.ENTITY_SCHEME);
        arrangeForMutableMode();
    }

    @Override
    protected Class<? extends MutablePanel> getMutableType() {
        return IncludeEntityViewPanel.class;
    }

    protected void monitorComponents(boolean monitor) {
        monitorComponents(root, monitor);
    }

    protected void monitorComponents(Component parent, boolean monitor) {
        for (Component child : parent.getChildren()) {

            if (bc.getBinder().equals(BinderUtil.getBinder(child))) {
                for (String event : MONITOR_EVENTS) {
                    if (monitor) {
                        child.addEventListener(event, changeMonitor);
                    } else {
                        child.removeEventListener(event, changeMonitor);
                    }
                }
            }

            monitorComponents(child, monitor);
        }
    }
}
