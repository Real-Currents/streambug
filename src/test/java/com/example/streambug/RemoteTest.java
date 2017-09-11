package com.example.streambug;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Alexander Wilhelmer
 * The remote server is only till 17.09.2017 online!
 */
@RunWith(SpringRunner.class)
public class RemoteTest {
   private static final Logger LOG = LoggerFactory.getLogger(StreamBugApplicationTests.class);

   private WebTestClient webTestClient;

   @Before
   public void setup() {
      this.webTestClient = WebTestClient.bindToServer().baseUrl("http://217.79.182.40:8091").build();

   }

   @Test
   public void testDownloadRemote() {
      Long size = 102400L;
      int iterations = 100;
      for (int i = 0; i < iterations; i++) {
         LOG.info(String.format("Calling iteration %s of %s ...", i + 1, iterations));
         webTestClient.get()
               .uri(uriBuilder -> uriBuilder.path("/download/stream/{n}").build(size))
               .accept(MediaType.APPLICATION_OCTET_STREAM)
               .exchange()
               .expectStatus()
               .isOk()
               .expectBodyList(DataBuffer.class)
               .consumeWith(allBuffers -> Assert.assertEquals(size.intValue(), allBuffers.getResponseBodyContent().length));

      }

   }

}
