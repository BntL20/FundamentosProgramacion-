import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;



public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
    //    String[][] rosco = cargarDatos("rosco_facil.txt");
        // ⬆⬆⬆⬆⬆ falta el metodo de cargar datos q se encarga persona 1

    //    jugarRosco(rosco);
    //   mostrarResultados(rosco);


    }

    //PERSONA 3
    //REGISTRO DE USUARIOS
    public static Usuario registrarUsuario(){
    return null;
    }

    //PERSONA 2
    //ELECCIÓN DE NIVEL
    public static String elegirNivel(){
        Scanner sc = new Scanner(System.in);
        String nivel;
        System.out.println("Elige nivel: infantil / facil / medio / avanzado");
        nivel = sc.nextLine().toLowerCase();

        if (nivel.equals("infantil")) {
            return "rosco_infantil.txt";
        } else if (nivel.equals("facil")) {
            return "rosco_facil.txt";
        } else if (nivel.equals("medio")) {
            return "rosco_medio.txt";
        } else {
            return "rosco_avanzado.txt";
        }
    }

    //PERSONA 1
    //CARGAR DATOS
    public static String[][] cargarDatos(String nombreFichero){ //Función que carga los ficheros en el programa para poder trabajar con ellos

        String [] [] rosco = new String [26][4]; //matriz que guarda la letra,la pregunta,la respuesta y el estado.

        //Inicializamos el estado de las preguntas para que siempre empiecen en 0
        for (int i = 0;i<26;i++){
            rosco [3][0] = "0";
        }

        //PRIMERA LECTURA DEL FICHERO
        try {
            //Contamos cuántas palabras hay por letra
            int [] contadorPorLetra = new int [26];

            BufferedReader br = new BufferedReader(new FileReader(nombreFichero));
            String linea = "";

            //Este while lee la línea del fichero hasta que esté vacía = fichero acabado
            while (null != linea){
                char letra = linea.charAt (0); //sacamos la primera letra de la linea
                int indice = letra - 'A'; //y convertimos las letras en números para que representen el índice
                contadorPorLetra[indice] ++;  //sumamos 1 a la letra del indice porque ha encontrado una pregunta más

            }

            br.close(); //cerramos el fichero

            //Elegir una posición aleatoria para cada letra
            int [] aleatorioPorLetra = new int [26]; //aquí guardamos qué pregunta queremos elegir de cada letra (solo 1 por letra)
            for (int i = 0; i<26;i++){
                aleatorioPorLetra[i] = (int) (Math.random() * contadorPorLetra [i]); /*Con Math.random() obtenemos un número entre 0 y 1.
                                                                                      Al multiplicarlo por contadorPorLetra pedimos un número aleatorio entre 0 y el indice
                                                                                      Con (int) quitamos los decimales*/
            }

        //SEGUNDA LECTURA DEL FICHERO
            //Volver a leer el fichero y guardar la elegida
            int[] contadorActual = new int [26]; //Almacena cuantas preguntas hemos leído ya de cada letra

            br = new BufferedReader (new FileReader (nombreFichero));//abrimos el fichero otra vez

            while ((linea = br.readLine()) != null){ //mientras haya líneas en el fichero lo lee línea a línea
                String [] partes = linea.split(";"); /*separa la línea en partes cada vez que encuentra un ;
                                                            partes [0] = letra
                                                            partes [1] = pregunta
                                                            partes [2] = respuesta*/
                char letra = partes [0].charAt(0);
                int indice = letra - 'A';

                if(contadorActual[indice] == (aleatorioPorLetra)){ //elige aleatoriamente la pregunta de la letra.
                    rosco [indice][0] = partes [0]; //guardamos la letra
                    rosco [indice][1] = partes [1]; //guardamos la pregunta
                    rosco [indice][2] = partes [2]; //guardamos la respuesta
                }

                contadorActual[indice]++; //sumamos 1 porque hemos leído otra pregunta de esa letra
            }

            br.close();//cerramos el fichero
        }

        catch(IOException e){
            System.out.println ("Error leyendo el fichero"); //si no funciona el try que imprima ese error
        }

        return rosco; //devolvemos la matriz completa y lista
    }


    //PERSONA 2
    //LOGICA DEL JUEGO
    public static void jugarRosco(String[][] rosco) {
        Scanner sc = new Scanner(System.in);
        String respuesta;
        char continuar;

        //PRIMERA VUELTA (donde se hacen las preguntas)
        for (int i = 0; i < 26; i++) {
            //Solo preguntas no planteadas (recuerden que: 0 = no preguntadas, 1 = correctas, 2 = incorrectas, 3 = pasapalabra
            if (rosco[i][3].equals("0")) {
                System.out.println("Letra " + rosco[i][0] + ":" + rosco[i][1]);
                respuesta = sc.nextLine();
                if (respuesta.equalsIgnoreCase("pasapalabra")) {
                    rosco[i][3] = "3";
                } else if (respuesta.equalsIgnoreCase(rosco[i][2])) {
                    rosco[i][3] = "1";
                    System.out.println("Correcto");
                } else {
                    rosco[i][3] = "2";
                    System.out.println("Incorrecto");
                }
            }
        }

        //SEGUNDA VUELTA (aquí se hacen las preguntas donde se respondieron "pasapalabra")
        if (hayPasapalabras(rosco)) {
            System.out.print("¿Desea continuar con las preguntas pendientes? (s/n): ");
            continuar = sc.nextLine().toLowerCase().charAt(0);

            if (continuar == 's') {
                for (int i = 0; i < 26; i++) {
                    if (rosco[i][3].equals("3")) {
                        System.out.println("Letra " + rosco[i][0] + ": " + rosco[i][1]);
                        respuesta = sc.nextLine();

                        if (respuesta.equalsIgnoreCase("pasapalabra")) {
                            //sigue siendo pasapalabra
                            rosco[i][3] = "3";
                        } else if (respuesta.equalsIgnoreCase(rosco[i][2])) {
                            rosco[i][3] = "1";
                            System.out.println("Correcto");
                        } else {
                            rosco[i][3] = "2";
                            System.out.println("Incorrecto");
                        }
                    }
                }
            }
        }
    }

    //RECORRE TODO EL ROSCO PARA SABER SI HAY PASAPALABRA O NO
    public static boolean hayPasapalabras(String[][] rosco) {
        for (int i = 0; i < 26; i++) {
            if (rosco[i][3].equals("3")) {
                return true;
            }
        }
        return false;
    }

    //UN PRINT DE LOS RESULTADOS
    public static void mostrarResultados(String[][] rosco){
        int aciertos = 0;
        int fallos = 0;
        int pasapalabras = 0;

        for (int i = 0; i < 26; i++) {
            if (rosco[i][3].equals("1")) {
                aciertos++;
            } else if (rosco[i][3].equals("2")) {
                fallos++;
            } else if (rosco[i][3].equals("3")) {
                pasapalabras++;
            }
        }

        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Aciertos: " + aciertos);
        System.out.println("Fallos: " + fallos);
        System.out.println("Pasapalabras:" +pasapalabras);
    }

    //PERSONA 1
    //GUARDAR PARTIDA
    public static void guardarDatosPartida(String nombreFichero, String correoUsuario, int aciertos, int fallos, int pasapalabras, String nivel){
        try {
            BufferedWriter bw = new BufferedWriter (new FileWriter (nombreFichero, true));

            bw.write (correoUsuario + ";" + aciertos + ";" + fallos + ";" + pasapalabras + ";" + nivel);
            bw.newLine();

            bw.close();
        }

        catch (IOException e){
            System.out.println ("Error al escribir el fichero");
        }
    }
}

