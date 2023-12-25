# ChatJPT

A minimalistic Java client for the [OpenAI API](https://platform.openai.com/docs/api-reference)

## Minimal sample

```java
ChatJPT chatJPT = ChatJPT.newBuilder("OPENAI_API_KEY").build();
ChatClient chatClient = chatJPT.newChatClient();
ChatRequest request = ChatRequest.newBuilder()
        .model("gpt-3.5-turbo")
        .message(Message.userMessage("Who won the world series in 2020?"))
        .build();
ChatResponse response = chatClient.sendRequest(request);
// ChatResponse[id=chatcmpl-123, created=1703499443, model=gpt-3.5-turbo-0613, systemFingerprint=fp_44709d6fcb, message=Message[role=assistant, content=The Los Angeles Dodgers won the World Series in 2020.], usage=Usage[promptTokens=17, completionTokens=13, totalTokens=30]]
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


