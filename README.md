# jvm-openai

[![build](https://github.com/StefanBratanov/jvm-openai/actions/workflows/build.yml/badge.svg)](https://github.com/StefanBratanov/jvm-openai/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.stefanbratanov/jvm-openai)](https://central.sonatype.com/artifact/io.github.stefanbratanov/jvm-openai)
[![javadoc](https://javadoc.io/badge2/io.github.stefanbratanov/jvm-openai/javadoc.svg)](https://javadoc.io/doc/io.github.stefanbratanov/jvm-openai)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=StefanBratanov_jvm-openai&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=StefanBratanov_jvm-openai)

A minimalistic unofficial [OpenAI API](https://platform.openai.com/docs/api-reference) client for the JVM, written in
Java

## Add dependency

Java 17+ is a prerequisite

#### Gradle

```groovy
implementation("io.github.stefanbratanov:jvm-openai:${version}")
```

#### Maven

```xml
<dependency>
    <groupId>io.github.stefanbratanov</groupId>
    <artifactId>jvm-openai</artifactId>
    <version>${version}</version>
</dependency>
```

## Minimal example

```java
OpenAI openAI = OpenAI.newBuilder(System.getenv("OPENAI_API_KEY")).build();

ChatClient chatClient = openAI.chatClient();
CreateChatCompletionRequest createChatCompletionRequest = CreateChatCompletionRequest.newBuilder()
    .model("gpt-3.5-turbo")
    .message(ChatMessage.userMessage("Who won the world series in 2020?"))
    .build();
ChatCompletion chatCompletion = chatClient.createChatCompletion(createChatCompletionRequest);
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

#### Beta APIs

| API                                                                     | Status |
|-------------------------------------------------------------------------|:------:|
| [Assistants](https://platform.openai.com/docs/api-reference/assistants) |   ✔️   |
| [Threads](https://platform.openai.com/docs/api-reference/threads)       | ️  ✔️  |
| [Messages](https://platform.openai.com/docs/api-reference/messages)     |   ✔️   |
| [Runs](https://platform.openai.com/docs/api-reference/runs)             |   ✔️   |

## More examples

- Configure an organization
```java
OpenAI openAI = OpenAI.newBuilder(System.getenv("OPENAI_API_KEY"))
    .organization("org-zweLLamVlP6c5n66zY334ivs")
    .build();
```
- Configure a custom Java's `HttpClient`
```java
HttpClient httpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(20))
    .executor(Executors.newFixedThreadPool(3))
    .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
    .build();
OpenAI openAI = OpenAI.newBuilder(System.getenv("OPENAI_API_KEY"))
    .httpClient(httpClient)
    .build();
```
- Configure a timeout for all requests
```java
OpenAI openAI = OpenAI.newBuilder(System.getenv("OPENAI_API_KEY"))
    .requestTimeout(Duration.ofSeconds(10))
    .build();
```
- Create chat completion async
```java
ChatClient chatClient = openAI.chatClient();
CreateChatCompletionRequest request = CreateChatCompletionRequest.newBuilder()
    .model("gpt-3.5-turbo")
    .message(ChatMessage.userMessage("Who won the world series in 2020?"))
    .build();
CompletableFuture<ChatCompletion> chatCompletion = chatClient.createChatCompletionAsync(request);
chatCompletion.thenAccept(System.out::println);
```
- Streaming
```java
ChatClient chatClient = openAI.chatClient();
CreateChatCompletionRequest request = CreateChatCompletionRequest.newBuilder()
    .message(ChatMessage.userMessage("Who won the world series in 2020?"))
    .stream(true)
    .build();
// with java.util.stream.Stream
chatClient.streamChatCompletion(request).forEach(System.out::println);
// with subscriber
chatClient.streamChatCompletion(request, new StreamChatCompletionSubscriber() {
    @Override
    public void onChunk(ChatCompletionChunk chunk) {
        System.out.println(chunk);
    }
    
    @Override
    public void onComplete() {
        // ...
    }
});
```
- Create image
```java
ImagesClient imagesClient = openAI.imagesClient();
CreateImageRequest createImageRequest = CreateImageRequest.newBuilder()
    .model("dall-e-3")
    .prompt("A cute baby sea otter")
    .build();
Images images = imagesClient.createImage(createImageRequest);
```
- Create speech
```java
AudioClient audioClient = openAI.audioClient();
SpeechRequest request = SpeechRequest.newBuilder()
    .model("ttl-1")
    .input("The quick brown fox jumped over the lazy dog.")
    .voice("alloy")
    .build();
Path output = Paths.get("/tmp/speech.mp3");
audioClient.createSpeech(request, output);
```
- Create translation
```java
AudioClient audioClient = openAI.audioClient();
TranslationRequest request = TranslationRequest.newBuilder()
    .model("whisper-1")
    .file(Paths.get("/tmp/german.m4a"))
    .build();
String translatedText = audioClient.createTranslation(request);
```
- List models
```java
ModelsClient modelsClient = openAI.modelsClient();
List<Model> models = modelsClient.listModels();
```
- Classifiy if text violates OpenAI's Content Policy
```java
ModerationsClient moderationsClient = openAI.moderationsClient();
ModerationRequest request = ModerationRequest.newBuilder()
    .input("I want to bake a cake.")
    .build();
Moderation moderation = moderationsClient.createModeration(request);
boolean violence = moderation.results().get(0).categories().violence();
```
- Build AI Assistant
```java
AssistantsClient assistantsClient = openAI.assistantsClient();
ThreadsClient threadsClient = openAI.threadsClient();
MessagesClient messagesClient = openAI.messagesClient();
RunsClient runsClient = openAI.runsClient();

// Step 1: Create an Assistant
CreateAssistantRequest createAssistantRequest = CreateAssistantRequest.newBuilder()
    .name("Math Tutor")
    .model("gpt-3.5-turbo-1106")
    .instructions("You are a personal math tutor. Write and run code to answer math questions.")
    .tool(Tool.codeInterpreterTool())
    .build();
Assistant assistant = assistantsClient.createAssistant(createAssistantRequest);

// Step 2: Create a Thread
CreateThreadRequest createThreadRequest = CreateThreadRequest.newBuilder().build();
Thread thread = threadsClient.createThread(createThreadRequest);

// Step 3: Add a Message to a Thread
CreateMessageRequest createMessageRequest = CreateMessageRequest.newBuilder()
    .role("user")
    .content("I need to solve the equation `3x + 11 = 14`. Can you help me?")
    .build();
ThreadMessage message = messagesClient.createMessage(thread.id(), createMessageRequest);

// Step 4: Run the Assistant
CreateRunRequest createRunRequest = CreateRunRequest.newBuilder()
    .assistantId(assistant.id())
    .instructions("Please address the user as Jane Doe. The user has a premium account.")
    .build();
ThreadRun run = runsClient.createRun(thread.id(), createRunRequest);

// Step 5: Check the Run status
ThreadRun retrievedRun = runsClient.retrieveRun(thread.id(), run.id());
String status = retrievedRun.status();

// Step 6: Display the Assistant's Response
PaginatedThreadMessages paginatedMessages = messagesClient.listMessages(thread.id(), PaginationQueryParameters.none());
List<ThreadMessage> messages = paginatedMessages.data();
```
