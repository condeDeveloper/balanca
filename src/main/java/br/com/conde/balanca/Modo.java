package br.com.conde.balanca;

/**
 * O que se sabe antes de comecar, e o que se precisa descobrir.
 *
 * <p>Os tres modos parecem o mesmo problema e nao sao: o numero de respostas
 * possiveis muda, e com ele o numero de pesagens necessarias.
 */
public enum Modo {

    /**
     * Sabe-se que a moeda falsa e mais LEVE. Resta descobrir qual: sao n respostas
     * possiveis.
     */
    SABE_O_SENTIDO("sabe que e mais leve"),

    /**
     * Nao se sabe se a falsa e mais leve ou mais pesada, e basta apontar qual e:
     * sao 2n casos possiveis, mas so n respostas.
     */
    SO_A_MOEDA("so apontar a moeda"),

    /**
     * Nao se sabe o sentido, e e preciso dizer qual moeda E se ela e leve ou
     * pesada: 2n casos e 2n respostas.
     */
    MOEDA_E_SENTIDO("moeda e sentido");

    private final String rotulo;

    Modo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    /** Quantos casos o mundo pode apresentar com {@code moedas} moedas. */
    public int casos(int moedas) {
        return this == SABE_O_SENTIDO ? moedas : 2 * moedas;
    }

    /**
     * O limite de contagem: com k pesagens ha 3^k resultados distintos, e cada
     * resposta precisa de um resultado so dela. Menos pesagens que isto e
     * impossivel, por contagem.
     *
     * <p>Este numero e um PISO, e o repositorio inteiro existe para mostrar
     * quando ele nao e alcancavel.
     */
    public int limiteDeContagem(int moedas) {
        int respostas = this == SABE_O_SENTIDO ? moedas
                : this == SO_A_MOEDA ? moedas : 2 * moedas;
        int pesagens = 0;
        long alcance = 1;
        while (alcance < respostas) {
            alcance *= 3;
            pesagens++;
        }
        return pesagens;
    }
}
