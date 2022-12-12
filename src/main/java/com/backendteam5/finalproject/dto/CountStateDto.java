package com.backendteam5.finalproject.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CountStateDto {
    private String state;
    private Long count;

    @QueryProjection
    public CountStateDto(String state, Long count){
        this.count = count;
        this.state = state;
    }
}