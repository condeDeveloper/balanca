package br.com.conde.balanca;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A estrategia famosa de doze moedas, conferida caso a caso.
 *
 * <p>A conferencia nao confia em rotulo de folha: ela pergunta se os 24 casos
 * produzem 24 caminhos de resultados diferentes. Se dois casos caem no mesmo
 * caminho, a estrategia nao os distingue, e o que estiver escrito na folha e
 * chute.
 */
@DisplayName("A estrategia de doze moedas")
class ClassicaTest {

    @Test
    @DisplayName("separa os 24 casos, cada um com o seu caminho de resultados")
    void separaTodosOsCasos() {
        assertThat(Classica.casosDistinguidos()).isEqualTo(24);
        assertThat(Classica.confusoes()).isEmpty();
    }

    @Test
    @DisplayName("usa exatamente tres pesagens para todo caso, nunca mais")
    void sempreTresPesagens() {
        for (Hipotese hipotese : Classica.todas()) {
            assertThat(Classica.caminho(hipotese))
                    .describedAs("%s", hipotese)
                    .hasSize(3);
        }
    }

    @Test
    @DisplayName("ha 24 caminhos distintos dentro dos 27 possiveis")
    void vinteEQuatroDosVinteESete() {
        Set<List<Integer>> caminhos = new HashSet<>();
        for (Hipotese hipotese : Classica.todas()) {
            caminhos.add(Classica.caminho(hipotese));
        }

        assertThat(caminhos).hasSize(24);
        assertThat(caminhos.size()).isLessThan(27);
    }

    @Test
    @DisplayName("o caminho de tres equilibrios nao e usado: ele sobraria para a 13a moeda")
    void oCaminhoQueSobra() {
        Set<List<Integer>> caminhos = new HashSet<>();
        for (Hipotese hipotese : Classica.todas()) {
            caminhos.add(Classica.caminho(hipotese));
        }

        assertThat(caminhos).doesNotContain(List.of(0, 0, 0));
    }

    @Test
    @DisplayName("a moeda que fica de fora da primeira pesagem equilibra na primeira")
    void aQueFicaDeFora() {
        for (int moeda = 8; moeda < 12; moeda++) {
            assertThat(Classica.caminho(new Hipotese(moeda, true)).get(0)).isZero();
            assertThat(Classica.caminho(new Hipotese(moeda, false)).get(0)).isZero();
        }
    }

    @Test
    @DisplayName("os casos simetricos tem caminhos simetricos: leve e pesada se espelham")
    void simetria() {
        for (int moeda = 0; moeda < 12; moeda++) {
            List<Integer> pesada = Classica.caminho(new Hipotese(moeda, true));
            List<Integer> leve = Classica.caminho(new Hipotese(moeda, false));

            assertThat(pesada).isNotEqualTo(leve);
        }
    }

    @Test
    @DisplayName("a estrategia so usa pesagens com o mesmo numero de moedas nos dois pratos")
    void pratosEquilibrados() {
        Pesagem primeira = Classica.grupo("1,2,3,4", "5,6,7,8");

        assertThat(primeira.tamanho()).isEqualTo(4);
        assertThat(Long.bitCount(primeira.esquerda()))
                .isEqualTo(Long.bitCount(primeira.direita()));
    }
}
