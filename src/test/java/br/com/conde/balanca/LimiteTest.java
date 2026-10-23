package br.com.conde.balanca;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O limite de contagem diz que com k pesagens ha 3^k respostas possiveis, entao
 * menos que isso e impossivel. O numero e correto e e um PISO: estes testes
 * mostram onde ele nao e alcancavel.
 */
@DisplayName("O limite de contagem contra a busca exata")
class LimiteTest {

    private int exato(int moedas, Modo modo) {
        return new Resolvedor(moedas, modo).minimo();
    }

    @Test
    @DisplayName("sabendo o sentido, o piso e sempre alcancado: log na base 3 basta")
    void sabendoOSentidoOPisoBasta() {
        for (int moedas = 2; moedas <= 9; moedas++) {
            assertThat(exato(moedas, Modo.SABE_O_SENTIDO))
                    .describedAs("%d moedas", moedas)
                    .isEqualTo(Modo.SABE_O_SENTIDO.limiteDeContagem(moedas));
        }
    }

    @Test
    @DisplayName("sem saber o sentido, com QUATRO moedas o piso diz 2 e a resposta e 3")
    void comQuatroOPisoNaoBasta() {
        assertThat(Modo.MOEDA_E_SENTIDO.limiteDeContagem(4)).isEqualTo(2);
        assertThat(exato(4, Modo.MOEDA_E_SENTIDO)).isEqualTo(3);
    }

    @Test
    @DisplayName("para so apontar a moeda, o piso falha a partir de cinco")
    void soApontarFalhaEmCinco() {
        assertThat(Modo.SO_A_MOEDA.limiteDeContagem(5)).isEqualTo(2);
        assertThat(exato(5, Modo.SO_A_MOEDA)).isEqualTo(3);

        assertThat(exato(4, Modo.SO_A_MOEDA)).isEqualTo(Modo.SO_A_MOEDA.limiteDeContagem(4));
    }

    @Test
    @DisplayName("com DUAS moedas e sentido desconhecido nao e caro: e impossivel")
    void comDuasEImpossivel() {
        assertThat(Modo.MOEDA_E_SENTIDO.limiteDeContagem(2)).isEqualTo(2);
        assertThat(exato(2, Modo.MOEDA_E_SENTIDO)).isEqualTo(Resolvedor.IMPOSSIVEL);
        assertThat(exato(2, Modo.SO_A_MOEDA)).isEqualTo(Resolvedor.IMPOSSIVEL);

        assertThat(exato(2, Modo.SABE_O_SENTIDO)).isEqualTo(1);
    }

    @Test
    @DisplayName("o exato nunca fica abaixo do piso: a contagem esta certa como limite")
    void oPisoNuncaEFurado() {
        for (Modo modo : Modo.values()) {
            for (int moedas = 2; moedas <= 8; moedas++) {
                int exato = exato(moedas, modo);
                if (exato == Resolvedor.IMPOSSIVEL) {
                    continue;
                }
                assertThat(exato)
                        .describedAs("%s com %d moedas", modo.rotulo(), moedas)
                        .isGreaterThanOrEqualTo(modo.limiteDeContagem(moedas));
            }
        }
    }

    @Test
    @DisplayName("saber o sentido vale uma pesagem inteira a partir de quatro moedas")
    void saberOSentidoEconomiza() {
        for (int moedas = 4; moedas <= 9; moedas++) {
            assertThat(exato(moedas, Modo.SABE_O_SENTIDO))
                    .describedAs("%d moedas", moedas)
                    .isLessThan(exato(moedas, Modo.MOEDA_E_SENTIDO));
        }
    }

    @Test
    @DisplayName("nao precisar do sentido nao economiza quase nada")
    void naoPrecisarDoSentidoQuaseNaoAjuda() {
        for (int moedas = 5; moedas <= 9; moedas++) {
            assertThat(exato(moedas, Modo.SO_A_MOEDA))
                    .describedAs("%d moedas", moedas)
                    .isEqualTo(exato(moedas, Modo.MOEDA_E_SENTIDO));
        }
    }
}
