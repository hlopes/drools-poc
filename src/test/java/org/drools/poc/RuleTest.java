
package org.drools.poc;

import lombok.extern.slf4j.Slf4j;
import org.drools.poc.applicant.Applicant;
import org.drools.poc.applicant.Application;
import org.drools.poc.fire.Alarm;
import org.drools.poc.fire.Fire;
import org.drools.poc.fire.Room;
import org.drools.poc.fire.Sprinkler;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.Test;
import org.kie.api.KieServices;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Slf4j
public class RuleTest {

    @Test
    public void test() {
        log.info("Creating RuleUnit");
        MeasurementUnit measurementUnit = new MeasurementUnit();

        try (RuleUnitInstance<MeasurementUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(measurementUnit)) {
            log.info("Insert data");
            measurementUnit.getMeasurements().add(new Measurement("color", "red"));
            measurementUnit.getMeasurements().add(new Measurement("color", "green"));
            measurementUnit.getMeasurements().add(new Measurement("color", "blue"));

            log.info("Run query. Rules are also fired");
            List<Measurement> queryResult = instance.executeQuery("FindColor").toList("$m");

            assertEquals(3, queryResult.size());
            assertTrue("contains red", measurementUnit.getControlSet().contains("red"));
            assertTrue("contains green", measurementUnit.getControlSet().contains("green"));
            assertTrue("contains blue", measurementUnit.getControlSet().contains("blue"));
        }
    }

    @Test
    public void testStateless() {
        var session = KieServices.Factory.get().getKieClasspathContainer().newStatelessKieSession("ApplicantSession");

        var applicant = Applicant.builder()
                .name("Mr John Smith")
                .age(16)
                .build();

        var application = Application.builder()
                .dateApplied(LocalDate.of(2023, Month.JANUARY, 1))
                .build();

        session.execute(List.of(applicant, application));

        assertFalse(applicant.isValid());
        assertTrue(application.isValid());
    }

    @Test
    public void testStateful() {
        var kSession = KieServices.Factory.get().getKieClasspathContainer().newKieSession("FireSession");
        kSession.addEventListener(new ObjectEventListener());

        var names = new String[]{"kitchen", "bedroom", "office", "livingroom"};
        var name2room = new HashMap<String, Room>();

        for (String name : names) {
            var room = Room.builder().name(name).build();
            name2room.put(name, room);
            kSession.insert(room);

            var sprinkler = Sprinkler.builder().room(room).build();
            kSession.insert(sprinkler);
        }

        kSession.fireAllRules();
        kSession.dispose();

        kSession.getObjects(o -> o.getClass() == Sprinkler.class).stream()
                .map(o -> (Sprinkler) o)
                .forEach(sprinkler -> assertFalse(sprinkler.isOn()));
    }


    @Test
    public void testStateful2() {
        var kSession = KieServices.Factory.get().getKieClasspathContainer().newKieSession("FireSession");
        kSession.addEventListener(new ObjectEventListener());

        var names = new String[]{"kitchen", "bedroom", "office", "livingroom"};
        var name2room = new HashMap<String, Room>();

        for (String name : names) {
            var room = Room.builder().name(name).build();
            name2room.put(name, room);
            kSession.insert(room);

            var sprinkler = Sprinkler.builder().room(room).build();
            kSession.insert(sprinkler);
        }

        Fire kitchenFire = Fire.builder().room(name2room.get("kitchen")).build();
        Fire officeFire = Fire.builder().room(name2room.get("office")).build();

        var kitchenFireHandle = kSession.insert(kitchenFire);
        var officeFireHandle = kSession.insert(officeFire);

        kSession.fireAllRules();

        var sprinklersBeforeFires = kSession.getObjects(o -> o.getClass() == Sprinkler.class).stream()
                .map(o -> (Sprinkler) o)
                .toList();

        var alarmCountBeforeFires = (long) kSession.getObjects(o -> o.getClass() == Alarm.class).size();

        assertEquals(2, sprinklersBeforeFires.stream().filter(Sprinkler::isOn).count());
        assertEquals(1, alarmCountBeforeFires);

        kSession.delete(kitchenFireHandle);
        kSession.delete(officeFireHandle);

        kSession.fireAllRules();

        var alarmCountAfterFires = (long) kSession.getObjects(o -> o.getClass() == Alarm.class).size();

        assertEquals(0, alarmCountAfterFires);

        kSession.dispose();
    }
}