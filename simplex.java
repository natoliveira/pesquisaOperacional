import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Simplex {

    // Declaração de variáveis globais
    public static Arquivo arquivo;
    public static LinkedHashMap<String, Double> solucaoOtima = new LinkedHashMap<>();

    public static void main(String[] args) throws FileNotFoundException {
        
        // Caminho do arquivo a ser lido
        String pathname = "src/arquivo.txt";
        
        // Instanciação do objeto Arquivo
        arquivo = new Arquivo();
        
        // Leitura do arquivo
        arquivo.lerArquivo(pathname);

        // Obtenção das restrições e função objetivo do arquivo
        BigDecimal[][] restricoes = arquivo.restricoes;
        BigDecimal[] func = arquivo.fObjetivo;
        
        try {
            // Chamada do método resolver
            resolver(restricoes, func);
        } catch (ArrayIndexOutOfBoundsException var4) {
            // Tratamento de exceção para índices fora dos limites
            System.out.println("POR FAVOR, VERIFICAR OS VALORES INFORMADOS,EXISTE INCONSISTE\u00caNCIA NOS ITENS INFORMADOS!!!");
        }
    }

    // Método principal que implementa o algoritmo Simplex
    public static BigDecimal[][] resolver(BigDecimal[][] matrizRestricao, BigDecimal[] FObjetiva) throws ArrayIndexOutOfBoundsException {
        int nLinhas = arquivo.nCoeficientes + 1;
        int nColunas = arquivo.nVariaveis + arquivo.nCoeficientes + 1;
        BigDecimal[][] tabela = new BigDecimal[nLinhas][nColunas];
        
        // Inicialização da tabela com zeros
        for(int i = 0; i < nLinhas; ++i) {
            for(int j = 0; j < nColunas; ++j) {
                tabela[i][j] = BigDecimal.ZERO;
            }
        }

        // Configuração da linha da função objetivo
        for(int i = 0; i < FObjetiva.length; ++i) {
            tabela[0][i] = FObjetiva[i].multiply(BigDecimal.valueOf(-1.0));
        }

        // Configuração das restrições na tabela
        for(int i = 1; i < nLinhas; ++i) {
            for(int j = 0; j < arquivo.nVariaveis; ++j) {
                tabela[i][j] = matrizRestricao[i-1][j];
            }
        }

        // Configuração das variáveis de folga e do lado direito na tabela
        for (int i = 1; i < nLinhas; i++) {
            for (int j = 0; j < nColunas; j++) {
                if (i + arquivo.nVariaveis == j + 1){
                    tabela[i][j] = BigDecimal.ONE;
                }
                if(j == nColunas - 1){
                    tabela[i][j] = matrizRestricao[i-1][matrizRestricao[0].length-1];
                }
            }
        }
        
        // Loop principal para encontrar a solução ótima
        while (!isSolucaoOtima(tabela[0])){
            int indiceColunaPivo = getIndiceMelhorContribuinte(tabela[0]);
            int indiceLinhaPivo = getIndiceLinhaPivo(tabela, indiceColunaPivo);

            // Atualizações da tabela com base no pivô
            tabela = atualizaLinhaPivo(tabela, indiceLinhaPivo, indiceColunaPivo);
            tabela = atualizarDemaisLinhas(tabela, indiceLinhaPivo, indiceColunaPivo);
        }
        
        // Imprime a tabela final
        printaTabela(tabela);

        // Mostra a solução ótima
        mostrarSolucaoOtima(tabela);
        return tabela;
    }

    // Métodos auxiliares

    // Método para mostrar a solução ótima
    private static void mostrarSolucaoOtima(BigDecimal[][] tabela) {
        for (int j = 0; j < tabela[0].length; j++) {
            BigDecimal[] colunaAtual = new BigDecimal[tabela.length];
            for (int i = 0; i < tabela.length; i++) {
                colunaAtual[i] = tabela[i][j];
            }
            if (isColunaSolucao(colunaAtual)){
                int indiceSolucao = getIndiceSolucao(colunaAtual);
                if (j < arquivo.nVariaveis){
                    solucaoOtima.put("x"+(j+1), tabela[indiceSolucao][tabela[0].length-1].doubleValue());
                } else{
                    solucaoOtima.put("s"+(j-arquivo.nVariaveis+1), tabela[indiceSolucao][tabela[0].length-1].doubleValue());
                }
            }
        }
        solucaoOtima.put("FMax", tabela[0][tabela[0].length-1].doubleValue());

        // Imprime a solução ótima
        imprimirSolucao();
    }

    // Método para imprimir a solução ótima
    private static void imprimirSolucao() {
        for (Map.Entry<String, Double> solucao:
             solucaoOtima.entrySet()) {
            System.out.println(solucao.getKey()+" --- "+solucao.getValue());
        }
    }

    // Método para obter o índice da variável básica na coluna solução
    private static int getIndiceSolucao(BigDecimal[] colunaAtual) {
        for (int i = 0; i < colunaAtual.length; i++) {
            if (colunaAtual[i].compareTo(BigDecimal.ONE) == 0)
                return i;
        }
        return -1;
    }

    // Método para verificar se a coluna é uma coluna solução
    private static boolean isColunaSolucao(BigDecimal[] colunaAtual) {
        for (BigDecimal elemento:
             colunaAtual) {
            if (!(elemento.compareTo(BigDecimal.ZERO) == 0 || elemento.compareTo(BigDecimal.ONE) == 0)){
                return false;
            }
        }
        return true;
    }

    // Método para atualizar as demais linhas da tabela durante a iteração
    private static BigDecimal[][] atualizarDemaisLinhas(BigDecimal[][] tabela, int indiceLinhaPivo, int indiceColunaPivo) {
        for (int i = 0; i < tabela.length; i++) {
            if (i != indiceLinhaPivo){
                BigDecimal[] linhaMultiplicada = new BigDecimal[tabela[i].length];
                for (int j = 0; j < tabela[i].length; j++) {
                    linhaMultiplicada[j] = tabela[i][indiceColunaPivo].multiply(tabela[indiceLinhaPivo][j]);
                }
                for (int j = 0; j < tabela[i].length; j++) {
                   
