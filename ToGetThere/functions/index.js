const admin = require("firebase-admin");
const functions = require("firebase-functions/v2");

functions.setGlobalOptions({region: "europe-west1"});
admin.initializeApp();

exports.sendNotification = functions.firestore.onDocumentCreated(
    "notifications/{notificationId}", async (event) => {
      const snapshot = event.data;
      if (!snapshot) {
        console.log("❌ Nessun dato nella notifica");
        return;
      }

      const data = snapshot.data();
      const {type, receiverId, senderId, tripId = 0,
        message = ""} = data;

      try {
        const db = admin.firestore();

        const receiverDoc = await db.collection("users").doc(receiverId).get();
        const senderDoc = await db.collection("users").doc(senderId).get();
        const tripSnapshot = await db.collection("trips")
            .where("tripId", "==", tripId).get();
        const tripDoc = tripSnapshot.docs[0];

        const receiverData = receiverDoc.data();
        const senderData = senderDoc.data();
        const tripData = tripDoc ? tripDoc.data() : null;

        const fcmToken = receiverData && receiverData.fcmToken;
        const senderName = `${(senderData && senderData.name) || ""}
     ${(senderData && senderData.surname) || ""}`.trim();

        const tripName = `${(tripData && tripData.name) || ""}`;

        if (!fcmToken) {
          console.log("⚠️ Nessun FCM token per l'utente", receiverId);
          return;
        }

        let notificationTitle = "New notification";
        let notificationBody = message || "You have a new notification";

        switch (type) {
          case "NewApplication":
            notificationTitle = "New join trip request";
            notificationBody =
            `${senderName} wants to join your trip ${tripName}`;
            break;

          case "ApplicationStatusUpdate":
            notificationTitle = "Update on your join trip request";
            notificationBody =
            `Check your appliance status on trip ${tripName}!`;
            break;

          case "UserReviewReceived":
            notificationTitle = "New review for you";
            notificationBody = `${senderName} wrote a review for you.`;
            break;

          case "TripReviewReceived":
            notificationTitle = "New trip review";
            notificationBody = `${senderName} wrote a review for your trip.`;
            break;

          default:
            console.log("ℹ️ Tipo di notifica non gestito:", type);
            return;
        }

        const fcmMessage = {
          notification: {
            title: notificationTitle,
            body: notificationBody,
          },
          token: fcmToken,
        };

        await admin.messaging().send(fcmMessage);
        console.log("✅ Notifica inviata a", receiverId);
      } catch (error) {
        console.error("❌ Errore durante l'invio della notifica:", error);
      }
    });

exports.sendGroupMessageNotification = functions.firestore
    .onDocumentCreated("chats/{chatId}/messages/{messageId}", async (event) => {
      const messageData = event.data.data();
      const chatId = event.params.chatId;

      if (!messageData) {
        console.log("❌ Nessun dato nel messaggio");
        return;
      }

      const {senderId, senderName, text} = messageData;

      try {
        const db = admin.firestore();

        // 1. Recupera il documento della chat
        const chatDoc = await db.collection("chats").doc(chatId).get();
        const chatData = chatDoc.data();

        if (!chatData.participants || !Array.isArray(chatData.participants)) {
          console.log("❌ Nessun partecipante nella chat", chatId);
          return;
        }

        // 3. Invia la notifica a tutti tranne il mittente
        const tokensToNotify = [];

        for (const userId of chatData.participants) {
          if (userId === senderId) continue;

          const userDoc = await db.collection("users").doc(userId).get();
          const userData = userDoc.data();
          const fcmToken = userData.fcmToken;

          if (fcmToken) {
            tokensToNotify.push({
              token: fcmToken,
              userId,
            });
          }
        }

        // 4. Invia una notifica a ciascun utente
        const messaging = admin.messaging();
        await Promise.all(
            tokensToNotify.map(({token, userId}) => {
              const fcmMessage = {
                notification: {
                  title: `New message from ${senderName}`,
                  body: text.length > 80 ? text.substring(0, 77) + "..." : text,
                },
                token,
              };
              return messaging.send(fcmMessage)
                  .then(() => console.log(`✅ Notifica inviata a ${userId}`))
                  .catch((err) =>
                    console.error(`❌ Errore con ${userId}:`, err));
            }),
        );
      } catch (error) {
        console.error("❌ Errore nella funzione di notifica messaggi:", error);
      }
    });
