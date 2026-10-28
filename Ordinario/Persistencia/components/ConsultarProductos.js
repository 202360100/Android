
import { useState } from 'react';
import { View, Text, Button, Alert, ScrollView } from 'react-native';
import { supabase } from '../lib/supabase';

export default function ConsultarProductos() {
  const [productos, setProductos] = useState([]);

  async function consultar() {
    const { data, error } = await supabase
      .from('productos')
      .select('*');

    if (error) {
      Alert.alert('Error', error.message);
      return;
    }

    setProductos(data);
  }

  return (
    <View style={{ padding: 15, gap: 8 }}>
      <Text>Consultar productos</Text>

      <Button title="Consultar" onPress={consultar} />

      <ScrollView style={{ maxHeight: 250 }}>
        {productos.map((item) => (
          <View key={item.id} style={{ paddingVertical: 8 }}>
            <Text>ID: {item.id}</Text>
            <Text>Producto: {item.producto}</Text>
            <Text>Descripción: {item.descripción}</Text>
            <Text>Precio: {item.precio}</Text>
          </View>
        ))}
      </ScrollView>
    </View>
  );
}
