package org.drools.poc.applicant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Applicant {
    private String name;
    private int age;
    private boolean valid;
}
