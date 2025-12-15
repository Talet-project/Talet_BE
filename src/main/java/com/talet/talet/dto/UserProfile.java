package com.talet.talet.dto;

import com.talet.talet.util.LanguageEnum;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class UserProfile {
    private String nickname;           // "엄마", "아빠" 등
    private List<LanguageEnum> languages;
}
