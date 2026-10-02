import React, {
  useRef,
  useState,
} from 'react';

import {
  Animated,
  StyleSheet,
  View,
} from 'react-native';

import {
  PinchGestureHandler,
  State,
} from 'react-native-gesture-handler';

import Unit from './Unit';

const GRID_SIZE = 50;
const CELL_SIZE = 30;

export default function BattleGrid({
  units,
}) {
  const scale = useRef(
    new Animated.Value(1)
  ).current;

  const lastScale = useRef(1);

  const [zoom, setZoom] = useState(1);

  const onPinchGestureEvent = Animated.event(
    [
      {
        nativeEvent: {
          scale,
        },
      },
    ],
    {
      useNativeDriver: true,
    }
  );

  const onPinchStateChange = (event) => {
    if (
      event.nativeEvent.oldState === State.ACTIVE
    ) {
      let newScale =
        lastScale.current *
        event.nativeEvent.scale;

      newScale = Math.max(
        0.5,
        Math.min(newScale, 2.5)
      );

      lastScale.current = newScale;

      scale.setValue(newScale);

      setZoom(newScale);
    }
  };

  const boardSize =
    GRID_SIZE * CELL_SIZE;

  return (
    <View style={styles.container}>

      <Animated.ScrollView
        horizontal
        contentContainerStyle={{
          width: boardSize * zoom,
        }}
        showsHorizontalScrollIndicator
      >

        <Animated.ScrollView
          contentContainerStyle={{
            height: boardSize * zoom,
          }}
          showsVerticalScrollIndicator
        >

          <PinchGestureHandler
            onGestureEvent={
              onPinchGestureEvent
            }
            onHandlerStateChange={
              onPinchStateChange
            }
          >

            <Animated.View
              style={[
                styles.board,
                {
                  width: boardSize,
                  height: boardSize,
                  transform: [
                    {
                      scale,
                    },
                  ],
                },
              ]}
            >

              {Array.from(
                { length: GRID_SIZE * GRID_SIZE }
              ).map((_, index) => {

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
                          x * CELL_SIZE,

                        top:
                          y * CELL_SIZE,

                        width:
                          CELL_SIZE,

                        height:
                          CELL_SIZE,
                      },
                    ]}
                  />
                );
              })}

              {units.map((unit) => (
                <View
                  key={unit.id}
                  style={{
                    position: 'absolute',

                    left:
                      unit.x * CELL_SIZE,

                    top:
                      unit.y * CELL_SIZE,

                    width: CELL_SIZE,

                    height: CELL_SIZE,
                  }}
                >
                  <Unit
                    type={unit.type}
                    size={CELL_SIZE}
                  />
                </View>
              ))}

            </Animated.View>

          </PinchGestureHandler>

        </Animated.ScrollView>

      </Animated.ScrollView>

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    overflow: 'hidden',
  },

  board: {
    position: 'relative',
    backgroundColor: '#e8e8e8',
  },

  cell: {
    position: 'absolute',
    borderWidth: 0.5,
    borderColor: '#cccccc',
  },
});
