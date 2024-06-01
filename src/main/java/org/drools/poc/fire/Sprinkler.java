package org.drools.poc.fire;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Sprinkler {
    private Room room;
    private boolean on;
}
