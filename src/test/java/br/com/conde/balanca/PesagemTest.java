package br.com.conde.balanca;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A balanca de pratos")
class PesagemTest {

    @Test
    @DisplayName("moeda fora dos dois pratos nao mexe no resultado")
    void moedaDeForaNaoPesa() {
        Pesagem pesagem = Classica.grupo("1,2", "3,4");

        assertThat(pesagem.resultado(new Hipotese(4, true))).isZero();
        assertThat(pesagem.resultado(new Hipotese(4, false))).isZero();
    }

    @Test
    @DisplayName("moeda pesada na esquerda faz a esquerda descer; leve faz subir")
    void oLadoQueDesce() {
        Pesagem pesagem = Classica.grupo("1,2", "3,4");

        assertThat(pesagem.resultado(new Hipotese(0, true))).isEqualTo(1);
        assertThat(pesagem.resultado(new Hipotese(0, false))).isEqualTo(-1);
        assertThat(pesagem.resultado(new Hipotese(2, true))).isEqualTo(-1);
        assertThat(pesagem.resultado(new Hipotese(2, false))).isEqualTo(1);
    }

    @Test
    @DisplayName("os dois pratos tem sempre o mesmo numero de moedas")
    void pratosEquilibrados() {
        for (int moedas = 2; moedas <= 8; moedas++) {
            for (Pesagem pesagem : Pesagem.todas(moedas)) {
                assertThat(Long.bitCount(pesagem.esquerda()))
                        .describedAs("%s", pesagem)
                        .isEqualTo(Long.bitCount(pesagem.direita()));
            }
        }
    }

    @Test
    @DisplayName("nenhuma moeda aparece nos dois pratos ao mesmo tempo")
    void semMoedaRepetida() {
        for (Pesagem pesagem : Pesagem.todas(7)) {
            assertThat(pesagem.esquerda() & pesagem.direita()).isZero();
        }
    }

    @Test
    @DisplayName("trocar os pratos de lado nao e outra pesagem, e a lista nao repete")
    void semEspelho() {
        List<Pesagem> todas = Pesagem.todas(6);

        for (Pesagem pesagem : todas) {
            Pesagem espelho = new Pesagem(pesagem.direita(), pesagem.esquerda());

            assertThat(todas).doesNotContain(espelho);
        }
    }

    @Test
    @DisplayName("com seis moedas existem exatamente 70 pesagens distintas")
    void quantasPesagensExistem() {
        assertThat(Pesagem.todas(2)).hasSize(1);
        assertThat(Pesagem.todas(3)).hasSize(3);
        assertThat(Pesagem.todas(4)).hasSize(9);
        assertThat(Pesagem.todas(6)).hasSize(70);
    }

    @Test
    @DisplayName("uma moeda falsa num prato sempre desequilibra, nunca empata")
    void moedaNoPratoSempreDesequilibra() {
        Pesagem pesagem = Classica.grupo("1,2,3", "4,5,6");

        for (int moeda = 0; moeda < 6; moeda++) {
            assertThat(pesagem.resultado(new Hipotese(moeda, true))).isNotZero();
            assertThat(pesagem.resultado(new Hipotese(moeda, false))).isNotZero();
        }
    }

    @Test
    @DisplayName("mais de treze moedas nao cabe nesta busca, e o construtor avisa")
    void tetoDoResolvedor() {
        assertThatThrownBy(() -> new Resolvedor(14, Modo.SABE_O_SENTIDO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("de 2 a 13");
    }
}
