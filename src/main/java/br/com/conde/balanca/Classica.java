package br.com.conde.balanca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A estrategia famosa: doze moedas, tres pesagens, achando a moeda falsa E
 * dizendo se ela e leve ou pesada.
 *
 * <p>Ela esta escrita a mao aqui, pesagem por pesagem, porque a busca exata nao
 * alcanca doze moedas: o numero de pesagens possiveis explode (mais de trinta
 * mil so no primeiro nivel) e a memoria nao salva, ja que cada ramo encontra um
 * conjunto diferente. Entao o papel deste arquivo e outro: a estrategia e dada,
 * e o programa <b>confere</b> que ela funciona.
 *
 * <p>A conferencia nao olha rotulo de folha nenhum. Ela pergunta uma coisa so:
 * os 24 casos possiveis produzem 24 sequencias de resultados DIFERENTES? Se
 * duas hipoteses produzem a mesma sequencia, a estrategia nao as distingue, e
 * nao adianta o que esteja escrito na folha.
 */
public final class Classica {

    public static final int MOEDAS = 12;

    private Classica() {
    }

    /** O caminho de resultados de uma hipotese: tres valores em -1, 0, 1. */
    public static List<Integer> caminho(Hipotese hipotese) {
        List<Integer> caminho = new ArrayList<>();
        Pesagem primeira = grupo("1,2,3,4", "5,6,7,8");
        int r1 = primeira.resultado(hipotese);
        caminho.add(r1);

        Pesagem segunda = switch (r1) {
            case 0 -> grupo("9,10,11", "1,2,3");
            case 1 -> grupo("1,2,5", "3,4,6");
            default -> grupo("5,6,1", "7,8,2");
        };
        int r2 = segunda.resultado(hipotese);
        caminho.add(r2);

        Pesagem terceira = terceira(r1, r2);
        caminho.add(terceira.resultado(hipotese));
        return caminho;
    }

    private static Pesagem terceira(int r1, int r2) {
        if (r1 == 0) {
            return switch (r2) {
                case 0 -> grupo("12", "1");
                default -> grupo("9", "10");
            };
        }
        if (r1 == 1) {
            return switch (r2) {
                case 0 -> grupo("7", "8");
                case 1 -> grupo("1", "2");
                default -> grupo("3", "4");
            };
        }
        return switch (r2) {
            case 0 -> grupo("3", "4");
            case 1 -> grupo("5", "6");
            default -> grupo("7", "8");
        };
    }

    /**
     * Quantos dos 24 casos a estrategia separa dos demais.
     *
     * <p>24 quer dizer que ela resolve o problema inteiro. Qualquer numero menor
     * aponta exatamente quantos pares de casos ela confunde.
     */
    public static int casosDistinguidos() {
        Map<List<Integer>, List<Hipotese>> porCaminho = new HashMap<>();
        for (Hipotese hipotese : todas()) {
            porCaminho.computeIfAbsent(caminho(hipotese), chave -> new ArrayList<>()).add(hipotese);
        }
        int distinguidos = 0;
        for (List<Hipotese> juntas : porCaminho.values()) {
            if (juntas.size() == 1) {
                distinguidos++;
            }
        }
        return distinguidos;
    }

    /** Os casos que a estrategia confunde, se houver algum. */
    public static List<List<Hipotese>> confusoes() {
        Map<List<Integer>, List<Hipotese>> porCaminho = new HashMap<>();
        for (Hipotese hipotese : todas()) {
            porCaminho.computeIfAbsent(caminho(hipotese), chave -> new ArrayList<>()).add(hipotese);
        }
        List<List<Hipotese>> confusoes = new ArrayList<>();
        for (List<Hipotese> juntas : porCaminho.values()) {
            if (juntas.size() > 1) {
                confusoes.add(juntas);
            }
        }
        return confusoes;
    }

    /** As 24 hipoteses: cada uma das doze moedas, leve ou pesada. */
    public static List<Hipotese> todas() {
        List<Hipotese> lista = new ArrayList<>();
        for (int moeda = 0; moeda < MOEDAS; moeda++) {
            lista.add(new Hipotese(moeda, false));
            lista.add(new Hipotese(moeda, true));
        }
        return lista;
    }

    /** Uma pesagem escrita do jeito que se fala: "1,2,3,4" contra "5,6,7,8". */
    public static Pesagem grupo(String esquerda, String direita) {
        return new Pesagem(mascara(esquerda), mascara(direita));
    }

    private static int mascara(String lista) {
        int mascara = 0;
        for (String numero : lista.split(",")) {
            mascara |= 1 << (Integer.parseInt(numero.trim()) - 1);
        }
        return mascara;
    }
}
