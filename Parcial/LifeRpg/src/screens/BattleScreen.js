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

import { useGame } from '../context/GameContext';
import colors from '../theme/colors';

export default function BattleScreen() {
  const {
    characters,
    enemies,
  } = useGame();

  const [selectedCharacter, setSelectedCharacter] =
    useState(null);

  const [selectedEnemy, setSelectedEnemy] =
    useState(null);

  const selectCharacter = (character) => {
    setSelectedCharacter(character);
  };

  const selectEnemy = (enemy) => {
    setSelectedEnemy(enemy);
  };

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
    >

      <PixelText
        size={27}
        color={colors.red}
        center
        bold
      >
        ⚔ ARENA
      </PixelText>

      <PixelText
        center
        color={colors.gray}
        style={styles.subtitle}
      >
        Elige quién se enfrentará a quién.
      </PixelText>

      <PixelText
        size={20}
        color={colors.gold}
        bold
        style={styles.section}
      >
        🧙 HÉROES
      </PixelText>

      {characters.length === 0 ? (
        <PixelCard>
          <PixelText
            center
            color={colors.gray}
          >
            No tienes personajes todavía.
            {'\n\n'}
            Genera uno desde el menú de personajes.
          </PixelText>
        </PixelCard>
      ) : (
        characters.map((character) => (
          <PixelButton
            key={character.id}
            title={`${character.name} · ${character.class}`}
            color={
              selectedCharacter?.id === character.id
                ? colors.greenDark
                : colors.blueDark
            }
            onPress={() =>
              selectCharacter(character)
            }
          />
        ))
      )}

      <PixelText
        size={20}
        color={colors.red}
        bold
        style={styles.section}
      >
        👹 ENEMIGOS
      </PixelText>

      {enemies.length === 0 ? (
        <PixelCard>
          <PixelText
            center
            color={colors.gray}
          >
            No tienes enemigos todavía.
            {'\n\n'}
            Invoca uno desde el menú de enemigos.
          </PixelText>
        </PixelCard>
      ) : (
        enemies.map((enemy) => (
          <PixelButton
            key={enemy.id}
            title={`${enemy.type} · Nivel ${enemy.level}`}
            color={
              selectedEnemy?.id === enemy.id
                ? colors.redDark
                : colors.panelLight
            }
            onPress={() =>
              selectEnemy(enemy)
            }
          />
        ))
      )}

      {selectedCharacter &&
        selectedEnemy && (
          <PixelCard style={styles.battleCard}>

            <PixelText
              size={23}
              color={colors.gold}
              center
              bold
            >
              ⚔ ENFRENTAMIENTO ⚔
            </PixelText>

            <View style={styles.fighters}>

              <View style={styles.fighter}>
                <PixelText
                  center
                  color={colors.blue}
                  bold
                >
                  {selectedCharacter.name}
                </PixelText>

                <PixelText
                  center
                  color={colors.gray}
                >
                  ❤️ {selectedCharacter.hp} HP
                </PixelText>
              </View>

              <PixelText
                size={25}
                color={colors.red}
                bold
              >
                VS
              </PixelText>

              <View style={styles.fighter}>
                <PixelText
                  center
                  color={colors.red}
                  bold
                >
                  {selectedEnemy.type}
                </PixelText>

                <PixelText
                  center
                  color={colors.gray}
                >
                  ❤️ {selectedEnemy.hp} HP
                </PixelText>
              </View>

            </View>

            <PixelButton
              title="⚔ COMENZAR COMBATE"
              color={colors.redDark}
              onPress={() => {
                alert(
                  `${selectedCharacter.name} VS ${selectedEnemy.type}`
                );
              }}
            />

          </PixelCard>
        )}

    </ScrollView>
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

  subtitle: {
    marginTop: 8,
  },

  section: {
    marginTop: 30,
    marginBottom: 10,
  },

  battleCard: {
    marginTop: 30,
  },

  fighters: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',

    marginVertical: 25,
  },

  fighter: {
    width: '38%',

    backgroundColor: colors.backgroundLight,

    padding: 12,

    borderWidth: 2,
    borderColor: colors.border,
  },
});