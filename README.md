# Montante do Cometa (Forge 1.20.1)

Mod de uma espadona com modelo animado (GeckoLib 4) e habilidade **Queda do Cometa**.

## Como compilar
1. Instale o JDK 17.
2. Copie `gradlew`, `gradlew.bat` e a pasta `gradle/` do MDK oficial do Forge 1.20.1
   (https://files.minecraftforge.net/ -> 1.20.1 -> Mdk) para esta pasta.
3. Rode `./gradlew build`. O .jar sai em `build/libs/`.
4. Para testar direto: `./gradlew runClient`.

No servidor e nos clientes são necessários: **Forge 47.x**, **GeckoLib 4.4+ para 1.20.1** e este .jar.

## Textura (IMPORTANTE)
O arquivo `src/main/resources/assets/montante_do_cometa/textures/item/montante_do_cometa.png`
é só um **placeholder** (gradiente azul). O modelo precisa do PNG original **512x1024**
feito junto com o modelo. Substitua mantendo o mesmo nome.

## Item
- ID: `montante_do_cometa:montante_do_cometa` (`/give @s montante_do_cometa:montante_do_cometa`)
- Dano 10, velocidade de ataque 1.0, material netherite, resistente a fogo, raridade épica.
- Receita: estrela do Nether + 2 end rods + espada de netherite + blaze rod (ver `data/.../recipes`).
- Botão direito: raio de 24 blocos, 14 de dano em área (raio 4), fogo por 6s, recarga 8s, gasta 5 de durabilidade.
  Os números ficam no topo de `MontanteDoCometaItem.java`.

## Ajuste de tamanho/posição na mão
Edite `assets/montante_do_cometa/models/item/montante_do_cometa.json` (campo `display`).
O modelo tem ~5,4 blocos de altura, por isso as escalas são pequenas. Se a espada aparecer
espelhada ou torta no inventário/mão, troque o sinal ou o valor das rotações.

## Compilar sem instalar nada (GitHub Actions)
1. Crie um repositório no GitHub e envie todo o conteudo desta pasta (incluindo `.github/`).
2. Abra a aba **Actions** do repositorio e espere o workflow "Build mod" terminar (uns 3-5 min).
3. Clique na execucao concluida e baixe o artefato **montante_do_cometa-jar** (contem o .jar).
