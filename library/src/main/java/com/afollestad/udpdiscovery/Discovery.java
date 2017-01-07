package com.afollestad.udpdiscovery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.util.Map;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Discovery extends Base {

    @SuppressLint("StaticFieldLeak") private static Discovery instance;

    @UiThread
    private Discovery(Context context) {
        super(context);
    }

    @NonNull
    @UiThread
    public static Discovery instance(@NonNull Context context) {
        if (context instanceof Activity) {
            context = context.getApplicationContext();
        }
        if (instance == null) {
            instance = new Discovery(context);
        } else {
            instance.context = context;
        }
        return instance;
    }

    public void refresh() {
        Request request = Request.create()
                .version(BuildConfig.VERSION_CODE)
                .type(RequestType.DISCOVER)
                .build();
        sendPacket(request, Request.class);
    }

    public Discovery discover(@NonNull EntityListener listener) {
        discover(listener, null);
        return this;
    }

    public Discovery discover(@NonNull EntityListener listener, @Nullable ErrorListener onerror) {
        this.entityListener = listener;
        this.senderErrorListener = onerror;
        startReceiver();
        refresh();
        return this;
    }

    public Discovery respond(@NonNull RequestListener listener) {
        respond(listener, null);
        return this;
    }

    public Discovery respond(@NonNull RequestListener listener, @Nullable ErrorListener onerror) {
        this.requestListener = listener;
        this.receiverErrorListener = onerror;
        startReceiver();
        return this;
    }

    public Discovery respondToAll() {
        return respondToAll(null);
    }

    public Discovery respondToAll(@Nullable ErrorListener onerror) {
        return respond(new RequestListener() {
            @Override public boolean onRequest(Entity entity) {
                return true;
            }
        }, onerror);
    }

    public void message(@NonNull Entity to, @NonNull Map<String, String> payload, @Nullable ErrorListener onerror) {
        Message message = Message.create()
                .payload(payload)
                .build();
        sendPacket(to.address(), message, Message.class, onerror);
    }

    public static void destroy() {
        if (instance != null) {
            instance.destroyInstance();
            instance = null;
        }
    }

    private void destroyInstance() {
        entityListener = null;
        requestListener = null;
        destroyBase();
    }
}