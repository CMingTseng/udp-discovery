package com.afollestad.udpdiscovery;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.Map;

/**
 * @author Aidan Follestad (afollestad)
 */
@AutoValue public abstract class Message implements Parcelable {

    public static Builder create() {
        return new AutoValue_Message.Builder();
    }

    public abstract Entity from();

    public abstract Map<String, String> payload();

    public Builder toBuilder() {
        return new AutoValue_Message.Builder(this);
    }

    public static TypeAdapter<Message> typeAdapter(Gson gson) {
        return new AutoValue_Message.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder from(Entity from);

        public abstract Builder payload(Map<String, String> payload);

        public abstract Message build();
    }
}
