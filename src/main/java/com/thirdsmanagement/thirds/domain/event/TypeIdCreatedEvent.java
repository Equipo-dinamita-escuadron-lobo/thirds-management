package com.thirdsmanagement.thirds.domain.event;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeIdCreatedEvent {
     
    private String typeId;
    private LocalDateTime date;

    public TypeIdCreatedEvent(String id) {
        this.typeId = id;
        this.date = LocalDateTime.now();
    }
}
