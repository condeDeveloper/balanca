package br.com.conde.balanca;

import java.util.List;
import java.util.Locale;

/**
 * As tabelas do README. Uso:
 * {@code mvn exec:java -Dexec.args="pesagens"} (ou {@code limite}, {@code estrategia}).
 */
public final class Medidor {

    private static final int TETO = 9;

    private Medidor() {
    }

    public static void main(String[] args) {
        String tabela = args.length > 0 ? args[0] : "tudo";
        boolean tudo = tabela.equals("tudo");
        if (tudo || tabela.equals("pesagens")) {
            pesagens();
        }
        if (tudo || tabela.equals("limite")) {
            limite();
        }
        if (tudo || tabela.equals("estrategia")) {
            estrategia();
        }
    }

    /** Quantas pesagens cada problema precisa, de verdade. */
    public static void pesagens() {
        System.out.println();
        System.out.println("PESAGENS  (o minimo no pior caso, por busca exata)");
        System.out.printf(Locale.ROOT, "%-8s", "moedas");
        for (Modo modo : Modo.values()) {
            System.out.printf(Locale.ROOT, " %24s", modo.rotulo());
        }
        System.out.println();
        for (int moedas = 2; moedas <= TETO; moedas++) {
            System.out.printf(Locale.ROOT, "%-8d", moedas);
            for (Modo modo : Modo.values()) {
                int exato = new Resolvedor(moedas, modo).minimo();
                System.out.printf(Locale.ROOT, " %24s", texto(exato));
            }
            System.out.println();
        }
        System.out.println();
    }

    /** O limite de contagem contra a resposta exata. */
    public static void limite() {
        System.out.println();
        System.out.println("LIMITE  (o piso por contagem contra o numero que a busca encontra)");
        System.out.printf(Locale.ROOT, "%-24s %8s %10s %8s %s%n",
                "modo", "moedas", "contagem", "exato", "o piso basta?");
        for (Modo modo : Modo.values()) {
            for (int moedas = 2; moedas <= TETO; moedas++) {
                int piso = modo.limiteDeContagem(moedas);
                int exato = new Resolvedor(moedas, modo).minimo();
                String veredito = exato == Resolvedor.IMPOSSIVEL ? "NEM COM INFINITAS"
                        : exato == piso ? "sim" : "NAO, precisa de " + (exato - piso) + " a mais";
                System.out.printf(Locale.ROOT, "%-24s %8d %10d %8s %s%n",
                        modo.rotulo(), moedas, piso, texto(exato), veredito);
            }
        }
        System.out.println();
    }

    /** A primeira pesagem de cada caso, e a estrategia famosa de doze moedas. */
    public static void estrategia() {
        System.out.println();
        System.out.println("ESTRATEGIA  (a melhor primeira pesagem, e quantas existem)");
        System.out.printf(Locale.ROOT, "%-24s %8s %12s %10s %s%n",
                "modo", "moedas", "pesagens", "estados", "primeira pesagem");
        for (Modo modo : Modo.values()) {
            for (int moedas : new int[]{5, 7, 9}) {
                Resolvedor resolvedor = new Resolvedor(moedas, modo);
                int minimo = resolvedor.minimo();
                String primeira = minimo == Resolvedor.IMPOSSIVEL
                        ? "-" : resolvedor.melhorPrimeira().toString();
                System.out.printf(Locale.ROOT, "%-24s %8d %12d %10d %s%n",
                        modo.rotulo(), moedas, resolvedor.pesagensPossiveis().size(),
                        resolvedor.guardados(), primeira);
            }
        }
        System.out.println();
        System.out.println("A ESTRATEGIA FAMOSA  (12 moedas, 3 pesagens, moeda e sentido)");
        System.out.println("  1a:  {1,2,3,4} contra {5,6,7,8}");
        System.out.println("  2a:  equilibrou -> {9,10,11} contra {1,2,3}");
        System.out.println("       esquerda   -> {1,2,5} contra {3,4,6}");
        System.out.println("       direita    -> {5,6,1} contra {7,8,2}");
        System.out.println("  3a:  uma moeda contra uma moeda, conforme o caminho");
        System.out.println();
        System.out.println("  casos separados: " + Classica.casosDistinguidos() + " de 24");
        List<List<Hipotese>> confusoes = Classica.confusoes();
        if (confusoes.isEmpty()) {
            System.out.println("  nenhum par de casos cai no mesmo caminho de resultados");
        } else {
            confusoes.forEach(par -> System.out.println("  CONFUNDE: " + par));
        }
        System.out.println();
    }

    private static String texto(int pesagens) {
        return pesagens == Resolvedor.IMPOSSIVEL ? "impossivel" : String.valueOf(pesagens);
    }
}
