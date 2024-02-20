/*
 * Copyright (C) 2024 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock;

import com.github.tomakehurst.wiremock.http.Request;
import java.util.Collection;

public interface MultipartParser {
  Collection<Request.Part> parse(byte[] body, String contentType);

  static Collection<Request.Part> parts(byte[] body, String contentType) {
    try {
      @SuppressWarnings("unchecked")
      final Class<? extends MultipartParser> parserClass =
          (Class<? extends MultipartParser>)
              Class.forName("com.github.tomakehurst.wiremock.jetty12.MultipartParser");
      return ((MultipartParser) parserClass.newInstance()).parse(body, contentType);
    } catch (Exception e) {
      return new com.github.tomakehurst.wiremock.jetty11.MultipartParser().parse(body, contentType);
    }
  }
}
