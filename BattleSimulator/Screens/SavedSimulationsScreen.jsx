import React, {
  useCallback,
  useState,
} from 'react';

import {
  ActivityIndicator,
  Alert,
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

import {
  useFocusEffect,
} from '@react-navigation/native';

import {
  getSimulations,
  deleteSimulation,
  clearSimulations,
} from '../storage/simulationStorage';

function formatDate(dateString) {
  const date =
    new Date(dateString);

  return date.toLocaleString();
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

function getResultData(result) {
  if (result === 'knights_won') {
    return {
      icon: '🏆',
      text: 'Ganaron los caballeros',
    };
  }

  if (result === 'orcs_won') {
    return {
      icon: '🏆',
      text: 'Ganaron los orcos',
    };
  }

  if (result === 'draw') {
    return {
      icon: '⚔️',
      text: 'Empate',
    };
  }

  return {
    icon: '🛑',
    text: 'Batalla terminada',
  };
}

export default function SavedSimulationsScreen() {
  const [
    simulations,
    setSimulations,
  ] = useState([]);

  const [
    loading,
    setLoading,
  ] = useState(true);

  const loadSimulations =
    useCallback(async () => {
      setLoading(true);

      const data =
        await getSimulations();

      setSimulations(data);

      setLoading(false);
    }, []);

  useFocusEffect(
    useCallback(() => {
      loadSimulations();
    }, [loadSimulations])
  );

  const handleDeleteSimulation = (
  simulationId
) => {
  Alert.alert(
    'Eliminar simulación',
    '¿Seguro que deseas eliminar esta batalla?',
    [
      {
        text: 'Cancelar',
        style: 'cancel',
      },
      {
        text: 'Eliminar',
        style: 'destructive',
        onPress: async () => {
          const success =
            await deleteSimulation(
              simulationId
            );

          if (success) {
            loadSimulations();
          }
        },
      },
    ]
  );
};

const handleClearSimulations = () => {
  Alert.alert(
    'Eliminar historial',
    '¿Seguro que deseas eliminar todas las simulaciones guardadas?',
    [
      {
        text: 'Cancelar',
        style: 'cancel',
      },
      {
        text: 'Eliminar todo',
        style: 'destructive',
        onPress: async () => {
          const success =
            await clearSimulations();

          if (success) {
            loadSimulations();
          }
        },
      },
    ]
  );
};

  const renderSimulation = ({
    item,
  }) => {
    const result =
      getResultData(
        item.result
      );

    return (
      <View style={styles.card}>
        <View style={styles.cardHeader}>
          <Text style={styles.cardTitle}>
            ⚔️ Batalla
          </Text>

          <Text style={styles.date}>
            {formatDate(
              item.date
            )}
          </Text>
        </View>

        <View style={styles.armies}>
          <View style={styles.army}>
            <Text style={styles.armyIcon}>
              🛡️
            </Text>

            <Text style={styles.armyValue}>
              {item.initialKnights}
              {' → '}
              {item.remainingKnights}
            </Text>

            <Text style={styles.armyLabel}>
              Caballeros
            </Text>
          </View>

          <View style={styles.divider} />

          <View style={styles.army}>
            <Text style={styles.armyIcon}>
              👹
            </Text>

            <Text style={styles.armyValue}>
              {item.initialOrcs}
              {' → '}
              {item.remainingOrcs}
            </Text>

            <Text style={styles.armyLabel}>
              Orcos
            </Text>
          </View>
        </View>

        <View style={styles.resultRow}>
          <Text style={styles.result}>
            {result.icon}{' '}
            {result.text}
          </Text>

          <Text style={styles.time}>
            ⏱️{' '}
            {formatTime(
              item.elapsedTime
            )}
          </Text>
        </View>
        <TouchableOpacity
          style={styles.deleteButton}
          onPress={() =>
            handleDeleteSimulation(
              item.id
            )
          }
        >
        <Text style={styles.deleteButtonText}>
          🗑️ Eliminar
        </Text>
        </TouchableOpacity>
      </View>
    );
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator
          size="large"
        />

        <Text style={styles.loadingText}>
          Cargando simulaciones...
        </Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>
        SIMULACIONES GUARDADAS
      </Text>

      <View style={styles.topInfo}>
        <Text style={styles.subtitle}>
          {simulations.length}{' '}
          {simulations.length === 1
            ? 'batalla guardada'
            : 'batallas guardadas'}
        </Text>

        {simulations.length > 0 && (
          <TouchableOpacity
            onPress={
              handleClearSimulations
            }
          >
          <Text style={styles.clearText}>
            🗑️ Borrar todo
          </Text>
        </TouchableOpacity>
        )}
      </View>

      {simulations.length === 0 ? (
        <View style={styles.empty}>
          <Text style={styles.emptyIcon}>
            ⚔️
          </Text>

          <Text style={styles.emptyTitle}>
            No hay simulaciones
          </Text>

          <Text style={styles.emptyText}>
            Completa una batalla
            para verla aquí.
          </Text>
        </View>
      ) : (
        <FlatList
          data={simulations}
          keyExtractor={(item) =>
            item.id
          }
          renderItem={
            renderSimulation
          }
          contentContainerStyle={
            styles.list
          }
          showsVerticalScrollIndicator={
            false
          }
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f2f2f2',
    padding: 20,
  },

  center: {
    flex: 1,
    backgroundColor: '#f2f2f2',
    justifyContent: 'center',
    alignItems: 'center',
  },

  loadingText: {
    marginTop: 12,
    color: '#666666',
    fontSize: 15,
  },

  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#222222',
    textAlign: 'center',
  },

  subtitle: {
    fontSize: 14,
    color: '#777777',
    textAlign: 'center',
    marginTop: 5,
  },

  list: {
    paddingBottom: 20,
  },

  card: {
    backgroundColor: '#ffffff',
    borderRadius: 18,
    padding: 18,
    marginBottom: 15,

    shadowColor: '#000000',
    shadowOffset: {
      width: 0,
      height: 3,
    },
    shadowOpacity: 0.08,
    shadowRadius: 6,

    elevation: 3,
  },

  cardHeader: {
    marginBottom: 15,
  },

  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#222222',
  },

  date: {
    fontSize: 12,
    color: '#888888',
    marginTop: 4,
  },

  armies: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
    borderRadius: 14,
    paddingVertical: 14,
  },

  army: {
    flex: 1,
    alignItems: 'center',
  },

  armyIcon: {
    fontSize: 25,
  },

  armyValue: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#222222',
    marginTop: 3,
  },

  armyLabel: {
    fontSize: 12,
    color: '#777777',
    marginTop: 2,
  },

  divider: {
    width: 1,
    height: 50,
    backgroundColor: '#dddddd',
  },

  resultRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 15,
  },

  result: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#444444',
  },

  time: {
    fontSize: 13,
    color: '#777777',
  },

  empty: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingBottom: 80,
  },

  emptyIcon: {
    fontSize: 55,
    marginBottom: 15,
  },

  emptyTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333333',
  },

  emptyText: {
    fontSize: 14,
    color: '#777777',
    marginTop: 6,
    textAlign: 'center',
  },
  
  topInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 15,
  },

  clearText: {
    color: '#aa3333',
    fontSize: 13,
    fontWeight: 'bold',
  },

  deleteButton: {
    marginTop: 12,
    alignItems: 'center',
    paddingVertical: 8,
  },

  deleteButtonText: {
    color: '#aa3333',
    fontSize: 13,
    fontWeight: 'bold',
  },
});
