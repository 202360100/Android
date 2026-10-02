import React, {
  useState,
} from 'react';

import {
  StyleSheet,
  Text,
  View,
} from 'react-native';

import BattleGrid from '../Components/BattleGrid';

function generateUnits(
  knights,
  orcs
) {
  const units = [];
  const occupied = new Set();

  const totalUnits =
    knights + orcs;

  while (
    units.length < totalUnits
  ) {

    const x =
      Math.floor(
        Math.random() * 50
      );

    const y =
      Math.floor(
        Math.random() * 50
      );

    const position =
      `${x}-${y}`;

    if (
      occupied.has(position)
    ) {
      continue;
    }

    occupied.add(position);

    const type =
      units.length < knights
        ? 'knight'
        : 'orc';

    units.push({
      id: units.length + 1,
      type,
      x,
      y,
    });
  }

  return units;
}

export default function BattleScreen({
  route,
}) {

  const {
    knights,
    orcs,
  } = route.params;

  const [
    units,
    setUnits,
  ] = useState(() =>
    generateUnits(
      knights,
      orcs
    )
  );

  return (
    <View style={styles.container}>

      <View style={styles.header}>

        <Text style={styles.title}>
          BATALLA
        </Text>

        <Text style={styles.counter}>
          🛡️ {knights}    👹 {orcs}
        </Text>

      </View>

      <BattleGrid
        units={units}
      />

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#222',
  },

  header: {
    padding: 15,
    backgroundColor: '#151515',
  },

  title: {
    color: '#fff',
    fontSize: 22,
    fontWeight: 'bold',
  },

  counter: {
    color: '#ddd',
    fontSize: 16,
    marginTop: 5,
  },
});
