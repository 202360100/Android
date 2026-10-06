import React from 'react';

import {
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';

function RuleCard({
  icon,
  title,
  children,
}) {
  return (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.cardIcon}>
          {icon}
        </Text>

        <Text style={styles.cardTitle}>
          {title}
        </Text>
      </View>

      <Text style={styles.cardText}>
        {children}
      </Text>
    </View>
  );
}

export default function HelpScreen() {
  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={
          styles.content
        }
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <Text style={styles.headerIcon}>
            📖
          </Text>

          <Text style={styles.title}>
            CÓMO JUGAR
          </Text>

          <Text style={styles.subtitle}>
            Reglas de Battle Simulator
          </Text>
        </View>

        <RuleCard
          icon="🎯"
          title="Objetivo"
        >
          El objetivo es eliminar por completo
          al ejército enemigo. La batalla termina
          cuando solo queda un bando con unidades.
        </RuleCard>

        <RuleCard
          icon="🗺️"
          title="Campo de batalla"
        >
          Cada batalla ocurre en un mapa de
          50 × 50 casillas. Cada casilla puede
          contener como máximo una unidad.
        </RuleCard>

        <RuleCard
          icon="🚶"
          title="Movimiento"
        >
          Cada segundo, las unidades intentan
          acercarse al enemigo más cercano.
          Una unidad solo puede avanzar una
          casilla por turno y no puede ocupar
          una casilla que ya esté ocupada.
        </RuleCard>

        <RuleCard
          icon="⚔️"
          title="Combate"
        >
          Cuando los ejércitos están cerca,
          cada unidad analiza las casillas que
          la rodean. Si hay más enemigos que
          aliados en esa zona, las unidades del
          bando superado son eliminadas.
        </RuleCard>

        <RuleCard
          icon="⚖️"
          title="Empates"
        >
          Si el número de enemigos y aliados
          es igual dentro de una zona de combate,
          ninguna unidad muere en ese turno.
        </RuleCard>

        <RuleCard
          icon="⏱️"
          title="Velocidad"
        >
          Puedes cambiar la velocidad de la
          simulación durante la batalla entre
          0.5×, 1×, 2× y 4×.
        </RuleCard>

        <RuleCard
          icon="⏸️"
          title="Pausa"
        >
          Puedes pausar y reanudar la batalla
          en cualquier momento. El tiempo y la
          simulación se detienen mientras está
          pausada.
        </RuleCard>

        <RuleCard
          icon="💾"
          title="Simulaciones guardadas"
        >
          Las batallas terminadas se almacenan
          localmente en el dispositivo. Puedes
          consultar o eliminar tu historial desde
          la sección de simulaciones guardadas.
        </RuleCard>

        <View style={styles.tip}>
          <Text style={styles.tipIcon}>
            💡
          </Text>

          <View style={styles.tipContent}>
            <Text style={styles.tipTitle}>
              Consejo
            </Text>

            <Text style={styles.tipText}>
              Experimenta con diferentes cantidades
              de caballeros y orcos. Cada batalla
              comienza con posiciones aleatorias,
              por lo que el resultado puede cambiar.
            </Text>
          </View>
        </View>

        <Text style={styles.footer}>
          Battle Simulator · Knights vs Orcs
        </Text>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f2f2f2',
  },

  content: {
    padding: 20,
    paddingBottom: 35,
  },

  header: {
    alignItems: 'center',
    paddingTop: 10,
    paddingBottom: 22,
  },

  headerIcon: {
    fontSize: 45,
    marginBottom: 8,
  },

  title: {
    fontSize: 27,
    fontWeight: 'bold',
    color: '#222222',
  },

  subtitle: {
    color: '#777777',
    fontSize: 14,
    marginTop: 5,
  },

  card: {
    backgroundColor: '#ffffff',
    borderRadius: 17,
    padding: 18,
    marginBottom: 12,

    shadowColor: '#000000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.06,
    shadowRadius: 5,

    elevation: 2,
  },

  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 9,
  },

  cardIcon: {
    fontSize: 24,
    marginRight: 11,
  },

  cardTitle: {
    color: '#222222',
    fontSize: 16,
    fontWeight: 'bold',
  },

  cardText: {
    color: '#666666',
    fontSize: 14,
    lineHeight: 21,
  },

  tip: {
    backgroundColor: '#e7eadf',
    borderRadius: 17,
    padding: 18,
    marginTop: 4,
    flexDirection: 'row',
  },

  tipIcon: {
    fontSize: 25,
    marginRight: 12,
  },

  tipContent: {
    flex: 1,
  },

  tipTitle: {
    color: '#3e4930',
    fontSize: 15,
    fontWeight: 'bold',
    marginBottom: 4,
  },

  tipText: {
    color: '#59634e',
    fontSize: 13,
    lineHeight: 20,
  },

  footer: {
    color: '#999999',
    textAlign: 'center',
    fontSize: 11,
    marginTop: 25,
  },
});
