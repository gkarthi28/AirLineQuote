	package org.example.gk.handlers;

	import io.vertx.core.Handler;
	import io.vertx.core.json.Json;
	import io.vertx.core.json.JsonObject;
	import io.vertx.ext.web.RoutingContext;
	import org.example.gk.models.ApiException;
	import org.example.gk.models.QuoteRequest;
	import org.example.gk.services.LoyaltyPointService;


	public class QuoteHandler implements Handler<RoutingContext> {

		private final LoyaltyPointService loyaltyPointService;

		public QuoteHandler(LoyaltyPointService _loyaltyPointService) {
			this.loyaltyPointService = _loyaltyPointService;
		}

		@Override
		public void handle(RoutingContext ctx) {
			QuoteRequest quoteRequest = ctx.body ( ).asPojo (QuoteRequest.class);
			loyaltyPointService.calCulatePoints (quoteRequest)
					.onSuccess (response -> {
						ctx.response ( ).putHeader ("Content-Type", "application/json")
								.end (Json.encodePrettily (response));
					})
					.onFailure (err -> {
						if ( err instanceof ApiException apiEx ) {
							ctx.response ( )
									.setStatusCode (apiEx.getStatusCode ( ))
									.end (Json.encode (new JsonObject ()
											.put ("statusCode",apiEx.getStatusCode ())
											.put("errorCode", apiEx.getErrorCode ( ))
											 .put("message", apiEx.getMessage ( )

									)));

						}
					});

		}
	}