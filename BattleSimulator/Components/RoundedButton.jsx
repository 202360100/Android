import React from 'react';
import {
  Pressable,
  StyleSheet,
  Text,
} from 'react-native';

export default function RoundedButton({
  title,
  onPress,
}) {
  return (
    <Pressable
      style={({ pressed }) => [
        styles.button,
        pressed && styles.pressed,
      ]}
      onPress={onPress}
    >
      <Text style={styles.text}>
        {title}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    backgroundColor: '#252525',
    borderRadius: 14,
    paddingVertical: 15,
    paddingHorizontal: 20,
    alignItems: 'center',
    marginTop: 15,
  },

  pressed: {
    opacity: 0.7,
  },

  text: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
