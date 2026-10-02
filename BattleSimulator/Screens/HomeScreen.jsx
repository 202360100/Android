import React from 'react';
import {
  StyleSheet,
  Text,
  View,
} from 'react-native';

export default function HomeScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>
        BATTLE SIMULATOR
      </Text>

      <Text style={styles.subtitle}>
        Knights vs Orcs
      </Text>

      <Text style={styles.description}>
        Simula una batalla entre caballeros y orcos
        utilizando reglas de movimiento y combate.
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f2f2f2',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 30,
  },

  title: {
    fontSize: 30,
    fontWeight: 'bold',
    color: '#222',
  },

  subtitle: {
    fontSize: 18,
    color: '#666',
    marginTop: 5,
  },

  description: {
    fontSize: 16,
    color: '#555',
    textAlign: 'center',
    marginTop: 30,
    lineHeight: 24,
  },
});
