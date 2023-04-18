package grapher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;

public class Grapher {

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        createGameWindow();

        java.lang.reflect.Method a;

        /*a = Grapher.class.getMethod("printHello");
        System.out.println(a.getName());
        a.invoke(null, null);
        a = Grapher.class.getMethod("print", String.class);
        System.out.println(a.getName());
        a.invoke(null, "your mom");*/
        
        FunctionManager.addMethod(
                //"return Math.pow(x,3)/1000;", 
                //"return Math.abs(x)", 
                //"return x > 0 ? 100 * Math.sin(Math.toRadians(x)) : x;",
                "return 100 % x;",
                "return (int)(double)(x/100)%2==0 ? x : -x"
        );
    }

    private static void createGameWindow() {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setName("Life");

        Graph panel = new Graph();
        window.add(panel);
        panel.init();
        
        var p = new javax.swing.JPanel(); //Replace w/ panel for inputting formulas
        p.setPreferredSize(new java.awt.Dimension(0, 250));
        p.setBackground(Color.red);
        window.add(p,BorderLayout.SOUTH);
        
        System.out.println(window.getComponentCount());
        
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void printHello() {
        System.out.println("hello");
    }
    
    public static void print(String a){
        System.out.println(a);
    }
}
