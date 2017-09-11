package com.example.streambug.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Alexander Wilhelmer
 */
@RestController
@RequestMapping("/download")
public class DownloadController {
   private static final Logger LOG = LoggerFactory.getLogger(DownloadController.class);

   @GetMapping(value = "/stream/{n}")
   public Flux<DataBuffer> getFileContent(ServerHttpRequest request, ServerHttpResponse response, @PathVariable(value = "n") Long size) {
      Mono<ClientResponse> exchange = WebClient.builder()
            .baseUrl("http://httpbin.org")
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder.path("/stream-bytes/{n}").build(size))
            .exchange();

      return exchange.flatMapMany(clientResponse -> {
         copyDownloadHeaders(response, clientResponse);
         return clientResponse.bodyToFlux(DataBuffer.class);
      });
   }

   @GetMapping(value = "/image/png")
   public Flux<DataBuffer> getPng(ServerHttpRequest request, ServerHttpResponse response) {
      Mono<ClientResponse> exchange = WebClient.builder()
            .baseUrl("http://httpbin.org")
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder.path("/image/png").build())
            .exchange();

      return exchange.flatMapMany(clientResponse -> {
         copyDownloadHeaders(response, clientResponse);
         return clientResponse.bodyToFlux(DataBuffer.class);
      });
   }

   @GetMapping(value = "/image/jpeg")
   public Flux<DataBuffer> getJpeg(ServerHttpRequest request, ServerHttpResponse response) {
      Mono<ClientResponse> exchange = WebClient.builder()
            .baseUrl("http://httpbin.org")
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder.path("/image/jpeg").build())
            .exchange();

      return exchange.flatMapMany(clientResponse -> {
         copyDownloadHeaders(response, clientResponse);
         return clientResponse.bodyToFlux(DataBuffer.class);
      });
   }

   @GetMapping(value = "/xml")
   public Flux<DataBuffer> getXml(ServerHttpRequest request, ServerHttpResponse response) {
      Mono<ClientResponse> exchange = WebClient.builder()
            .baseUrl("http://httpbin.org")
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder.path("/xml").build())
            .exchange();

      return exchange.flatMapMany(clientResponse -> {
         copyDownloadHeaders(response, clientResponse);
         return clientResponse.bodyToFlux(DataBuffer.class);
      });
   }


   @GetMapping(value = "/image/slow")
   public Flux<DataBuffer> getImageHttpWatch(ServerHttpRequest request, ServerHttpResponse response) {
      Mono<ClientResponse> exchange = WebClient.builder()
            .baseUrl("https://www.httpwatch.com")
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder.path("httpgallery/chunked/chunkedimage.aspx").build())
            .exchange();

      return exchange.flatMapMany(clientResponse -> {
         copyDownloadHeaders(response, clientResponse);
         return clientResponse.bodyToFlux(DataBuffer.class);
      });
   }


   public void copyDownloadHeaders(ServerHttpResponse response, ClientResponse clientResponse) {
      HttpHeaders httpHeaders = clientResponse.headers().asHttpHeaders();
      response.getHeaders().setContentType(httpHeaders.getContentType());
      response.getHeaders().setLastModified(httpHeaders.getLastModified());
      if (httpHeaders.getCacheControl() != null) {
         response.getHeaders().setCacheControl(httpHeaders.getCacheControl());
      }
      if (httpHeaders.getETag() != null) {
         response.getHeaders().setETag(httpHeaders.getETag());
      }
      if (httpHeaders.getContentLength() > 0) {
         response.getHeaders().setContentLength(httpHeaders.getContentLength());
      }
      response.getHeaders().put(HttpHeaders.CONTENT_RANGE, httpHeaders.getValuesAsList(HttpHeaders.CONTENT_RANGE));
      response.getHeaders().put(HttpHeaders.ACCEPT_RANGES, httpHeaders.getValuesAsList(HttpHeaders.ACCEPT_RANGES));
   }

   @ExceptionHandler
   public Mono<ResponseEntity<?>> handleException(Exception e) {
      LOG.error("Error!", e);
      return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
   }
}
