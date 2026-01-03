import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.File;



public class Main {
    static Usuario[] usuarios = new Usuario[10];
    static int numUsuarios = 0;

    public static void main(String[] args) {
        //Carga usuarios existentes desde la matriz
        //leerUsuarios();

        //Registrar o identificar usuario
        Usuario usuarioActual = registrarUsuario();

        //Elegir nivel (devuelve directamente el fichero)
        String nivel = elegirNivel();
        String nombreFichero = obtenerFicheroNivel(nivel);

        //Cargar datos del rosco según el nivel
        String[][] rosco = cargarDatos(nombreFichero);

        //Lógica del juego
        jugarRosco(rosco);
        mostrarResultados(rosco);

        //Guardar resultados del usuario
        int[] r = calcularResultados(rosco);
        mostrarResultados(rosco);

        guardarDatosPartida("src/Data/MarcadorUsuario.txt", usuarioActual.correo, r[0], r[1], r[2], nivel);

    }

    //REGISTRO DE USUARIOS
    public static Usuario registrarUsuario(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce nombre: ");
        String nombre = sc.nextLine();

        int edad;
        do {
            System.out.print("Introduce edad: ");
            edad = sc.nextInt();
            sc.nextLine();

            if (edad <= 0) {
                System.err.println("Error: La edad debe ser > 0");
            }
        } while (edad <= 0);

        String correo;
        do {
            System.out.print("Introduce correo: ");
            correo = sc.nextLine();

            if (!correo.contains("@")) {
                System.out.println("Error: el correo debe contener '@'.");
            }
        } while (!correo.contains("@"));

        // ===== BUSCAR USUARIO EXISTENTE =====
        for (int i = 0; i < numUsuarios; i++) {
            if (usuarios[i].correo.equalsIgnoreCase(correo)) {
                System.out.println("Usuario encontrado. Bienvenido de nuevo.");
                return usuarios[i];
            }
        }

        // ===== CREAR NUEVO USUARIO =====
        Usuario nuevoUsuario = new Usuario(nombre, edad, correo);
        usuarios[numUsuarios] = nuevoUsuario;
        numUsuarios++;

        System.out.println("Usuario registrado correctamente.");

        return nuevoUsuario;
    }

    //ELECCIÓN DE NIVEL
    public static String elegirNivel(){
        Scanner sc = new Scanner(System.in);
        String nivel;
        do {
            System.out.println("Elige nivel: infantil / facil / medio / avanzado");
            nivel = sc.nextLine().toLowerCase();

            if(!nivel.equals("infantil") && !nivel.equals("facil") && !nivel.equals("medio") && !nivel.equals("avanzado")){
                System.out.println("Error de elección de nivel. Coloque uno de los niveles indicados");
            }
        }while (!nivel.equals("infantil") && !nivel.equals("facil") && !nivel.equals("medio") && !nivel.equals("avanzado"));

        return nivel;
    }

    public static String obtenerFicheroNivel(String nivel){
        return "src/Data/rosco_"+nivel+".txt";
    }

    //CARGAR DATOS
    public static String[][] cargarDatos(String nombreFichero){ //Función que carga los ficheros en el programa para poder trabajar con ellos

        String [] [] rosco = new String [26][4]; //matriz que guarda la letra,la pregunta,la respuesta y el estado.

        //Inicializamos el estado de las preguntas para que siempre empiecen en 0
        for (int i = 0;i<26;i++){
            rosco [i][3] = "0";
        }

        //PRIMERA LECTURA DEL FICHERO
        try {
            //Contamos cuántas palabras hay por letra
            int [] contadorPorLetra = new int [26];
            BufferedReader br = new BufferedReader(new FileReader(nombreFichero));
            String linea;
            //Este while lee la línea del fichero hasta que esté vacía = fichero acabado
            while ((linea = br.readLine()) != null){
                char letra = linea.charAt (0); //sacamos la primera letra de la línea
                int indice = letra - 'A';//y convertimos las letras en números para que representen el índice
                if(indice >= 0 && indice < 26) {
                    contadorPorLetra[indice]++;  //sumamos 1 a la letra del índice porque ha encontrado una pregunta más
                }
            }
            br.close(); //cerramos el fichero

            //Elegir una posición aleatoria para cada letra
            int [] aleatorioPorLetra = new int [26]; //aquí guardamos qué pregunta queremos elegir de cada letra (solo 1 por letra)
            for (int i = 0; i<26;i++){
                aleatorioPorLetra[i] = (int) (Math.random() * contadorPorLetra [i]); /*Con Math.random() obtenemos un número entre 0 y 1.
                                                                                     Al multiplicarlo por contadorPorLetra pedimos un número aleatorio entre 0 y el índice
                                                                                     Con (int) quitamos los decimales*/
            }

        //SEGUNDA LECTURA DEL FICHERO
            //Volver a leer el fichero y guardar la elegida
            int[] contadorActual = new int [26]; //Almacena cuantas preguntas hemos leído ya de cada letra
            br = new BufferedReader (new FileReader (nombreFichero));//abrimos el fichero otra vez
            while ((linea = br.readLine()) != null){ //mientras haya líneas en el fichero lo lee línea a línea
                String [] partes = linea.split(";"); /*separa la línea en partes cada vez que encuentra un;
                                                            partes [0] = letra
                                                            partes [1] = pregunta
                                                            partes [2] = respuesta*/
                char letra = partes [0].charAt(0);
                int indice = letra - 'A';
                if(indice >= 0 && indice < 26) {
                    if (contadorActual[indice] == (aleatorioPorLetra[indice])) { //elige aleatoriamente la pregunta de la letra.
                        rosco[indice][0] = partes[0]; //guardamos la letra
                        rosco[indice][1] = partes[1]; //guardamos la pregunta
                        rosco[indice][2] = partes[2]; //guardamos la respuesta
                    }
                    contadorActual[indice]++; //sumamos 1 porque hemos leído otra pregunta de esa letra
                }
            }
            br.close();//cerramos el fichero

        }catch(IOException e){
            System.out.println ("Error leyendo el fichero"); //si no funciona el try que imprima ese error
        }
        return rosco; //devolvemos la matriz completa y lista
    }

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
                respuesta = sc.nextLine().trim();
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
    public static void mostrarResultados(String[][] rosco) {
        int[] r = calcularResultados(rosco);

        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Aciertos: " + r[0]);
        System.out.println("Fallos: " + r[1]);
        System.out.println("Pasapalabras: " + r[2]);
    }

