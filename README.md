# UDP Discovery

This is very simple and easy to use library, allowing you to discover other devices on a Wifi
network that implement the same basic protocol.

---

# The Protocol

This library uses a simple JSON protocol, over UDP (User Datagram Packets). UDP allows you to send
packets over a Wifi network, including broadcasts which are sent to all connected devices.

### Discovery Request

When you send a discovery request, you broadcast a message that asks all devices on the network to
respond, making them visible to you.

```json
{
  "type": "discover",
  "version": 1
}
```

### Discovery Response

When you receive a discovery request, you can choose to send a response which makes you visible to
the requester. This is sent directly to the requester, *not* as a broadcast.

```json
{
  "type": "response",
  "version": 1
}
```

### Short Message

You can exchange short messages directly with entities. A payload object can contain whatever you'd like,
the values need to be strings (even for numbers, booleans, etc.).

```json
{
  "payload": {
    "entry1": "value1",
    "entry2": "another value?"
  }
}
```

**The overall JSON needs to be 1024 bytes or less, including the brackets, quotes, etc.**

---

# Gradle Dependency

[ ![jCenter](https://api.bintray.com/packages/drummer-aidan/maven/udpdiscovery/images/download.svg) ](https://bintray.com/drummer-aidan/maven/udpdiscovery/_latestVersion)
[![Build Status](https://travis-ci.org/afollestad/udp-discovery.svg)](https://travis-ci.org/afollestad/udp-discovery)

The Gradle dependency is available via [jCenter](https://bintray.com/drummer-aidan/maven/udpdiscovery/view).
jCenter is the default Maven repository used by Android Studio.

### Dependency

Add this to your module's `build.gradle` file:

```gradle
dependencies {
	// ... other dependencies

	compile 'com.afollestad:udpdiscovery:1.1.0'
}
```

---

# Usage

This library makes managing these messages and broadcasts easy, along with parsing the data.

### Discovering Entities

Discovering entities means finding other devices on that network.

```java
Discovery.instance(this)
    .discover(entity -> {
        // Do something with the entity
    });
```

This automatically broadcasts a discovery request, and waits for responses indefinitely.

You can manually send out further discovery requests with the refresh method:

```java
Discovery.instance(this).refresh();
```

### Responding to Entities

Responding to entities means sending responses for discovery requests. This makes you visible to
the requester.

```java
Discovery.instance(this)
    .respond(entity -> {
        // If you return true, a response is sent to the entity, making you visible
        return true;
    });
```

You can also choose to automatically respond to all, if you don't want to do any filtering.

```java
Discovery.instance(this)
    respondToAll();
```

### Error Handling

As seen in the sample project, you can provide a second callback to the `discover` and `respond`
methods to receive errors asynchronously. If these callbacks are not specified, exceptions are
thrown instead.

```java
Discovery.instance(this)
    .discover(entity -> {
        // Entity discovered
    }, throwable -> {
        // Error related to discovery, or packet retrieval in general
    })
    .respond(entity -> {
        // Discovery request received
        return true;
    }, throwable -> {
        // Error related to discovery responses, or sending packets in general
    });
```

Even `respondToAll()` has an optional error handler parameter.

Notice that you can also chain methods.

### Messaging

You can to exchange short messages directly with entities.

```java
Entity recipient = ...
Map<String, String> payload = new HashMap<>();
payload.put("body", "Hello!");

Discovery.instance(this)
    .message(recipient, payload);
```

An optional third parameter accepts an error listener; without that, an exception is thrown if an error occurs during sending.

### Cleanup

When you are done with the library, e.g. when your app closes, you **should do cleanup**.

```java
Discovery.destroy();
```

I recommend doing this in `onPause()`, and reinitializing in `onResume()` when your app comes into
view again. You probably **do not** want to leak networking while your `Activity` is in the background.
Any type of background networking **needs to be done from a Service**.