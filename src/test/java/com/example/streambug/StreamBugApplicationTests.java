package com.example.streambug;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StreamBugApplicationTests {
   private static final Logger LOG = LoggerFactory.getLogger(StreamBugApplicationTests.class);

   @Autowired
   private WebTestClient webTestClient;



   @Test
   public void testDownloadLocal() {
      Long size = 102400L;
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
