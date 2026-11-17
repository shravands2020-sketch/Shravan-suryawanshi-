const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_latur_01";
const BOB_UID = "bob_latur_02";
const OWNER_UID = "owner_dsslatur10";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write quote requests or service bookings", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const past = new Date(Date.now() - 2000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("quote_requests").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("quote_requests").doc("q_1").set({
      userId: ALICE_UID,
      category: "CAR",
      vehicleOrSystem: "Maruti Swift Petrol",
      selectedBatteryModel: "Amaron Flo DIN45",
      customerName: "Rajesh Kulkarni",
      phoneNumber: "+91 98506 38290",
      exchangeOldBattery: true,
      doorstepService: true,
      locationArea: "Kaneri Chowk, Latur",
      estimatedPriceInr: 4850,
      status: "PENDING",
      createdAt: past,
      updatedAt: past,
    })
  );
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("service_bookings").get());
});

test("Owner can create, list, update, cancel, and delete quote requests matching Kotlin payloads", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);
  const quoteRef = aliceDb.collection("users").doc(ALICE_UID).collection("quote_requests").doc("quote_001");

  await assertSucceeds(
    quoteRef.set({
      userId: ALICE_UID,
      category: "CAR",
      vehicleOrSystem: "Hyundai Creta SX",
      selectedBatteryModel: "Amaron Black DIN60",
      customerName: "Dhananjay Patil",
      phoneNumber: "+91 98506 38290",
      exchangeOldBattery: true,
      doorstepService: true,
      locationArea: "Patel Nagar, Latur",
      estimatedPriceInr: 6200,
      status: "PENDING",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("quote_requests").get()
  );

  await assertSucceeds(
    quoteRef.update({
      locationArea: "MIDC Ring Road, Latur",
      exchangeOldBattery: false,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertSucceeds(
    quoteRef.update({
      status: "CANCELLED",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  await assertFails(
    quoteRef.update({
      locationArea: "Ausaroad",
      updatedAt: new Date(Date.now() - 200),
    })
  );

  await assertSucceeds(quoteRef.delete());
});

test("Verified shop owner dsslatur10@gmail.com can list all quotes/bookings and update status & price; spoofed unverified email fails", async () => {
  const created = new Date(Date.now() - 12000);
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(ALICE_UID).collection("quote_requests").doc("q_admin_1").set({
      userId: ALICE_UID,
      category: "INVERTER",
      vehicleOrSystem: "3BHK Home",
      selectedBatteryModel: "Amaron Current Tall Tubular 150Ah",
      customerName: "Alice Deshmukh",
      phoneNumber: "+91 98506 38290",
      exchangeOldBattery: true,
      doorstepService: true,
      locationArea: "Patel Nagar, Latur",
      estimatedPriceInr: 11000,
      status: "PENDING",
      createdAt: created,
      updatedAt: created,
    });
  });

  // Spoofed email without email_verified: true MUST fail
  const spoofDb = testEnv.authenticatedContext("spoof_uid", {
    email: "dsslatur10@gmail.com",
    email_verified: false,
  }).firestore();
  await assertFails(spoofDb.collectionGroup("quote_requests").get());

  // Non-admin user MUST fail collectionGroup read
  const bobDb = testEnv.authenticatedContext(BOB_UID, {
    email: "bob@gmail.com",
    email_verified: true,
  }).firestore();
  await assertFails(bobDb.collectionGroup("quote_requests").get());

  // Verified owner dsslatur10@gmail.com succeeds
  const adminDb = testEnv.authenticatedContext(OWNER_UID, {
    email: "dsslatur10@gmail.com",
    email_verified: true,
  }).firestore();

  await assertSucceeds(adminDb.collectionGroup("quote_requests").get());
  await assertSucceeds(
    adminDb.collection("users").doc(ALICE_UID).collection("quote_requests").doc("q_admin_1").update({
      status: "CONFIRMED",
      estimatedPriceInr: 10800,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertSucceeds(
    adminDb.collection("users").doc(ALICE_UID).collection("quote_requests").doc("q_admin_1").update({
      status: "COMPLETED",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});
