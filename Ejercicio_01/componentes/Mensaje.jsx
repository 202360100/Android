import { View, Text, StyleSheet } from "react-native";

const mitexto = "Mensaje desde un objeto"
const num = 123

//const double = (n) => {n * 2}

export default function Mensaje( props ){
    return(
        <View>
            <Text style = {estilos.formato_mensaje}>{ props.titulo }</Text>
            <Text style = {estilos.formato_mensaje}>{ props.numero }</Text>
        </View>
    );
}

const estilos = StyleSheet.create({
    formato_mensaje: {
        backgroundColor: 'black',
        color: 'green',
        fontSize: '2em'
    },
})