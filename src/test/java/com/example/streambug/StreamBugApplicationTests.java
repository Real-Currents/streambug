package com.example.streambug;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StreamBugApplicationTests {
   private static final Logger LOG = LoggerFactory.getLogger(StreamBugApplicationTests.class);

   private WebClient webClient;

   @LocalServerPort
   private int port;

   @Before
   public void setup() {
      this.webClient = WebClient.builder().baseUrl("http://localhost:" + this.port).build();

   }

   @Test
   @Ignore
   // TODO not implemented in rc3 now ...
   public void testDownload() {
      long size = 235520;
      DataBuffer data = webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/download/stream/{n}").build(size))
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .retrieve()
            .bodyToMono(DataBuffer.class)
            .block(Duration.ofMinutes(1));

      Assert.notNull(data, "DataBuffer is null!");

      LOG.info(String.format("Length of Byte Array: %s", data.asByteBuffer().array().length));

   }

}
