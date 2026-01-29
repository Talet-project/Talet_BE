package com.talet.talet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AddBookRequestDTO {
    private String name;
    private String englishName;
    private String thumbnail;
    private Map<String, String> stillImages;
    private List<String> tags;
}
