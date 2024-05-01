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
public class ThirdTypeCreatedEvent {
      private Long thirdTypeId;
    private LocalDateTime date;

    public ThirdTypeCreatedEvent(Long id) {
        this.thirdTypeId = id;
        this.date = LocalDateTime.now();
    }
}

