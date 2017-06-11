package relatorios;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author CAIO
 */
public class Relatorios {
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) { 
        Relatorios r = new Relatorios();
        //Lendo o arquivo
        r.ReadFileTextFromPackage("relatorios.modelos", "test.html");
        //Modificando 
        r.replaceText("$title1", "Caio");
        r.replaceText("$title2", "Relatorio v0.1"); 

        //-------------2º - Substituiçao adicionando conjunto de dados dentro do loop do html
        //cada string contem um conjunto de dados
        ArrayList<String[]> block = new ArrayList<>(); 
        //Cria uma lista da classe   
        for (int i = 11; i < 222; i+= 11) {
            //Adiciona os valores do conjunto de dados entre o loop in e loop fim
            block.add(new String[]{"{{NOME}}", "Caio"+i,
                                   "{{SEXO}}", "M"+i,
                                   "{{SALARIO}}", "R$ 15"+i});            
        }       
        //multiplica o conteudo que esta entre o loopin e loop fim a linas a baixo
        r.MultiplyRow(block);
        System.out.println(r.getText());
        
//        String caminho = r.SaveToJarDirectory("Caio\\xa.html");
//        String caminho = r.Save("C:\\MA\\"+r.getCurrentDate("dd-MM-yyyy")+".html");
        String caminho = r.SaveOpenDialog();

        //pergunta se deseja abrir
        r.AskIfWannaOpen(caminho);     
        
    }         
    private String text;
    
    //Encapsulamento

    public String getText() { return text; }
    public void setText(String text) { this.text = text; } 
    public void replaceText(String oldString, String newString) { text = text.replace(oldString, newString); }
    
    public Relatorios(String nomePacote, String nomeArquivo)
    {
        ReadFileTextFromPackage(nomePacote, nomeArquivo);
    } 
    
    public Relatorios()
    {
    
    }   

    /**
     *  Multiplica as linhas acrescentando dados
     * @param cjDados conjunto da dados a ser armazenado em cada linha
     */
    public void MultiplyRow(ArrayList<String[]>  cjDados)
    {
        //Tamanho do primeiro array dentro do ArrayList
        int arrayLenght = cjDados.get(0).length;        
        //converte o parametro para um array de arrays 
        String[][] chaveEvalores = cjDados.toArray(new String[cjDados.size()][arrayLenght]);        
        //Pego a parte entre LOOP_IN e LOOP_FIM
        String partOld = text.substring(text.indexOf("{{LOOP_IN}}"), text.lastIndexOf("{{LOOP_FIM}}") + "{{LOOP_FIM}}".length());             
        //Array para armazenar cada bloco/linha (<tr></tr>)
        String[] newParts = new String[chaveEvalores.length];
        
        //para cada Array de Array
        for (int i = 0; i < newParts.length; i++) {
            String keyWord;
            String valor;                 
            newParts[i] = partOld;
            for (int j = 0; j < chaveEvalores[i].length; j += 2) {
                keyWord =  chaveEvalores[i][j]; //par sao as chaves
                valor = chaveEvalores[i][j+1]; //impar sao os valores
                
                newParts[i] = newParts[i].replace(keyWord, valor);
            }     
        }        
        //Junto os bloco/linha(<tr></tr> adicionando espaço
//        String completePart = String.join(System.lineSeparator(), newParts); //JAVA >= 8
        String completePart = strJoin(newParts, System.lineSeparator());    //JAVA < 8
        //remove os textos de controle
        completePart = completePart.replace("{{LOOP_IN}}", "").replace("{{LOOP_FIM}}", "");
        //Subistitui a parte antiga pela pela nova 
        text = text.replace(partOld, completePart);
        //remove as linhas em vazias em branco
        text = text.replaceAll("(?m)^[ \t]*\r?\n", "");    
    }
   
     /**
     * Lê um arquivo de texto que esta em algum pacote
     * @param namePackage Nome do pacote ex (relatorio.relatodios)
     * @param nameFile Nome do arquivo que esta dentro do pacote "test.html"
     */
    public final void ReadFileTextFromPackage(String namePackage, String nameFile)
    {
       namePackage = namePackage.contains(".") ? namePackage.substring(namePackage.indexOf('.')+1) : "/"+namePackage;
        try{
            StringBuilder contentBuilder = new StringBuilder();
            InputStream in = getClass().getResourceAsStream(namePackage +"/"+nameFile);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) { //"modelos/test.html"
                    String line;
                    while ((line = br.readLine()) != null) {
                        contentBuilder.append(line);
                        contentBuilder.append(System.getProperty("line.separator"));// new line any OS
                    }
                    System.out.println("TEXTO LIDO: "+br.toString());
                }           
            text = contentBuilder.toString();        
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERRO TRY/CATCH (ReadFileTextFromPackage)", JOptionPane.ERROR_MESSAGE);
        }
    }    
    
    /**
     * Salva em um caminho espeficico
     * @param pathOrFile caminho em String ou File
     * @return O caminho que salvou
     */
    public String Save(Object pathOrFile)
    {
        File file = null;
        String path = null;        
        //tenta converter o objeto para um file
        try{
            file = (File) pathOrFile;
        }catch(Exception ex){           
            path = pathOrFile.toString();
            String decodedPath;
            try {  decodedPath = URLDecoder.decode(path, "UTF-8"); }
            catch (UnsupportedEncodingException ex1) { decodedPath = path; }
            
            file = new File(decodedPath);
        } 
        //cria as pastas e o arquivo
        createFoldersAndFile(file);
        //escreve dentro dele
        writeFile(file, text, true);
        
        return file.toString();
    }
    
    /**
     * Abre um dialogo perguntando onde deseja salvar
     * @return  O caminho onde salvou
     */
    public String SaveOpenDialog()
    {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".html"))
                file = new File(fileChooser.getSelectedFile()+".html");
            //Cria o arquivo e escreve
            writeFile(file, text, true);            
            return file.toString();
        }
        return null;
    }

    /**
     * Salva no diretorio do JAR
     * @param folderAndFile pastas ou subpastas + arquivo a ser criado ex(Pasta1\\Pasta2\\arquivo.html)
     * @return
     */
    public String SaveToJarDirectory(String folderAndFile)
    {                 
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();            
        String decodedPath;
        try {  decodedPath = URLDecoder.decode(path, "UTF-8"); } 
        catch (UnsupportedEncodingException ex) { decodedPath = path; }
        
        decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("/"));            
        File directoryMyJar = new File(decodedPath, "\\"+folderAndFile);            
        //cria as pastas e o arquivo
        createFoldersAndFile(directoryMyJar);
        //escreve dentro dele
        writeFile(directoryMyJar, text, true); 
        
        return decodedPath;
    }
    
    /**
     * Pergunta se deseja abrir o arquivo
     * @param path caminho do arquivo
     */
    public void AskIfWannaOpen(String path)
    {        
        if(path == null) return;
        File file = new File(path);
        if(file != null && file.exists())
        {
            int dialogResult = JOptionPane.showConfirmDialog(null, "O relatorio foi criado em: "+System.lineSeparator()+ file.toString() + System.lineSeparator()+"deseja abrir?", "RELATORIO CRIADO", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(dialogResult == JOptionPane.YES_OPTION)
            {                
                try { Desktop.getDesktop().open(file); }
                catch(Exception ex) { }
            }                
        }
    }
    
    private void writeFile(File file, String text, boolean Overwrite) 
    {  
        try (BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, !Overwrite), "UTF-8"))){ //"C:\\Users\\CAIO\\Desktop\\test\\test2.txt"
            bf.write(text);
            bf.close();
            if(Overwrite)
                System.out.println("TEXTO NOVO: "+file.toString());
            else
                System.out.println("TEXTO ADICIONADO: "+file.toString());
        }catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERRO TRY/CATCH (writeFile)", JOptionPane.ERROR_MESSAGE);
        }  
    }
    
    private void createFoldersAndFile(File f) 
    {
        //cria as pastas definina no caminho se nao haver
        if(f.getParentFile().mkdirs())
            System.out.println("SUBPASTAS CRIADAS ");
        
        try {
            //se o arquivo foi criado
            if(f.createNewFile())
                System.out.println("ARQUIVO CRIADO: "+f.toString());
            else
                System.out.println("ARQUIVO JÁ EXISTENTE: "+f.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERRO TRY/CATCH (createFoldersAndFile)", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Obtem a data atual do sistema em string no formato passado pelo parametro
     * @param formatDate
     * @return
     */
    public String getCurrentDate(String formatDate)
    {
        return new SimpleDateFormat(formatDate).format(new Date()); //"yyyy-MM-dd HHmmss"
    }
    
    private String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();        
        for (int i = 0; i < aArr.length; i++) {
            if (i > 0)
                sbStr.append(sSep);
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }
}
