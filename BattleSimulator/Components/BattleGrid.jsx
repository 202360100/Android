import React, {
  useEffect,
  useRef,
  useState,
} from 'react';

import {
  Animated,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

import Unit from './Unit';

const GRID_SIZE = 50;
const BASE_CELL_SIZE = 30;

const MIN_ZOOM = 0.5;
const MAX_ZOOM = 2.5;
const ZOOM_STEP = 0.25;

function CombatEffect({
  x,
  y,
  cellSize,
}) {
  const flashOpacity = useRef(
    new Animated.Value(0)
  ).current;

  const flashScale = useRef(
    new Animated.Value(0.3)
  ).current;

  const particleProgress = useRef(
    new Animated.Value(0)
  ).current;

  useEffect(() => {
    flashOpacity.setValue(0);
    flashScale.setValue(0.3);
    particleProgress.setValue(0);

    Animated.parallel([
      Animated.sequence([
        Animated.timing(
          flashOpacity,
          {
            toValue: 1,
            duration: 60,
            useNativeDriver: true,
          }
        ),
        Animated.timing(
          flashOpacity,
          {
            toValue: 0,
            duration: 220,
            useNativeDriver: true,
          }
        ),
      ]),

      Animated.timing(
        flashScale,
        {
          toValue: 1.5,
          duration: 280,
          useNativeDriver: true,
        }
      ),

      Animated.timing(
        particleProgress,
        {
          toValue: 1,
          duration: 300,
          useNativeDriver: true,
        }
      ),
    ]).start();
  }, []);

  const particle1X =
    particleProgress.interpolate({
      inputRange: [0, 1],
      outputRange: [0, -cellSize * 0.45],
    });

  const particle1Y =
    particleProgress.interpolate({
      inputRange: [0, 1],
      outputRange: [0, -cellSize * 0.35],
    });

  const particle2X =
    particleProgress.interpolate({
      inputRange: [0, 1],
      outputRange: [0, cellSize * 0.45],
    });

  const particle2Y =
    particleProgress.interpolate({
      inputRange: [0, 1],
      outputRange: [0, -cellSize * 0.25],
    });

  const particle3X =
    particleProgress.interpolate({
      inputRange: [0, 1],
      outputRange: [0, -cellSize * 0.4],
    });

  const particle3Y =
    particleProgress.interpolate({
      inputRange: [0, 1],
      outputRange: [0, cellSize * 0.4],
    });

  const particle4X =
    particleProgress.interpolate({
      inputRange: [0, 1],
      outputRange: [0, cellSize * 0.35],
    });

  const particle4Y =
    particleProgress.interpolate({
      inputRange: [0, 1],
      outputRange: [0, cellSize * 0.45],
    });

  return (
    <View
      pointerEvents="none"
      style={[
        styles.combatEffect,
        {
          width: cellSize,
          height: cellSize,
          left: x * cellSize,
          top: y * cellSize,
        },
      ]}
    >
      <Animated.View
        style={[
          styles.combatFlash,
          {
            opacity: flashOpacity,
            transform: [
              {
                scale: flashScale,
              },
            ],
          },
        ]}
      />

      <Animated.View
        style={[
          styles.particle,
          {
            opacity: flashOpacity,
            transform: [
              { translateX: particle1X },
              { translateY: particle1Y },
            ],
          },
        ]}
      />

      <Animated.View
        style={[
          styles.particle,
          {
            opacity: flashOpacity,
            transform: [
              { translateX: particle2X },
              { translateY: particle2Y },
            ],
          },
        ]}
      />

      <Animated.View
        style={[
          styles.particle,
          {
            opacity: flashOpacity,
            transform: [
              { translateX: particle3X },
              { translateY: particle3Y },
            ],
          },
        ]}
      />

      <Animated.View
        style={[
          styles.particle,
          {
            opacity: flashOpacity,
            transform: [
              { translateX: particle4X },
              { translateY: particle4Y },
            ],
          },
        ]}
      />
    </View>
  );
}

export default function BattleGrid({ units, combatPositions = [], }) {
  const [zoom, setZoom] = useState(1);

  const [
    displayedUnits,
    setDisplayedUnits,
  ] = useState(units);

  useEffect(() => {
  setDisplayedUnits((currentUnits) => {
    const currentIds =
      new Set(
        units.map(
          (unit) => unit.id
        )
      );

    const newUnits = [
      ...units,
    ];

      /*
       * Conservamos temporalmente las
       * unidades que desaparecieron para
       * poder reproducir su animación.
       */
      for (const oldUnit of currentUnits) {
        if (
          !currentIds.has(oldUnit.id) &&
          !newUnits.some(
            (unit) =>
              unit.id === oldUnit.id
          )
        ) {
          newUnits.push({
            ...oldUnit,
            isDead: true,
          });
        }
      }

      return newUnits;
    });
  }, [units]);

  const cellSize =
    BASE_CELL_SIZE * zoom;

  const boardSize =
    GRID_SIZE * cellSize;

  const decreaseZoom = () => {
    setZoom((currentZoom) => {
      return Math.max(
        MIN_ZOOM,
        currentZoom - ZOOM_STEP
      );
    });
  };
  

  const increaseZoom = () => {
    setZoom((currentZoom) => {
      return Math.min(
        MAX_ZOOM,
        currentZoom + ZOOM_STEP
      );
    });
  };

  const removeDeadUnit = (unitId) => {
    setDisplayedUnits(
      (currentUnits) =>
        currentUnits.filter(
          (unit) =>
            unit.id !== unitId
        )
    );
  };

  return (
    <View style={styles.container}>

      {/* TABLERO */}

      <View style={styles.boardContainer}>

        <ScrollView
          horizontal
          showsHorizontalScrollIndicator
        >

          <ScrollView
            showsVerticalScrollIndicator
          >

            <View
              style={[
                styles.board,
                {
                  width: boardSize,
                  height: boardSize,
                },
              ]}
            >

              {/* CASILLAS */}

              {Array.from({
                length:
                  GRID_SIZE * GRID_SIZE,
              }).map((_, index) => {

                const x =
                  index % GRID_SIZE;

                const y =
                  Math.floor(
                    index / GRID_SIZE
                  );

                return (
                  <View
                    key={`${x}-${y}`}
                    style={[
                      styles.cell,
                      {
                        left:
                          x * cellSize,

                        top:
                          y * cellSize,

                        width:
                          cellSize,

                        height:
                          cellSize,
                      },
                    ]}
                  />
                );
              })}

              {/* UNIDADES */}

              {displayedUnits.map((unit) => (
                <Unit
                key={unit.id}
                type={unit.type}
                size={cellSize}
                x={unit.x}
                y={unit.y}
                cellSize={cellSize}
                isDead={unit.isDead}
                onDeathAnimationEnd={() => {
                  removeDeadUnit(unit.id);
                }}
                />
              ))}

              {combatPositions.map((position) => (
                <CombatEffect
                  key={position.key}
                  x={position.x}
                  y={position.y}
                  cellSize={cellSize}
                />
              ))}

            </View>

          </ScrollView>

        </ScrollView>

      </View>

      {/* CONTROLES DE ZOOM */}

      <View style={styles.zoomControls}>

        <TouchableOpacity
          style={[
            styles.zoomButton,
            zoom <= MIN_ZOOM &&
              styles.disabledButton,
          ]}
          onPress={decreaseZoom}
          disabled={zoom <= MIN_ZOOM}
        >
          <Text style={styles.zoomButtonText}>
            −
          </Text>
        </TouchableOpacity>

        <View style={styles.zoomDisplay}>

          <Text style={styles.zoomText}>
            {Math.round(zoom * 100)}%
          </Text>

        </View>

        <TouchableOpacity
          style={[
            styles.zoomButton,
            zoom >= MAX_ZOOM &&
              styles.disabledButton,
          ]}
          onPress={increaseZoom}
          disabled={zoom >= MAX_ZOOM}
        >
          <Text style={styles.zoomButtonText}>
            +
          </Text>
        </TouchableOpacity>

      </View>

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#222',
  },

  boardContainer: {
    flex: 1,
  },

  board: {
    position: 'relative',
    backgroundColor: '#5f7048',
  },

  cell: {
    position: 'absolute',
    borderWidth: 0.5,
    borderColor: '#71845a',
  },

  zoomControls: {
    height: 70,
    backgroundColor: '#151515',

    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',

    gap: 15,
  },

  zoomButton: {
    width: 45,
    height: 45,

    borderRadius: 12,

    backgroundColor: '#333',

    alignItems: 'center',
    justifyContent: 'center',
  },

  disabledButton: {
    opacity: 0.35,
  },

  zoomButtonText: {
    color: '#ffffff',
    fontSize: 28,
    fontWeight: 'bold',
    lineHeight: 30,
  },

  zoomDisplay: {
    minWidth: 70,
    alignItems: 'center',
  },

  zoomText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: 'bold',
  },

  combatEffect: {
    position: 'absolute',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 20,
  },

  combatFlash: {
    width: 14,
    height: 14,
    borderRadius: 7,
    backgroundColor: '#ffffff',
  },

  particle: {
    position: 'absolute',
    width: 5,
    height: 5,
    borderRadius: 3,
    backgroundColor: '#ffffff',
  },
});
