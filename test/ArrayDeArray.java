
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author CAIO
 */
public class ArrayDeArray {
    public static void main(String[] args)
    {
        testArray3();
    }   
    
    public static void testArray2()
    {
        String[][][] kk = new String[][][]{
            new String[][]{
                new String[]{
                    "Leao", "Macaco"
                },
                new String[]{
                    "Cadeira", "Mesa"
                }
            },
            new String[][]{
                new String[]{
                    "Jural", "Jatoba", "Caio"
                },
                new String[]{
                    "Sao Paulo", "Rio de Janeiro"
                }
            }, 
        };
        
        String meuNome = kk[1][0][2];
        System.out.println(meuNome);
    }
    
    public static void testArray3()
    {
        ArrayList<String[]> block = new ArrayList<>(); 
        //Cria uma lista da classe   
        for (int i = 0; i < 5; i++) {
            //Adiciona os valores de todas colunas ou conjunto de dados entre o loop in e loop fim
            block.add(new String[]{"{{NOME}}", "Caio"+i,
                                   "{{SEXO}}", "M"+i,
                                   "{{SALARIO}}", "R$ 1500"+i});

            
        }        
        int tamanhoCadaVar = block.get(0).length;
        //cria um array de arrays 
        String[][] a = block.toArray(new String[block.size()][tamanhoCadaVar]);
        
        for (int i = 0; i < a.length; i++) {
            String keyWord;
            String valor;
            for (int j = 0; j < a[i].length; j += 2) {
                keyWord =  a[i][j]; //par sao as chaves
                valor = a[i][j+1]; //impar sao os valores                
            
                System.out.println("Chave :"+ keyWord + "   Valor :"+ valor);
            }
//            break;
        }
        
//        for (int i = 0; i < a.length; i++) {
//            for (int j = 0; j < a[i].length; j++) {
//                System.out.println(a[i][j]);
//            }
//        }
//        for(String[] b : a)
//        {
//            for(String c : b)
//                System.out.println(c);            
//        }
    }
}
