import { ScrollView, Text } from 'react-native';

import AgregarProducto from './components/AgregarProducto';
import ConsultarProductos from './components/ConsultarProductos';
import ActualizarProducto from './components/ActualizarProducto';
import EliminarProducto from './components/EliminarProducto';

export default function App() {
  return (
    <ScrollView contentContainerStyle={{ padding: 20, paddingTop: 50, backgroundColor: "white" }}>
      <Text style={{ fontSize: 24, fontWeight: 'bold' }}>
        CRUD de productos
      </Text>

      <AgregarProducto />
      <ConsultarProductos />
      <ActualizarProducto />
      <EliminarProducto />
    </ScrollView>
  );
}
