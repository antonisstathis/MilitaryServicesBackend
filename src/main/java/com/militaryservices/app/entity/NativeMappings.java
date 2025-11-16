package com.militaryservices.app.entity;

import jakarta.persistence.*;

@Entity
@SqlResultSetMapping(
        name = "ServiceRatioMapping",
        classes = @ConstructorResult(
                targetClass = com.militaryservices.app.dto.ServiceRatioDto.class,
                columns = {
                        @ColumnResult(name = "sold_id", type = Integer.class),
                        @ColumnResult(name = "ser_name", type = String.class),
                        @ColumnResult(name = "service_heavy_count", type = Long.class),
                        @ColumnResult(name = "total_heavy_count", type = Long.class),
                        @ColumnResult(name = "percent_share", type = Double.class)
                }
        )
)
public class NativeMappings {

    @Id
    @GeneratedValue
    private Long id;  // dummy ID required by JPA
}

