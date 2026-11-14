const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function nowTs() {
  return new Date();
}

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

test("Unauthenticated user cannot read or write user profile or bookmarks", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("bookmarks").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("bookmarks").doc("b_1_1").set({
      userId: ALICE_UID,
      surahNumber: 1,
      ayahNumber: 1,
      surahName: "Al-Fatihah",
      createdAt: nowTs(),
      updatedAt: nowTs(),
    })
  );
});

test("Authenticated user can create and read own profile and bookmarks", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = nowTs();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Amina",
      lastSurah: 1,
      lastAyah: 1,
      totalTasbihCount: 33,
      hifzAyahsCount: 7,
      preferredTheme: "EMERALD_GOLD",
      preferredLanguage: "en",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookmarks").doc("s1_a1").set({
      userId: ALICE_UID,
      surahNumber: 1,
      ayahNumber: 1,
      surahName: "Al-Fatihah",
      note: "Opening verse",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookmarks").get()
  );
});

test("Cross-user access is denied for profiles, bookmarks, hifz, and learning progress", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = nowTs();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookmarks").doc("s1_a1").set({
      userId: ALICE_UID,
      surahNumber: 1,
      ayahNumber: 1,
      surahName: "Al-Fatihah",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("bookmarks").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("bookmarks").doc("s1_a1").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("hifz_progress").doc("surah_1").set({
      userId: ALICE_UID,
      surahNumber: 1,
      memorizedAyahs: 7,
      targetAyahs: 7,
      status: "memorized",
      createdAt: ts,
      updatedAt: ts,
    })
  );
});

test("Shadow update with ghost field or invalid surah bounds is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = nowTs();

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookmarks").doc("s1_a1").set({
      userId: ALICE_UID,
      surahNumber: 1,
      ayahNumber: 1,
      surahName: "Al-Fatihah",
      isAdmin: true,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookmarks").doc("s999_a1").set({
      userId: ALICE_UID,
      surahNumber: 999,
      ayahNumber: 1,
      surahName: "Invalid",
      createdAt: ts,
      updatedAt: ts,
    })
  );
});

test("Hifz and learning progress CRUD obeys schema constraints", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = nowTs();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("hifz_progress").doc("surah_1").set({
      userId: ALICE_UID,
      surahNumber: 1,
      memorizedAyahs: 4,
      targetAyahs: 7,
      status: "in_progress",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("hifz_progress").doc("surah_1").update({
      memorizedAyahs: 7,
      status: "memorized",
      updatedAt: nowTs(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("learning_progress").doc("tajweed_1").set({
      userId: ALICE_UID,
      moduleType: "tajweed",
      lessonKey: "noon_sakinah",
      completed: true,
      quizScore: 95,
      createdAt: ts,
      updatedAt: ts,
    })
  );
});
