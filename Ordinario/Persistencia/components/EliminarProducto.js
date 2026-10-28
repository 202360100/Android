
import { useState } from 'react';
import { View, Text, TextInput, Button, Alert } from 'react-native';
import { supabase } from '../lib/supabase';

export default function EliminarProducto() {
  const [id, setId] = useState('');

  async function eliminar() {
    if (!id.trim()) {
      Alert.alert('Error', 'Ingresa el ID del producto');
      return;
    }

    const { data, error } = await supabase
      .from('productos')
      .delete()
      .eq('id', Number(id))
      .select();

    if (error) {
      Alert.alert('Error', error.message);
      return;
    }

    if (!data || data.length === 0) {
      Alert.alert('Aviso', 'No se encontró el producto o no tienes permiso para eliminarlo');
      return;
    }

    Alert.alert('Éxito', 'Producto eliminado');
    setId('');
  }

  return (
    <View style={{ padding: 15, gap: 8 }}>
      <Text>Eliminar producto</Text>

      <TextInput
        placeholder="ID del producto"
        value={id}
        onChangeText={setId}
        keyboardType="numeric"
      />

      <Button title="Eliminar" onPress={eliminar} />
    </View>
  );
}
