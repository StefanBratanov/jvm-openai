package io.github.stefanbratanov.chatjpt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

class MultipartBodyPublisher implements HttpRequest.BodyPublisher {

  private final SubmissionPublisher<ByteBuffer> publisher = new SubmissionPublisher<>();
  private final List<byte[]> multipartBodyParts;

  private MultipartBodyPublisher(List<byte[]> multipartBodyParts) {
    this.multipartBodyParts = multipartBodyParts;
  }

  @Override
  public long contentLength() {
    return multipartBodyParts.stream().mapToLong(bodyPart -> bodyPart.length).sum();
  }

  @Override
  public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
    subscriber.onSubscribe(
        new Flow.Subscription() {
          private int index = 0;

          @Override
          public void request(long n) {
            long remaining = n;
            while (remaining-- > 0 && index < multipartBodyParts.size()) {
              subscriber.onNext(ByteBuffer.wrap(multipartBodyParts.get(index++)));
            }
            if (index == multipartBodyParts.size()) {
              subscriber.onComplete();
            }
          }

          @Override
          public void cancel() {}
        });
  }

  static Builder newBuilder(long boundary) {
    return new Builder(boundary);
  }

  static class Builder {

    private final long boundary;
    private final String separator;

    private final List<byte[]> multipartBodyParts = new ArrayList<>();

    Builder(long boundary) {
      this.boundary = boundary;
      separator =
          "--" + boundary + System.lineSeparator() + "Content-Disposition: form-data; name=";
    }

    Builder textPart(String key, Object value) {
      multipartBodyParts.add(
          (separator
                  + "\""
                  + key
                  + "\""
                  + System.lineSeparator()
                  + System.lineSeparator()
                  + value
                  + System.lineSeparator())
              .getBytes());
      return this;
    }

    Builder filePart(String key, Path value) {
      try {
        String mimeType = Files.probeContentType(value);
        multipartBodyParts.add(
            (separator
                    + "\""
                    + key
                    + "\"; filename=\""
                    + value.getFileName()
                    + "\""
                    + System.lineSeparator()
                    + "Content-Type: "
                    + mimeType
                    + System.lineSeparator()
                    + System.lineSeparator())
                .getBytes());
        multipartBodyParts.add(Files.readAllBytes(value));
        multipartBodyParts.add(System.lineSeparator().getBytes());
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
