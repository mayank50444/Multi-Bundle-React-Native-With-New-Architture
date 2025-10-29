
import 'react';
import { AppRegistry } from 'react-native';
import App from './Biz';
import { name as appName } from './app.biz.json';

console.log('biz bundle message');
AppRegistry.registerComponent(appName, () => App);
console.log('registerComponent', appName);
