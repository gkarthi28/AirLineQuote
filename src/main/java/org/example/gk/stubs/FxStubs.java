package org.example.gk.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.example.gk.constants.Constants;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

public class FxStubs {

	public  void setFxServer500Status(WireMockServer fxServer) {
		fxServer.stubFor(get(urlPathEqualTo ("/fx/latest"))
				.withQueryParam ("from",equalTo ("USD"))
				.withQueryParam ("to",equalTo ("EUR"))
				.willReturn(aResponse()
						               .withStatus (500)
						               .withStatusMessage ("Fx Issue")
						               .withBody("FX Issue:: Error in Finding the Fx rate")));
	}

	public void  setFxServerFailOnceThenRecover(WireMockServer fxServer) {
		fxServer.stubFor(get(urlPathEqualTo(Constants.FX_URI) )
						.withQueryParam ("from",equalTo ("USD"))
						.withQueryParam ("to",equalTo ("EUR"))
				.inScenario("retry")
				.whenScenarioStateIs(STARTED)
				.willReturn(aResponse().withStatus(500)
						.withBody ("FX Issue:: Error in Finding the Fx rate")
				 )
				.willSetStateTo("second"));

		fxServer.stubFor(get(urlPathEqualTo(Constants.FX_URI))
				.withQueryParam ("from",equalTo ("USD"))
				.withQueryParam ("to",equalTo ("EUR"))
				.inScenario("retry")
				.whenScenarioStateIs("second")
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("{\"rate\":1.2}")));

	}

	public  void setFxServerTimeOut(WireMockServer fxServer) {
		fxServer.stubFor(get(urlPathEqualTo(Constants.FX_URI))
				.withQueryParam ("from",equalTo ("USD"))
				.withQueryParam ("to",equalTo ("EUR"))
				.willReturn(aResponse()
				.withFixedDelay(5000)));
	}

	public  void runAllCurrencies(WireMockServer fxServer) {

			fxServer.stubFor (get (urlPathEqualTo ("/fx/latest"))
					.withQueryParam ("from", equalTo ("USD"))
					.withQueryParam ("to", equalTo ("EUR"))
					.willReturn (aResponse ( )
							.withHeader ("Content-Type", "application/json")
							.withBody ("""
									{"rate":3.67}""")));

			fxServer.stubFor (get (urlPathEqualTo ("/fx/latest"))
					.withQueryParam ("from", equalTo ("JPY"))
					.withQueryParam ("to", equalTo ("EUR"))
					.willReturn (aResponse ( )
							.withHeader ("Content-Type", "application/json")
							.withBody ("""
									{"rate":1.25}""")));


	}

	public void setFxServer200status(WireMockServer fxServer) {
		fxServer.stubFor (get (urlPathEqualTo ("/fx/latest"))
				.withQueryParam ("from", equalTo ("USD"))
				.withQueryParam ("to", equalTo ("EUR"))
				.willReturn (aResponse ( )
						.withHeader ("Content-Type", "application/json")
						.withBody ("""
									{"rate":3.67}""")));

	}

	public  void setFxServerResourceNotFound(WireMockServer fxServer) {
		fxServer.stubFor(get(urlPathEqualTo(Constants.FX_URI))
				.withQueryParam ("from",equalTo ("USD"))
				.withQueryParam ("to",equalTo ("EUR"))
				.willReturn(aResponse ()
						.withStatus(404)));
	}

}
