package com.wemeet.dating.model.request;

import com.wemeet.dating.model.enums.ReportType;
import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class ReportRequest {
    private long userId;
    @NotNull
    private ReportType type;
}
