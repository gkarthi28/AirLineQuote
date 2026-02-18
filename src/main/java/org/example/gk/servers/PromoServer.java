package org.example.gk.servers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class PromoServer {
	public static WireMockServer promoServer;

	public static void startServer(){
		promoServer = new WireMockServer(8090);
		promoServer.start();

	}

	public static void stopServer() {
		if(promoServer.isRunning ()) {
			promoServer.stop ();
		}
	}
}
