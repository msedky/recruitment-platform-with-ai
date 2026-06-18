package com.recruitment.platform.model.enums;

public enum OutboxStatus {
    PENDING,     // created, not yet published to broker
    PUBLISHED,   // successfully published to broker
    FAILED       // exceeded max retry count — ERROR log triggered
}