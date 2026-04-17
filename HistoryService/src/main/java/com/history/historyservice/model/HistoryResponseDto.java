package com.history.historyservice.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryResponseDto {
    private List<QuantityHistoryDto> items;
    private int page;
    private int size;
    private long totalElements;
    private boolean isLast;
}