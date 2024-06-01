package org.drools.poc.fire;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Fire {
    private Room room;
}
