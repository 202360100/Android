import React from 'react';

import {
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

export default function HomeScreen({
  navigation,
}) {
  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={
          styles.content
        }
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.hero}>
          <Text style={styles.swords}>
            ⚔️
          </Text>

          <Text style={styles.title}>
            BATTLE
          </Text>

          <Text style={styles.titleAccent}>
            SIMULATOR
          </Text>

          <Text style={styles.subtitle}>
            Knights vs Orcs
          </Text>

          <Text style={styles.description}>
            Crea tus ejércitos y observa cómo
            se enfrentan en una batalla
            completamente simulada.
          </Text>
        </View>

        <TouchableOpacity
          style={styles.primaryButton}
          onPress={() =>
            navigation.navigate(
              'Nueva simulación'
            )
          }
        >
          <Text style={styles.primaryIcon}>
            ⚔️
          </Text>

          <View style={styles.buttonContent}>
            <Text style={styles.primaryButtonTitle}>
              NUEVA BATALLA
            </Text>

            <Text style={styles.primaryButtonSubtitle}>
              Configura tus ejércitos
            </Text>
          </View>

          <Text style={styles.arrow}>
            ›
          </Text>
        </TouchableOpacity>

        <View style={styles.quickRow}>
          <TouchableOpacity
            style={styles.quickCard}
            onPress={() =>
              navigation.navigate(
                'Simulaciones guardadas'
              )
            }
          >
            <Text style={styles.quickIcon}>
              💾
            </Text>

            <Text style={styles.quickTitle}>
              Batallas
            </Text>

            <Text style={styles.quickSubtitle}>
              Guardadas
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.quickCard}
            onPress={() =>
              navigation.navigate(
                'Ayuda'
              )
            }
          >
            <Text style={styles.quickIcon}>
              📖
            </Text>

            <Text style={styles.quickTitle}>
              Cómo jugar
            </Text>

            <Text style={styles.quickSubtitle}>
              Reglas
            </Text>
          </TouchableOpacity>
        </View>

        <View style={styles.infoCard}>
          <Text style={styles.infoTitle}>
            ⚔️ ¿Cómo funciona?
          </Text>

          <Text style={styles.infoText}>
            Dos ejércitos se enfrentan en un
            campo de batalla de 50 × 50
            casillas. Cada unidad busca
            acercarse al ejército enemigo
            hasta que solo quede un bando.
          </Text>
        </View>

        <Text style={styles.footer}>
          Simulación local · Sin conexión
        </Text>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#151515',
  },

  content: {
    padding: 22,
    paddingBottom: 35,
  },

  hero: {
    alignItems: 'center',
    paddingTop: 30,
    paddingBottom: 30,
  },

  swords: {
    fontSize: 58,
    marginBottom: 18,
  },

  title: {
    color: '#ffffff',
    fontSize: 38,
    fontWeight: '900',
    letterSpacing: 3,
  },

  titleAccent: {
    color: '#a9b889',
    fontSize: 38,
    fontWeight: '900',
    letterSpacing: 3,
    marginTop: -5,
  },

  subtitle: {
    color: '#aaaaaa',
    fontSize: 17,
    marginTop: 10,
    letterSpacing: 1,
  },

  description: {
    color: '#888888',
    fontSize: 14,
    lineHeight: 21,
    textAlign: 'center',
    marginTop: 18,
    maxWidth: 330,
  },

  primaryButton: {
    backgroundColor: '#5f7048',
    borderRadius: 18,
    padding: 17,
    flexDirection: 'row',
    alignItems: 'center',
  },

  primaryIcon: {
    fontSize: 28,
    marginRight: 14,
  },

  buttonContent: {
    flex: 1,
  },

  primaryButtonTitle: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: 'bold',
    letterSpacing: 0.5,
  },

  primaryButtonSubtitle: {
    color: '#d4dbc8',
    fontSize: 12,
    marginTop: 3,
  },

  arrow: {
    color: '#ffffff',
    fontSize: 30,
    fontWeight: '300',
  },

  quickRow: {
    flexDirection: 'row',
    gap: 12,
    marginTop: 12,
  },

  quickCard: {
    flex: 1,
    backgroundColor: '#222222',
    borderRadius: 16,
    padding: 17,
    minHeight: 125,
  },

  quickIcon: {
    fontSize: 27,
    marginBottom: 12,
  },

  quickTitle: {
    color: '#ffffff',
    fontSize: 15,
    fontWeight: 'bold',
  },

  quickSubtitle: {
    color: '#777777',
    fontSize: 12,
    marginTop: 3,
  },

  infoCard: {
    backgroundColor: '#1e1e1e',
    borderRadius: 18,
    padding: 20,
    marginTop: 12,
    borderWidth: 1,
    borderColor: '#2d2d2d',
  },

  infoTitle: {
    color: '#ffffff',
    fontSize: 17,
    fontWeight: 'bold',
    marginBottom: 10,
  },

  infoText: {
    color: '#999999',
    fontSize: 14,
    lineHeight: 21,
  },

  footer: {
    color: '#555555',
    textAlign: 'center',
    fontSize: 11,
    marginTop: 25,
  },
});
