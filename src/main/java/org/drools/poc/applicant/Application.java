package org.drools.poc.applicant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Setter
@Getter
public class Application {
    private Date dateApplied;
    private boolean valid;
}
