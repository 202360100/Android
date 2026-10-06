import React, {
  useEffect,
  useRef,
} from 'react';

import {
  Animated,
  Image,
  StyleSheet,
} from 'react-native';

const knightImage =
  require('../assets/images/knight.png');

const orcImage =
  require('../assets/images/orc.png');

export default function Unit({
  type,
  size,
  x,
  y,
  cellSize,
  isDead = false,
  onDeathAnimationEnd,
}) {
  const position = useRef(
    new Animated.ValueXY({
      x: x * cellSize,
      y: y * cellSize,
    })
  ).current;

  const opacity =
    useRef(
      new Animated.Value(1)
    ).current;

  const scale =
    useRef(
      new Animated.Value(1)
    ).current;

  const imageSource =
    type === 'knight'
      ? knightImage
      : orcImage;

  /*
   * Animación de movimiento.
   */
  useEffect(() => {
    if (isDead) {
      return;
    }

    Animated.timing(position, {
      toValue: {
        x: x * cellSize,
        y: y * cellSize,
      },
      duration: 180,
      useNativeDriver: true,
    }).start();
  }, [
    x,
    y,
    cellSize,
    isDead,
    position,
  ]);

  /*
   * Animación de muerte.
   */
  useEffect(() => {
    if (!isDead) {
      return;
    }

    Animated.parallel([
      Animated.timing(
        opacity,
        {
          toValue: 0,
          duration: 300,
          useNativeDriver: true,
        }
      ),

      Animated.timing(
        scale,
        {
          toValue: 0.2,
          duration: 300,
          useNativeDriver: true,
        }
      ),
    ]).start(() => {
      if (onDeathAnimationEnd) {
        onDeathAnimationEnd();
      }
    });
  }, [
    isDead,
    opacity,
    scale,
    onDeathAnimationEnd,
  ]);

  return (
    <Animated.View
      style={[
        styles.unit,
        {
          width: cellSize,
          height: cellSize,
          opacity,
          transform: [
            ...position.getTranslateTransform(),
            {
              scale,
            },
          ],
        },
      ]}
    >
      <Image
        source={imageSource}
        style={{
          width: size,
          height: size,
        }}
        resizeMode="contain"
      />
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  unit: {
    position: 'absolute',
    left: 0,
    top: 0,
  },
});
