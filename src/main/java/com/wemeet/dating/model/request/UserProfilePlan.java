package com.wemeet.dating.model.request;

import com.wemeet.dating.model.response.PlanWithLimit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserProfilePlan extends UserProfile {
    private PlanWithLimit plan;
}
