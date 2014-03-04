package my.joblet;

import org.web4thejob.web.panel.DefaultMutableEntityViewPanel;
import org.web4thejob.web.panel.DirtyListener;
import org.web4thejob.web.panel.MutableMode;
import org.web4thejob.web.panel.MutablePanel;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class DefaultMyJobletPanel extends DefaultMutableEntityViewPanel implements MyJobletPanel {

    public DefaultMyJobletPanel(MutableMode mutableMode) {
        super(mutableMode);
    }

    public DefaultMyJobletPanel() {
        super();
    }

    @Override
    protected DirtyListener getDirtyListener() {
        if (dirtyHandler == null) {
            dirtyHandler = new MyDirtyHandler();
        }

        return dirtyHandler;
    }

    @Override
    protected Class<? extends MutablePanel> getMutableType() {
        return MyJobletPanel.class;
    }

    public class MyDirtyHandler extends DirtyHandler {

        @Override
        public void onDirty(boolean dirty, Object... args) {
            super.onDirty(dirty, args);

            if (dirty) {
                //do something...
            }
        }
    }
}
