import React from 'react';
import {
  TouchableOpacity,
  View,
  StyleSheet,
} from 'react-native';

import PixelText from './PixelText';
import colors from '../theme/colors';

export default function PixelButton({
  title,
  onPress,
  color = colors.goldDark,
  disabled = false,
}) {
  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={disabled}
      activeOpacity={0.75}
      style={[
        styles.container,
        {
          opacity: disabled ? 0.45 : 1,
        },
      ]}
    >
      <View
        style={[
          styles.button,
          {
            backgroundColor: color,
          },
        ]}
      >
        <PixelText
          bold
          center
          color={colors.white}
        >
          {title}
        </PixelText>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    marginVertical: 6,
  },

  button: {
    minHeight: 48,

    paddingHorizontal: 20,

    justifyContent: 'center',
    alignItems: 'center',

    borderWidth: 3,
    borderColor: colors.border,

    borderTopColor: colors.white,
    borderLeftColor: colors.white,
  },
});