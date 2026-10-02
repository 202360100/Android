import React, { useState } from 'react';
import {
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';

import RoundedButton from '../Components/RoundedButton';

export default function NewSimulationScreen({ navigation }) {
  const [knights, setKnights] = useState('10');
  const [orcs, setOrcs] = useState('10');

  const startSimulation = () => {
    const numberOfKnights = Number(knights);
    const numberOfOrcs = Number(orcs);

    if (
      !Number.isInteger(numberOfKnights) ||
      !Number.isInteger(numberOfOrcs) ||
      numberOfKnights <= 0 ||
      numberOfOrcs <= 0
    ) {
      return;
    }

    navigation.navigate('Batalla', {
      knights: numberOfKnights,
      orcs: numberOfOrcs,
    });
  };

  return (
    <View style={styles.container}>

      <Text style={styles.title}>
        NUEVA SIMULACIÓN
      </Text>

      <Text style={styles.description}>
        Configura los ejércitos que participarán
        en la batalla.
      </Text>

      <View style={styles.section}>

        <Text style={styles.label}>
          🛡️ Caballeros
        </Text>

        <TextInput
          style={styles.input}
          value={knights}
          onChangeText={setKnights}
          keyboardType="number-pad"
          placeholder="Cantidad de caballeros"
          maxLength={3}
        />

      </View>

      <View style={styles.section}>

        <Text style={styles.label}>
          👹 Orcos
        </Text>

        <TextInput
          style={styles.input}
          value={orcs}
          onChangeText={setOrcs}
          keyboardType="number-pad"
          placeholder="Cantidad de orcos"
          maxLength={3}
        />

      </View>

      <RoundedButton
        title="COMENZAR BATALLA"
        onPress={startSimulation}
      />

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f2f2f2',
    padding: 30,
    justifyContent: 'center',
  },

  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#222',
    textAlign: 'center',
  },

  description: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    marginTop: 10,
    marginBottom: 40,
    lineHeight: 22,
  },

  section: {
    marginBottom: 25,
  },

  label: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 10,
  },

  input: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    paddingHorizontal: 15,
    paddingVertical: 12,
    fontSize: 18,
    borderWidth: 1,
    borderColor: '#ddd',
  },
});
