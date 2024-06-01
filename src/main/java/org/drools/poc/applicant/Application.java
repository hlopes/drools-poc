package org.drools.poc.applicant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Setter
@Getter
public class Application {
    private LocalDate dateApplied;
    private boolean valid;
}
