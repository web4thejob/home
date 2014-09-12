package org.web4thejob.message;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.web4thejob.web.panel.Panel;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>This class is used as a request scoped memory by panels in order to to avoid processing the same message more
 * than once within the same request (ie flattening of the message chain).
 *
 * @author Veniamin Isaias
 * @since 3.6.1
 */
@Component
@Scope("request")
public class MessageChainFlattener extends LinkedHashMap<Panel, Set<Message>> {

    public void markProcessed(Panel panel, Message message) {
        if (containsKey(panel)) {
            get(panel).add(message);
            return;
        }

        Set<Message> set = new LinkedHashSet<Message>();
        set.add(message);
        put(panel, set);
    }

    public boolean isProcessed(Panel panel, Message message) {
        if (containsKey(panel)) {
            return get(panel).contains(message);
        }

        return false;
    }


}
