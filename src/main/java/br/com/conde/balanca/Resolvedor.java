package br.com.conde.balanca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quantas pesagens sao necessarias, de verdade.
 *
 * <p>Busca em minimax sobre o conjunto de hipoteses ainda vivas: para cada
 * pesagem possivel, o pior dos tres resultados decide; entre as pesagens, a
 * melhor decide. Nao e simulacao e nao e heuristica: e a resposta exata.
 *
 * <p>A memoria guarda o resultado por conjunto de hipoteses, porque o mesmo
 * conjunto aparece por caminhos diferentes o tempo todo.
 */
public final class Resolvedor {

    /** Nenhum numero de pesagens resolve. Acontece de verdade, e nao e erro. */
    public static final int IMPOSSIVEL = 99;

    // A resposta de um problema inteiro (n moedas, um modo) nao muda, e achar a de
    // nove moedas custa segundos. Guardar entre instancias tira um minuto da bateria
    // de testes, que roda tres vezes no CI, uma por sistema.
    private static final Map<String, Integer> MINIMOS = new HashMap<>();

    private final int moedas;
    private final Modo modo;
    private final List<Pesagem> pesagens;
    private final Map<Long, Integer> guardados = new HashMap<>();

    public Resolvedor(int moedas, Modo modo) {
        if (moedas < 2 || moedas > 13) {
            throw new IllegalArgumentException("de 2 a 13 moedas; " + moedas + " nao cabe");
        }
        this.moedas = moedas;
        this.modo = modo;
        this.pesagens = Pesagem.todas(moedas);
    }

    public int moedas() {
        return moedas;
    }

    public Modo modo() {
        return modo;
    }

    public List<Pesagem> pesagensPossiveis() {
        return pesagens;
    }

    public int guardados() {
        return guardados.size();
    }

    /** O numero de pesagens que a melhor estrategia gasta no pior caso. */
    public int minimo() {
        String chave = moedas + ":" + modo;
        Integer guardado = MINIMOS.get(chave);
        if (guardado != null) {
            return guardado;
        }
        int resposta = minimo(Hipotese.todas(moedas, modo));
        MINIMOS.put(chave, resposta);
        return resposta;
    }

    /** Quantas pesagens faltam, dado o que ainda esta em aberto. */
    public int minimo(long vivas) {
        if (resolvido(vivas)) {
            return 0;
        }
        Integer guardado = guardados.get(vivas);
        if (guardado != null) {
            return guardado;
        }
        guardados.put(vivas, IMPOSSIVEL);  // marca de visitado; toda pesagem encolhe o conjunto
        int melhor = IMPOSSIVEL;
        for (Pesagem pesagem : pesagens) {
            long[] partes = partir(pesagem, vivas);
            if (naoSeparaNada(partes, vivas)) {
                continue;
            }
            int pior = 0;
            for (long parte : partes) {
                if (parte == 0) {
                    continue;
                }
                pior = Math.max(pior, minimo(parte));
                if (pior + 1 >= melhor) {
                    break;
                }
            }
            melhor = Math.min(melhor, pior + 1);
        }
        guardados.put(vivas, melhor);
        return melhor;
    }

    /** A melhor primeira pesagem, para mostrar a estrategia. */
    public Pesagem melhorPrimeira() {
        long vivas = Hipotese.todas(moedas, modo);
        int alvo = minimo(vivas);
        for (Pesagem pesagem : pesagens) {
            long[] partes = partir(pesagem, vivas);
            if (naoSeparaNada(partes, vivas)) {
                continue;
            }
            int pior = 0;
            for (long parte : partes) {
                if (parte != 0) {
                    pior = Math.max(pior, minimo(parte));
                }
            }
            if (pior + 1 == alvo) {
                return pesagem;
            }
        }
        throw new IllegalStateException("nenhuma pesagem alcanca o minimo");
    }

    /** As tres mascaras que a pesagem produz: esquerda desce, direita desce, equilibra. */
    public long[] partir(Pesagem pesagem, long vivas) {
        long[] partes = new long[3];
        for (int indice = 0; indice < 2 * moedas; indice++) {
            if ((vivas & (1L << indice)) == 0) {
                continue;
            }
            int resultado = pesagem.resultado(Hipotese.doIndice(indice, moedas));
            partes[resultado + 1] |= 1L << indice;
        }
        return partes;
    }

    /** Ja da para responder? Depende do modo: as vezes duas hipoteses dao a mesma resposta. */
    public boolean resolvido(long vivas) {
        int quantas = Long.bitCount(vivas);
        if (quantas <= 1) {
            return true;
        }
        if (modo != Modo.SO_A_MOEDA) {
            return false;
        }
        int primeira = -1;
        for (int indice = 0; indice < 2 * moedas; indice++) {
            if ((vivas & (1L << indice)) == 0) {
                continue;
            }
            int moeda = Hipotese.doIndice(indice, moedas).moeda();
            if (primeira < 0) {
                primeira = moeda;
            } else if (primeira != moeda) {
                return false;
            }
        }
        return true;
    }

    private boolean naoSeparaNada(long[] partes, long vivas) {
        for (long parte : partes) {
            if (parte == vivas) {
                return true;
            }
        }
        return false;
    }

    /**
     * Existe estrategia que resolve em {@code pesagens} pesagens?
     *
     * <p>E uma pergunta mais barata que "qual o minimo", porque a contagem poda
     * antes de tentar: se sobraram mais hipoteses do que 3 elevado ao numero de
     * pesagens que restam, nao ha o que tentar. E assim que doze moedas em tres
     * pesagens cabe num teste rapido.
     */
    public boolean cabeEm(int pesagensRestantes) {
        return cabeEm(Hipotese.todas(moedas, modo), pesagensRestantes);
    }

    public boolean cabeEm(long vivas, int pesagensRestantes) {
        if (resolvido(vivas)) {
            return true;
        }
        if (pesagensRestantes == 0) {
            return false;
        }
        long alcance = 1;
        for (int i = 0; i < pesagensRestantes; i++) {
            alcance *= 3;
        }
        if (respostas(vivas) > alcance) {
            return false;
        }
        for (Pesagem pesagem : pesagens) {
            long[] partes = partir(pesagem, vivas);
            if (naoSeparaNada(partes, vivas)) {
                continue;
            }
            boolean todas = true;
            for (long parte : partes) {
                if (parte != 0 && !cabeEm(parte, pesagensRestantes - 1)) {
                    todas = false;
                    break;
                }
            }
            if (todas) {
                return true;
            }
        }
        return false;
    }

    /** Quantas respostas distintas ainda sao possiveis, que e o que precisa caber em 3^k. */
    public int respostas(long vivas) {
        if (modo != Modo.SO_A_MOEDA) {
            return Long.bitCount(vivas);
        }
        long moedasVivas = 0;
        for (int indice = 0; indice < 2 * moedas; indice++) {
            if ((vivas & (1L << indice)) != 0) {
                moedasVivas |= 1L << Hipotese.doIndice(indice, moedas).moeda();
            }
        }
        return Long.bitCount(moedasVivas);
    }

    /** As hipoteses de uma mascara, para a saida ficar legivel. */
    public List<Hipotese> hipoteses(long vivas) {
        List<Hipotese> lista = new ArrayList<>();
        for (int indice = 0; indice < 2 * moedas; indice++) {
            if ((vivas & (1L << indice)) != 0) {
                lista.add(Hipotese.doIndice(indice, moedas));
            }
        }
        return lista;
    }
}
