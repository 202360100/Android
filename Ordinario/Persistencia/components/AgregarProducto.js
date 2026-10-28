
import { useState } from 'react';
import { View, Text, TextInput, Button, Alert } from 'react-native';
import { supabase } from '../lib/supabase';

export default function AgregarProducto() {
  const [producto, setProducto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');

  async function agregar() {
    if (!producto.trim() || !descripcion.trim() || !precio.trim()) {
      Alert.alert('Error', 'Completa todos los campos');
      return;
    }

    const { error } = await supabase.from('productos').insert({
      producto: producto.trim(),
      descripción: descripcion.trim(),
      precio: Number(precio),
    });

    if (error) {
      Alert.alert('Error', error.message);
      return;
    }

    Alert.alert('Éxito', 'Producto agregado');
    setProducto('');
    setDescripcion('');
    setPrecio('');
  }

  return (
    <View style={{ padding: 15, gap: 8 }}>
      <Text>Agregar producto</Text>

      <TextInput
        placeholder="Nombre del producto"
        value={producto}
        onChangeText={setProducto}
      />

      <TextInput
        placeholder="Descripción"
        value={descripcion}
        onChangeText={setDescripcion}
      />

      <TextInput
        placeholder="Precio"
        value={precio}
        onChangeText={setPrecio}
        keyboardType="decimal-pad"
      />

      <Button title="Agregar" onPress={agregar} />
    </View>
  );
}
