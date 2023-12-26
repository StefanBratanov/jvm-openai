# ChatJPT

[![build](https://github.com/StefanBratanov/chatjpt/actions/workflows/build.yml/badge.svg)](https://github.com/StefanBratanov/chatjpt/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.stefanbratanov/chatjpt)](https://central.sonatype.com/artifact/io.github.stefanbratanov/chatjpt)


A minimalistic Java client for the [OpenAI API](https://platform.openai.com/docs/api-reference)

## Add dependency

Java 17+ is a prerequisite.

#### Gradle
```groovy
implementation("io.github.stefanbratanov:chatjpt:${version}")
```

#### Maven
```xml
<dependency>
    <groupId>io.github.stefanbratanov</groupId>
    <artifactId>chatjpt</artifactId>
    <version>${version}</version>
</dependency>
```

## Minimal sample

```java
ChatJPT chatJPT = ChatJPT.newBuilder("OPENAI_API_KEY").build();
ChatClient chatClient = chatJPT.chatClient();
ChatRequest request = ChatRequest.newBuilder()
        .model("gpt-3.5-turbo")
        .message(Message.userMessage("Who won the world series in 2020?"))
        .build();
ChatResponse response = chatClient.sendRequest(request);
// ChatResponse[id=chatcmpl-123, created=1703506594, model=gpt-3.5-turbo-0613, systemFingerprint=fp_44709d6fcb, choices=[Choice[index=0, message=Message[role=assistant, content=The Los Angeles Dodgers won the World Series in 2020.], finishReason=stop]], usage=Usage[promptTokens=0, completionTokens=0, totalTokens=0]]
```

## Supported APIs

| API                                                                       | Status |
|---------------------------------------------------------------------------|:------:|
| [Audio](https://platform.openai.com/docs/api-reference/audio)             |        |
| [Chat](https://platform.openai.com/docs/api-reference/chat)               | ️  ✔️  |
| [Embeddings](https://platform.openai.com/docs/api-reference/embeddings)   |        |
| [Fine-tuning](https://platform.openai.com/docs/api-reference/fine-tuning) |        |
| [Files](https://platform.openai.com/docs/api-reference/files)             |        |
| [Images](https://platform.openai.com/docs/api-reference/images)           |        |
| [Models](https://platform.openai.com/docs/api-reference/models)           |   ✔️   |
| [Moderations](https://platform.openai.com/docs/api-reference/moderations) |        |

There are no plans to support the Beta APIs.


