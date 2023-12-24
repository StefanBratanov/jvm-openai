package com.stefanbratanov.chatjpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Based on <a href="https://platform.openai.com/docs/api-reference/chat">Chat</a> */
public class ChatClient extends OpenAIClient<ChatRequest, ChatResponse> {

  private final String[] headers;
  private final ObjectMapper objectMapper;

  ChatClient(URI baseUrl, String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
    super(baseUrl.resolve(Endpoint.CHAT.getPath()), apiKey, httpClient);
    headers =
        new String[] {
          "Content-Type", Constants.JSON_MEDIA_TYPE, "Accept", Constants.JSON_MEDIA_TYPE
        };
    this.objectMapper = objectMapper;
  }

  @Override
  String[] getHeaders() {
    return headers;
  }

  @Override
  HttpRequest.BodyPublisher createBodyPublisher(ChatRequest request) {
    try {
      return HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(request));
    } catch (JsonProcessingException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  ChatResponse deserializeResponse(ChatRequest request, HttpResponse<byte[]> httpResponse) {
    try {
      return objectMapper.readValue(httpResponse.body(), ChatResponse.class);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
