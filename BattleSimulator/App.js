import React, { useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';

import SplashScreen from './Screens/SplashScreen';
import AppDrawer from './navigation/AppDrawer';

export default function App() {
  const [showSplash, setShowSplash] = useState(true);

  if (showSplash) {
    return (
      <SplashScreen
        onFinish={() => setShowSplash(false)}
      />
    );
  }

  return (
    <NavigationContainer>
      <AppDrawer />
    </NavigationContainer>
  );
}
