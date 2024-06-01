package org.drools.poc;

import lombok.extern.slf4j.Slf4j;
import org.drools.poc.fire.Alarm;
import org.drools.poc.fire.Sprinkler;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

@Slf4j
public class ObjectEventListener implements RuleRuntimeEventListener {
    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        var object = event.getObject();

        if (object instanceof Alarm) {
            log.info("====> Raise the alarm " + object);
        } else {
            log.info("====> " + object + " inserted");
        }
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        var object = event.getObject();

        if (object instanceof Sprinkler) {
            log.info("====> Turn on the sprinkler for room " + ((Sprinkler) object).getRoom());
        } else {
            log.info("====> " + event.getObject() + " updated");
        }
    }

    @Override
    public void objectDeleted(ObjectDeletedEvent event) {
        var object = event.getOldObject();

        if (object instanceof Alarm) {
            log.info("====> Cancel the alarm " + object);
        } else {
            log.info("====> " + object + " deleted");
        }
    }
}
