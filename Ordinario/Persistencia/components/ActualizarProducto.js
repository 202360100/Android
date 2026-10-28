
import { useState } from 'react';
import { View, Text, TextInput, Button, Alert } from 'react-native';
import { supabase } from '../lib/supabase';

export default function ActualizarProducto() {
  const [id, setId] = useState('');
  const [producto, setProducto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');

  async function actualizar() {
    if (
      !id.trim() ||
      !producto.trim() ||
      !descripcion.trim() ||
      !precio.trim()
    ) {
      Alert.alert('Error', 'Completa todos los campos');
      return;
    }

    const { data, error } = await supabase
      .from('productos')
      .update({
        producto: producto.trim(),
        descripción: descripcion.trim(),
        precio: Number(precio),
      })
      .eq('id', Number(id))
      .select();

    if (error) {
      Alert.alert('Error', error.message);
      return;
    }

    if (!data || data.length === 0) {
      Alert.alert('Aviso', 'No se encontró el producto o no tienes permiso para actualizarlo');
      return;
    }

    Alert.alert('Éxito', 'Producto actualizado');
  }

  return (
    <View style={{ padding: 15, gap: 8 }}>
      <Text>Actualizar producto</Text>

      <TextInput
        placeholder="ID del producto"
        value={id}
        onChangeText={setId}
        keyboardType="numeric"
      />

      <TextInput
        placeholder="Nuevo nombre"
        value={producto}
        onChangeText={setProducto}
      />

      <TextInput
        placeholder="Nueva descripción"
        value={descripcion}
        onChangeText={setDescripcion}
      />

      <TextInput
        placeholder="Nuevo precio"
        value={precio}
        onChangeText={setPrecio}
        keyboardType="decimal-pad"
      />

      <Button title="Actualizar" onPress={actualizar} />
    </View>
  );
}
