import { useEffect, useRef } from "react";
import { Animated, View, Text, Image } from "react-native";

export default function App() {

  const opacity = useRef(
    new Animated.Value(0)
  ).current;

  const position = useRef(
    new Animated.Value(-250)
  ).current;

  const scale = useRef(
    new Animated.Value(0)
  ).current;

  useEffect(() => {

    {/*Animated.timing(
      opacity,
      {
        toValue: 1,
        duration: 5000,
        useNativeDriver: true
      }
    ).start();

    Animated.timing(
      position,
      {
        toValue: 0,
        duration: 5000,
        useNativeDriver: true
      }
    ).start();

    Animated.timing(
      scale, 
      {
        toValue: 1,
        duration: 5000,
        useNativeDriver: true
      }
    ).start();*/}

    Animated.parallel([
      Animated.timing(
        opacity,
        {
          toValue: 1,
          duration: 5000,
          useNativeDriver: true
        }
      ),

      Animated.timing(
        position,
        {
          toValue: 0,
          duration: 5000,
          useNativeDriver: true
        }
      ),

      Animated.timing(
        scale, 
        {
          toValue: 1,
          duration: 5000,
          useNativeDriver: true
        }
      ),

    ]).start();
  
  }, []);

  return (

    <View
      style = {{
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: 'white'
      }}
    >
      <Animated.Text
        style = {{
          fontSize: 120,
          transform: [
            {translateX: position},
            {scale: scale}
          ],
          opacity: opacity
        }}
      >
        <Image
          source = {require("./assets/Android.png")}
          style = {{
            width: 372,
            height: 366
          }}
        />
      </Animated.Text>

    </View>
  );


}