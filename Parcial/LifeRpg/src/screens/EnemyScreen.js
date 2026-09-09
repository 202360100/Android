import React, {
  useState,
} from 'react';

import {
  ScrollView,
  View,
  StyleSheet,
} from 'react-native';

import PixelText from '../components/PixelText';
import PixelCard from '../components/PixelCard';
import PixelButton from '../components/PixelButton';

import { enemyTypes } from '../data/enemies';
import { useGame } from '../context/GameContext';

import colors from '../theme/colors';

export default function EnemyScreen() {
  const {
    enemies,
    addEnemy,
  } = useGame();

  const [generated, setGenerated] =
    useState(null);

  const generateEnemy = () => {
    const type =
      enemyTypes[
        Math.floor(
          Math.random() * enemyTypes.length
        )
      ];

    const enemy = {
      ...type,

      id:
        `enemy_${Date.now()}_${Math.floor(
          Math.random() * 9999
        )}`,

      level: 1,
    };

    setGenerated(enemy);

    addEnemy(enemy);
  };

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
    >

      <PixelText
        size={26}
        color={colors.red}
        bold
        center
      >
        👹 INVOCAR ENEMIGO
      </PixelText>

      <PixelText
        color={colors.gray}
        center
        style={styles.intro}
      >
        Genera una criatura aleatoria de nivel 1.
      </PixelText>

      <PixelButton
        title="☠ INVOCAR ENEMIGO"
        color={colors.redDark}
        onPress={generateEnemy}
      />

      {generated && (
        <EnemyCard enemy={generated} />
      )}

      {enemies.length > 0 && (
        <PixelText
          color={colors.gray}
          center
          style={styles.counter}
        >
          Enemigos invocados: {enemies.length}
        </PixelText>
      )}

    </ScrollView>
  );
}

function EnemyCard({ enemy }) {
  return (
    <PixelCard style={styles.card}>

      <PixelText
        size={28}
        color={colors.red}
        bold
        center
      >
        {enemy.type}
      </PixelText>

      <PixelText
        center
        color={colors.gray}
        style={styles.level}
      >
        NIVEL {enemy.level}
      </PixelText>

      <View style={styles.separator} />

      <View style={styles.stats}>

        <View style={styles.stat}>
          <PixelText color={colors.hp}>
            ❤️ HP
          </PixelText>

          <PixelText bold size={20}>
            {enemy.hp}
          </PixelText>
        </View>

        <View style={styles.stat}>
          <PixelText color={colors.gold}>
            🛡 CA
          </PixelText>

          <PixelText bold size={20}>
            {enemy.ac}
          </PixelText>
        </View>

        <View style={styles.stat}>
          <PixelText color={colors.white}>
            ⚔ ATAQUE
          </PixelText>

          <PixelText bold size={20}>
            {enemy.attack}
          </PixelText>
        </View>

      </View>

      <PixelText
        color={colors.gold}
        bold
        style={styles.abilityTitle}
      >
        HABILIDAD
      </PixelText>

      <PixelText color={colors.white}>
        {enemy.ability}
      </PixelText>

      <PixelText
        color={colors.gray}
        style={styles.description}
      >
        {enemy.description}
      </PixelText>

      <PixelText
        color={colors.red}
        bold
      >
        DAÑO: {enemy.damage}
      </PixelText>

    </PixelCard>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },

  content: {
    padding: 20,
    paddingBottom: 100,
  },

  intro: {
    marginVertical: 20,
  },

  card: {
    marginTop: 20,
  },

  level: {
    marginTop: 5,
  },

  separator: {
    height: 3,
    backgroundColor: colors.redDark,
    marginVertical: 15,
  },

  stats: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },

  stat: {
    backgroundColor: colors.backgroundLight,

    padding: 10,

    width: '31%',

    borderWidth: 2,
    borderColor: colors.border,
  },

  abilityTitle: {
    marginTop: 20,
    marginBottom: 5,
  },

  description: {
    marginVertical: 15,
    lineHeight: 21,
  },

  counter: {
    marginTop: 20,
  },
});