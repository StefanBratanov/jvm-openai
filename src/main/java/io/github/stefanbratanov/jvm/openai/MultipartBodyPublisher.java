package io.github.stefanbratanov.jvm.openai;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

class MultipartBodyPublisher implements HttpRequest.BodyPublisher {

  private final List<byte[]> multipartBodyParts;

  private MultipartBodyPublisher(List<byte[]> multipartBodyParts) {
    this.multipartBodyParts = multipartBodyParts;
  }

  @Override
  public long contentLength() {
    return multipartBodyParts.stream().mapToInt(bodyPart -> bodyPart.length).sum();
  }

  @Override
  public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
    subscriber.onSubscribe(
        new Flow.Subscription() {
          private int index = 0;

          @Override
          public void request(long n) {
            long elementsToEmit = Math.min(n, (long) multipartBodyParts.size() - index);
            for (int i = 0; i < elementsToEmit; i++) {
              subscriber.onNext(ByteBuffer.wrap(multipartBodyParts.get(index++)));
            }
            if (index == multipartBodyParts.size()) {
              subscriber.onComplete();
            }
          }

          @Override
          public void cancel() {
            // No action needed on cancel in this implementation as resources are automatically
            // managed and released when no longer needed.
          }
        });
  }

  static Builder newBuilder(long boundary) {
    return new Builder(boundary);
  }

  static class Builder {

    private static final String CRLF = "\r\n";

    private final long boundary;
    private final String separator;

    private final List<byte[]> multipartBodyParts = new ArrayList<>();

    Builder(long boundary) {
      this.boundary = boundary;
      separator = "--" + boundary + CRLF + "Content-Disposition: form-data; name=";
    }

    Builder textPart(String key, Object value) {
      multipartBodyParts.add(
          (separator + "\"" + key + "\"" + CRLF + CRLF + value + CRLF).getBytes());
      return this;
    }

    Builder filePart(String key, Path value) {
      try {
        String mimeType = Files.probeContentType(value);
        byte[] fileBytes = Files.readAllBytes(value);
        multipartBodyParts.add(
            (separator
                    + "\""
                    + key
                    + "\"; filename=\""
                    + value.getFileName()
                    + "\""
                    + CRLF
                    + "Content-Type: "
                    + mimeType
                    + CRLF
                    + "Content-Length: "
                    + fileBytes.length
                    + CRLF
                    + CRLF)
                .getBytes());
        multipartBodyParts.add(fileBytes);
        multipartBodyParts.add(CRLF.getBytes());
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
      return this;
    }

    MultipartBodyPublisher build() {
      multipartBodyParts.add(("--" + boundary + "--").getBytes());
      return new MultipartBodyPublisher(multipartBodyParts);
    }
  }
}
