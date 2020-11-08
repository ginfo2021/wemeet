package com.wemeet.dating.model.response;

import com.wemeet.dating.model.request.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwipeSuggestions {
    List<UserProfile> profiles;
    int swipesLeft;
}
