
package org.drools.poc;

import lombok.extern.slf4j.Slf4j;
import org.drools.poc.applicant.Applicant;
import org.drools.poc.applicant.Application;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.Test;
import org.kie.api.KieServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;
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
    public void test2() {
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
}