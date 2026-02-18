package org.example.gk.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.gk.constants.Constants;
import org.example.gk.gateway.FxGateway;
import org.example.gk.gateway.PromoGateWay;
import org.example.gk.handlers.QuoteHandler;
import org.example.gk.services.LoyalityPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {
	private static final Logger log =
			LoggerFactory.getLogger(HttpServerVerticle.class);

	@Override
	public void start(Promise<Void> promise) {
		FxGateway fxGateway = new FxGateway(vertx,8089);
		PromoGateWay promoGateway = new PromoGateWay(vertx,8090);
		LoyalityPointService service =
				new LoyalityPointService(fxGateway, promoGateway);
		QuoteHandler quoteHandler = new QuoteHandler(service);
		Router router = Router.router (vertx);
        router.route ().handler (BodyHandler.create ());
		router.post(Constants.POINT_URI).handler(quoteHandler);
		vertx.createHttpServer()
				.requestHandler(router)
				.listen(8085)
				.onSuccess (server -> {
					log.info ("Server has been started at 8085" );
					promise.complete ();
				}).onFailure (s->{
					log.info ("Server has been started" );
					promise.fail (s.getMessage ());
				})
		  ;
	}
}
