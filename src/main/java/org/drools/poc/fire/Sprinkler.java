package org.drools.poc.fire;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Sprinkler {
    private Room room;
    private boolean on;
}
