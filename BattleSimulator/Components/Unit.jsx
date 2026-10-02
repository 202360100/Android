import React from 'react';
import {
  Image,
  StyleSheet,
} from 'react-native';

const knightImage = require('../assets/images/knight.png');
const orcImage = require('../assets/images/orc.png');

export default function Unit({ type, size }) {
  const imageSource = type === 'knight'
    ? knightImage
    : orcImage;

  return (
    <Image
      source={imageSource}
      style={[
        styles.unit,
        {
          width: size,
          height: size,
        },
      ]}
      resizeMode="contain"
    />
  );
}

const styles = StyleSheet.create({
  unit: {
    position: 'absolute',
  },
});
