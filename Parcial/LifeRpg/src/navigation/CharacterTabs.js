import React from 'react';

import {
  createBottomTabNavigator,
} from '@react-navigation/bottom-tabs';

import CharacterScreen from '../screens/CharacterScreen';
import colors from '../theme/colors';

const Tab =
  createBottomTabNavigator();

export default function CharacterTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,

        tabBarStyle: {
          backgroundColor: colors.panel,
          borderTopColor: colors.border,
          borderTopWidth: 3,

          height: 65,
        },

        tabBarActiveTintColor: colors.gold,
        tabBarInactiveTintColor: colors.gray,

        tabBarLabelStyle: {
          fontWeight: '900',
          fontSize: 12,
        },
      }}
    >
      <Tab.Screen
        name="Generar"
        component={CharacterScreen}
        options={{
          tabBarIcon: () => '🎲',
        }}
      />

      <Tab.Screen
        name="Ficha"
        component={CharacterScreen}
        initialParams={{
          showLast: true,
        }}
        options={{
          tabBarIcon: () => '📜',
        }}
      />
    </Tab.Navigator>
  );
}