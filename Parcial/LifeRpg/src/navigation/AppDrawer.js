import React from 'react';

import {
	createDrawerNavigator,
} from '@react-navigation/drawer';

import HomeScreen from '../screens/HomeScreen';
import DiceScreen from '../screens/DiceScreen';
import CharacterTabs from './CharacterTabs';
import EnemyScreen from '../screens/EnemyScreen';
import BattleScreen from '../screens/BattleScreen';
import PixelText from '../components/PixelText';
import colors from '../theme/colors';

const Drawer = createDrawerNavigator();

const drawerIcon = (icon) => ({ color, size }) => (
	<PixelText size={size - 2} color={color} center>
		{icon}
	</PixelText>
);

export default function AppDrawer() {
	return (
		<Drawer.Navigator
			initialRouteName="Inicio"
			screenOptions={{
				headerStyle: {
					backgroundColor: colors.panel,
				},
				headerTintColor: colors.gold,
				headerTitleStyle: {
					fontWeight: '900',
				},
				drawerStyle: {
					backgroundColor: colors.background,
					width: 285,
				},
				drawerActiveBackgroundColor: colors.panel,
				drawerActiveTintColor: colors.gold,
				drawerInactiveTintColor: colors.gray,
				drawerLabelStyle: {
					fontWeight: '900',
					fontSize: 14,
				},
			}}
		>
			<Drawer.Screen
				name="Inicio"
				component={HomeScreen}
				options={{ drawerIcon: drawerIcon('⚔') }}
			/>
			<Drawer.Screen
				name="Tiradados"
				component={DiceScreen}
				options={{ drawerIcon: drawerIcon('🎲') }}
			/>
			<Drawer.Screen
				name="Personajes"
				component={CharacterTabs}
				options={{ drawerIcon: drawerIcon('🧙') }}
			/>
			<Drawer.Screen
				name="Enemigos"
				component={EnemyScreen}
				options={{ drawerIcon: drawerIcon('👹') }}
			/>
			<Drawer.Screen
				name="Arena"
				component={BattleScreen}
				options={{ drawerIcon: drawerIcon('⚔') }}
			/>
		</Drawer.Navigator>
	);
}
