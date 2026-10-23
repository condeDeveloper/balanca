package br.com.conde.balanca;

import java.util.ArrayList;
import java.util.List;

/**
 * Uma pesagem: um grupo no prato esquerdo, outro no direito, do MESMO tamanho.
 *
 * <p>O mesmo tamanho nao e detalhe: e a restricao que faz o limite de contagem
 * nao ser alcancavel. Uma balanca de pratos so responde "qual lado desce", e
 * para a resposta significar alguma coisa os dois lados precisam ter a mesma
 * quantidade de moedas.
 *
 * <p>Moedas ja inocentadas continuam servindo, como peso de referencia. Esquecer
 * disso e o erro que faz muita gente achar que 12 moedas nao cabem em tres
 * pesagens.
 */
public record Pesagem(int esquerda, int direita) {

    /** Quantas moedas tem em cada prato. */
    public int tamanho() {
        return Long.bitCount(esquerda);
    }

    /**
     * O que a balanca mostra se a hipotese for a verdadeira.
     *
     * @return 1 se o prato esquerdo desce, -1 se o direito desce, 0 se equilibra
     */
    public int resultado(Hipotese hipotese) {
        int bit = 1 << hipotese.moeda();
        boolean naEsquerda = (esquerda & bit) != 0;
        boolean naDireita = (direita & bit) != 0;
        if (!naEsquerda && !naDireita) {
            return 0;
        }
        boolean desceEsquerda = naEsquerda == hipotese.pesada();
        return desceEsquerda ? 1 : -1;
    }

    /**
     * Todas as pesagens que vale a pena tentar com {@code moedas} moedas.
     *
     * <p>Trocar os pratos de lado da a mesma informacao, entao so as metades com
     * a menor moeda na esquerda sao geradas.
     */
    public static List<Pesagem> todas(int moedas) {
        List<Pesagem> lista = new ArrayList<>();
        for (int tamanho = 1; tamanho <= moedas / 2; tamanho++) {
            gerar(moedas, tamanho, 0, 0, 0, 0, 0, lista);
        }
        return lista;
    }

    private static void gerar(int moedas, int tamanho, int proxima,
                              int esquerda, int naEsquerda, int direita, int naDireita,
                              List<Pesagem> lista) {
        if (naEsquerda == tamanho && naDireita == tamanho) {
            lista.add(new Pesagem(esquerda, direita));
            return;
        }
        if (proxima >= moedas) {
            return;
        }
        int faltam = moedas - proxima;
        if (faltam < (tamanho - naEsquerda) + (tamanho - naDireita)) {
            return;
        }
        int bit = 1 << proxima;
        if (naEsquerda < tamanho) {
            gerar(moedas, tamanho, proxima + 1, esquerda | bit, naEsquerda + 1,
                    direita, naDireita, lista);
        }
        // a menor moeda usada fica sempre na esquerda: evita contar a mesma pesagem
        // duas vezes, uma com os pratos trocados
        if (naDireita < tamanho && naEsquerda > 0) {
            gerar(moedas, tamanho, proxima + 1, esquerda, naEsquerda,
                    direita | bit, naDireita + 1, lista);
        }
        gerar(moedas, tamanho, proxima + 1, esquerda, naEsquerda, direita, naDireita, lista);
    }

    @Override
    public String toString() {
        return lado(esquerda) + " contra " + lado(direita);
    }

    private String lado(int grupo) {
        StringBuilder texto = new StringBuilder("{");
        for (int moeda = 0; moeda < 32; moeda++) {
            if ((grupo & (1 << moeda)) != 0) {
                if (texto.length() > 1) {
                    texto.append(',');
                }
                texto.append(moeda + 1);
            }
        }
        return texto.append('}').toString();
    }
}
