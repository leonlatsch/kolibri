package dev.leonlatsch.olivia.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageType {
    @JsonProperty("UNDEFINED")
    UNDEFINED,

    @JsonProperty("TEXT")
    TEXT,

    @JsonProperty("IMAGE")
    IMAGE,

    @JsonProperty("AUDIO")
    AUDIO,

    @JsonProperty("VIDEO")
    VIDEO
}
