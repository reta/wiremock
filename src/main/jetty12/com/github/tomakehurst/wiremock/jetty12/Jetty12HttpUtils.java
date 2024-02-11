/*
 * Copyright (C) 2015-2024 Thomas Akehurst
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
package com.github.tomakehurst.wiremock.jetty12;

import com.github.tomakehurst.wiremock.jetty.JettyHttpUtils;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.ee10.servlet.ServletApiResponse;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.SelectableChannelEndPoint;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.server.AbstractMetaDataConnection;
import org.eclipse.jetty.server.Response;

class Jetty12HttpUtils implements JettyHttpUtils {
  @Override
  public Response unwrapResponse(HttpServletResponse httpServletResponse) {
    if (httpServletResponse instanceof HttpServletResponseWrapper) {
      ServletResponse unwrapped = ((HttpServletResponseWrapper) httpServletResponse).getResponse();
      return (Response) unwrapped;
    } else if (httpServletResponse instanceof ServletApiResponse) {
      Response unwrapped = ((ServletApiResponse) httpServletResponse).getResponse();
      return unwrapped;
    }

    return (Response) httpServletResponse;
  }

  @Override
  public Socket socket(Response response) {
    final AbstractMetaDataConnection connectionMetaData =
        (AbstractMetaDataConnection) response.getRequest().getConnectionMetaData();
    SelectableChannelEndPoint ep = (SelectableChannelEndPoint) connectionMetaData.getEndPoint();
    return ((SocketChannel) ep.getChannel()).socket();
  }

  @Override
  public Socket tlsSocket(Response response) {
    final AbstractMetaDataConnection connectionMetaData =
        (AbstractMetaDataConnection) response.getRequest().getConnectionMetaData();
    final SslConnection.SslEndPoint sslEndpoint =
        (SslConnection.SslEndPoint) connectionMetaData.getEndPoint();
    final SelectableChannelEndPoint endpoint =
        (SelectableChannelEndPoint) sslEndpoint.getSslConnection().getEndPoint();
    return ((SocketChannel) endpoint.getChannel()).socket();
  }

  @Override
  public void setStatusWithReason(
      com.github.tomakehurst.wiremock.http.Response response,
      HttpServletResponse httpServletResponse) {
    // Servlet 6 is not accepting the reason / message anymore
    httpServletResponse.setStatus(response.getStatus());
  }

  @Override
  public EndPoint unwrapEndPoint(Response jettyResponse) {
    final AbstractMetaDataConnection connectionMetaData =
        (AbstractMetaDataConnection) jettyResponse.getRequest().getConnectionMetaData();
    return connectionMetaData.getEndPoint();
  }
}
