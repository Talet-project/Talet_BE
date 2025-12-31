package com.talet.talet.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupertoneTTSRequest {
    private String text;
    private String language;
    private String style;
    private String model;
    private String output_format;
    private VoiceSettings voiceSettings;
    private Boolean include_phonemes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VoiceSettings {
        private Integer pitch_shift;
        private Integer pitch_variance;
        private Double speed;
        private Integer duration;
        private Integer similarity;
        private Integer text_guidance;
        private Integer subharmonic_amplitude_control;
    }

    public static SupertoneTTSRequest defaultRequest(String text) {
        return SupertoneTTSRequest.builder()
                .text(text)
                .language("ko")
                .style("neutral")
                .model("sona_speech_1")
                .output_format("wav")
                .voiceSettings(VoiceSettings.builder()
                        .pitch_shift(0)
                        .pitch_variance(1)
                        .speed(1.0)
                        .duration(0)
                        .similarity(3)
                        .text_guidance(3)
                        .subharmonic_amplitude_control(1)
                        .build())
                .include_phonemes(false)
                .build();
    }
}
