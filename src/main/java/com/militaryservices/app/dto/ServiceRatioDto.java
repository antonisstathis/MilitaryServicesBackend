package com.militaryservices.app.dto;

public class ServiceRatioDto {

    private Integer soldId;
    private String serviceName;
    private Long serviceHeavyCount;
    private Long totalHeavyCount;
    private Double percentShare;

    public ServiceRatioDto(Integer soldId,
                           String serviceName,
                           Long serviceHeavyCount,
                           Long totalHeavyCount,
                           Double percentShare) {

        this.soldId = soldId;
        this.serviceName = serviceName;
        this.serviceHeavyCount = serviceHeavyCount;
        this.totalHeavyCount = totalHeavyCount;
        this.percentShare = percentShare;
    }

    public Integer getSoldId() {
        return soldId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Long getServiceHeavyCount() {
        return serviceHeavyCount;
    }

    public Long getTotalHeavyCount() {
        return totalHeavyCount;
    }

    public Double getPercentShare() {
        return percentShare;
    }

    @Override
    public String toString() {
        return "ServiceRatioDTO{" +
                "soldId=" + soldId +
                ", serviceName='" + serviceName + '\'' +
                ", serviceHeavyCount=" + serviceHeavyCount +
                ", totalHeavyCount=" + totalHeavyCount +
                ", percentShare=" + percentShare +
                '}';
    }
}

