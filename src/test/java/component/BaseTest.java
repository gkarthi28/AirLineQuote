package component;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.example.gk.constants.Constants;
import org.example.gk.gateway.FxGateway;
import org.example.gk.gateway.PromoGateWay;
import org.example.gk.handlers.QuoteHandler;
import org.example.gk.services.LoyaltyPointService;
import org.example.gk.stubs.FxStubs;
import org.example.gk.stubs.PromoStubs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ExtendWith (VertxExtension.class)
public class BaseTest {
	private static final Logger log =
			LoggerFactory.getLogger(BaseTest.class);
	protected  int port;
	protected   WireMockServer fxServer;
	protected   WireMockServer promoServer;
	protected FxStubs fxStubs;
	protected PromoStubs promoStubs;
	@BeforeEach
	public   void setUp(Vertx vertx, VertxTestContext tc) {
		fxServer = new WireMockServer (
				WireMockConfiguration.options().dynamicPort ());
		promoServer = new WireMockServer (
				WireMockConfiguration.options().dynamicPort ());
		fxServer.start();
		promoServer.start();
		fxStubs = new FxStubs ();
		promoStubs = new PromoStubs ();
		Router router = Router.router (vertx);
		router.route().handler (BodyHandler.create ());
		router.route ().handler (ctx->{
			log.info ("request {} :",ctx.request ());
			ctx.next ();
		});
		router.post (Constants.POINT_URI)
				.handler (new QuoteHandler (new LoyaltyPointService(new FxGateway(vertx, fxServer.port ( )),
						new PromoGateWay (vertx, promoServer.port ( )))));
		vertx.createHttpServer ( )
				.requestHandler (router)
				.listen (0)
				.onSuccess (res -> {
					log.info ("Server has been Started at" + res.actualPort () );
					port = res.actualPort ();
					tc.completeNow ();
				})
				.onFailure (res -> {
					log.error (res.getMessage () );
					tc.failNow ("Server is not started");
				});

	}


	@AfterEach
	public void tearDown() {
		if( fxServer.isRunning ( ) ) {
			fxServer.stop ();
		}
		if( promoServer.isRunning ( ) ) {
			promoServer.stop ();
		}
	}


}
