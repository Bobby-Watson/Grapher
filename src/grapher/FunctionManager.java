package grapher;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class FunctionManager {

    private static Path path;

    private static final ArrayList<String> methodStrings = new ArrayList<>();
    private static HashMap<Method, Color> functions = new HashMap<>();

    private static void initFunctionFile() {
        path = Path.of(new File("").getAbsolutePath() + "\\src\\Grapher\\Functions.java");

        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException ex) {
            System.out.println("could not create file");
            ex.printStackTrace();
        }
    }

    public static void addMethod(String... strings) {
        for (String s : strings) {
            methodStrings.add(s);
        }
        parse();
    }

    private static void parse() {
        initFunctionFile();

        File functionFile = path.toFile();

        try {
            FileWriter writer = new FileWriter(functionFile);

            writer.write("package grapher;\n\npublic class Functions {\n");

            int n = 0;
            for (String s : methodStrings) {
                String methodText = "";

                methodText += "\n\tpublic static Double method" + n + "(Double x) {\n";

                methodText += ("\t\t" + s);
                if (!methodText.substring(methodText.length() - 1).equals(";")) {
                    methodText += ";";
                }

                methodText += "\n\t}";

                writer.write(methodText);
                n++;
            }

            writer.write("\n}");
            writer.close();
            
            
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            //int a = compiler.run(null, null, null, "grapher" + File.separator + functionFile.getName());
            int a = compiler.run(null, null, null, functionFile.getAbsolutePath());
            if(a == 0){
                System.out.println("Compilation successful");
            }
            else{
                System.out.println("Compilation failed");
            }
            
            //new javax.tools.SimpleJavaFileObject(java.net.URI.create(""), javax.tools.SimpleJavaFileObject.Kind.SOURCE);
            //java.lang //somehow make the program compile the file while running
        } catch (IOException ex) {
        }
        
        getMethods();
    }

    private static void getMethods(){
        Method m = getMethod(0);
        
        for(int i = 1; m != null; i++){
            functions.put(m, Color.getHSBColor((float)Math.random(), 1, 1));
            
            m = getMethod(i);
        }
    }
    
    private static Method getMethod(int i) {
        try {
            return Functions.class.getDeclaredMethod("method" + i, Double.class);
        } catch (NoSuchMethodException ex) {
        }
        
        System.out.println("No method found: method" + i);
        return null;
    }

    public static Double getValue(int i, double x) {
        try {
            return (Double) getMethod(i).invoke(null, x);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(FunctionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    public static void forEach(BiConsumer<? super Method, ? super Color> c){
        functions.forEach(c);
    }
}
