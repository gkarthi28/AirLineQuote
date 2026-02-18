package component.quote;

import component.BaseTest;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.example.gk.constants.Constants;
import org.junit.jupiter.api.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.example.gk.TestData.TestDataGenerator.validRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ResilienceTests extends BaseTest {

	@Test
	public void shouldReturn500WhenFxserverFails(Vertx vertx, VertxTestContext tc) {
		fxStubs.setFxServer500Status (fxServer);
		client = WebClient.create (vertx);
		client.post (port, Constants.HOST, Constants.POINT_URI)
				.sendJson (validRequest ( ), ar -> {
					if ( ar.succeeded ( ) && ar.result ( ).statusCode ( ) == 500 ) {
						tc.verify (() -> {
							assertEquals (500, ar.result ( ).bodyAsJsonObject ( ).getInteger (Constants.STATUS_CODE));
							assertTrue (ar.result ( ).bodyAsJsonObject ( ).getString (Constants.MESSAGE).contains ("FX Issue"));

						});
						tc.completeNow ( );
					} else {
						tc.failNow (ar.result ( ).bodyAsString ( ));
					}
				});
	}

	@Test
	void ShouldSucceedWhenFxFailsOnceThenRecovers(Vertx vertx, VertxTestContext tc) {
		fxStubs.setFxServerFailOnceThenRecover (fxServer);
		promoStubs.setPromoServer200status (promoServer);
		WebClient client = WebClient.create (vertx);
		client.post (port, Constants.HOST, Constants.POINT_URI)
				.sendJson (validRequest ( ), ar -> {
					if ( ar.succeeded ( ) ) {
						var response = ar.result ( );
						tc.verify (() -> {
							assertEquals (200, ar.result ( ).statusCode ( ));
							assertEquals (ar.result ( ).bodyAsJsonObject ( ).getDouble ("effectiveFxRate"), 1.2);
						});

						tc.completeNow ( );

					} else {
						tc.failNow (ar.cause ( ));
					}
				});
	}

	@Test
	void shouldFailWhenFxTimesOut(Vertx vertx, VertxTestContext tc) {
		fxStubs.setFxServerTimeOut (fxServer);
		WebClient client = WebClient.create (vertx);
		client.post (port, Constants.HOST, Constants.POINT_URI)
				.sendJson (validRequest ( ), ar -> {
					if ( ar.succeeded ( ) ) {
						tc.verify (() -> {
							assertEquals (500, ar.result ( ).statusCode ( ));
						});
						tc.completeNow ( );
					} else {
						tc.failNow (ar.cause ( ));
					}
				});
	}

	@Test
	void shouldRetryExactlyThreeTimes(Vertx vertx, VertxTestContext tc) {
		fxStubs.setFxServer500Status (fxServer);
		WebClient client = WebClient.create (vertx);
		client.post (port, Constants.HOST, Constants.POINT_URI)
				.sendJson (validRequest ( ), ar -> {
					if ( ar.succeeded ( ) ) {
						tc.verify (() -> {
							fxServer.verify (3,
									getRequestedFor (urlPathEqualTo (Constants.FX_URI)));
						});
						tc.completeNow ( );
					} else {
						tc.failNow (ar.cause ( ));
					}
				});
	}

	@Test
	void shouldReturn404WhenFxServerNotFound(Vertx vertx, VertxTestContext tc) {
		fxStubs.setFxServerResourceNotFound (fxServer);
		WebClient client = WebClient.create (vertx);
		client.post (port, Constants.HOST, Constants.POINT_URI)
				.sendJson (validRequest ( ), ar -> {
					if ( ar.succeeded ( ) ) {
						tc.verify (() -> {
							assertEquals (404, ar.result ( ).statusCode ( ));
						});
						tc.completeNow ( );
					} else {
						tc.failNow (ar.cause ( ));
					}
				});

	}


	@AfterEach
	public void tearDown() {
		if ( fxServer != null ) {
			fxServer.stop ( );
		}

		if ( promoServer != null ) {
			promoServer.stop ( );
		}
	}

}
