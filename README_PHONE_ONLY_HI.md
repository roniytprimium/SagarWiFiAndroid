# Sirf Android phone se APK banane ka tareeka

Is project mein GitHub Actions cloud-build workflow shamil hai. Phone par Android Studio install karne ki zaroorat nahi.

1. Chrome mein https://github.com kholen aur free account sign in/create karein.
2. `+` > **New repository**; naam `SagarWiFiAndroid` rakhein aur repository banayein.
3. Project ke extracted files repository mein upload karein. ZIP ko seedha upload karna kaafi nahi: ZIP ko Files app se extract karna hoga.
4. Mobile UI se `.github/workflows/build-apk.yml` upload mushkil ho, to repository mein **Add file > Create new file** select karein. Filename `.github/workflows/build-apk.yml` rakhein aur is project ke isi naam ki file ka content paste karein.
5. Files `main` branch mein commit karein.
6. Repository ke **Actions** tab mein `Build Sagar WiFi APK` workflow kholen. Agar automatic run na ho to **Run workflow** dabayein.
7. Successful run kholkar **Artifacts** se `SagarWiFi-debug-APK` download karein.
8. Artifact ZIP extract karein; andar `app-debug.apk` hogi. APK tap karke install karein. Android ki permission prompt aaye to install allow karein.

## Important
- Debug APK testing ke liye hai; Play Store ke liye signed release build alag chahiye.
- Firebase push notifications ke liye Firebase project, `google-services.json`, Google Services Gradle plugin aur FCM configuration zaroori hai.
- WhatsApp number source mein `919727341942` hai; publish se pehle sahi business number verify karein.
- Website ke WiFi plans/recharge pages WebView mein khulenge; actual payment verification/recharge activation ke liye provider integration chahiye.
