import React, {
	useRef,
	useState,
} from 'react';

import {
	Animated,
	StyleSheet,
	View,
} from 'react-native';

import PixelButton from './PixelButton';
import PixelText from './PixelText';
import PixelCard from './PixelCard';
import colors from '../theme/colors';

export default function Dice() {
	const rotation = useRef(new Animated.Value(0)).current;
	const scale = useRef(new Animated.Value(1)).current;
	const [result, setResult] = useState('?');
	const [rolling, setRolling] = useState(false);

	const roll = () => {
		if (rolling) {
			return;
		}

		const finalResult = Math.floor(Math.random() * 20) + 1;

		setRolling(true);
		setResult('🎲');
		rotation.setValue(0);
		scale.setValue(1);

		Animated.parallel([
			Animated.timing(rotation, {
				toValue: 6,
				duration: 900,
				useNativeDriver: true,
			}),
			Animated.sequence([
				Animated.timing(scale, {
					toValue: 1.35,
					duration: 300,
					useNativeDriver: true,
				}),
				Animated.timing(scale, {
					toValue: 1,
					duration: 300,
					useNativeDriver: true,
				}),
			]),
		]).start(() => {
			setResult(finalResult);
			setRolling(false);
		});
	};

	const rotate = rotation.interpolate({
		inputRange: [0, 1],
		outputRange: ['0deg', '360deg'],
	});

	return (
		<>
			<PixelCard style={styles.card}>
				<Animated.View
					style={[
						styles.dice,
						{
							transform: [
								{ rotate },
								{ scale },
							],
						},
					]}
				>
					<PixelText size={52} color={colors.gold} bold center>
						{result}
					</PixelText>
				</Animated.View>

				<PixelText color={colors.gray} center style={styles.label}>
					RESULTADO D20
				</PixelText>
			</PixelCard>

			<View style={styles.buttonWrapper}>
				<PixelButton
					title={rolling ? '🎲 LANZANDO...' : '⚔ LANZAR D20'}
					onPress={roll}
					disabled={rolling}
				/>
			</View>
		</>
	);
}

const styles = StyleSheet.create({
	card: {
		marginTop: 40,
		alignItems: 'center',
	},

	dice: {
		width: 170,
		height: 170,
		backgroundColor: colors.purple,
		borderWidth: 6,
		borderColor: colors.border,
		justifyContent: 'center',
		alignItems: 'center',
	},

	label: {
		marginTop: 25,
	},

	buttonWrapper: {
		marginTop: 8,
	},
});
