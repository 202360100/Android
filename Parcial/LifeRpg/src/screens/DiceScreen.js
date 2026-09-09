import React from 'react';

import {
  View,
  StyleSheet,
} from 'react-native';

import PixelText from '../components/PixelText';
import Dice from '../components/Dice';

import colors from '../theme/colors';

export default function DiceScreen() {
  return (
    <View style={styles.container}>

      <PixelText
        size={27}
        color={colors.gold}
        bold
        center
      >
        🎲 TIRADADOS
      </PixelText>

      <PixelText
        color={colors.gray}
        center
        style={styles.subtitle}
      >
        Dado de veinte caras
      </PixelText>

      <Dice />

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,

    padding: 20,
  },

  subtitle: {
    marginTop: 8,
  },

});