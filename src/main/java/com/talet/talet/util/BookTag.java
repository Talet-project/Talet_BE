package com.talet.talet.util;

import java.util.ArrayList;
import java.util.List;

public enum BookTag {
    COURAGE,
    WISDOM,
    GOOD_AND_EVIL,
    SHARING,
    FAMILY_LOVE,
    FRIENDSHIP,
    JUSTICE,
    GROWTH;

    public String toApiValue() {
        return this.name().toLowerCase();
    }

    // 👉 프론트에서 온 소문자 → enum
    public static BookTag from(String value) {
        return BookTag.valueOf(value.toUpperCase());
    }

    // ✅ List<String> → List<BookTag>
    public static List<BookTag> fromList(List<String> values) {
        List<BookTag> result = new ArrayList<>();

        if (values == null) return result;

        for (String v : values) {
            result.add(from(v));
        }

        return result;
    }

    public static List<String> toApiList(List<BookTag> tags) {
        List<String> result = new ArrayList<>();

        if (tags == null) return result;

        for (BookTag tag : tags) {
            result.add(tag.toApiValue());
        }

        return result;
    }

}
