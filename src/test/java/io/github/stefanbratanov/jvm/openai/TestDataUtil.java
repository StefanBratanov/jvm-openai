package io.github.stefanbratanov.jvm.openai;

import io.github.stefanbratanov.jvm.openai.CreateChatCompletionRequest.StreamOptions;
import io.github.stefanbratanov.jvm.openai.FineTuningJobIntegration.Wandb;
import io.github.stefanbratanov.jvm.openai.RunStepsClient.PaginatedThreadRunSteps;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.ImageFileContent;
import io.github.stefanbratanov.jvm.openai.ThreadMessage.Content.ImageUrlContent;
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
import io.github.stefanbratanov.jvm.openai.ToolResources.FileSearch.VectorStore;
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
            .responseFormat(oneOf(ResponseFormat.text(), ResponseFormat.json()))
            .seed(randomInt())
            .serviceTier(oneOf("auto", "default"))
            .stop(arrayOf(randomInt(0, 4), () -> randomString(5), String[]::new))
            .stream(randomBoolean())
            .streamOptions(StreamOptions.withUsageIncluded())
            .temperature(randomDouble(0.0, 2.0))
            .topP(randomDouble(0.0, 1.0))
            .tools(listOf(randomInt(0, 5), this::randomFunctionTool));
    runOne(
        () -> builder.toolChoice(oneOf("none", "auto", "required")),
        () ->
            builder.toolChoice(
                ToolChoice.functionToolChoice(new ToolChoice.Function(randomString(5)))));
    return builder.parallelToolCalls(randomBoolean()).user(randomString(6)).build();
  }

  public ChatCompletion randomChatCompletion() {
    return new ChatCompletion(
        randomString(10),
        randomLong(1, 100_000),
        randomString(10),
        oneOf("scale", "default"),
        randomString(10),
        listOf(randomInt(1, 3), this::randomChatCompletionChoice),
        randomUsage());
  }

  public SpeechRequest randomSpeechRequest() {
    return SpeechRequest.newBuilder()
        .model(randomTtsModel())
        .input(randomString(15))
        .voice(oneOf(Voice.ALLOY, Voice.ECHO, Voice.FABLE, Voice.ONYX, Voice.NOVA, Voice.SHIMMER))
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
                // satisfy exclusiveMinimum of 0
                Optional.of(oneOf("auto", randomDouble(0 + EPSILON, 10_000))),
                Optional.of(oneOf("auto", randomInt(1, 50)))))
        .suffix(randomString(1, 40))
        .validationFile(randomString(10))
        .integrations(listOf(randomInt(1, 5), this::randomIntegration))
        .seed(randomInt(0, 2147483646))
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
        randomString(12),
        listOf(randomInt(1, 5), this::randomIntegration),
        randomInt(0, 2147483646));
  }

  public FineTuningClient.PaginatedFineTuningJobs randomPaginatedFineTuningJobs() {
    return new FineTuningClient.PaginatedFineTuningJobs(
        listOf(randomInt(1, 10), this::randomFineTuningJob), randomBoolean());
  }

  public FineTuningJobEvent randomFineTuningJobEvent() {
    return new FineTuningJobEvent(
        randomString(12), randomLong(5, 120_000), oneOf("info", "warn", "error"), randomString(10));
  }

  public FineTuningJobCheckpoint randomFineTuningJobCheckpoint() {
    return new FineTuningJobCheckpoint(
        randomString(5),
        randomLong(1, 10_000),
        randomString(5),
        randomInt(0, 1000),
        new FineTuningJobCheckpoint.Metrics(
            randomDouble(),
            randomDouble(),
            randomDouble(),
            randomDouble(),
            randomDouble(),
            randomDouble(),
            randomDouble()),
        randomString(5));
  }

  public FineTuningClient.PaginatedFineTuningEvents randomPaginatedFineTuningEvents() {
    return new FineTuningClient.PaginatedFineTuningEvents(
        listOf(randomInt(1, 10), this::randomFineTuningJobEvent), randomBoolean());
  }

  public FineTuningClient.PaginatedFineTuningCheckpoints randomPaginatedFineTuningCheckpoints() {
    return new FineTuningClient.PaginatedFineTuningCheckpoints(
        listOf(randomInt(1, 10), this::randomFineTuningJobCheckpoint),
        randomString(5),
        randomString(5),
        randomBoolean());
  }

  public Batch randomBatch() {
    return new Batch(
        randomString(5),
        randomString(15),
        new Batch.Errors(
            listOf(
                randomInt(0, 5),
                () ->
                    new Batch.Errors.Data(
                        randomString(4), randomString(10), randomString(7), randomInt(0, 1000)))),
        randomString(6),
        randomString(4),
        oneOf(
            "validating",
            "failed",
            "in_progress",
            "finalizing",
            "completed",
            "expired",
            "cancelling",
            "cancelled"),
        randomString(6),
        randomString(6),
        randomLong(0, 42_000),
        randomLong(0, 42_000),
        randomLong(0, 42_000),
        randomLong(0, 42_000),
        randomLong(0, 42_000),
        randomLong(0, 42_000),
        randomLong(0, 42_000),
        randomLong(0, 42_000),
        randomLong(0, 42_000),
        new Batch.RequestCounts(randomInt(1, 20), randomInt(1, 15), randomInt(1, 5)),
        randomMetadata());
  }

  public CreateBatchRequest randomCreateBatchRequest() {
    return CreateBatchRequest.newBuilder()
        .inputFileId(randomString(7))
        .endpoint(oneOf("/v1/chat/completions", "/v1/embeddings", "/v1/completions"))
        .completionWindow(oneOf("24h"))
        .metadata(randomMetadata())
        .build();
  }

  public BatchClient.PaginatedBatches randomPaginatedBatches() {
    return new BatchClient.PaginatedBatches(
        listOf(randomInt(1, 20), this::randomBatch),
        randomString(5),
        randomString(5),
        randomBoolean());
  }

  public File randomFile() {
    return new File(
        randomString(15),
        randomInt(1, 1000),
        randomLong(1, 42_000),
        randomString(7),
        oneOf(
            "assistants",
            "assistants_output",
            "batch",
            "batch_output",
            "fine-tune",
            "fine-tune-results",
            "vision"));
  }

  public CreateImageRequest randomCreateImageRequest() {
    return CreateImageRequest.newBuilder()
        .prompt(randomString(10, 1000))
        .model(oneOf(OpenAIModel.DALL_E_2, OpenAIModel.DALL_E_3))
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
    return builder
        .model(oneOf(OpenAIModel.TEXT_MODERATION_LATEST, OpenAIModel.TEXT_MODERATION_STABLE))
        .build();
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
        .instructions(randomString(15, 256000))
        .tools(listOf(randomInt(1, 5), this::randomTool))
        .toolResources(randomToolResources(true))
        .metadata(randomMetadata())
        .temperature(randomDouble(0, 2))
        .topP(randomDouble(0, 1))
        .responseFormat(randomAssistantsResponseFormat())
        .build();
  }

  public ModifyAssistantRequest randomModifyAssistantRequest() {
    return ModifyAssistantRequest.newBuilder()
        .model(randomModel())
        .name(randomString(10, 256))
        .description(randomString(10, 512))
        .instructions(randomString(15, 256000))
        .tools(listOf(randomInt(1, 5), this::randomTool))
        .toolResources(randomToolResources(false))
        .metadata(randomMetadata())
        .temperature(randomDouble(0, 2))
        .topP(randomDouble(0, 1))
        .responseFormat(randomAssistantsResponseFormat())
        .build();
  }

  public Assistant randomAssistant() {
    return new Assistant(
        randomString(7),
        randomLong(1, 99_999),
        randomString(5, 256),
        randomString(10, 512),
        randomModel(),
        randomString(15, 256000),
        listOf(randomInt(1, 20), this::randomTool),
        randomToolResources(false),
        randomMetadata(),
        randomDouble(0, 2),
        randomDouble(0, 1),
        randomAssistantsResponseFormat());
  }

  public CreateThreadRequest randomCreateThreadRequest() {
    return CreateThreadRequest.newBuilder()
        .messages(
            listOf(
                randomInt(1, 5),
                () ->
                    CreateThreadRequest.Message.newBuilder()
                        .content(randomString(1, 32768))
                        .attachments(listOf(randomInt(1, 5), this::randomAttachment))
                        .metadata(randomMetadata())
                        .build()))
        .toolResources(randomToolResources(true))
        .metadata(randomMetadata())
        .build();
  }

  public Attachment randomAttachment() {
    return new Attachment(
        randomString(5),
        listOf(randomInt(1, 2), () -> oneOf(Tool.codeInterpreterTool(), Tool.fileSearchTool())));
  }

  public Thread randomThread() {
    return new Thread(
        randomString(7), randomLong(1, 42_000), randomToolResources(false), randomMetadata());
  }

  public ModifyThreadRequest randomModifyThreadRequest() {
    return ModifyThreadRequest.newBuilder()
        .toolResources(randomToolResources(false))
        .metadata(randomMetadata())
        .build();
  }

  public CreateMessageRequest randomCreateMessageRequest() {
    CreateMessageRequest.Builder builder =
        CreateMessageRequest.newBuilder().role(oneOf("user", "assistant"));
    runOne(
        () -> builder.content(randomString(1, 256000)),
        () -> {
          List<ContentPart> content =
              listOf(
                  randomInt(1, 5),
                  () ->
                      oneOf(
                          ContentPart.textContentPart(randomString(15)),
                          ContentPart.imageUrlContentPart(randomString(7)),
                          ContentPart.imageUrlContentPart(
                              randomString(7), oneOf("auto", "low", "high")),
                          ContentPart.imageFileContentPart(randomString(7)),
                          ContentPart.imageFileContentPart(
                              randomString(7), oneOf("auto", "low", "high"))));
          builder.content(content);
        });
    return builder
        .attachments(listOf(randomInt(1, 3), this::randomAttachment))
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
                            randomString(10),
                            listOf(randomInt(1, 8), this::randomThreadMessageAnnotation))),
                    new ImageFileContent(
                        new ImageFileContent.ImageFile(
                            randomString(4), Optional.of(oneOf("auto", "low", "high")))),
                    new ImageUrlContent(
                        new ImageUrlContent.ImageUrl(
                            randomString(7), Optional.of(oneOf("auto", "low", "high")))))),
        randomString(8),
        randomString(5),
        listOf(randomInt(1, 3), this::randomAttachment),
        randomMetadata());
  }

  public ThreadMessageDelta randomThreadMessageDelta() {
    return new ThreadMessageDelta(
        randomString(6),
        new ThreadMessageDelta.Delta(
            oneOf("user", "assistant"),
            listOf(
                randomInt(1, 8),
                () ->
                    oneOf(
                        new ThreadMessageDelta.Delta.Content.TextContent(
                            randomInt(0, 25),
                            new ThreadMessageDelta.Delta.Content.TextContent.Text(
                                randomString(10),
                                listOf(randomInt(1, 8), this::randomThreadMessageDeltaAnnotation))),
                        new ThreadMessageDelta.Delta.Content.ImageFileContent(
                            randomInt(0, 25),
                            new ThreadMessageDelta.Delta.Content.ImageFileContent.ImageFile(
                                randomString(4), Optional.of(oneOf("auto", "low", "high")))),
                        new ThreadMessageDelta.Delta.Content.ImageUrlContent(
                            randomInt(0, 25),
                            new ThreadMessageDelta.Delta.Content.ImageUrlContent.ImageUrl(
                                randomString(4), Optional.of(oneOf("auto", "low", "high"))))))));
  }

  public CreateRunRequest randomCreateRunRequest() {
    return CreateRunRequest.newBuilder()
        .assistantId(randomString(6))
        .model(randomModel())
        .instructions(randomString(10, 100))
        .additionalInstructions(randomString(5, 50))
        .additionalMessages(listOf(randomInt(1, 5), this::randomCreateMessageRequest))
        .tools(listOf(randomInt(1, 20), this::randomTool))
        .metadata(randomMetadata())
        .temperature(randomDouble(0, 2))
        .topP(randomDouble(0, 1))
        .stream(randomBoolean())
        .maxPromptTokens(randomInt(256, 10_000))
        .maxCompletionTokens(randomInt(256, 10_000))
        .truncationStrategy(randomTruncationStrategy())
        .toolChoice(randomAssistantsToolChoice())
        .parallelToolCalls(randomBoolean())
        .responseFormat(randomAssistantsResponseFormat())
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
        .temperature(randomDouble(0, 2))
        .topP(randomDouble(0, 1))
        .stream(randomBoolean())
        .maxPromptTokens(randomInt(256, 10_000))
        .maxCompletionTokens(randomInt(256, 10_000))
        .truncationStrategy(randomTruncationStrategy())
        .toolChoice(randomAssistantsToolChoice())
        .parallelToolCalls(randomBoolean())
        .responseFormat(randomAssistantsResponseFormat())
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
            "incomplete",
            "expired"),
        ThreadRun.RequiredAction.submitToolOutputsRequiredAction(
            new ThreadRun.RequiredAction.SubmitToolOutputs(
                listOf(randomInt(1, 5), () -> randomFunctionToolCall(false)))),
        new LastError(
            oneOf("server_error", "rate_limit_exceeded", "invalid_prompt"), randomString(5, 20)),
        randomLong(5, 999),
        randomLong(4, 333),
        randomLong(7, 888),
        randomLong(9, 345),
        randomLong(10, 2442),
        new ThreadRun.IncompleteDetails(oneOf("max_completion_tokens", "max_prompt_tokens")),
        randomModel(),
        randomString(10, 200),
        listOf(randomInt(1, 20), this::randomTool),
        randomMetadata(),
        randomUsage(),
        randomDouble(0, 2),
        randomDouble(0, 1),
        randomInt(256, 10_000),
        randomInt(256, 10_000),
        randomTruncationStrategy(),
        randomAssistantsToolChoice(),
        randomBoolean(),
        randomAssistantsResponseFormat());
  }

  public PaginatedThreadRunSteps randomPaginatedThreadRunSteps() {
    return new PaginatedThreadRunSteps(
        listOf(randomInt(1, 20), this::randomThreadRunStep),
        randomString(5),
        randomString(5),
        randomBoolean());
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
        new LastError(oneOf("server_error", "rate_limit_exceeded"), randomString(5, 20)),
        randomLong(5, 999),
        randomLong(4, 333),
        randomLong(7, 888),
        randomLong(9, 345),
        randomMetadata(),
        randomUsage());
  }

  public ThreadRunStepDelta randomThreadRunStepDelta() {
    return new ThreadRunStepDelta(
        randomString(5), new ThreadRunStepDelta.Delta(randomStepDeltaDetails()));
  }

  public SubmitToolOutputsRequest randomSubmitToolOutputsRequest() {
    return SubmitToolOutputsRequest.newBuilder()
        .toolOutputs(
            listOf(
                randomInt(1, 5),
                () ->
                    SubmitToolOutputsRequest.ToolOutput.newBuilder()
                        .toolCallId(randomString(6))
                        .output(randomString(5, 20))
                        .build()))
        .stream(randomBoolean())
        .build();
  }

  public CreateVectorStoreRequest randomCreateVectorStoreRequest() {
    return CreateVectorStoreRequest.newBuilder()
        .fileIds(listOf(randomInt(0, 20), () -> randomString(4)))
        .name(randomString(7))
        .expiresAfter(randomExpiresAfter())
        .chunkingStrategy(
            oneOf(ChunkingStrategy.autoChunkingStrategy(), randomStaticChunkingStrategy()))
        .metadata(randomMetadata())
        .build();
  }

  public io.github.stefanbratanov.jvm.openai.VectorStore randomVectorStore() {
    return new io.github.stefanbratanov.jvm.openai.VectorStore(
        randomString(5),
        randomLong(5, 10_000),
        randomString(7),
        randomLong(100, 100_000),
        new io.github.stefanbratanov.jvm.openai.VectorStore.FileCounts(
            randomInt(0, 10),
            randomInt(0, 10),
            randomInt(0, 10),
            randomInt(0, 10),
            randomInt(0, 40)),
        oneOf("expired", "in_progress", "completed"),
        randomExpiresAfter(),
        randomLong(7000, 100_000),
        randomLong(10, 9999),
        randomMetadata());
  }

  public CreateVectorStoreFileRequest randomCreateVectorStoreFileRequest() {
    return CreateVectorStoreFileRequest.newBuilder()
        .fileId(randomString(5))
        .chunkingStrategy(
            oneOf(ChunkingStrategy.autoChunkingStrategy(), randomStaticChunkingStrategy()))
        .build();
  }

  public VectorStoreFile randomVectorStoreFile() {
    return new VectorStoreFile(
        randomString(5),
        randomLong(5, 10_000),
        randomLong(900, 9999),
        randomString(5),
        oneOf("in_progress", "completed", "cancelled", "failed"),
        new LastError(
            oneOf("internal_error", "file_not_found", "parsing_error", "unhandled_mime_type"),
            randomString(10)),
        oneOf(randomStaticChunkingStrategy(), ChunkingStrategy.otherChunkingStrategy()));
  }

  public CreateVectorStoreFileBatchRequest randomCreateVectorStoreFileBatchRequest() {
    return CreateVectorStoreFileBatchRequest.newBuilder()
        .fileIds(listOf(randomInt(1, 20), () -> randomString(5)))
        .chunkingStrategy(
            oneOf(ChunkingStrategy.autoChunkingStrategy(), randomStaticChunkingStrategy()))
        .build();
  }

  public VectorStoreFileBatch randomVectorStoreFileBatch() {
    return new VectorStoreFileBatch(
        randomString(5),
        randomLong(100, 9898),
        randomString(5),
        oneOf("in_progress", "completed", "cancelled", "failed"),
        new VectorStoreFileBatch.FileCounts(
            randomInt(0, 10),
            randomInt(0, 10),
            randomInt(0, 10),
            randomInt(0, 10),
            randomInt(0, 40)));
  }

  private ChunkingStrategy.StaticChunkingStrategy randomStaticChunkingStrategy() {
    int randomMaxChunkSizeTokens = randomInt(100, 4096);
    return ChunkingStrategy.staticChunkingStrategy(
        randomMaxChunkSizeTokens, randomMaxChunkSizeTokens / 2);
  }

  private ExpiresAfter randomExpiresAfter() {
    return oneOf(ExpiresAfter.lastActiveAt(randomInt(1, 365)));
  }

  private FineTuningJobIntegration randomIntegration() {
    return oneOf(FineTuningJobIntegration.wandbIntegration(randomWandb()));
  }

  private Wandb randomWandb() {
    return Wandb.newBuilder()
        .project(randomString(5, 20))
        .name(randomString(5, 20))
        .entity(randomString(5, 20))
        .tags(listOf(randomInt(1, 5), () -> randomString(5, 10)))
        .build();
  }

  private TruncationStrategy randomTruncationStrategy() {
    return oneOf(TruncationStrategy.auto(), TruncationStrategy.lastMessages(randomInt(1, 100)));
  }

  private AssistantsToolChoice randomAssistantsToolChoice() {
    return oneOf(
        AssistantsToolChoice.none(),
        AssistantsToolChoice.auto(),
        AssistantsToolChoice.namedToolChoice(
            ToolChoice.functionToolChoice(new ToolChoice.Function(randomString(5)))),
        AssistantsToolChoice.namedToolChoice(ToolChoice.codeInterpreterToolChoice()),
        AssistantsToolChoice.namedToolChoice(ToolChoice.fileSearchToolChoice()));
  }

  private AssistantsResponseFormat randomAssistantsResponseFormat() {
    return oneOf(
        AssistantsResponseFormat.none(),
        AssistantsResponseFormat.auto(),
        AssistantsResponseFormat.responseFormat(ResponseFormat.text()),
        AssistantsResponseFormat.responseFormat(ResponseFormat.json()));
  }

  private StepDetails randomStepDetails() {
    return oneOf(
        new MessageCreationStepDetails(
            new MessageCreationStepDetails.MessageCreation(randomString(6))),
        new ToolCallsStepDetails(listOf(randomInt(1, 10), this::randomToolCall)));
  }

  private ThreadRunStepDelta.StepDetails randomStepDeltaDetails() {
    return oneOf(
        new ThreadRunStepDelta.StepDetails.MessageCreationStepDetails(
            new ThreadRunStepDelta.StepDetails.MessageCreationStepDetails.MessageCreation(
                randomString(6))),
        new ThreadRunStepDelta.StepDetails.ToolCallsStepDetails(
            listOf(randomInt(1, 10), this::randomDeltaToolCall)));
  }

  private DeltaToolCall randomDeltaToolCall() {
    return oneOf(
        DeltaToolCall.fileSearchToolCall(randomDeltaIndex(), randomString(5)),
        DeltaToolCall.functionToolCall(
            randomDeltaIndex(),
            randomString(5),
            new DeltaToolCall.FunctionToolCall.Function(
                randomDeltaIndex(), randomString(5), randomString(10), randomString(5))),
        randomCodeInterpreterDeltaToolCall());
  }

  private int randomDeltaIndex() {
    return randomInt(0, 100);
  }

  private ToolCall randomToolCall() {
    return oneOf(
        randomFunctionToolCall(true),
        randomCodeInterpreterToolCall(),
        ToolCall.fileSearchToolCall(randomString(5)));
  }

  private Usage randomUsage() {
    return new Usage(randomInt(0, 100), randomInt(0, 100), randomInt(0, 100));
  }

  private Annotation randomThreadMessageAnnotation() {
    return oneOf(
        new FileCitationAnnotation(
            randomString(10, 100),
            new FileCitationAnnotation.FileCitation(randomString(8)),
            randomInt(0, 100),
            randomInt(0, 100)),
        new FilePathAnnotation(
            randomString(10, 100),
            new FilePathAnnotation.FilePath(randomString(8)),
            randomInt(0, 100),
            randomInt(0, 100)));
  }

  private ThreadMessageDelta.Delta.Content.TextContent.Text.Annotation
      randomThreadMessageDeltaAnnotation() {
    return oneOf(
        new ThreadMessageDelta.Delta.Content.TextContent.Text.Annotation.FileCitationAnnotation(
            randomInt(0, 25),
            randomString(10, 100),
            new ThreadMessageDelta.Delta.Content.TextContent.Text.Annotation.FileCitationAnnotation
                .FileCitation(randomString(8), randomString(5, 20)),
            randomInt(0, 100),
            randomInt(0, 100)),
        new ThreadMessageDelta.Delta.Content.TextContent.Text.Annotation.FilePathAnnotation(
            randomInt(0, 25),
            randomString(10, 100),
            new ThreadMessageDelta.Delta.Content.TextContent.Text.Annotation.FilePathAnnotation
                .FilePath(randomString(8)),
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

  private ToolResources randomToolResources(boolean includeVectorStores) {
    List<String> fileIds = listOf(randomInt(1, 20), () -> randomString(7));
    String[] vectorStoreIds = arrayOf(1, () -> randomString(7), String[]::new);
    VectorStore[] vectorStores =
        arrayOf(
            1,
            () ->
                VectorStore.newBuilder()
                    .fileIds(listOf(randomInt(1, 10_000), () -> randomString(7)))
                    .chunkingStrategy(
                        oneOf(
                            ChunkingStrategy.autoChunkingStrategy(),
                            randomStaticChunkingStrategy()))
                    .metadata(randomMetadata())
                    .build(),
            VectorStore[]::new);
    if (includeVectorStores) {
      return oneOf(
          ToolResources.codeInterpreterToolResources(fileIds),
          ToolResources.fileSearchToolResources(vectorStoreIds),
          ToolResources.fileSearchToolResources(vectorStores),
          ToolResources.codeInterpreterAndFileSearchToolResources(fileIds, vectorStoreIds),
          ToolResources.codeInterpreterAndFileSearchToolResources(fileIds, vectorStores));
    }
    return oneOf(
        ToolResources.codeInterpreterToolResources(fileIds),
        ToolResources.fileSearchToolResources(vectorStoreIds),
        ToolResources.codeInterpreterAndFileSearchToolResources(fileIds, vectorStoreIds));
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
                        ContentPart.imageUrlContentPart(randomString(7)),
                        ContentPart.imageUrlContentPart(
                            randomString(7), oneOf("auto", "low", "high"))),
                ContentPart[]::new)),
        ChatMessage.assistantMessage(randomString(10)),
        ChatMessage.assistantMessage(
            randomString(10), listOf(randomInt(1, 5), () -> randomFunctionToolCall(false))),
        ChatMessage.toolMessage(randomString(10), randomString(9)));
  }

  private Map<Integer, Integer> randomLogitBias(int length) {
    return randomKeyValueMap(length, this::randomInt, () -> randomInt(-100, 100));
  }

  private Map<String, String> randomMetadata() {
    return randomKeyValueMap(
        randomInt(1, 16), () -> randomString(3, 64), () -> randomString(10, 512));
  }

  private <K, V> Map<K, V> randomKeyValueMap(
      int length, Supplier<K> keyGenerator, Supplier<V> valueGenerator) {
    Map<K, V> keyValueMap = new HashMap<>();
    for (int i = 0; i < length; i++) {
      keyValueMap.put(keyGenerator.get(), valueGenerator.get());
    }
    return keyValueMap;
  }

  private ChatCompletion.Choice randomChatCompletionChoice() {
    return new ChatCompletion.Choice(
        randomInt(0, 10),
        new ChatCompletion.Choice.Message(
            randomString(10),
            listOf(randomInt(0, 3), () -> randomFunctionToolCall(false)),
            Role.ASSISTANT.getId()),
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
        "gpt-4-turbo",
        "gpt-4-turbo-2024-04-09",
        "gpt-4-0125-preview",
        "gpt-4-turbo-preview",
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
        "gpt-3.5-turbo-0613",
        "gpt-3.5-turbo-1106",
        "gpt-3.5-turbo-0125",
        "gpt-3.5-turbo-16k-0613");
  }

  private OpenAIModel randomTtsModel() {
    return oneOf(OpenAIModel.TTS_1, OpenAIModel.TTS_1_HD);
  }

  private String randomFinishReason() {
    return oneOf("stop", "length", "tool_calls", "content_filter", "function_call");
  }

  private Tool randomTool() {
    return oneOf(
        randomFunctionTool(), Tool.fileSearchTool(randomInt(1, 50)), Tool.codeInterpreterTool());
  }

  private DeltaToolCall randomCodeInterpreterDeltaToolCall() {
    return DeltaToolCall.codeInterpreterToolCall(
        randomDeltaIndex(),
        randomString(5),
        new DeltaToolCall.CodeInterpreterToolCall.CodeInterpreter(
            randomString(5, 99),
            listOf(
                randomInt(1, 8),
                () ->
                    oneOf(
                        DeltaToolCall.CodeInterpreterToolCall.CodeInterpreter.Output.logOutput(
                            randomDeltaIndex(), randomString(5, 42)),
                        DeltaToolCall.CodeInterpreterToolCall.CodeInterpreter.Output.imageOutput(
                            randomDeltaIndex(),
                            new DeltaToolCall.CodeInterpreterToolCall.CodeInterpreter.Output
                                .ImageOutput.Image(randomString(4)))))));
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
        Function.newBuilder()
            .name(randomString(10))
            .description(randomString(15))
            .parameters(
                Map.of(
                    "type",
                    "object",
                    "properties",
                    "{\"person_name\":{\"type\":\"string\", \"description\":\"the persons name, in lower case\"}}",
                    "required",
                    "[\"person_name\"]"))
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
