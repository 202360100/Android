const express = require('express');
const { MongoClient } = require('mongodb');
const cors = require("cors");

const dns = require('node:dns/promises');
dns.setServers(["1.1.1.1", "8.8.8.8"]);

const app = express();
app.use(cors());

const uri = "mongodb://MarioMongo:CristobalColon2005Mario@ac-flztvrq-shard-00-00.ner595f.mongodb.net:27017,ac-flztvrq-shard-00-01.ner595f.mongodb.net:27017,ac-flztvrq-shard-00-02.ner595f.mongodb.net:27017/?ssl=true&replicaSet=atlas-gd66l7-shard-0&authSource=admin&appName=MongoCluster"
const client = new MongoClient(uri);

async function main(){
    await client.connect();
    const db = client.db("sample_mflix");
    const movies = db.collection("movies");

    app.get("/movies", async(req, res)=>{
        try {
            // CORRECCIÓN: Pasamos el filtro vacío {} y las opciones de proyección juntas
            const data = await movies
            .find({}, { projection: { poster: 1, title: 1, fullplot: 1 } })
            .limit(60)
            .toArray();
            
            res.json(data);
        } catch (error) {
            console.error("Error en la consulta:", error);
            res.status(500).json({ error: "Error al obtener datos" });
        }
    });

    app.listen(4000, '0.0.0.0', () => {
        console.log('Server running on port 4000');
    });
}

main().catch(console.error);