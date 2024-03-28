package io.github.stefanbratanov.jvm.openai;

import io.github.stefanbratanov.jvm.openai.ChatMessage.UserMessage.UserMessageWithContentParts.ContentPart;
import io.github.stefanbratanov.jvm.openai.CreateChatCompletionRequest.ResponseFormat;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.ImageFileContent;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.TextContent;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.TextContent.Text.Annotation;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.TextContent.Text.Annotation.FileCitationAnnotation;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.TextContent.Text.Annotation.FilePathAnnotation;
import io.github.stefanbratanov.jvm.openai.ThreadRunStep.StepDetails;
import io.github.stefanbratanov.jvm.openai.ThreadRunStep.StepDetails.MessageCreationStepDetails;
import io.github.stefanbratanov.jvm.openai.ThreadRunStep.StepDetails.ToolCallsStepDetails;
import io.github.stefanbratanov.jvm.openai.Tool.FunctionTool;
import io.github.stefanbratanov.jvm.openai.ToolCall.CodeInterpreterToolCall.CodeInterpreter;
import io.github.stefanbratanov.jvm.openai.ToolCall.CodeInterpreterToolCall.CodeInterpreter.Output.ImageOutput;
import io.github.stefanbratanov.jvm.openai.ToolCall.FunctionToolCall;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDataUtil {

  private static final String CHARSET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final double EPSILON = 0.000001;

  private final Random random = new Random();

  public CreateChatCompletionRequest randomCreateChatCompletionRequest() {
    CreateChatCompletionRequest.Builder builder =
        CreateChatCompletionRequest.newBuilder()
            .model(randomModel())
            .messages(listOf(randomInt(1, 3), this::randomChatMessage))
            .frequencyPenalty(randomDouble(-2.0, 2.0))
            .logitBias(randomLogitBias(randomInt(0, 6)))
            .logprobs(randomBoolean())
            .topLogprobs(randomInt(0, 20))
            .maxTokens(randomInt(0, 10_000))
            .n(randomInt(1, 128))
            .presencePenalty(randomDouble(-2.0, 2.0))
            .responseFormat(oneOf(ResponseFormat.json(), ResponseFormat.text()))
            .seed(randomInt())
            .stop(arrayOf(randomInt(0, 4), () -> randomString(5), String[]::new))
            .stream(randomBoolean())
            .temperature(randomDouble(0.0, 2.0))
            .topP(randomDouble(0.0, 1.0))
            .tools(listOf(randomInt(0, 5), this::randomFunctionTool));
    runOne(
        () -> builder.toolChoice(oneOf("none", "auto")),
        () ->
            builder.toolChoice(
                ToolChoice.functionToolChoice(new ToolChoice.Function(randomString(5)))));
    return builder.build();
  }

  public ChatCompletion randomChatCompletion() {
    return new ChatCompletion(
        randomString(10),
        randomLong(1, 100_000),
        randomString(10),
        randomString(10),
        listOf(randomInt(1, 3), this::randomChatCompletionChoice),
        randomUsage());
  }

  public SpeechRequest randomSpeechRequest() {
    return SpeechRequest.newBuilder()
        .model(randomTtsModel())
        .input(randomString(15))
        .voice(oneOf("alloy", "echo", "fable", "onyx", "nova", "shimmer"))
        .responseFormat(oneOf("mp3", "opus", "aac", "flac"))
        .speed(randomDouble(0.25, 4.0))
        .build();
  }

  public EmbeddingsRequest randomEmbeddingsRequest() {
    EmbeddingsRequest.Builder builder = EmbeddingsRequest.newBuilder();
    runOne(
        () -> builder.input(arrayOf(randomInt(1, 5), () -> randomString(10), String[]::new)),
        () -> builder.input(randomIntArray(randomInt(1, 5))),
        () -> builder.input(listOf(randomInt(1, 10), () -> randomIntArray(randomInt(1, 5)))));
    return builder
        .model(oneOf("text-embedding-ada-002", "text-embedding-3-small", "text-embedding-3-large"))
        .encodingFormat(oneOf("float", "base64"))
        .dimensions(randomInt(1, 10))
        .user(randomString(10))
        .build();
  }

  public Embeddings randomEmbeddings() {
    return new Embeddings(
        listOf(randomInt(1, 10), this::randomEmbedding),
        randomModel(),
        new Embeddings.Usage(randomInt(1, 100), randomInt(1, 100)));
  }

  public CreateFineTuningJobRequest randomCreateFineTuningJobRequest() {
    return CreateFineTuningJobRequest.newBuilder()
        .model(oneOf("babbage-002", "davinci-002", "gpt-3.5-turbo"))
        .trainingFile(randomString(5))
        .hyperparameters(
            new CreateFineTuningJobRequest.Hyperparameters(
                Optional.of(oneOf("auto", randomInt(1, 256))),
                Optional.of(oneOf("auto", randomInt(0, 10_000))),
                Optional.of(oneOf("auto", randomInt(1, 50)))))
        .suffix(randomString(1, 40))
        .validationFile(randomString(10))
        .build();
  }

  public FineTuningJob randomFineTuningJob() {
    return new FineTuningJob(
        randomString(12),
        randomLong(5, 120_000),
        new FineTuningJob.Error(randomString(5), randomString(15), randomString(6)),
        randomString(12),
        randomLong(5, 100_000),
        new FineTuningJob.Hyperparameters(oneOf("auto", randomInt(1, 50))),
        randomModel(),
        randomString(12),
        listOf(randomInt(1, 5), () -> randomString(6)),
        oneOf("validating_files", "queued", "running", "succeeded", "failed", "cancelled"),
        randomInt(5, 1000),
        randomString(10),
        randomString(12));
  }

  public FineTuningClient.PaginatedFineTuningJobs randomPaginatedFineTuningJobs() {
    return new FineTuningClient.PaginatedFineTuningJobs(
        listOf(randomInt(1, 10), this::randomFineTuningJob), randomBoolean());
  }

  public FineTuningJobEvent randomFineTuningJobEvent() {
    return new FineTuningJobEvent(
        randomString(12), randomLong(5, 120_000), oneOf("info", "warn", "error"), randomString(10));
  }

  public FineTuningClient.PaginatedFineTuningEvents randomPaginatedFineTuningEvents() {
    return new FineTuningClient.PaginatedFineTuningEvents(
        listOf(randomInt(1, 10), this::randomFineTuningJobEvent), randomBoolean());
  }

  public File randomFile() {
    return new File(
        randomString(15),
        randomInt(1, 1000),
        randomLong(1, 42_000),
        randomString(7),
        oneOf("fine-tune", "fine-tune-results", "assistants", "assistants_output"));
  }

  public CreateImageRequest randomCreateImageRequest() {
    return CreateImageRequest.newBuilder()
        .prompt(randomString(10, 1000))
        .model(oneOf("dall-e-2", "dall-e-3"))
        .n(randomInt(1, 10))
        .quality(oneOf("standard", "hd"))
        .responseFormat(oneOf("url", "b64_json"))
        .size(oneOf("256x256", "512x512", "1024x1024", "1792x1024", "1024x1792"))
        .style(oneOf("vivid", "natural"))
        .user(randomString(8))
        .build();
  }

  public Images randomImages() {
    return new Images(randomLong(1, 10_000), listOf(randomInt(1, 5), this::randomImage));
  }

  public Model randomModelObject() {
    return new Model(randomString(10), randomLong(1, 10_000), randomString(7));
  }

  public ModerationRequest randomModerationRequest() {
    ModerationRequest.Builder builder = ModerationRequest.newBuilder();
    runOne(
        () -> builder.input(randomString(10)),
        () -> builder.inputs(listOf(randomInt(1, 5), () -> randomString(10))));
    return builder.model(oneOf("text-moderation-latest", "text-moderation-stable")).build();
  }

  public Moderation randomModeration() {
    return new Moderation(
        randomString(6),
        randomModel(),
        listOf(
            randomInt(1, 5),
            () ->
                new Moderation.Result(
                    randomBoolean(),
                    new Moderation.Result.Categories(
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean(),
                        randomBoolean()),
                    new Moderation.Result.CategoryScores(
                        randomDouble(),
                        randomDouble(),
                        randomDouble(),
                        randomDouble(),
                        randomDouble(),
                        randomDouble(),
                        randomDouble(),
                        randomDouble(),
                        randomDouble(),
                        randomDouble(),
                        randomDouble()))));
  }

  public CreateAssistantRequest randomCreateAssistantRequest() {
    return CreateAssistantRequest.newBuilder()
        .model(randomModel())
        .name(randomString(10, 256))
        .description(randomString(10, 512))
        .instructions(randomString(15, 32768))
        .tools(listOf(randomInt(1, 5), this::randomTool))
        .fileIds(randomFileIds(20))
        .metadata(randomMetadata())
        .build();
  }

  public Assistant randomAssistant() {
    return new Assistant(
        randomString(7),
        randomLong(1, 99_999),
        randomString(5, 256),
        randomString(10, 512),
        randomModel(),
        randomString(15, 32768),
        listOf(randomInt(1, 20), this::randomTool),
        listOf(randomInt(1, 20), () -> randomString(7)),
        randomMetadata());
  }

  public CreateThreadRequest randomCreateThreadRequest() {
    return CreateThreadRequest.newBuilder()
        .messages(
            listOf(
                randomInt(1, 5),
                () ->
                    CreateThreadRequest.Message.newBuilder()
                        .content(randomString(1, 32768))
                        .fileIds(randomFileIds(10))
                        .metadata(randomMetadata())
                        .build()))
        .metadata(randomMetadata())
        .build();
  }

  public Thread randomThread() {
    return new Thread(randomString(7), randomLong(1, 42_000), randomMetadata());
  }

  public ModifyThreadRequest randomModifyThreadRequest() {
    return ModifyThreadRequest.newBuilder().metadata(randomMetadata()).build();
  }

  public CreateMessageRequest randomCreateMessageRequest() {
    return CreateMessageRequest.newBuilder()
        .content(randomString(1, 32768))
        .fileIds(randomFileIds(10))
        .metadata(randomMetadata())
        .build();
  }

  public ThreadMessage randomThreadMessage() {
    return new ThreadMessage(
        randomString(6),
        randomLong(1, 31_000),
        randomString(8),
        oneOf("in_progress", "incomplete", "completed"),
        new ThreadMessage.IncompleteDetails(
            oneOf("content_filter", "max_tokens", "run_cancelled", "run_expired", "run_failed")),
        randomLong(1, 25_000),
        randomLong(1, 24_000),
        oneOf("user", "assistant"),
        listOf(
            randomInt(1, 8),
            () ->
                oneOf(
                    new TextContent(
                        new TextContent.Text(
                            randomString(10), listOf(randomInt(1, 8), this::randomAnnotation))),
                    new ImageFileContent(new ImageFileContent.ImageFile(randomString(4))))),
        randomString(8),
        randomString(5),
        randomFileIds(10),
        randomMetadata());
  }

  public ThreadMessageFile randomThreadMessageFile() {
    return new ThreadMessageFile(randomString(7), randomLong(1, 9999), randomString(6));
  }

  public CreateRunRequest randomCreateRunRequest() {
    return CreateRunRequest.newBuilder()
        .assistantId(randomString(6))
        .model(randomModel())
        .instructions(randomString(10, 100))
        .additionalInstructions(randomString(5, 50))
        .tools(listOf(randomInt(1, 20), this::randomTool))
        .metadata(randomMetadata())
        .build();
  }

  public CreateThreadAndRunRequest randomCreateThreadAndRunRequest() {
    return CreateThreadAndRunRequest.newBuilder()
        .assistantId(randomString(6))
        .thread(randomCreateThreadRequest())
        .model(randomModel())
        .instructions(randomString(10, 100))
        .tools(listOf(randomInt(1, 20), this::randomTool))
        .metadata(randomMetadata())
        .build();
  }

  public ThreadRun randomThreadRun() {
    return new ThreadRun(
        randomString(5),
        randomLong(20, 10_000),
        randomString(5),
        randomString(6),
        oneOf(
            "queued",
            "in_progress",
            "requires_action",
            "cancelling",
            "cancelled",
            "failed",
            "completed",
            "expired"),
        ThreadRun.RequiredAction.submitToolOutputsRequiredAction(
            new ThreadRun.RequiredAction.SubmitToolOutputs(
                listOf(randomInt(1, 5), () -> randomFunctionToolCall(false)))),
        new ThreadRun.LastError(
            oneOf("server_error", "rate_limit_exceeded", "invalid_prompt"), randomString(5, 20)),
        randomLong(5, 999),
        randomLong(4, 333),
        randomLong(7, 888),
        randomLong(9, 345),
        randomLong(10, 2442),
        randomModel(),
        randomString(10, 200),
        listOf(randomInt(1, 20), this::randomTool),
        randomFileIds(20),
        randomMetadata(),
        randomUsage());
  }

  public ThreadRunStep randomThreadRunStep() {
    return new ThreadRunStep(
        randomString(5),
        randomLong(20, 10_000),
        randomString(5),
        randomString(6),
        randomString(8),
        oneOf("message_creation", "tool_calls"),
        oneOf("in_progress", "cancelled", "failed", "completed", "expired"),
        randomStepDetails(),
        new ThreadRunStep.LastError(
            oneOf("server_error", "rate_limit_exceeded"), randomString(5, 20)),
        randomLong(5, 999),
        randomLong(4, 333),
        randomLong(7, 888),
        randomLong(9, 345),
        randomMetadata(),
        randomUsage());
  }

  public SubmitToolOutputsRequest randomSubmitToolOutputsRequest() {
    return new SubmitToolOutputsRequest(
        listOf(
            randomInt(1, 5),
            () ->
                SubmitToolOutputsRequest.ToolOutput.newBuilder()
                    .toolCallId(randomString(6))
                    .output(randomString(5, 20))
                    .build()));
  }

  private StepDetails randomStepDetails() {
    return oneOf(
        new MessageCreationStepDetails(
            new MessageCreationStepDetails.MessageCreation(randomString(6))),
        new ToolCallsStepDetails(
            listOf(
                randomInt(1, 10),
                () ->
                    oneOf(
                        randomFunctionToolCall(true),
                        randomCodeInterpreterToolCall(),
                        ToolCall.retrievalToolCall(randomString(5))))));
  }

  private Usage randomUsage() {
    return new Usage(randomInt(0, 100), randomInt(0, 100), randomInt(0, 100));
  }

  private Annotation randomAnnotation() {
    return oneOf(
        new FileCitationAnnotation(
            randomString(10, 100),
            new FileCitationAnnotation.FileCitation(randomString(8), randomString(5, 20)),
            randomInt(0, 100),
            randomInt(0, 100)),
        new FilePathAnnotation(
            randomString(10, 100),
            new FilePathAnnotation.FilePath(randomString(8)),
            randomInt(0, 100),
            randomInt(0, 100)));
  }

  private Embeddings.Embedding randomEmbedding() {
    return new Embeddings.Embedding(
        randomInt(1, 10_000), listOf(randomInt(1, 10), this::randomDouble));
  }

  private Images.Image randomImage() {
    return oneOf(
        new Images.Image(randomString(200), null, randomString(10)),
        new Images.Image(null, randomString(7), randomString(10)));
  }

  private List<String> randomFileIds(int max) {
    return listOf(randomInt(1, max), () -> randomString(7));
  }

  private ChatMessage randomChatMessage() {
    return oneOf(
        ChatMessage.systemMessage(randomString(10)),
        ChatMessage.userMessage(randomString(10)),
        ChatMessage.userMessage(
            arrayOf(
                randomInt(1, 5),
                () ->
                    oneOf(
                        ContentPart.textContentPart(randomString(15)),
                        ContentPart.imageContentPart(randomString(7)),
                        ContentPart.imageContentPart(
                            randomString(7), oneOf("auto", "low", "high"))),
                ContentPart[]::new)),
        ChatMessage.assistantMessage(randomString(10)),
        ChatMessage.assistantMessage(
            randomString(10), listOf(randomInt(1, 5), () -> randomFunctionToolCall(false))),
        ChatMessage.toolMessage(randomString(10), randomString(9)));
  }

  private Map<Integer, Integer> randomLogitBias(int length) {
    Map<Integer, Integer> logitBias = new HashMap<>();
    for (int i = 0; i < length; i++) {
      logitBias.put(randomInt(), randomInt(-100, 100));
    }
    return logitBias;
  }

  private Map<String, String> randomMetadata() {
    int length = randomInt(1, 16);
    Map<String, String> metadata = new HashMap<>();
    for (int i = 0; i < length; i++) {
      metadata.put(randomString(3, 64), randomString(10, 512));
    }
    return metadata;
  }

  private ChatCompletion.Choice randomChatCompletionChoice() {
    return new ChatCompletion.Choice(
        randomInt(0, 10),
        new ChatCompletion.Choice.Message(
            randomString(10),
            listOf(randomInt(0, 3), () -> randomFunctionToolCall(false)),
            Constants.ASSISTANT_MESSAGE_ROLE),
        randomLogprobs(),
        randomFinishReason());
  }

  private Logprobs randomLogprobs() {
    return new Logprobs(listOf(randomInt(1, 4), this::randomLogprobsContent));
  }

  private Logprobs.Content randomLogprobsContent() {
    return new Logprobs.Content(
        randomString(10),
        randomDouble(0.0, 1.0),
        randomBytes(randomInt(1, 10)),
        listOf(randomInt(1, 5), this::randomTopLogprob));
  }

  private Logprobs.TopLogprob randomTopLogprob() {
    return new Logprobs.TopLogprob(
        randomString(10), randomDouble(0.0, 1.0), randomBytes(randomInt(1, 10)));
  }

  private String randomModel() {
    return oneOf(
        "gpt-4-1106-preview",
        "gpt-4-vision-preview",
        "gpt-4",
        "gpt-4-0314",
        "gpt-4-0613",
        "gpt-4-32k",
        "gpt-4-32k-0314",
        "gpt-4-32k-0613",
        "gpt-3.5-turbo",
        "gpt-3.5-turbo-16k",
        "gpt-3.5-turbo-0301",
        "gpt-3.5-turbo-0613",
        "gpt-3.5-turbo-1106",
        "gpt-3.5-turbo-16k-0613");
  }

  private String randomTtsModel() {
    return oneOf("tts-1", "tts-1-hd");
  }

  private String randomFinishReason() {
    return oneOf("stop", "length", "tool_calls", "content_filter", "function_call");
  }

  private Tool randomTool() {
    return oneOf(randomFunctionTool(), Tool.retrievalTool(), Tool.codeInterpreterTool());
  }

  private ToolCall randomCodeInterpreterToolCall() {
    return ToolCall.codeInterpreterToolCall(
        randomString(5),
        new CodeInterpreter(
            randomString(5, 99),
            listOf(
                randomInt(1, 8),
                () ->
                    oneOf(
                        CodeInterpreter.Output.logOutput(randomString(5, 42)),
                        CodeInterpreter.Output.imageOutput(
                            new ImageOutput.Image(randomString(4)))))));
  }

  private ToolCall randomFunctionToolCall(boolean withOutput) {
    return ToolCall.functionToolCall(
        randomString(5),
        new FunctionToolCall.Function(
            randomString(5), randomString(10), withOutput ? randomString(5) : null));
  }

  private FunctionTool randomFunctionTool() {
    return Tool.functionTool(
        FunctionTool.Function.newBuilder()
            .name(randomString(10))
            .description(randomString(15))
            .build());
  }

  private int randomInt() {
    return random.nextInt();
  }

  private int randomInt(int min, int max) {
    return random.nextInt(min, max + 1);
  }

  private double randomDouble() {
    return random.nextDouble();
  }

  private double randomDouble(double min, double max) {
    return random.doubles(1, min, max + EPSILON).findFirst().orElse(0.0);
  }

  private long randomLong(long min, long max) {
    return random.nextLong(min, max + 1);
  }

  private boolean randomBoolean() {
    return random.nextBoolean();
  }

  private int[] randomIntArray(int length) {
    return random.ints(length).toArray();
  }

  private List<Byte> randomBytes(int length) {
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return IntStream.range(0, length).mapToObj(i -> bytes[i]).toList();
  }

  private String randomString(int min, int max) {
    return randomString(randomInt(min, max));
  }

  private String randomString(int length) {
    return random
        .ints(length, 0, CHARSET.length())
        .mapToObj(CHARSET::charAt)
        .map(Object::toString)
        .collect(Collectors.joining());
  }

  @SafeVarargs
  private <T> T oneOf(T... choices) {
    int randomIndex = random.nextInt(choices.length);
    return choices[randomIndex];
  }

  private <T> List<T> listOf(int length, Supplier<T> choiceSupplier) {
    return IntStream.range(0, length).mapToObj(i -> choiceSupplier.get()).toList();
  }

  private <T> T[] arrayOf(int length, Supplier<T> choiceSupplier, IntFunction<T[]> generator) {
    return listOf(length, choiceSupplier).toArray(generator);
  }

  private void runOne(Runnable... choices) {
    oneOf(choices).run();
  }
}
