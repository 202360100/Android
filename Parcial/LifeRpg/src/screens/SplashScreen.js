import React, {
  useEffect,
  useRef,
} from 'react';

import {
  View,
  Animated,
  StyleSheet,
} from 'react-native';

import PixelText from '../components/PixelText';
import colors from '../theme/colors';

export default function SplashScreen({ onFinish }) {
  const opacity = useRef(
    new Animated.Value(0)
  ).current;

  const scale = useRef(
    new Animated.Value(0.5)
  ).current;

  const swordPosition = useRef(
    new Animated.Value(-80)
  ).current;

  useEffect(() => {
    Animated.parallel([
      Animated.timing(opacity, {
        toValue: 1,
        duration: 700,
        useNativeDriver: true,
      }),

      Animated.spring(scale, {
        toValue: 1,
        friction: 5,
        tension: 80,
        useNativeDriver: true,
      }),

      Animated.timing(swordPosition, {
        toValue: 0,
        duration: 900,
        useNativeDriver: true,
      }),
    ]).start();

    const timer = setTimeout(() => {
      onFinish();
    }, 2200);

    return () => clearTimeout(timer);
  }, []);

  return (
    <View style={styles.container}>

      <Animated.View
        style={[
          styles.sword,
          {
            transform: [
              {
                translateY: swordPosition,
              },
            ],
          },
        ]}
      >
        <PixelText
          size={48}
          center
          color={colors.gold}
        >
          ⚔
        </PixelText>
      </Animated.View>

      <Animated.View
        style={{
          opacity,
          transform: [
            {
              scale,
            },
          ],
        }}
      >
        <PixelText
          size={42}
          center
          color={colors.gold}
          bold
        >
          LIFERPG
        </PixelText>

        <PixelText
          size={14}
          center
          color={colors.gray}
          style={styles.subtitle}
        >
          FORJA TU PROPIA LEYENDA
        </PixelText>
      </Animated.View>

      <View style={styles.loading}>
        <View style={styles.loadingBar}>
          <View style={styles.loadingFill} />
        </View>
      </View>

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,

    justifyContent: 'center',
    alignItems: 'center',
  },

  sword: {
    marginBottom: 20,
  },

  subtitle: {
    marginTop: 8,
    letterSpacing: 2,
  },

  loading: {
    position: 'absolute',
    bottom: 80,
  },

  loadingBar: {
    width: 180,
    height: 14,

    backgroundColor: colors.border,

    borderWidth: 3,
    borderColor: colors.borderLight,
  },

  loadingFill: {
    width: '70%',
    height: '100%',

    backgroundColor: colors.gold,
  },
});