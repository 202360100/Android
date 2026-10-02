import React from 'react';
import { createDrawerNavigator } from '@react-navigation/drawer';

import HomeScreen from '../Screens/HomeScreen';
import NewSimulationScreen from '../Screens/NewSimulationScreen';
import SavedSimulationsScreen from '../Screens/SavedSimulationsScreen';
import HelpScreen from '../Screens/HelpScreen';
import BattleScreen from '../Screens/BattleScreen';

const Drawer = createDrawerNavigator();

export default function AppDrawer() {
  return (
    <Drawer.Navigator>

      <Drawer.Screen
        name="Inicio"
        component={HomeScreen}
      />

      <Drawer.Screen
        name="Nueva simulación"
        component={NewSimulationScreen}
      />

      <Drawer.Screen
        name="Simulaciones guardadas"
        component={SavedSimulationsScreen}
      />

      <Drawer.Screen
        name="Ayuda"
        component={HelpScreen}
      />

      <Drawer.Screen
        name="Batalla"
        component={BattleScreen}
        options={{
          drawerItemStyle: { display: 'none' },
        }}
      />

    </Drawer.Navigator>
  );
}
