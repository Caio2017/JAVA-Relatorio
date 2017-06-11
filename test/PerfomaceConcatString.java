/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author caios
 */
public class PerfomaceConcatString {
    public static void main(String[] args)
    {
        testContentBuilder();
    }
        
    public static void testStringMore()
    {
        String x = "inicio";
        for (int i = 0; i < 90000; i++) {
            x += "a";
        }
        System.out.println("concluido");  
    }
            
    public static void testContentBuilder()
    {
        StringBuilder contentBuilder = new StringBuilder("inicio");
        for (int i = 0; i < 90000; i++) {
            contentBuilder.append("a");
        }
        String t = contentBuilder.toString();
        System.out.println("Concluido");
    }
}
