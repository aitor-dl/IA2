package com.example.demo;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;
import org.json.simple.JSONArray;

public class Astar {
    private Graph<String, String> estaciones = new DefaultUndirectedGraph<>(SupplierUtil.createStringSupplier(),
            SupplierUtil.createStringSupplier(), true);
    private Map<String, Double> heuristica = new HashMap<>(); /* Valor estimado desde cada nodo hasta el nodo destino */
    private String origen;
    private String destino;
    private JSONArray coords;
    private JSONArray aristas;

    public Astar(String origen, String destino, String peso) throws IOException, ParseException {
        this.origen = origen;
        this.destino = destino;
        JSONParser parser = new JSONParser();
        InputStream is = getClass().getResourceAsStream("/json/coordenadas.json");
        Reader rd = new InputStreamReader(is, "UTF-8");
        coords = (JSONArray) parser.parse(rd);
        is = getClass().getResourceAsStream("/json/aristas.json");
        rd = new InputStreamReader(is, "UTF-8");
        aristas = (JSONArray) parser.parse(rd);
        /* inicializar estructuras de datos */
        Set<String> repes = new HashSet<String>();
        for (int i = 0; i < aristas.size(); i++) {
            JSONObject jobj = (JSONObject) aristas.get(i);
            String origen1 = (String) jobj.get("Origen");
            String destino1 = (String) jobj.get("Destino");
            if (!repes.contains(origen1)) {

                estaciones.addVertex(origen1);
                repes.add(origen1);

            }
            if (!repes.contains(destino1)) {

                estaciones.addVertex(destino1);
                repes.add(destino1);

            }

            estaciones.addEdge(origen1, destino1);
            JSONObject jpeso = (JSONObject) jobj.get("Peso");
            Double p = ((Number) jpeso.get(peso)).doubleValue();
            estaciones.setEdgeWeight(origen1, destino1, p);
        }
        initHeuristica();
    }

    public void initHeuristica() throws IllegalArgumentException {
        boolean encontrado = false;
        JSONObject jdestino = null;
        for (int i = 0; i < coords.size() && !encontrado; i++) {
            jdestino = (JSONObject) coords.get(i);
            encontrado = ((String) jdestino.get("Name")).equals(destino);
        }
        if (jdestino == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < coords.size(); i++) {
            JSONObject jorigen = (JSONObject) coords.get(i);
            if (!((String) jorigen.get("Name")).equals(destino)) {
                double origenLat = ((Number) jorigen.get("Latitude")).doubleValue();
                double origenLong = ((Number) jorigen.get("Longitude")).doubleValue();
                double destLat = ((Number) jdestino.get("Latitude")).doubleValue();
                double destLong = ((Number) jdestino.get("Longitude")).doubleValue();
                double dLat = Math.abs(origenLat - destLat);
                double dLon = Math.abs(origenLong - destLong);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(destLat)
                                * Math.cos(origenLat);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                heuristica.put((String) jorigen.get("Name"), 6371 * c);
            } else {
                heuristica.put((String) jorigen.get("Name"), 0.0);
            }
        }
    }

    public String algoritmo() {
        Map<String, Double> visitados = new HashMap<>();
        Map<String, Double> listaAbierta = new HashMap<>();
        String camino = origen;
        Double pesoCamino = Double.MAX_VALUE;
        String nodo = origen;
        listaAbierta.put(nodo, heuristica.get(nodo));
        do {
            camino = menorCamino(listaAbierta.entrySet());
            System.out.println("-------------------------------------------------------------");
            System.out.println("camino actual: " + camino);
            pesoCamino = calcularPeso(camino);
            String[] caminoArray = camino.split("/");
            nodo = caminoArray[caminoArray.length - 1];
            System.out.println("nodo actual: " + nodo);
            visitados.put(nodo, pesoCamino + heuristica.get(nodo));
            if (!nodo.equals(destino)) {
                actualizarEstructuras(listaAbierta, camino, pesoCamino);
                System.out.println("ListaAbierta: " + listaAbierta.toString());
                System.out.println("Visitados: " + visitados.toString());
            }
        } while (!nodo.equals(destino));
        return camino;
    }

    public double calcularPeso(String res) {
        double peso = 0;
        String[] camino = res.split("/");
        for (int i = 0; i < camino.length - 1; i++) {
            Set<String> edge = estaciones.getAllEdges(camino[i], camino[i + 1]);
            peso += estaciones.getEdgeWeight(edge.iterator().next());
        }
        return peso;
    }

    private String menorCamino(Set<Entry<String, Double>> entrySet) {
        String menorCamino = "";
        Double menorValor = Double.MAX_VALUE;
        Iterator<Entry<String, Double>> it = entrySet.iterator();
        while (it.hasNext()) {
            Entry<String, Double> entry = it.next();
            if (menorValor > entry.getValue()) {
                menorValor = entry.getValue();
                menorCamino = entry.getKey();
            }
        }
        return menorCamino;
    }

    private void actualizarEstructuras(Map<String, Double> listaAbierta, String camino, Double peso) {
        String[] caminoArray = camino.split("/");
        String nodo = caminoArray[caminoArray.length - 1];
        Set<String> edges = estaciones.edgesOf(nodo);
        for (String edge : edges) {
            System.out.println("edge:" + edge);
            String nodoFuente = estaciones.getEdgeSource(edge);
            String nodoObjetivo = estaciones.getEdgeTarget(edge);
            nodoObjetivo = nodoObjetivo.equals(nodo) ? nodoFuente : nodoObjetivo;
            System.out.println("nodo de la arista: " + nodoObjetivo);
            if (!contieneNodo(caminoArray, nodoObjetivo)) {
                System.out.println("NO CONTIENE NODO OBJETIVO");
                String cActualizado = camino;
                cActualizado = camino + "/" + nodoObjetivo;
                System.out.println("camino actualizado: " + cActualizado);
                double pActualizado = peso + estaciones.getEdgeWeight(edge) + heuristica.get(nodoObjetivo);
                boolean contieneCamino = contieneCamino(listaAbierta, cActualizado);
                System.out.println("contieneCamino: " + contieneCamino);
                listaAbierta.remove(camino);
                if (!contieneCamino || pActualizado < listaAbierta.get(cActualizado)) {
                    listaAbierta.put(cActualizado, pActualizado);
                }
            }
        }
    }

    // array = [Vaux,Laurent]
    // nodoObjetivo = Vaux
    // encontrado = true

    private boolean contieneNodo(String[] caminoArray, String nodoObjetivo) {
        boolean encontrado = false;
        for (int i = 0; i < caminoArray.length && !encontrado; i++) {
            encontrado = caminoArray[i].equals(nodoObjetivo);
        }
        return encontrado;
    }

    // listaAbierta = {Vaux/Laurent}
    // camino = Vaux/Laurent
    // cArrayList = [Vaux]
    // caminoArray = [Vaux,Laurent]

    private boolean contieneCamino(Map<String, Double> listaAbierta, String camino) {
        Set<String> keySet = listaAbierta.keySet();
        Iterator<String> it = keySet.iterator();
        String[] caminoArray = camino.split("/");
        boolean encontrado = true;
        while (it.hasNext() && encontrado) {
            String[] cArrayList = it.next().split("/");
            encontrado = cArrayList.length == caminoArray.length;
            for (int i = 0; i < cArrayList.length && encontrado; i++) {
                String nodo = caminoArray[i];
                encontrado = false;
                for (int j = 0; j < caminoArray.length && !encontrado; j++) {
                    encontrado = nodo.equals(caminoArray[j]);
                }
            }
        }
        return encontrado;
    }

}
