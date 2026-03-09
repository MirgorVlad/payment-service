package org.mirgor.common.dto;

import java.math.BigDecimal;

public record EntityCountPrice(Long count, BigDecimal price) {

    public BigDecimal total() {
        return price.multiply(BigDecimal.valueOf(count));
    }
}
