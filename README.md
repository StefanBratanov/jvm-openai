# ChatJPT

[![build](https://github.com/StefanBratanov/chatjpt/actions/workflows/build.yml/badge.svg)](https://github.com/StefanBratanov/chatjpt/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.stefanbratanov/chatjpt)](https://central.sonatype.com/artifact/io.github.stefanbratanov/chatjpt)
[![javadoc](https://javadoc.io/badge2/io.github.stefanbratanov/chatjpt/javadoc.svg)](https://javadoc.io/doc/io.github.stefanbratanov/chatjpt)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=StefanBratanov_chatjpt&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=StefanBratanov_chatjpt)

A minimalistic unofficial Java client for the [OpenAI API](https://platform.openai.com/docs/api-reference)

## Add dependency

Java 17+ is a prerequisite

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
ChatRequest chatRequest = ChatRequest.newBuilder()
        .model("gpt-3.5-turbo")
        .message(ChatMessage.userMessage("Who won the world series in 2020?"))
        .build();
ChatResponse response = chatClient.sendRequest(chatRequest);
// ChatResponse[id=chatcmpl-123, created=1703506594, model=gpt-3.5-turbo-0613, systemFingerprint=fp_44709d6fcb, choices=[Choice[index=0, message=AssistantMessage[content=The Los Angeles Dodgers won the World Series in 2020.], logProbs=null, finishReason=stop]], usage=Usage[promptTokens=17, completionTokens=13, totalTokens=30]]

ImagesClient imagesClient = chatJPT.imagesClient();
CreateImageRequest createImageRequest = CreateImageRequest.newBuilder()
        .model("dall-e-3")
        .prompt("A cute baby sea otter")
        .build();
Images images = imagesClient.createImage(createImageRequest);
// Images[created=1704009569, data=[Image[b64Json=null, url=https://foo.bar/cute-baby-sea-otter.png, revisedPrompt=Generate an image of a baby sea otter, exuding cuteness. The small, furry creature should be floating blissfully on its back in clear, calm waters, its round button eyes are brimming with innocence and curiosity.]]]
```

## Supported APIs

| API                                                                       | Status |
|---------------------------------------------------------------------------|:------:|
| [Audio](https://platform.openai.com/docs/api-reference/audio)             |   ✔️   |
| [Chat](https://platform.openai.com/docs/api-reference/chat)               | ️  ✔️  |
| [Embeddings](https://platform.openai.com/docs/api-reference/embeddings)   |   ✔️   |
| [Fine-tuning](https://platform.openai.com/docs/api-reference/fine-tuning) |   ✔️   |
| [Files](https://platform.openai.com/docs/api-reference/files)             |   ✔️   |
| [Images](https://platform.openai.com/docs/api-reference/images)           |   ✔️   |
| [Models](https://platform.openai.com/docs/api-reference/models)           |   ✔️   |
| [Moderations](https://platform.openai.com/docs/api-reference/moderations) |   ✔️   |

Beta APIs are currently not supported


