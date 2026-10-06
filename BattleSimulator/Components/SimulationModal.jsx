import React, {
  useEffect,
  useRef,
} from 'react';

import {
  Animated,
  Modal,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

function getResultData(status) {
  if (status === 'knights_won') {
    return {
      icon: '🏆',
      title: '¡Victoria!',
      subtitle:
        'Los caballeros han ganado la batalla.',
    };
  }

  if (status === 'orcs_won') {
    return {
      icon: '🏆',
      title: '¡Victoria!',
      subtitle:
        'Los orcos han ganado la batalla.',
    };
  }

  if (status === 'draw') {
    return {
      icon: '⚔️',
      title: 'Empate',
      subtitle:
        'Ambos ejércitos fueron eliminados.',
    };
  }

  return {
    icon: '🛑',
    title: 'Batalla terminada',
    subtitle:
      'La simulación fue terminada manualmente.',
  };
}

function formatTime(seconds) {
  const minutes =
    Math.floor(seconds / 60);

  const remainingSeconds =
    seconds % 60;

  return `${String(minutes).padStart(2, '0')}:${String(
    remainingSeconds
  ).padStart(2, '0')}`;
}

export default function SimulationModal({
  visible,
  status,
  elapsedTime,
  remainingKnights,
  remainingOrcs,
  onNewBattle,
  onGoHome,
}) {
  const result =
    getResultData(status);

  const fadeAnim =
    useRef(
      new Animated.Value(0)
    ).current;

  const scaleAnim =
    useRef(
      new Animated.Value(0.85)
    ).current;

  useEffect(() => {
    if (!visible) {
      return;
    }

    fadeAnim.setValue(0);
    scaleAnim.setValue(0.85);

    Animated.parallel([
      Animated.timing(
        fadeAnim,
        {
          toValue: 1,
          duration: 300,
          useNativeDriver: true,
        }
      ),

      Animated.spring(
        scaleAnim,
        {
          toValue: 1,
          friction: 7,
          tension: 70,
          useNativeDriver: true,
        }
      ),
    ]).start();
  }, [
    visible,
    fadeAnim,
    scaleAnim,
  ]);

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={() => {}}
    >
      <View style={styles.overlay}>
        <Animated.View
          style={[
            styles.modal,
            {
              opacity: fadeAnim,
              transform: [
                {
                  scale: scaleAnim,
                },
              ],
            },
          ]}
        >
          <Text style={styles.icon}>
            {result.icon}
          </Text>

          <Text style={styles.title}>
            {result.title}
          </Text>

          <Text style={styles.subtitle}>
            {result.subtitle}
          </Text>

          <View style={styles.stats}>
            <View style={styles.stat}>
              <Text style={styles.statIcon}>
                🛡️
              </Text>

              <Text style={styles.statValue}>
                {remainingKnights}
              </Text>

              <Text style={styles.statLabel}>
                Caballeros
              </Text>
            </View>

            <View style={styles.divider} />

            <View style={styles.stat}>
              <Text style={styles.statIcon}>
                👹
              </Text>

              <Text style={styles.statValue}>
                {remainingOrcs}
              </Text>

              <Text style={styles.statLabel}>
                Orcos
              </Text>
            </View>
          </View>

          <View style={styles.timeContainer}>
            <Text style={styles.timeLabel}>
              Tiempo de batalla
            </Text>

            <Text style={styles.time}>
              {formatTime(elapsedTime)}
            </Text>
          </View>

          <TouchableOpacity
            style={styles.primaryButton}
            onPress={onNewBattle}
          >
            <Text style={styles.primaryButtonText}>
              NUEVA BATALLA
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.secondaryButton}
            onPress={onGoHome}
          >
            <Text style={styles.secondaryButtonText}>
              VOLVER AL INICIO
            </Text>
          </TouchableOpacity>
        </Animated.View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor:
      'rgba(0, 0, 0, 0.75)',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 25,
  },

  modal: {
    width: '100%',
    maxWidth: 400,
    backgroundColor: '#ffffff',
    borderRadius: 24,
    padding: 25,
    alignItems: 'center',
  },

  icon: {
    fontSize: 55,
    marginBottom: 10,
  },

  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#222222',
  },

  subtitle: {
    fontSize: 15,
    color: '#666666',
    textAlign: 'center',
    marginTop: 8,
    lineHeight: 21,
  },

  stats: {
    width: '100%',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent:
      'space-evenly',
    marginTop: 25,
    paddingVertical: 18,
    backgroundColor: '#f4f4f4',
    borderRadius: 16,
  },

  stat: {
    alignItems: 'center',
    flex: 1,
  },

  statIcon: {
    fontSize: 25,
  },

  statValue: {
    fontSize: 25,
    fontWeight: 'bold',
    color: '#222222',
    marginTop: 3,
  },

  statLabel: {
    fontSize: 12,
    color: '#777777',
    marginTop: 2,
  },

  divider: {
    width: 1,
    height: 55,
    backgroundColor: '#dddddd',
  },

  timeContainer: {
    alignItems: 'center',
    marginTop: 20,
    marginBottom: 10,
  },

  timeLabel: {
    fontSize: 13,
    color: '#777777',
  },

  time: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#222222',
    marginTop: 3,
  },

  primaryButton: {
    width: '100%',
    backgroundColor: '#252525',
    borderRadius: 14,
    paddingVertical: 15,
    alignItems: 'center',
    marginTop: 15,
  },

  primaryButtonText: {
    color: '#ffffff',
    fontSize: 15,
    fontWeight: 'bold',
  },

  secondaryButton: {
    width: '100%',
    borderRadius: 14,
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 8,
  },

  secondaryButtonText: {
    color: '#555555',
    fontSize: 14,
    fontWeight: 'bold',
  },
});
