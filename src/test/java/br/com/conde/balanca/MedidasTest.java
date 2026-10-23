package br.com.conde.balanca;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Os numeros publicados no README, presos em teste. */
@DisplayName("Os numeros do README")
class MedidasTest {

    private int exato(int moedas, Modo modo) {
        return new Resolvedor(moedas, modo).minimo();
    }

    @Test
    @DisplayName("tabela das pesagens: a coluna de quem sabe o sentido")
    void colunaDoSentidoConhecido() {
        assertThat(exato(2, Modo.SABE_O_SENTIDO)).isEqualTo(1);
        assertThat(exato(3, Modo.SABE_O_SENTIDO)).isEqualTo(1);
        assertThat(exato(4, Modo.SABE_O_SENTIDO)).isEqualTo(2);
        assertThat(exato(9, Modo.SABE_O_SENTIDO)).isEqualTo(2);
    }

    @Test
    @DisplayName("tabela das pesagens: as duas colunas de sentido desconhecido")
    void colunasDoSentidoDesconhecido() {
        assertThat(exato(3, Modo.SO_A_MOEDA)).isEqualTo(2);
        assertThat(exato(4, Modo.SO_A_MOEDA)).isEqualTo(2);
        assertThat(exato(5, Modo.SO_A_MOEDA)).isEqualTo(3);
        assertThat(exato(9, Modo.SO_A_MOEDA)).isEqualTo(3);

        assertThat(exato(3, Modo.MOEDA_E_SENTIDO)).isEqualTo(2);
        assertThat(exato(4, Modo.MOEDA_E_SENTIDO)).isEqualTo(3);
        assertThat(exato(9, Modo.MOEDA_E_SENTIDO)).isEqualTo(3);
    }

    @Test
    @DisplayName("a primeira pesagem otima poe cerca de um terco das moedas em cada prato")
    void aPrimeiraPesagemOtima() {
        assertThat(new Resolvedor(9, Modo.MOEDA_E_SENTIDO).melhorPrimeira().tamanho()).isEqualTo(3);
        assertThat(new Resolvedor(9, Modo.SABE_O_SENTIDO).melhorPrimeira().tamanho()).isEqualTo(3);
        assertThat(new Resolvedor(7, Modo.MOEDA_E_SENTIDO).melhorPrimeira().tamanho()).isEqualTo(2);
    }

    @Test
    @DisplayName("quantas pesagens distintas existem, por numero de moedas")
    void quantasPesagens() {
        assertThat(new Resolvedor(5, Modo.SABE_O_SENTIDO).pesagensPossiveis()).hasSize(25);
        assertThat(new Resolvedor(7, Modo.SABE_O_SENTIDO).pesagensPossiveis()).hasSize(196);
        assertThat(new Resolvedor(9, Modo.SABE_O_SENTIDO).pesagensPossiveis()).hasSize(1569);
    }

    @Test
    @DisplayName("o numero de estados guardados cresce rapido, e e por isso que 12 moedas nao cabe")
    void oCustoDaBusca() {
        Resolvedor sete = new Resolvedor(7, Modo.MOEDA_E_SENTIDO);
        Resolvedor nove = new Resolvedor(9, Modo.MOEDA_E_SENTIDO);
        // direto no conjunto, sem passar pela resposta guardada do problema inteiro:
        // o que se quer medir aqui e o tamanho da busca, nao o tempo de consulta
        sete.minimo(Hipotese.todas(7, Modo.MOEDA_E_SENTIDO));
        nove.minimo(Hipotese.todas(9, Modo.MOEDA_E_SENTIDO));

        assertThat(sete.guardados()).isGreaterThan(1000);
        assertThat(nove.guardados()).isGreaterThan(9000);
        assertThat(nove.guardados()).isGreaterThan(5 * sete.guardados());
    }

    @Test
    @DisplayName("o medidor roda inteiro sem lancar")
    void medidorRoda() {
        Medidor.pesagens();
        Medidor.limite();
        Medidor.estrategia();
    }
}
