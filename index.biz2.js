import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import {
  SafeAreaView,
  StatusBar,
  Text,
  useColorScheme,
} from 'react-native';
import { Colors } from 'react-native/Libraries/NewAppScreen';

function Biz2App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  useEffect(() => {
    console.log('Biz2App mounted');

    // Cleanup runs when the ReactRootView is unmounted
    return () => {
      console.log('Biz2App unmounted');
    };
  }, []);


  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  console.log('render Biz2 component');

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <Text>Business Screen 2</Text>
    </SafeAreaView>
  );
}

AppRegistry.registerComponent('Biz2Bundle', () => Biz2App);

console.log('registered component Biz2Bundle');
