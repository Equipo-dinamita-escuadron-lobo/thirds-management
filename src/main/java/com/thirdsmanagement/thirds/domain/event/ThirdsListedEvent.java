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
public class ThirdsListedEvent {

    private Long entId;
    private LocalDateTime date;

    public ThirdsListedEvent(Long entId){
        this.entId = entId;
        this.date = LocalDateTime.now();
    }

}
