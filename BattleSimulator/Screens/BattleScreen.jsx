import React, {
  useEffect,
  useState,
} from 'react';

import {
  Alert,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

import BattleGrid from '../Components/BattleGrid';
import SimulationModal from '../Components/SimulationModal';
import { saveSimulation, } from '../storage/simulationStorage';
import { simulateTurn } from '../simulation/simulationEngine';

function generateUnits(knights, orcs) {
  const units = [];
  const occupied = new Set();

  const addUnit = (
    type,
    count,
    minX,
    maxX
  ) => {
    let created = 0;

    while (created < count) {
      const x =
        Math.floor(
          Math.random() *
            (maxX - minX + 1)
        ) + minX;

      const y =
        Math.floor(
          Math.random() * 50
        );

      const position =
        `${x}-${y}`;

      if (occupied.has(position)) {
        continue;
      }

      occupied.add(position);

      units.push({
        id: units.length + 1,
        type,
        x,
        y,
        stuckTurns: 0,
      });

      created++;
    }
  };

  // Caballeros: lado izquierdo
  addUnit(
    'knight',
    knights,
    0,
    24
  );

  // Orcos: lado derecho
  addUnit(
    'orc',
    orcs,
    25,
    49
  );

  return units;
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

function getResultText(status) {
  if (status === 'knights_won') {
    return 'knights_won';
  }

  if (status === 'orcs_won') {
    return 'orcs_won';
  }

  if (status === 'draw') {
    return 'draw';
  }

  if (status === 'terminated') {
    return 'terminated';
  }

  return 'unknown';
}

function createInitialUnits(knights, orcs) {
  return generateUnits(knights, orcs);
}

export default function BattleScreen({
  route,
  navigation,
}) {
  const {
    knights,
    orcs,
    battleId,
  } = route.params;

  const [
    units,
    setUnits,
  ] = useState(() =>
    createInitialUnits(
      knights,
      orcs
    )
  );

  const [
    paused,
    setPaused,
  ] = useState(false);

  const [
    speed,
    setSpeed,
  ] = useState(1);

  const [
    elapsedTime,
    setElapsedTime,
  ] = useState(0);

  const [
    status,
    setStatus,
  ] = useState('fighting');

  const [
    combatPositions,
    setCombatPositions,
  ] = useState([]);

  const [
    showResultModal,
    setShowResultModal,
  ] = useState(false);

  const [
    simulationSaved,
    setSimulationSaved,
  ] = useState(false);

  useEffect(() => {
    if (!battleId) {
      return;
    }

  setUnits(
    createInitialUnits(
      knights,
      orcs
    )
  );

  setPaused(false);
  setSpeed(1);
  setElapsedTime(0);
  setStatus('fighting');
  setShowResultModal(false);
  setSimulationSaved(false);
}, [
  battleId,
  knights,
  orcs,
]);

  /*
   * Calculamos el intervalo a partir
   * de la velocidad seleccionada.
   *
   * 1x = 1000 ms
   * 2x = 500 ms
   * 4x = 250 ms
   * 0.5x = 2000 ms
   */
  const intervalTime =
    1000 / speed;

  /*
   * Motor de simulación.
   */
  useEffect(() => {
    if (
      paused ||
      status !== 'fighting'
    ) {
      return;
    }

    const interval =
      setInterval(() => {
        setUnits((currentUnits) => {
        const result =
        simulateTurn(
          currentUnits
        );

        setCombatPositions(
          result.combatPositions || []
        );

        if (
          result.status !==
            'fighting'
          ) {
        setStatus(
          result.status
        );

        setPaused(true);

        setShowResultModal(true);

        const finalKnights =
          result.units.filter(
            (unit) =>
              unit.type === 'knight'
          ).length;

        const finalOrcs =
          result.units.filter(
            (unit) =>
              unit.type === 'orc'
          ).length;

        saveBattleResult(
          result.status,
          finalKnights,
          finalOrcs
        );
      }

          return result.units;
        });
      }, intervalTime);

    return () => {
      clearInterval(interval);
    };
  }, [
    paused,
    speed,
    status,
    intervalTime,
  ]);

  useEffect(() => {
    if (combatPositions.length === 0) {
      return;
    }

    const timer = setTimeout(() => {
      setCombatPositions([]);
    }, 350);

    return () => {
      clearTimeout(timer);
    };
  }, [combatPositions]);

  /*
   * Contador de tiempo.
   *
   * Se detiene cuando la batalla
   * está pausada o terminada.
   */
  useEffect(() => {
    if (
      paused ||
      status !== 'fighting'
    ) {
      return;
    }

    const timer =
      setInterval(() => {
        setElapsedTime(
          (currentTime) =>
            currentTime + 1
        );
      }, 1000);

    return () => {
      clearInterval(timer);
    };
  }, [
    paused,
    status,
  ]);

  const remainingKnights =
    units.filter(
      (unit) =>
        unit.type === 'knight'
    ).length;

  const remainingOrcs =
    units.filter(
      (unit) =>
        unit.type === 'orc'
    ).length;

  /*
   * Cambiar velocidad.
   */
  const changeSpeed = () => {
    setSpeed((currentSpeed) => {
      if (currentSpeed === 0.5) {
        return 1;
      }

      if (currentSpeed === 1) {
        return 2;
      }

      if (currentSpeed === 2) {
        return 4;
      }

      return 0.5;
    });
  };

  const saveBattleResult = async (
  finalStatus,
  finalKnights,
  finalOrcs
) => {
  if (simulationSaved) {
    return;
  }

  const savedSimulation =
    await saveSimulation({
      battleId,
      date: new Date().toISOString(),
      initialKnights: knights,
      initialOrcs: orcs,
      remainingKnights:
        finalKnights,
      remainingOrcs:
        finalOrcs,
      elapsedTime,
      result:
        getResultText(
          finalStatus
        ),
    });

  if (savedSimulation) {
    setSimulationSaved(true);
  }
};

  /*
   * Pausar / reanudar.
   */
  const togglePause = () => {
    if (
      status !== 'fighting'
    ) {
      return;
    }

    setPaused(
      (currentPaused) =>
        !currentPaused
    );
  };

  /*
   * Terminar manualmente.
   */
  const terminateBattle = () => {
    if (status !== 'fighting') {
      return;
    }

    Alert.alert(
      'Terminar batalla',
      '¿Seguro que deseas terminar la simulación?',
      [
        {
          text: 'Cancelar',
          style: 'cancel',
        },
        {
          text: 'Terminar',
          style: 'destructive',
          onPress: () => {
            setPaused(true);
            setStatus('terminated');
            setShowResultModal(true);

            saveBattleResult(
              'terminated',
              remainingKnights,
              remainingOrcs
            );
          },
        },
      ]
    );
  };

  /*
   * Texto del estado.
   */
  const getStatusText = () => {
    if (status === 'knights_won') {
      return '🏆 Ganaron los caballeros';
    }

    if (status === 'orcs_won') {
      return '🏆 Ganaron los orcos';
    }

    if (status === 'draw') {
      return '⚔️ Empate';
    }

    if (status === 'terminated') {
      return '🛑 Batalla terminada';
    }

    if (paused) {
      return '⏸️ Batalla pausada';
    }

    return '⚔️ Batalla en curso';
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <View style={styles.headerTop}>
          <Text style={styles.title}>
            BATALLA
          </Text>

          <Text style={styles.time}>
            {formatTime(elapsedTime)}
          </Text>
        </View>

        <View style={styles.statsRow}>
          <Text style={styles.counter}>
            🛡️ {remainingKnights}
          </Text>

          <Text style={styles.counter}>
            👹 {remainingOrcs}
          </Text>

          <Text style={styles.speed}>
            ⚡ {speed}x
          </Text>
        </View>

        <Text style={styles.status}>
          {getStatusText()}
        </Text>
      </View>

      <BattleGrid
        units={units}
        combatPositions={combatPositions}
      />

      <View style={styles.controls}>
        <TouchableOpacity
          style={styles.controlButton}
          onPress={togglePause}
          disabled={
            status !== 'fighting'
          }
        >
          <Text style={styles.controlButtonText}>
            {paused
              ? '▶ Reanudar'
              : '⏸ Pausar'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.controlButton}
          onPress={changeSpeed}
          disabled={
            status !== 'fighting'
          }
        >
          <Text style={styles.controlButtonText}>
            ⚡ Velocidad
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            styles.controlButton,
            styles.dangerButton,
          ]}
          onPress={terminateBattle}
          disabled={
            status !== 'fighting'
          }
        >
          <Text style={styles.controlButtonText}>
            🛑 Terminar
          </Text>
        </TouchableOpacity>
      </View>
      <SimulationModal
        visible={showResultModal}
        status={status}
        elapsedTime={elapsedTime}
        remainingKnights={remainingKnights}
        remainingOrcs={remainingOrcs}
        onNewBattle={() => {
          setShowResultModal(false);
          navigation.navigate('Nueva simulación');
        }}
        onGoHome={() => {
          setShowResultModal(false);
          navigation.navigate('Inicio');
        }}
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
    paddingHorizontal: 15,
    paddingTop: 12,
    paddingBottom: 10,
    backgroundColor: '#151515',
  },

  headerTop: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },

  title: {
    color: '#ffffff',
    fontSize: 22,
    fontWeight: 'bold',
  },

  time: {
    color: '#ffffff',
    fontSize: 18,
    fontWeight: 'bold',
  },

  statsRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 8,
    gap: 20,
  },

  counter: {
    color: '#dddddd',
    fontSize: 16,
  },

  speed: {
    color: '#bbbbbb',
    fontSize: 15,
  },

  status: {
    color: '#aaaaaa',
    fontSize: 14,
    marginTop: 7,
  },

  controls: {
    backgroundColor: '#151515',
    padding: 10,
    flexDirection: 'row',
    gap: 8,
  },

  controlButton: {
    flex: 1,
    backgroundColor: '#333333',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
    justifyContent: 'center',
  },

  dangerButton: {
    backgroundColor: '#552222',
  },

  controlButtonText: {
    color: '#ffffff',
    fontSize: 13,
    fontWeight: 'bold',
  },
});
