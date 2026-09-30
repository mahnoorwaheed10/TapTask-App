const {setGlobalOptions} = require("firebase-functions");
const {onCall} = require("firebase-functions/https");
const logger = require("firebase-functions/logger");
const Stripe = require("stripe");

setGlobalOptions({maxInstances: 10});

// Stripe secret key will be added securely through Firebase environment config.
// DO NOT put your Stripe secret key directly in this file.

exports.createPaymentIntent = onCall(async (request) => {
  try {
    const {amount, currency = "usd"} = request.data;

    if (!amount || amount <= 0) {
      throw new Error("Invalid payment amount.");
    }

    const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);

    const paymentIntent = await stripe.paymentIntents.create({
      amount: Math.round(amount),
      currency: currency,
      automatic_payment_methods: {
        enabled: true,
      },
    });

    logger.info("Stripe PaymentIntent created successfully.");

    return {
      clientSecret: paymentIntent.client_secret,
      paymentIntentId: paymentIntent.id,
    };
  } catch (error) {
    logger.error("Stripe PaymentIntent error:", error);
    throw new Error(error.message || "Unable to create payment.");
  }
});