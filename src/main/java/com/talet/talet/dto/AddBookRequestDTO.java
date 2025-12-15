package com.talet.talet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddBookRequestDTO {
    private String name;
    private String englishName;
    private String thumbnail;
    private List<String> stillImages;
    private List<String> tags;
}
