# balanca

A moeda falsa e a balança de pratos, do zero em Java 21: **quantas pesagens são
realmente necessárias**, achadas por busca exata, contra o que o limite de
contagem promete.

O argumento de contagem é conhecido: cada pesagem tem três resultados, então k
pesagens distinguem no máximo 3^k respostas. Ele dá um **piso**, e é correto.
Este repositório mede o quanto esse piso é alcançável — e a resposta é: menos do
que parece.

```
mvn -B -ntp verify
mvn -B -ntp exec:java -Dexec.args="pesagens"   # (ou limite, estrategia)
```

## Três problemas que parecem um só

| modo | o que se sabe | o que se precisa responder | casos |
|---|---|---|---:|
| sabe que é mais leve | a falsa é mais leve | qual moeda | n |
| só apontar a moeda | nada | qual moeda | 2n |
| moeda e sentido | nada | qual moeda **e** se é leve ou pesada | 2n |

## Quantas pesagens, de verdade

| moedas | sabe que é mais leve | só apontar a moeda | moeda e sentido |
|---:|---:|---:|---:|
| 2 | 1 | **impossível** | **impossível** |
| 3 | 1 | 2 | 2 |
| 4 | 2 | 2 | 3 |
| 5 | 2 | 3 | 3 |
| 6 | 2 | 3 | 3 |
| 7 | 2 | 3 | 3 |
| 8 | 2 | 3 | 3 |
| 9 | 2 | 3 | 3 |

**Com duas moedas e sentido desconhecido o problema não é caro: é impossível.**
A única pesagem que existe é uma contra a outra, e ela sempre desequilibra sem
dizer qual das duas é a culpada. Mil pesagens não resolvem, porque a segunda
daria o mesmo resultado da primeira. O limite de contagem, aqui, diz que uma
pesagem bastaria.

## O piso de contagem e a resposta exata

| modo | moedas | piso por contagem | exato | o piso basta? |
|---|---:|---:|---:|---|
| sabe que é mais leve | 2 a 9 | 1 ou 2 | 1 ou 2 | **sempre** |
| só apontar a moeda | 4 | 2 | 2 | sim |
| só apontar a moeda | 5 | 2 | **3** | não, falta uma |
| moeda e sentido | 3 | 2 | 2 | sim |
| moeda e sentido | 4 | 2 | **3** | não, falta uma |
| moeda e sentido | 2 | 2 | **impossível** | nem com infinitas |

Quando se sabe o sentido, o piso **sempre** é alcançado: dividir em três partes
iguais faz exatamente o que a contagem promete, e é por isso que esse caso dá a
impressão de que a contagem resolve o problema.

Quando o sentido é desconhecido, o piso falha. O motivo é a restrição física que
a contagem ignora: **os dois pratos precisam ter o mesmo número de moedas**. Com
quatro moedas e dois resultados por moeda, 8 casos cabem folgados em 9 — e
mesmo assim não existe pesagem que divida esses 8 casos em três grupos de no
máximo 3.

E repare na terceira coluna: **não precisar do sentido quase não ajuda.** De 5 a
9 moedas, apontar a moeda custa exatamente as mesmas pesagens que apontar a
moeda e dizer se ela é leve ou pesada. A informação do sentido sai de graça.

## A estratégia famosa, conferida

Doze moedas, três pesagens, achando a moeda **e** o sentido:

```
1a:  {1,2,3,4} contra {5,6,7,8}
2a:  equilibrou -> {9,10,11} contra {1,2,3}
     esquerda   -> {1,2,5} contra {3,4,6}
     direita    -> {5,6,1} contra {7,8,2}
3a:  uma moeda contra uma moeda, conforme o caminho
```

```
casos separados: 24 de 24
nenhum par de casos cai no mesmo caminho de resultados
```

A conferência não olha rótulo de folha nenhum. Ela pergunta uma coisa só: os 24
casos produzem 24 sequências de resultados **diferentes**? Se dois casos caem no
mesmo caminho, a estratégia não os distingue, e o que estiver escrito na folha é
chute. Dos 27 caminhos possíveis, 24 são usados e o caminho "equilibrou três
vezes" sobra — é ele que serviria para uma décima terceira moeda, se a pergunta
fosse só apontá-la.

Repare também na segunda pesagem do ramo que equilibrou: ela usa as moedas 1, 2
e 3, **já inocentadas**, como peso de referência. Esquecer que isso é permitido
é o que faz muita gente achar que doze moedas não cabem em três pesagens.

## Por que doze não sai da busca

| moedas | pesagens possíveis | estados visitados |
|---:|---:|---:|
| 5 | 25 | 109 |
| 7 | 196 | 1.050 |
| 9 | 1.569 | 9.897 |

As duas colunas crescem juntas, e o produto delas é o custo. Com doze moedas são
mais de trinta mil pesagens possíveis só no primeiro nível, e a memória não
ajuda porque cada ramo encontra um conjunto de hipóteses diferente. Por isso a
estratégia de doze está escrita à mão aqui e é **verificada**, não procurada: a
busca exata responde até nove, e daí em diante a resposta vem de outro lugar.

## O que as medidas me corrigiram

1. **"O limite de contagem dá o número de pesagens."** Dá um piso. Com quatro
   moedas e sentido desconhecido ele diz 2 e a resposta é 3, porque ele ignora
   que os pratos precisam ter o mesmo número de moedas.
2. **"Com poucas moedas é sempre mais fácil."** Com duas moedas e sentido
   desconhecido é impossível, enquanto com três é possível em duas pesagens. O
   problema fica mais fácil ao ganhar uma moeda.
3. **"Não precisar do sentido economiza pesagem."** De 5 a 9 moedas não
   economiza nada: as duas perguntas custam 3.
4. **"A pesagem só usa as moedas suspeitas."** Usa as inocentadas também, como
   referência, e a estratégia clássica depende disso na segunda pesagem.

## Como está organizado

- `Modo`: os três problemas e o piso de contagem de cada um.
- `Hipotese`: um caso possível (moeda i, leve ou pesada). O conjunto vivo é uma
  máscara de bits, porque é ele o estado da busca.
- `Pesagem`: dois grupos do mesmo tamanho, e o que a balança mostra para cada
  hipótese. A geração descarta os espelhos (trocar os pratos não é outra
  pesagem).
- `Resolvedor`: minimax sobre o conjunto vivo, com memória por conjunto. Devolve
  `IMPOSSIVEL` quando nenhuma pesagem separa nada, que acontece de verdade.
- `Classica`: a estratégia de doze moedas escrita à mão e verificada caso a caso.
- `Medidor`: as três tabelas acima, rodando no CI nos três sistemas.

28 testes.