    public static int[] calcularResultados(String[][] rosco) {
        int aciertos = 0;
        int fallos = 0;
        int pasapalabras = 0;

        for (int i = 0; i < 26; i++) {
            if (rosco[i][3].equals("1")){
                aciertos++;
            }
            else if (rosco[i][3].equals("2")){
                fallos++;
            }
            else if (rosco[i][3].equals("3")){
                pasapalabras++;
            }
        }

        return new int[]{aciertos, fallos, pasapalabras};
    }

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
    public static void historialPartida (String nombreFichero){
        try{
            Scanner file = new Scanner (new File(nombreFichero));

            System.out.println("Historial de partidas");

            while(file.hasNextLine()){
                String linea = file.nextLine();
                String []datos = linea.split(";");

                System.out.println("Correo:"+ datos[0]+n/
                        "Aciertos: "+ datos[1]+n/
                        "Fallos: "+ datos[2]+n/
                        "Pasapalabras: "+ datos[3]+n/
                        "Nivel: "+ datos[4]);
            }
            file.close();
        }
        catch (FileNotFoundException e){
            System.out.println("No hay estadisticas registradas");
        }
    }

    public static void mejorPuntaje(String nombreFichero){
        int mejor=0;

        try{
            Scanner file= new Scanner(new File(nombreFichero));
            while(file.hasNextLine()){
                String []datos = file.nextLine().split(";");
                int aciertos = Integer.parseInt(datos[1]);


                if(aciertos> mejor){
                    mejor = aciertos;
                }
            }

            file.close();
            System.out.println("Mejor puntuacion registrada: "+ mejor + "aciertos");
        }
        catch(Exception e){
            System.out.println("Error al leer las estadisticas");
        }
    }
    public static void partidasPorNivel(String nombreFichero){
        int infantil =0;
        int facil=0;
        int medio=0;
        int avanzado=0;


        try{
            Scanner file = new Scanner(new File(nombreFichero));

            while(file.hasNextLine()){

                String[]datos = file.nextLine().split(";");

                String nivel = datos[4].toLowerCase();

                if(nivel.equals("infantil")){
                    infantil++;
                }
                else if(nivel.equals("facil")){
                    facil++;
                }
                else if(nivel.equals("medio")){
                    medio++;
                }
                else if(nivel.equals("avanzado")){
                    avanzado++;
                }

            }
            file.close();


            System.out.println("Partidas por nivel: ");
            System.out.println("Infantil: "+ infantil);
            System.out.println("Facil: "+ facil);
            System.out.println("Medio: "+ medio);
            System.out.println("Avanzado: "+ avanzado);
        }
        catch(Exception e){
            System.out.println("Error al leer el fichero de estadisticas");
        }
    }




    // ESTO CREO QUE ES AL FINAL DEL JUEGO LO DE PREGUNTAR AL USUARIO SI DESEA VER LAS ESTADISTICAS
    //System.out.println("¿Desea ver las estadísticas? (s/n)");
    //String opcion = in.nextLine();

//if (opcion.equalsIgnoreCase("s")) {
       // Estadisticas.mostrarhistorialPartida("data/marcadorUsuario.txt");
        //Estadisticas.mostrarmejorPuntaje("data/marcadorUsuario.txt");
       // Estadisticas.mostrarpartidasPorNivel("data/marcadorUsuario.txt");
    }

}

