package org.example.gk;

import io.vertx.core.Vertx;
import org.example.gk.servers.FxMockServer;
import org.example.gk.servers.PromoServer;
import org.example.gk.stubs.FxStubs;
import org.example.gk.stubs.PromoStubs;
import org.example.gk.verticles.HttpServerVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
	private static final Logger log =
			LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		FxMockServer.startServer ();
		PromoServer.startServer ();
		FxStubs fxStubs = new FxStubs ();
		PromoStubs promoStubs = new PromoStubs ();
		fxStubs.runAllCurrencies (FxMockServer.fxServer);
		promoStubs.setPromoServer200status (PromoServer.promoServer);

		vertx.deployVerticle(new HttpServerVerticle (),
			ar -> {
				if (ar.succeeded()) {
					log.info("HTTP Verticle deployed successfully");
				} else {
					log.error (ar.cause ().getMessage ());
				}
			});

	}
}