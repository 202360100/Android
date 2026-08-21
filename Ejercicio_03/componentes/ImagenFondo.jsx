import { View, ImageBackground, StyleSheet, Dimensions, Image, Text } from "react-native";

export default function ImagenFondo(){
    return(
        <View>
            <ImageBackground 
            style = {styles.fondo}
            source = {require('../assets/Explosion.jpg')}
            >
                <View style = {styles.container}>
                    <Text style = {styles.Texto}>
                        Amor
                    </Text>
                    <Image
                        style = {styles.foto}
                        source={require('../assets/Velkos.png')}
                    />
                </View>
            </ImageBackground>
        </View>
    );
}

const styles = StyleSheet.create({
    fondo: {
        width: Dimensions.get("window").width,
        height: Dimensions.get("window").height
    },
    foto: {
        width: 400,
        height: 400,
        opacity: 0.9,
        resizeMode: "contain"
    },
    container: {
        flex: 1,
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "rgb(50,10,0,0.2)"
    },
    Texto: {
        color: "#fff",
        backgroundColor: "rgb(0,0,0,0.6)",
        width: Dimensions.get("window").width,
        textAlign: "center",
        fontSize: 45,
        fontFamily: "Times New Roman",
    }
});