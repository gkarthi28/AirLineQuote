package component.quote;

import component.BaseTest;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.example.gk.constants.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.example.gk.TestData.TestDataGenerator.validRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResilencePromoServerTest extends BaseTest {
	@Test
	public void shouldReturn500WhenPromoserverFails(Vertx vertx, VertxTestContext tc) {
		fxStubs.setFxServer200status (fxServer);
		promoStubs.setPromoServer500Status (promoServer);
		client = WebClient.create (vertx);
		client.post (port, Constants.HOST, Constants.POINT_URI)
				.sendJson (validRequest ( ), ar -> {
					if ( ar.succeeded ( ) && ar.result ( ).statusCode ( ) == 500 ) {
						tc.verify (() -> {
							assertEquals (500, ar.result ( ).bodyAsJsonObject ( ).getInteger (Constants.STATUS_CODE));
							assertTrue (ar.result ( ).bodyAsJsonObject ( ).getString (Constants.MESSAGE).contains ("Promo Issue"));

						});
						tc.completeNow ( );
					} else {
						tc.failNow (ar.result ( ).bodyAsString ( ));
					}
				});
	}

	@Test
	void shouldFailWhenPromoserverTimesOut(Vertx vertx, VertxTestContext tc) {
		fxStubs.setFxServer200status (fxServer);
		promoStubs.setPromoServerTimeOut (promoServer);
		WebClient client = WebClient.create (vertx);
		client.post (port, Constants.HOST, Constants.POINT_URI)
				.sendJson (validRequest ( ), ar -> {
					if ( ar.succeeded ( ) ) {
						tc.verify (() -> {
							assertEquals (200, ar.result ( ).statusCode ( ));
						});
						tc.completeNow ( );
					} else {
						tc.failNow (ar.cause ( ));
					}
				});
	}


	@Test
	void shouldReturn200WhenPromoServerNotFound(Vertx vertx, VertxTestContext tc) {
		fxStubs.setFxServer200status (fxServer);
		promoStubs.setPromoServerResourceNotFound (promoServer);
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
