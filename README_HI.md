# Sagar WiFi Android App — v1.1

**Design:** Black + Blue  
**Package:** `com.sagarwifi.app`  
**Website:** https://sagarwifi.blogspot.com/?m=1

## Is source project mein kya hai
- Blogger website ke pages/posts WebView mein
- Black + Blue app header, Home, Refresh, WhatsApp shortcut
- Website ke andar wale Blogspot pages app mein; external links browser/appropriate app mein
- Back navigation aur internet error screen
- Android 13+ notification permission/channel
- Firebase Cloud Messaging receive service aur notification tap par Blogger URL open karne ka flow
- WhatsApp number website/template ke pehle se diye gaye number `919727341942` par set hai; business number badla ho to `MainActivity.kt` mein update karein.
- Existing Blogger site ke WiFi plans/recharge pages WebView mein use honge. Pehle se listed plans: ₹19/5 GB/2 days, ₹25/7 GB/2 days, ₹29/10 GB/3 days, ₹49/40 GB/7 days, ₹99/100 GB/15 days. UPI ID noted in the site context: `sagargamit@ptyes`. App payment ko verify ya WiFi service ko automatically activate nahi karta; payment/activation workflow ko provider-side integration chahiye.

## APK build (Android Studio)
1. ZIP extract karein.
2. Android Studio stable version: https://developer.android.com/studio
3. **Open** karke extracted `SagarWiFiAndroid` folder select karein.
4. Gradle sync complete karein; SDK Platform 35 install karne dein.
5. Test APK: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. Android Studio build notification se **Locate** select karke APK file paayein.
7. Share/public release se pehle **Build > Generate Signed Bundle / APK** se signed APK banayein aur keystore surakshit rakhein.

## Firebase: manual push notifications
1. https://console.firebase.google.com/ par Firebase project banayein.
2. Android app add karein. Package name **exactly** `com.sagarwifi.app` hona chahiye.
3. Firebase se `google-services.json` download karke project ke `app/` folder mein rakhein.
4. `app/build.gradle.kts` mein Google Services plugin wali line uncomment karein:
   `id("com.google.gms.google-services")`
5. Gradle sync karein, phir app ko dobara build/install karein.
6. App kholkar notification permission allow karein.
7. Firebase Console > Messaging se test notification bhejein. Foreground mein `SagarFirebaseMessagingService` notification dikhata hai; background mein Firebase SDK notification payload ko system tray mein handle kar sakta hai.
8. Kisi specific Blogger page par kholne ke liye data payload mein `url` key aur `https://sagarwifi.blogspot.com/...` URL bhejein. App sirf isi exact host ka URL accept karta hai.

## Automatic notification jab Blogger par nayi post aaye
Firebase apne-aap Blogger feed monitor nahi karta. Iske liye alag trusted automation/backend chahiye:
1. Blogger Atom/RSS feed ko interval par check karein.
2. Naye post ID ko store karke duplicate alerts rokhein.
3. Firebase Admin SDK se FCM message send karein.
4. Firebase service-account credentials sirf backend/secret storage mein rakhein — Android app mein kabhi embed na karein.

Is automation ke liye GitHub Actions, Cloud Functions, ya apne server par scheduled worker use kiya ja sakta hai. Is starter ZIP mein backend secret/service-account shamil nahi hai.

## Zaroori limitation
- WhatsApp number prefilled hai based on the prior website template context; publish karne se pehle verify kar lein.
- Website ke plans/recharge UI tabhi dikhega jab Blogger site par woh pages/posts available hon.
- Payment success verification, subscriber account management aur automatic WiFi activation is WebView starter ka hissa nahi hain.
- Build ko Android Studio mein sync/build/test karna zaroori hai; is ZIP mein compiled APK nahi hai.
