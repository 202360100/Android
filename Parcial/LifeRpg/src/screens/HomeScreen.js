import React from 'react';

import {
  View,
  StyleSheet,
} from 'react-native';

import PixelText from '../components/PixelText';
import PixelCard from '../components/PixelCard';
import colors from '../theme/colors';

export default function HomeScreen() {
  return (
    <View style={styles.container}>

      <PixelText
        size={48}
        color={colors.gold}
        center
        bold
      >
        ⚔
      </PixelText>

      <PixelText
        size={36}
        color={colors.gold}
        center
        bold
      >
        LIFERPG
      </PixelText>

      <PixelText
        size={15}
        color={colors.gray}
        center
        style={styles.subtitle}
      >
        TU AVENTURA COMIENZA AQUÍ
      </PixelText>

      <PixelCard style={styles.card}>

        <PixelText
          size={20}
          color={colors.gold}
          center
          bold
        >
          BIENVENIDO, AVENTURERO
        </PixelText>

        <PixelText
          center
          color={colors.gray}
          style={styles.description}
        >
          Crea héroes, invoca enemigos y
          decide quién sobrevivirá al combate.
        </PixelText>

      </PixelCard>

      <PixelText
        size={13}
        color={colors.darkGray}
        center
        style={styles.footer}
      >
        Abre el menú para comenzar tu aventura
      </PixelText>

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,

    justifyContent: 'center',
    alignItems: 'center',

    padding: 20,
  },

  subtitle: {
    marginTop: 8,
    letterSpacing: 2,
  },

  card: {
    width: '100%',
    marginTop: 40,
  },

  description: {
    marginTop: 15,
    lineHeight: 24,
  },

  footer: {
    position: 'absolute',
    bottom: 30,
  },
});