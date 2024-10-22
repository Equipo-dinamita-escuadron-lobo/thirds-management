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
public class ThirdCreatedEvent {
    private Long thId;

    private LocalDateTime date;

    public ThirdCreatedEvent(Long id) {
        this.thId = id;
        this.date = LocalDateTime.now();
    }
}
