package br.com.conde.balanca;

/**
 * Um caso possivel do mundo: a moeda {@code moeda} e a falsa, e ela e leve ou
 * pesada.
 *
 * <p>O conjunto de hipoteses ainda vivas e tudo que o resolvedor sabe. Comeca
 * com todas e vai encolhendo a cada pesagem; quando sobra uma so, acabou. Por
 * isso o estado e representado como uma mascara de bits: a hipotese de indice
 * {@code i} e o bit {@code i}, e uma pesagem vira tres mascaras.
 */
public record Hipotese(int moeda, boolean pesada) {

    /** O indice desta hipotese na mascara, com {@code moedas} moedas no total. */
    public int indice(int moedas) {
        return pesada ? moedas + moeda : moeda;
    }

    public static Hipotese doIndice(int indice, int moedas) {
        return indice < moedas
                ? new Hipotese(indice, false)
                : new Hipotese(indice - moedas, true);
    }

    /** Todas as hipoteses do modo, como mascara de bits. */
    public static long todas(int moedas, Modo modo) {
        long mascara = 0;
        for (int moeda = 0; moeda < moedas; moeda++) {
            mascara |= 1L << moeda;
            if (modo != Modo.SABE_O_SENTIDO) {
                mascara |= 1L << (moedas + moeda);
            }
        }
        return mascara;
    }

    @Override
    public String toString() {
        return "moeda " + (moeda + 1) + (pesada ? " pesada" : " leve");
    }
}
