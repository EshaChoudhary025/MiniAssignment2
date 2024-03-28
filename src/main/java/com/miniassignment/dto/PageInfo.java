package com.miniassignment.dto;

public class PageInfo {

    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private Long total;

    public PageInfo(boolean hasNextPage, boolean hasPreviousPage, Long total) {
        this.hasNextPage = hasNextPage;
        this.hasPreviousPage = hasPreviousPage;
        this.total = total;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public Long getTotal() {
        return total;
    }
}
