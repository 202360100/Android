import { StatusBar } from 'expo-status-bar';
import React, { useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';

import SplashScreen from './src/screens/SplashScreen';
import AppDrawer from './src/navigation/AppDrawer';
import { GameProvider } from './src/context/GameContext';

export default function App() {
  const [showSplash, setShowSplash] = useState(true);

  if (showSplash) {
    return <SplashScreen onFinish={() => setShowSplash(false)} />;
  }

  return (
    <GameProvider>
      <NavigationContainer>
        <AppDrawer />
      </NavigationContainer>
      <StatusBar style="auto" />
    </GameProvider>
  );
}
