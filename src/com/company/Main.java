package com.company;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.io.File;
import java.io.FileWriter;

import static java.nio.file.StandardOpenOption.APPEND;

public class Main {

    public static void main(String[] args) {

        try {
            final Path RUTA = Paths.get("docs/BTC-USD.csv");
            lecturaSecuencial(RUTA);
            double promedio = promedio(RUTA);
            System.out.println("El promedio de los precios de apertura es: " + promedio);
            System.out.println("El La desviacion estandar de apertura es: " + desviacionEstandar(promedio,RUTA));
            extremosBitcoin(RUTA, promedio);

        }catch (Exception e){
            System.out.println("Hubo un error al acceder el archivo: " + e.getMessage());
        }
    }
/* El metodo lecturaSecuencial crea un documento con el metodo crear archivo, luego lee linea por line y separa cada
dato en comas (proceso que se repotira en los demas metodos  por peticion del documento en las partes que dice leer linea a linea). Luego por cada linea separa los datos
por, y utiliza solo los datos necesarios y los evalua para en un nuevo archivo escribir la fecha y el concepto de cierre
* */

    public static void lecturaSecuencial(Path RUTA){
        Path archivoSecuencial = crearArchivo(1);
        List<String> lineasArchivo;
        try{
            lineasArchivo = Files.readAllLines(RUTA);
            String[] datos;
            for (int i = 1; i < lineasArchivo.size(); i++) {
                datos = lineasArchivo.get(i).split(",");
                String datoLinea = "\r"+datos[0]+"\t"+concepto(datos[4]);
                byte[] filaActual = datoLinea.getBytes();
                FileChannel canal = FileChannel.open(archivoSecuencial, APPEND);
                ByteBuffer buffer = ByteBuffer.wrap(filaActual);
                while (buffer.hasRemaining()) {
                    canal.write(buffer);
                }
            }
        }catch (IOException e){
            System.out.println("Hubo un error: " + e.getMessage());
        }catch (NumberFormatException e){
            System.out.println("Hubo un error: " + e.getMessage());
        }
    }

    /* El metodo crearArchivo crea un documento con los titulos de fecha y concepto, para que el metodo lecturaSecuencial escriba
    los datos correspondientes, se deja expresado la parte para la lectura aleatoria, pero por la condición de los datos este metodo
    no puede realizarce.
* */
    public static Path crearArchivo(int llamado){// La variable llamado tiene 2 valores, esto para saber que funcion lo llama y asi crear el archivo correspondiente
        String ruta = "";
        String columnas = "Fecha\tConcepto";
        byte[] bytesColumnas = columnas.getBytes();

        if(llamado == 1 ){
            ruta = "docs/secuencial.txt";
        }else{
            ruta = "docs/aleatorio.txt";
        }
        File file = new File(ruta);
        try {

            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), bytesColumnas);

        } catch (Exception e) {
            System.out.println("Hubo un error : " + e.getMessage());
        }

        return file.toPath();
    }

      /* El metodo concepto, evalua un valor y dependiendo de los rangos planteados por el ejercicio clasifica ese valor ente
      muy bajo, bajo, medio, alto y muy alto y entrega esta clasificación como string
* */

    public static String concepto(String numero) throws NumberFormatException{

        double concepto = Double.parseDouble(numero);

        if(concepto < 30000){
            return "MUY BAJO";
        }else if( concepto>= 30000  && concepto < 40000){
            return "BAJO";
        }else if( concepto>= 40000  && concepto < 50000){
            return "MEDIO";
        }else if( concepto>= 50000  && concepto < 60000){
            return "ALTO";
        }else{
            return "MUY ALTO";
        }

    }

        /* Lee todos los datos y calcula el promedio, teniendo en cuenta que la primera fila de los datos no hace parte de los mismos.
* */

    public static double promedio(Path RUTA){
        double promedio = 0;
        List<String> lineasArchivo;
        try{
            lineasArchivo = Files.readAllLines(RUTA);
            String[] datos;
            for (int i = 1; i < lineasArchivo.size(); i++) {
                datos = lineasArchivo.get(i).split(",");
                promedio += Double.parseDouble(datos[1]);
            }
            promedio = promedio/ lineasArchivo.size()-1;
        }catch (IOException e){
            System.out.println("Hubo un error: " + e.getMessage());
        }catch (NumberFormatException e){
            System.out.println("Hubo un error: " + e.getMessage());
        }
        return promedio;
    }

    /* Lee todos los datos y calcula la desviación estandar, teniendo en cuenta que la primera fila de los datos no hace parte de los mismos.
     * */

    public static double desviacionEstandar(double promedio, Path RUTA){
        double desviacion = 0;
        List<String> lineasArchivo;
        try{
            lineasArchivo = Files.readAllLines(RUTA);
            String[] datos;
            for (int i = 1; i < lineasArchivo.size(); i++) {
                datos = lineasArchivo.get(i).split(",");
                desviacion += Math.pow(Double.parseDouble(datos[1])-promedio,2);
            }
            desviacion= desviacion/(lineasArchivo.size()-1)-1;
        }catch (IOException e){
            System.out.println("Hubo un error: " + e.getMessage());
        }catch (NumberFormatException e){
            System.out.println("Hubo un error: " + e.getMessage());
        }
        return Math.sqrt(desviacion);
    }

    /* Lee todos los datos y por cada fila calcula cual es el valor maximo del bitcoin y el minimo, guardandolos en unas variables espicificas donde
    se compararan con cada fila, asi obteniendo estos valores y los imprime en consola
     * */

    public static void extremosBitcoin(Path RUTA,double promedio){
        String[] mayor= new String[2];
        String[] menor= new String[2];
        mayor[1] = "0";
        menor[1] = String.valueOf(promedio);

        List<String> lineasArchivo;
        try{
            lineasArchivo = Files.readAllLines(RUTA);
            String[] datos;
            for (int i = 1; i < lineasArchivo.size(); i++) {
                datos = lineasArchivo.get(i).split(",");
                for (int j = 1; j < datos.length-1 ; j++) {
                    if (Double.parseDouble(mayor[1]) < Double.parseDouble(datos[j])) {
                        mayor[0]= datos[0];
                        mayor[1]= datos[j];
                    }

                    if (Double.parseDouble(menor[1]) > Double.parseDouble(datos[j])) {
                        menor[0]= datos[0];
                        menor[1]= datos[j];
                    }
                }

            }

            System.out.println("La fecha del mayor valor del Bitcoin es: " +  mayor[0] +" Su valor: " + mayor[1]);
            System.out.println("La fecha del menor valor del Bitcoin es: " +  menor[0] +" Su valor: " + menor[1]);


        }catch (IOException e){
            System.out.println("Hubo un error: " + e.getMessage());
        }catch (NumberFormatException e){
            System.out.println("Hubo un error: " + e.getMessage());
        }

    }



}
