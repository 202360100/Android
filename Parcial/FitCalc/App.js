import React from "react";
import { useState } from "react";
import { Text, StyleSheet, Button, View, SafeAreaView, TextInput } from "react-native";
import CustomModal from "./componentes/CustomModal"

export default function App(){
    const [ modalVisible, setModalVisible ] = useState(false);
    const [peso, setPeso] = useState("")
    const [altura, setAltura] = useState("")
    const [IMC, setIMC] = useState("")
    
    const Presionado = () => {
        setModalVisible(true)
        setIMC(peso / (altura * altura))
    }

    let mensaje = ""

    if ( IMC <= 20 ) {
      mensaje = "Estás flaquito hermano, ve a mcdonalds"
    } else if ( IMC <= 27 ) {
      mensaje = "Tas bien manito"
    } else {
      mensaje = "Tas pasado de burgers hermano, come unas ensaladas y deja el lol"
    }


    return(
        <SafeAreaView style = { styles.container }>
            <View>
                <CustomModal
                    visible = { modalVisible }
                    onClose = { () => setModalVisible(false) }
                    contenido = { IMC }
                    mensaje = { mensaje }
                />
                <Text>Peso (kg)</Text>
                <TextInput
                    placeholder="70"
                    value = { peso }
                    onChangeText={ p => setPeso(p) }
                />
                <Text>Altura (m)</Text>
                <TextInput
                    placeholder="1.75"
                    value = { altura }
                    onChangeText={ a => setAltura(a) }
                />
                <Button
                    title = "Calcular IMC"
                    onPress = { Presionado }
                />
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
        alignItems: 'center',
        justifyContent: 'center',
    },
});