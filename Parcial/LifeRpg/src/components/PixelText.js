import React from 'react';
import { Text } from 'react-native';
import colors from '../theme/colors';

export default function PixelText({
  children,
  size = 16,
  color = colors.white,
  bold = false,
  center = false,
  style,
}) {
  return (
    <Text
      style={[
        {
          color,
          fontSize: size,
          fontWeight: bold ? '900' : '600',
          textAlign: center ? 'center' : 'left',
          letterSpacing: 0.5,
        },
        style,
      ]}
    >
      {children}
    </Text>
  );
}