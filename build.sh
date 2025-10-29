npx react-native bundle --platform android --config metro.common.config.js --dev false --entry-file index.common.js --bundle-output ./android/app/src/main/assets/common.android.bundle --assets-dest=./android/app/src/main/assets/ --minify false --reset-cache

npx react-native bundle --platform android --config metro.business.config.js --dev false --entry-file index.js --bundle-output ./android/app/src/main/assets/index.android.bundle --assets-dest=./android/app/src/main/assets/ --minify false --reset-cache


npx react-native bundle --platform android --config metro.business.config.js --dev false --entry-file index.biz.js --bundle-output ./android/app/src/main/assets/biz.android.bundle --assets-dest=./android/app/src/main/assets/ --minify false --reset-cache
