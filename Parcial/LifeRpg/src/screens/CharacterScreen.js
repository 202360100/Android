import React, {
  useState,
} from 'react';

import {
  View,
  ScrollView,
  StyleSheet,
} from 'react-native';

import PixelText from '../components/PixelText';
import PixelCard from '../components/PixelCard';
import PixelButton from '../components/PixelButton';

import { races } from '../data/races';
import { classes } from '../data/classes';
import { randomName } from '../data/names';

import { useGame } from '../context/GameContext';
import colors from '../theme/colors';

function randomNumber(min, max) {
  return Math.floor(
    Math.random() * (max - min + 1)
  ) + min;
}

function generateStats() {
  return {
    strength: randomNumber(8, 16),
    dexterity: randomNumber(8, 16),
    constitution: randomNumber(8, 16),
    intelligence: randomNumber(8, 16),
    wisdom: randomNumber(8, 16),
    charisma: randomNumber(8, 16),
  };
}

export default function CharacterScreen({
  route,
}) {
  const {
    characters,
    addCharacter,
  } = useGame();

  const [generated, setGenerated] =
    useState(null);

  const showLast =
    route?.params?.showLast === true;

  const generateCharacter = () => {
    const race =
      races[
        Math.floor(
          Math.random() * races.length
        )
      ];

    const characterClass =
      classes[
        Math.floor(
          Math.random() * classes.length
        )
      ];

    const stats = generateStats();

    const character = {
      id:
        `char_${Date.now()}_${Math.floor(
          Math.random() * 9999
        )}`,

      name: randomName(),

      race: race.name,

      class: characterClass.name,

      level: 1,

      background: [
        'Soldado',
        'Ermitaño',
        'Forastero',
        'Artesano',
        'Noble',
        'Criminal',
      ][
        Math.floor(Math.random() * 6)
      ],

      alignment: [
        'Legal Bueno',
        'Neutral Bueno',
        'Caótico Bueno',
        'Legal Neutral',
        'Neutral',
        'Caótico Neutral',
        'Legal Malvado',
      ][
        Math.floor(Math.random() * 7)
      ],

      stats,

      hp:
        characterClass.hp +
        Math.floor(stats.constitution / 4),

      armorClass:
        10 +
        Math.floor(stats.dexterity / 2),

      weapon:
        characterClass.weapon,

      description:
        `${randomName()} pertenece a la estirpe ${race.name} y sigue el camino del ${characterClass.name}. ${race.description} ${characterClass.description}`,
    };

    setGenerated(character);

    addCharacter(character);
  };

  const characterToShow =
    generated ||
    characters[characters.length - 1];

  if (showLast) {
    return (
      <ScrollView
        style={styles.container}
        contentContainerStyle={styles.content}
      >
        <PixelText
          size={25}
          color={colors.gold}
          bold
          center
        >
          📜 ÚLTIMA FICHA
        </PixelText>

        {!characterToShow ? (
          <PixelCard style={styles.card}>
            <PixelText center color={colors.gray}>
              Todavía no has generado ningún
              personaje.
            </PixelText>
          </PixelCard>
        ) : (
          <CharacterCard
            character={characterToShow}
          />
        )}
      </ScrollView>
    );
  }

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
    >
      <PixelText
        size={26}
        color={colors.gold}
        bold
        center
      >
        🧙 FORJA UN HÉROE
      </PixelText>

      <PixelText
        center
        color={colors.gray}
        style={styles.intro}
      >
        Todo será generado al azar.
        {'\n'}
        El nivel inicial siempre será 1.
      </PixelText>

      <PixelButton
        title="⚔ GENERAR PERSONAJE"
        onPress={generateCharacter}
      />

      {generated && (
        <CharacterCard
          character={generated}
        />
      )}
    </ScrollView>
  );
}

function CharacterCard({ character }) {
  const stat = (label, value) => (
    <View style={styles.stat}>
      <PixelText
        size={12}
        color={colors.gray}
      >
        {label}
      </PixelText>

      <PixelText
        size={18}
        color={colors.white}
        bold
      >
        {value}
      </PixelText>
    </View>
  );

  return (
    <PixelCard style={styles.card}>

      <PixelText
        size={27}
        color={colors.gold}
        center
        bold
      >
        {character.name}
      </PixelText>

      <PixelText
        center
        color={colors.gray}
        style={styles.identity}
      >
        Nivel {character.level} · {character.race}
        {'\n'}
        {character.class}
      </PixelText>

      <View style={styles.separator} />

      <PixelText
        color={colors.gold}
        bold
      >
        TRASFONDO
      </PixelText>

      <PixelText
        color={colors.gray}
        style={styles.text}
      >
        {character.background}
      </PixelText>

      <PixelText
        color={colors.gold}
        bold
      >
        ALINEAMIENTO
      </PixelText>

      <PixelText
        color={colors.gray}
        style={styles.text}
      >
        {character.alignment}
      </PixelText>

      <View style={styles.statsContainer}>
        {stat('FUE', character.stats.strength)}
        {stat('DES', character.stats.dexterity)}
        {stat('CON', character.stats.constitution)}
        {stat('INT', character.stats.intelligence)}
        {stat('SAB', character.stats.wisdom)}
        {stat('CAR', character.stats.charisma)}
      </View>

      <View style={styles.combat}>
        <PixelText color={colors.hp} bold>
          ❤️ HP: {character.hp}
        </PixelText>

        <PixelText color={colors.gold} bold>
          🛡 CA: {character.armorClass}
        </PixelText>

        <PixelText color={colors.white} bold>
          ⚔ {character.weapon}
        </PixelText>
      </View>

      <View style={styles.separator} />

      <PixelText
        color={colors.gray}
        style={styles.description}
      >
        {character.description}
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
    lineHeight: 22,
  },

  card: {
    marginTop: 20,
  },

  identity: {
    marginTop: 8,
    lineHeight: 22,
  },

  separator: {
    height: 3,
    backgroundColor: colors.goldDark,
    marginVertical: 15,
  },

  text: {
    marginTop: 5,
    marginBottom: 15,
  },

  statsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',

    marginTop: 5,
  },

  stat: {
    width: '30%',

    backgroundColor: colors.backgroundLight,

    padding: 10,
    marginBottom: 8,

    borderWidth: 2,
    borderColor: colors.border,
  },

  combat: {
    marginTop: 12,
    gap: 10,
  },

  description: {
    lineHeight: 22,
  },
});