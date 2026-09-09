import React from 'react';
import { View, StyleSheet } from 'react-native';
import colors from '../theme/colors';

export default function PixelCard({ children, style }) {
  return (
    <View style={[styles.card, style]}>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.panel,

    borderWidth: 3,
    borderColor: colors.border,

    borderTopColor: colors.borderLight,
    borderLeftColor: colors.borderLight,

    padding: 16,

    shadowColor: '#000',
    shadowOffset: {
      width: 4,
      height: 4,
    },
    shadowOpacity: 0.5,
    shadowRadius: 0,

    elevation: 5,
  },
});