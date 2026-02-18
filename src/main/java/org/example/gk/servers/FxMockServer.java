package org.example.gk.servers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public  class FxMockServer {
	public static WireMockServer fxServer;
	public static  void startServer() {

		 fxServer = new WireMockServer (
				 WireMockConfiguration.options().port(8089)
		);

		fxServer.start();

	}

	public static void stopServer() {
		if(fxServer.isRunning ()) {
			fxServer.stop ();
		}
	}
}
