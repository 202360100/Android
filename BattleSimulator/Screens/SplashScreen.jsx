import React, { useEffect, useRef } from 'react';
import {
  Animated,
  StyleSheet,
  Text,
  View,
} from 'react-native';

export default function SplashScreen({ onFinish }) {
  const fadeAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.timing(fadeAnim, {
      toValue: 1,
      duration: 1200,
      useNativeDriver: true,
    }).start();

    const timer = setTimeout(() => {
      onFinish();
    }, 2500);

    return () => clearTimeout(timer);
  }, []);

  return (
    <View style={styles.container}>
      <Animated.View
        style={[
          styles.content,
          {
            opacity: fadeAnim,
          },
        ]}
      >
        <Text style={styles.icon}>⚔️</Text>

        <Text style={styles.title}>
          BATTLE SIMULATOR
        </Text>

        <Text style={styles.subtitle}>
          Knights vs Orcs
        </Text>
      </Animated.View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#151515',
    justifyContent: 'center',
    alignItems: 'center',
  },

  content: {
    alignItems: 'center',
  },

  icon: {
    fontSize: 70,
    marginBottom: 20,
  },

  title: {
    color: '#ffffff',
    fontSize: 28,
    fontWeight: 'bold',
    letterSpacing: 2,
  },

  subtitle: {
    color: '#aaaaaa',
    fontSize: 16,
    marginTop: 8,
  },
});
