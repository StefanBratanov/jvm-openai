# jvm-openai

[![build](https://github.com/StefanBratanov/jvm-openai/actions/workflows/build.yml/badge.svg)](https://github.com/StefanBratanov/jvm-openai/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=StefanBratanov_jvm-openai&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=StefanBratanov_jvm-openai)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.stefanbratanov/jvm-openai)](https://central.sonatype.com/artifact/io.github.stefanbratanov/jvm-openai)
[![javadoc](https://javadoc.io/badge2/io.github.stefanbratanov/jvm-openai/javadoc.svg)](https://javadoc.io/doc/io.github.stefanbratanov/jvm-openai)

A minimalistic unofficial [OpenAI API](https://platform.openai.com/docs/api-reference) client for the JVM, written in
Java. The only dependency used is [Jackson](https://github.com/FasterXML/jackson) for JSON parsing.

## Add dependency

Java 17+ is a prerequisite

### Gradle

```groovy
implementation("io.github.stefanbratanov:jvm-openai:${version}")
```

### Maven

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
    .model(OpenAIModel.GPT_3_5_TURBO)
    .message(ChatMessage.userMessage("Who won the world series in 2020?"))
    .build();
ChatCompletion chatCompletion = chatClient.createChatCompletion(createChatCompletionRequest);
```

## Supported APIs

### Endpoints

| API                                                                       | Status |
|---------------------------------------------------------------------------|:------:|
| [Audio](https://platform.openai.com/docs/api-reference/audio)             |   ✔️   |
| [Chat](https://platform.openai.com/docs/api-reference/chat)               |   ✔️   |
| [Embeddings](https://platform.openai.com/docs/api-reference/embeddings)   |   ✔️   |
| [Fine-tuning](https://platform.openai.com/docs/api-reference/fine-tuning) |   ✔️   |
| [Batch](https://platform.openai.com/docs/api-reference/batch)             |   ✔️   |
| [Files](https://platform.openai.com/docs/api-reference/files)             |   ✔️   |
| [Uploads](https://platform.openai.com/docs/api-reference/uploads)         |   ✔️   |
| [Images](https://platform.openai.com/docs/api-reference/images)           |   ✔️   |
| [Models](https://platform.openai.com/docs/api-reference/models)           |   ✔️   |
| [Moderations](https://platform.openai.com/docs/api-reference/moderations) |   ✔️   |

### Assistants (Beta)

| API                                                                                                    | Status |
|--------------------------------------------------------------------------------------------------------|:------:|
| [Assistants](https://platform.openai.com/docs/api-reference/assistants)                                |   ✔️   |
| [Threads](https://platform.openai.com/docs/api-reference/threads)                                      |   ✔️   |
| [Messages](https://platform.openai.com/docs/api-reference/messages)                                    |   ✔️   |
| [Runs](https://platform.openai.com/docs/api-reference/runs)                                            |   ✔️   |
| [Run Steps](https://platform.openai.com/docs/api-reference/run-steps)                                  |   ✔️   |
| [Vector Stores](https://platform.openai.com/docs/api-reference/vector-stores)                          |   ✔️     |
| [Vector Store Files](https://platform.openai.com/docs/api-reference/vector-stores-files)               |   ✔️     |
| [Vector Store File Batches](https://platform.openai.com/docs/api-reference/vector-stores-file-batches) |   ✔️     |

### Administration

| API                                                                              | Status |
|----------------------------------------------------------------------------------|:------:|
| [Invites](https://platform.openai.com/docs/api-reference/invite)                  |   ✔️   |
| [Users](https://platform.openai.com/docs/api-reference/users)                     |   ✔️     |
| [Projects](https://platform.openai.com/docs/api-reference/projects)            |   ✔️     |
| [Project Users](https://platform.openai.com/docs/api-reference/project-users)      |        |
| [Project Service Accounts](https://platform.openai.com/docs/api-reference/project-service-accounts) |        |
| [Project API Keys](https://platform.openai.com/docs/api-reference/project-api-keys)         |        |
| [Audit Logs](https://platform.openai.com/docs/api-reference/audit-logs)             |        |

> **_NOTE:_** Legacy APIs are not supported

## More examples

- Configure an organization and project
```java
OpenAI openAI = OpenAI.newBuilder(System.getenv("OPENAI_API_KEY"))
    .organization("org-zweLLamVlP6c5n66zY334ivs")
    .project(System.getenv("PROJECT_ID"))        
    .build();
```
- Configure a custom base url
```java
OpenAI openAI = OpenAI.newBuilder(System.getenv("OPENAI_API_KEY"))
    .baseUrl("https://api.foobar.com/v1/")     
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
    .model(OpenAIModel.GPT_3_5_TURBO)
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
chatClient.streamChatCompletion(request, new ChatCompletionStreamSubscriber() {
    @Override
    public void onChunk(ChatCompletionChunk chunk) {
        System.out.println(chunk);
    }

    @Override
    public void onException(Throwable ex) {
        // ...
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
    .model(OpenAIModel.DALL_E_3)
    .prompt("A cute baby sea otter")
    .build();
Images images = imagesClient.createImage(createImageRequest);
```
- Create speech
```java
AudioClient audioClient = openAI.audioClient();
SpeechRequest request = SpeechRequest.newBuilder()
    .model(OpenAIModel.TTS_1)
    .input("The quick brown fox jumped over the lazy dog.")
    .voice(Voice.ALLOY)
    .build();
Path output = Paths.get("/tmp/speech.mp3");
audioClient.createSpeech(request, output);
```
- Create translation
```java
AudioClient audioClient = openAI.audioClient();
TranslationRequest request = TranslationRequest.newBuilder()
    .model(OpenAIModel.WHISPER_1)
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
- Create and execute a batch
```java
// Upload JSONL file containing requests for the batch
FilesClient filesClient = openAI.filesClient();
UploadFileRequest uploadInputFileRequest = UploadFileRequest.newBuilder()
    .file(Paths.get("/tmp/batch-requests.jsonl"))
    .purpose(Purpose.BATCH)
    .build();
File inputFile = filesClient.uploadFile(uploadInputFileRequest);

BatchClient batchClient = openAI.batchClient();
CreateBatchRequest request = CreateBatchRequest.newBuilder()
    .inputFileId(inputFile.id())
    .endpoint("/v1/chat/completions")
    .completionWindow("24h")
    .build();
Batch batch = batchClient.createBatch(request);
// check status of the batch
Batch retrievedBatch = batchClient.retrieveBatch(batch.id());
System.out.println(retrievedBatch.status());      
```
- Upload large file in multiple parts
```java
UploadsClient uploadsClient = openAI.uploadsClient();
CreateUploadRequest createUploadRequest = CreateUploadRequest.newBuilder()
    .filename("training_examples.jsonl")
    .purpose(Purpose.FINE_TUNE)
    .bytes(2147483648)
    .mimeType("text/jsonl")
    .build();
Upload upload = uploadsClient.createUpload(createUploadRequest);

UploadPart part1 = uploadsClient.addUploadPart(upload.id(), Paths.get("/tmp/part1.jsonl"));
UploadPart part2 = uploadsClient.addUploadPart(upload.id(), Paths.get("/tmp/part2.jsonl"));

CompleteUploadRequest completeUploadRequest = CompleteUploadRequest.newBuilder()
    .partIds(List.of(part1.id(), part2.id()))
    .build();

Upload completedUpload = uploadsClient.completeUpload(upload.id(), completeUploadRequest);
// the created usable File object
File file = completedUpload.file();
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
    .model(OpenAIModel.GPT_3_5_TURBO_1106)
    .instructions("You are a personal math tutor. Write and run code to answer math questions.")
    .tool(Tool.codeInterpreterTool())
    .build();
Assistant assistant = assistantsClient.createAssistant(createAssistantRequest);

// Step 2: Create a Thread
CreateThreadRequest createThreadRequest = CreateThreadRequest.newBuilder().build();
Thread thread = threadsClient.createThread(createThreadRequest);

// Step 3: Add a Message to a Thread
CreateMessageRequest createMessageRequest = CreateMessageRequest.newBuilder()
    .role(Role.USER)
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
MessagesClient.PaginatedThreadMessages paginatedMessages = messagesClient.listMessages(thread.id(), PaginationQueryParameters.none(), Optional.empty());
List<ThreadMessage> messages = paginatedMessages.data();
```
- Build AI Assistant with File Search Enabled
```java
AssistantsClient assistantsClient = openAI.assistantsClient();
ThreadsClient threadsClient = openAI.threadsClient();
MessagesClient messagesClient = openAI.messagesClient();
RunsClient runsClient = openAI.runsClient();
VectorStoresClient vectorStoresClient = openAI.vectorStoresClient();
FilesClient filesClient = openAI.filesClient();
VectorStoreFileBatchesClient vectorStoreFileBatchesClient = openAI.vectorStoreFileBatchesClient();

// Step 1: Create a new Assistant with File Search Enabled
CreateAssistantRequest createAssistantRequest = CreateAssistantRequest.newBuilder()
    .name("Financial Analyst Assistant")
    .model(OpenAIModel.GPT_4_TURBO)
    .instructions("You are an expert financial analyst. Use you knowledge base to answer questions about audited financial statements.")
    .tool(Tool.fileSearchTool())
    .build();
Assistant assistant = assistantsClient.createAssistant(createAssistantRequest);

// Step 2: Upload files and add them to a Vector Store
CreateVectorStoreRequest createVectorStoreRequest = CreateVectorStoreRequest.newBuilder()
    .name("Financial Statements")
    .build();
VectorStore vectorStore = vectorStoresClient.createVectorStore(createVectorStoreRequest);
UploadFileRequest uploadFileRequest1 = UploadFileRequest.newBuilder()
    .file(Paths.get("edgar/goog-10k.pdf"))
    .purpose(Purpose.ASSISTANTS)
    .build();
File file1 = filesClient.uploadFile(uploadFileRequest1);
UploadFileRequest uploadFileRequest2 = UploadFileRequest.newBuilder()
    .file(Paths.get("edgar/brka-10k.txt"))
    .purpose(Purpose.ASSISTANTS)
    .build();
File file2 = filesClient.uploadFile(uploadFileRequest2);
CreateVectorStoreFileBatchRequest createVectorStoreFileBatchRequest = CreateVectorStoreFileBatchRequest.newBuilder()
    .fileIds(List.of(file1.id(), file2.id()))
    .build();
VectorStoreFileBatch batch = vectorStoreFileBatchesClient.createVectorStoreFileBatch(vectorStore.id(), createVectorStoreFileBatchRequest);
// need to query the status of the file batch for completion
vectorStoreFileBatchesClient.retrieveVectorStoreFileBatch(vectorStore.id(), batch.id());

// Step 3: Update the assistant to use the new Vector Store
ModifyAssistantRequest modifyAssistantRequest = ModifyAssistantRequest.newBuilder()
    .toolResources(ToolResources.fileSearchToolResources(vectorStore.id()))
    .build();
assistantsClient.modifyAssistant(assistant.id(), modifyAssistantRequest);

// Step 4: Create a thread
CreateThreadRequest.Message message = CreateThreadRequest.Message.newBuilder()
    .role(Role.USER)
    .content("How many shares of AAPL were outstanding at the end of of October 2023?")
    .build();
CreateThreadRequest createThreadRequest = CreateThreadRequest.newBuilder()
    .message(message)
    .build();
Thread thread = threadsClient.createThread(createThreadRequest);

// Step 5: Create a run and check the output
CreateRunRequest createRunRequest = CreateRunRequest.newBuilder()
    .assistantId(assistant.id())
    .instructions("Please address the user as Jane Doe. The user has a premium account.")
    .build();
ThreadRun run = runsClient.createRun(thread.id(), createRunRequest);
// check the run status
ThreadRun retrievedRun = runsClient.retrieveRun(thread.id(), run.id());
String status = retrievedRun.status();
// display the Assistant's Response
MessagesClient.PaginatedThreadMessages paginatedMessages = messagesClient.listMessages(thread.id(), PaginationQueryParameters.none(), Optional.empty());
List<ThreadMessage> messages = paginatedMessages.data();
```
- Create a run and stream the result of executing the run ([Assistants Streaming](https://platform.openai.com/docs/api-reference/assistants-streaming))
```java
RunsClient runsClient = openAI.runsClient();
CreateRunRequest createRunRequest = CreateRunRequest.newBuilder()
    .assistantId(assistant.id())
    .instructions("Please address the user as Jane Doe. The user has a premium account.")
    .stream(true)   
    .build();
// with java.util.stream.Stream
runsClient.createRunAndStream(thread.id(), createRunRequest).forEach(assistantStreamEvent -> {
    System.out.println(assistantStreamEvent.event());
    System.out.println(assistantStreamEvent.data());
});
// with subscriber
runsClient.createRunAndStream(thread.id(), createRunRequest, new AssistantStreamEventSubscriber() {
    @Override
    public void onThread(String event, Thread thread) {
        // ...
    }

    @Override
    public void onThreadRun(String event, ThreadRun threadRun) {
        // ...
    }

    @Override
    public void onThreadRunStep(String event, ThreadRunStep threadRunStep) {
        // ...
    }

    @Override
    public void onThreadRunStepDelta(String event, ThreadRunStepDelta threadRunStepDelta) {
        // ...
    }

    @Override
    public void onThreadMessage(String event, ThreadMessage threadMessage) {
        // ...
    }

    @Override
    public void onThreadMessageDelta(String event, ThreadMessageDelta threadMessageDelta) {
        // ...
    }

    @Override
    public void onUnknownEvent(String event, String data) {
        // ...
    }

    @Override
    public void onException(Throwable ex) {
        // ...
    }

    @Override
    public void onComplete() {
        // ...
    }    
});
// "createThreadAndRunAndStream" and "submitToolOutputsAndStream" methods are also available
```
