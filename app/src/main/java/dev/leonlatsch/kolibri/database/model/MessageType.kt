package dev.leonlatsch.kolibri.database.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class MessageType {
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
