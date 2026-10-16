# Selenium Automation Framework: Sauce Demo

Framework de automação de testes web em **Java + Selenium WebDriver + TestNG**, seguindo o padrão **Page Object Model (POM)**.
A aplicação testada é o [Sauce Demo](https://www.saucedemo.com/), uma loja virtual de demonstração.

**Recursos:**

- Testes orientados a dados com **Excel**, **JSON**, dados fixos e parâmetros do XML do TestNG.
- Testes com **todos os perfis de usuário** do Sauce Demo, inclusive os que têm defeitos de propósito.
- Páginas **dinâmicas** (Dynamic Catalog): carregamento com spinner, lista infinita com lazy load e slider automático, sempre com espera explícita, nunca `Thread.sleep`.
- Validação de **arquivo baixado**: o recibo do pedido em PDF é baixado pelo navegador e tem o conteúdo conferido.
- Relatório HTML (Extent Reports) com prints, logs (Log4j2) e envio do relatório por e-mail.
- Navegador e modo headless escolhidos por configuração.
- Integração contínua com **GitHub Actions** e **Jenkins**.

---

## Tecnologias

| Ferramenta | Para que serve |
|---|---|
| Java 17 + Maven | Linguagem e gerenciador de build/dependências |
| Selenium WebDriver 4 | Controla o navegador |
| TestNG | Organiza e executa os testes (anotações, DataProvider, suítes, listeners) |
| Apache POI | Lê os dados de teste das planilhas Excel |
| Jackson | Lê os dados de teste dos arquivos JSON |
| Apache PDFBox | Lê o texto do recibo em PDF baixado |
| Extent Reports | Gera o relatório HTML da execução |
| Log4j2 | Gera os logs no console e em arquivo |
| Jakarta Mail | Envia o relatório por e-mail |
| GitHub Actions / Jenkins | Executam os testes automaticamente (CI) |

---

## Estrutura de pastas

```
selenium-framework/
├── pom.xml                          # Dependências e configuração do Maven/Surefire
├── README.md
├── Jenkinsfile                      # Pipeline do Jenkins
├── .github/workflows/ci.yml         # Pipeline do GitHub Actions
├── src/
│   ├── main/java/com/test/automation/      # FRAMEWORK: código reutilizável, sem testes
│   │   ├── config/
│   │   │   └── ConfigReader.java           # Lê o config.properties
│   │   ├── data/                           # Modelos de dados (usados pelas páginas e pelos testes)
│   │   │   ├── User.java                   # enum com os usuários do Sauce Demo
│   │   │   ├── Product.java                # enum com os produtos do catálogo (nome, id, preço)
│   │   │   ├── ProductScenario.java        # record: um cenário do teste de detalhes do produto
│   │   │   └── OrderScenario.java          # record: um cenário do cálculo do resumo do pedido
│   │   ├── driver/
│   │   │   └── DriverFactory.java          # Cria o navegador (chrome/firefox/edge, headless) e define a pasta de downloads
│   │   ├── pages/                          # PAGE OBJECTS: uma classe por página
│   │   │   ├── BasePage.java               # Base de todas as páginas: esperas e ações comuns
│   │   │   ├── LoginPage.java
│   │   │   ├── InventoryPage.java          # Lista de produtos e ordenação
│   │   │   ├── ProductDetailsPage.java     # Detalhes de um produto
│   │   │   ├── CartPage.java               # Carrinho
│   │   │   ├── CheckoutInformationPage.java# Checkout passo 1: dados do comprador
│   │   │   ├── CheckoutOverviewPage.java   # Checkout passo 2: resumo e valores
│   │   │   ├── CheckoutCompletePage.java   # Confirmação do pedido
│   │   │   ├── SpinnerPage.java            # Dynamic Catalog: spinner de carregamento
│   │   │   ├── LazyLoadPage.java           # Dynamic Catalog: lista infinita com lazy load
│   │   │   ├── SliderPage.java             # Dynamic Catalog: slider automático
│   │   │   └── components/                 # Partes que se repetem em várias páginas
│   │   │       ├── HeaderComponent.java    # Menu lateral e ícone do carrinho
│   │   │       └── FooterComponent.java    # Redes sociais e copyright
│   │   ├── reports/
│   │   │   └── ExtentReportManager.java    # Cria o relatório HTML
│   │   ├── listeners/
│   │   │   └── EmailReportListener.java    # Envia o relatório por e-mail ao fim da suíte
│   │   └── utils/                          # Utilitários genéricos
│   │       ├── ExcelUtils.java             # Leitura das planilhas
│   │       ├── JsonUtils.java              # Leitura dos arquivos JSON
│   │       ├── EmailUtils.java             # Envio de e-mail (SMTP)
│   │       ├── ScreenshotUtils.java        # Captura de tela
│   │       ├── DownloadUtils.java          # Limpa a pasta de downloads e espera o arquivo terminar de baixar
│   │       ├── PdfUtils.java               # Extrai o texto de um PDF (PDFBox)
│   │       └── Log.java                    # Atalho para o Log4j2
│   ├── main/resources/
│   │   └── log4j2.xml                      # Configuração dos logs
│   ├── test/java/com/test/automation/      # TESTES
│   │   ├── base/
│   │   │   └── BaseTest.java               # Abre/fecha navegador, relatório e prints
│   │   ├── dataproviders/                  # De onde vêm os dados de cada módulo
│   │   │   ├── LoginDataProvider.java      # Excel e dados fixos do login
│   │   │   ├── CheckoutDataProvider.java   # Excel do carrinho e do checkout + JSON do resumo do pedido
│   │   │   ├── ProductDataProvider.java    # JSON dos cenários de produto
│   │   │   ├── UserDataProvider.java       # Lista de usuários que fazem login
│   │   │   └── DynamicCatalogDataProvider.java # Cenários do Lazy Load (dados fixos)
│   │   └── tests/                          # Uma pasta por módulo/funcionalidade
│   │       ├── login/
│   │       │   ├── BaseLoginTest.java      # Validações comuns do login (SUCCESS x ERROR)
│   │       │   ├── LoginTest.java          # Um método por cenário (aula 1)
│   │       │   ├── LoginTestData.java      # Data-driven: Excel ou dados fixos
│   │       │   └── LoginTestParameters.java# Dados vindos do XML do TestNG
│   │       ├── inventory/
│   │       │   ├── ProductSortTest.java          # Ordenação, para cada usuário
│   │       │   ├── BaseProductDetailsTest.java   # Validações comuns dos detalhes do produto
│   │       │   ├── ProductDetailsTest.java       # Detalhes com o usuário padrão (regressão)
│   │       │   └── ProductDetailsUsersTest.java  # Detalhes com os outros usuários (defeitos)
│   │       ├── cart/
│   │       │   ├── CartTest.java
│   │       │   ├── RemoveFromCartTest.java # Remover pela lista de produtos e pelo carrinho
│   │       │   └── CartPersistenceTest.java# Carrinho mantido ao navegar, recarregar e após logout/login
│   │       ├── checkout/
│   │       │   ├── CheckoutTest.java
│   │       │   ├── CancelCheckoutTest.java # Cancel em cada passo e Continue Shopping
│   │       │   ├── OrderSummaryTest.java   # Cálculo do resumo: item total, taxa e total
│   │       │   └── OrderReceiptPdfTest.java# Recibo em PDF: download e conteúdo
│   │       ├── navigation/                 # Partes presentes em todas as páginas
│   │       │   ├── SideMenuTest.java       # Menu lateral: All Items, About, Logout, Reset App State
│   │       │   └── FooterTest.java         # Rodapé: redes sociais e copyright
│   │       └── dynamiccatalog/             # Páginas dinâmicas do menu Dynamic Catalog
│   │           ├── SpinnerTest.java        # Spinner some e os produtos aparecem
│   │           ├── LazyLoadTest.java       # Lista infinita e cards que só carregam quando aparecem
│   │           └── SliderTest.java         # Troca automática, pontos e volta ao primeiro
│   └── test/resources/
│       ├── config.properties.example       # Modelo de configuração (versionado)
│       ├── config.properties               # Configuração local com senha (NÃO versionado)
│       ├── testdata/                       # Massa de dados
│       │   ├── LoginData.xlsx              # aba Login
│       │   ├── CheckoutData.xlsx           # abas Cart e Checkout
│       │   ├── product-scenarios.json      # detalhes do produto: usuário padrão
│       │   ├── product-scenarios-users.json# detalhes do produto: outros usuários
│       │   └── order-scenarios.json        # cálculo do resumo do pedido e recibo em PDF
│       └── suites/                         # Suítes do TestNG
│           ├── regression.xml              # Regressão (padrão do mvn test): deve ficar verde
│           ├── login.xml
│           ├── login-parameters.xml        # Login com parâmetros no próprio XML
│           ├── checkout.xml
│           ├── inventory.xml               # Ordenação e detalhes do produto
│           ├── product-users.xml           # Detalhes do produto com os outros usuários
│           ├── navigation.xml              # Menu lateral e rodapé
│           └── dynamic-catalog.xml         # Spinner, Lazy Load e Slider
├── docs/exemplos-aula/                     # Material de estudo, fora da compilação
│   └── LoginPageBasica.java                # Page Object com By (antes do PageFactory)
├── reports/      # (gerado) relatórios HTML           ┐
├── screenshots/  # (gerado) prints SUCCESS/FAILURE/SKIP │
├── downloads/    # (gerado) PDFs baixados nos testes    ├─ ignorados pelo Git
├── logs/         # (gerado) selenium-framework.log     │
└── target/       # (gerado) build do Maven            ┘
```

### Por que `main` e `test`?

- **`src/main`** guarda o **framework**: o que é reutilizável e não é teste (páginas, navegador, utilitários, relatório, modelos de dados).
- **`src/test`** guarda os **testes** e tudo o que só existe para eles (classe base de teste, DataProviders, massa de dados, suítes, configuração).

O Maven compila o `src/main` **primeiro e sozinho**: ele não enxerga nada do `src/test`. Já o `src/test` enxerga tudo do `src/main`. Por isso, se uma classe de `src/main` precisa de algo (a `LoginPage` usa o `User`), esse algo também tem que estar em `src/main`.

### As camadas do Page Object

```
Teste (tests/)  ──usa──▶  Página (pages/)  ──usa──▶  Selenium WebDriver
   "o quê"                  "como"                     navegador
   Asserts                  locators + ações
```

- A **página** sabe *como* interagir: localizadores (`@FindBy`) e ações (`login`, `addProductToCart`). **Nunca faz Assert.** Ela executa ações e responde perguntas (true/false, textos, listas).
- O **teste** diz *o que* validar: monta o cenário chamando as páginas e decide se passou com `Assert`.
- Se um botão mudar no site, só a página muda. Os testes continuam iguais.
- Ações que levam a outra página devolvem essa página (ex.: `cartPage.goToCheckout()` devolve `CheckoutInformationPage`).
- Partes que se repetem em várias telas são **componentes** (`HeaderComponent`, `FooterComponent`). Cada página os oferece por `header()` e `footer()`: `inventoryPage.header().openCart()`.

**Escolha dos localizadores**, nesta ordem: `id` → `name` → `data-test` → classe estável → XPath relativo. Nunca XPath absoluto nem por posição.

**`@FindBy` ou `By`?** Elemento que **sempre existe** na página (mesmo escondido) usa `@FindBy`. Elemento que é **criado e removido** conforme a tela (contador do carrinho, submenu, mensagem de erro) usa `By` com `driver.findElements(...)`, que devolve uma lista vazia em vez de erro.

**Para saber se uma página carregou, use sempre um método que espera** (`waitVisible`, `hasTitle`, `hasPageUrl`). O `isDisplayed()` sem espera responde na hora e gera testes instáveis quando a página ainda está sendo desenhada.

---

## Usuários do Sauce Demo

Todos usam a senha `secret_sauce` e estão no enum `User`. Exceto o padrão, cada um tem **defeitos de propósito**, para treinar a detecção de bugs:

| Usuário | Constante | Comportamento |
|---|---|---|
| `standard_user` | `User.STANDARD` | sem defeitos |
| `locked_out_user` | `User.LOCKED_OUT` | bloqueado: não consegue fazer login |
| `problem_user` | `User.PROBLEM` | ordenação não funciona; o clique abre o produto seguinte; falhas no carrinho |
| `performance_glitch_user` | `User.PERFORMANCE_GLITCH` | funciona, mas com ~5 segundos de lentidão na lista de produtos |
| `error_user` | `User.ERROR` | ordenação abre um alerta; descrição do produto não carrega; erros no carrinho |
| `visual_user` | `User.VISUAL` | preços exibidos aleatórios na lista; layout desalinhado |

Uso: `new LoginPage(driver).loginAs(User.PROBLEM)`.

---

## Configuração

1. Copie `src/test/resources/config.properties.example` para `config.properties` na mesma pasta.
2. Preencha os dados de e-mail (ou deixe `email.enabled=false`).
3. Se quiser, troque a pasta de downloads em `download.dir` (padrão: `downloads`, dentro do projeto). Ela é limpa antes de cada teste do PDF.

Qualquer chave pode ser trocada numa execução, sem editar o arquivo:

```bash
mvn test -Dbrowser=firefox -Dheadless=true -Demail.enabled=false
```

---

## Como executar

| Comando | O que roda | Resultado esperado |
|---|---|---|
| `mvn test` | Regressão (`regression.xml`): login, carrinho, checkout, cálculo do resumo, recibo em PDF, detalhes do produto, menu lateral, rodapé e Dynamic Catalog | tudo passa |
| `mvn test -Dsuite=login` | Só login | tudo passa |
| `mvn test -Dsuite=login-parameters` | Login com parâmetros do XML | tudo passa |
| `mvn test -Dsuite=checkout` | Carrinho, checkout, cálculo do resumo e recibo em PDF | tudo passa |
| `mvn test -Dtest=OrderReceiptPdfTest` | Só uma classe, sem usar o XML da suíte (útil para testar algo novo) | |
| `mvn test -Dsuite=inventory` | Ordenação (5 usuários) e detalhes do produto (usuário padrão) | 32 execuções, **8 falhas esperadas** |
| `mvn test -Dsuite=product-users` | Detalhes do produto com os outros usuários | 16 execuções, **10 falhas esperadas** |
| `mvn test -Dsuite=navigation` | Menu lateral (inclui acesso sem login) e rodapé | tudo passa |
| `mvn test -Dsuite=dynamic-catalog` | Spinner, Lazy Load e Slider | tudo passa |
| `mvn clean test` | Limpa o build antes de rodar | |

**A regressão precisa ficar sempre verde**: é ela que roda na CI a cada push. Os testes que expõem os defeitos conhecidos dos usuários especiais ficam em suítes próprias (`inventory`, `product-users`). As falhas delas são esperadas e mostram que os testes encontram os bugs.

Pelo VS Code, o botão ▶ ao lado da classe ou do método também funciona, exceto em `LoginTestParameters`, que depende do XML. Ele fica como *SKIP* se for executado pelo botão.

### Saídas de cada execução

- `reports/ExtentReport_<data-hora>.html`: relatório com passos, resultado e prints das falhas.
- `screenshots/SUCCESS|FAILURE|SKIP/<método>_<id>_<parâmetro>_<data-hora>.png`, por exemplo `testSortByNameZToA_ORD02_problem_user_...png`.
  O id vem do nome do teste no relatório, no padrão **`ID - descrição`** (ex.: `FTR01 - Links das redes sociais`). Testes que seguem esse padrão ganham o id no print automaticamente.
- `downloads/swag-labs-order-<data-hora>.pdf`: recibo baixado no teste do PDF (a pasta é limpa a cada cenário).
- `logs/selenium-framework.log`
- E-mail com o resumo (passou/falhou) e o relatório anexo, se `email.enabled=true`.

---

## Dados de teste

Cada tipo de dado fica num lugar:

| Tipo de dado | Onde fica | Exemplo |
|---|---|---|
| Configuração do ambiente (URL, navegador, senhas) | `config.properties` ou `-D` na linha de comando | `browser=chrome` |
| Massa de dados dos cenários | Excel ou JSON em `testdata/` | `LoginData.xlsx`, `product-scenarios.json` |
| Constantes do sistema (lista fechada, usada pelo nome) | `enum` ou constantes no código | `User.PROBLEM`, `Product.BACKPACK`, `InventoryPage.SORT_NAME_A_TO_Z` |

Em todos os formatos, **um cenário por linha, com os valores esperados escritos**. Para criar um cenário novo, basta acrescentar uma linha, sem mexer no código.

### Excel (login, carrinho e checkout)

**Um arquivo por módulo e uma aba por funcionalidade:** `LoginData.xlsx` (aba `Login`) e `CheckoutData.xlsx` (abas `Cart` e `Checkout`).

- A linha 1 é o cabeçalho, e as colunas seguem a mesma ordem dos parâmetros do método de teste.
- A primeira coluna é sempre o `testId`: ele aparece no relatório e no nome do print.
- `expectedResult` = `SUCCESS` ou `ERROR`, com a mensagem esperada em `expectedMessage`.
- Célula vazia vira texto vazio, o que permite testar campos em branco.

### JSON (detalhes do produto, resumo do pedido e recibo em PDF)

Cada arquivo é uma lista `[ ]` de cenários `{ }`. O Jackson converte cada cenário num objeto `ProductScenario` (um `record`):

```json
{
  "testId": "PD07",
  "scenario": "problem_user abre o primeiro produto (id 0)",
  "user": "PROBLEM",
  "productName": "Sauce Labs Bike Light",
  "expectedId": 0,
  "expectedPrice": 9.99,
  "expectedDescription": "A red light isn't the desired state..."
}
```

- As chaves do JSON têm **os mesmos nomes** dos campos do `ProductScenario`: é assim que o Jackson sabe onde colocar cada valor.
- O campo `user` usa o nome da constante do enum (`"PROBLEM"`), e o Jackson converte para `User.PROBLEM`.
- Um campo pode ser uma **lista** de tamanho variável, como os produtos de cada pedido em `order-scenarios.json`:
  `"products": ["Sauce Labs Backpack", "Sauce Labs Bike Light"]`. É um dos casos em que o JSON é melhor que o Excel,
  onde a lista viraria texto separado por `;`.
- Os valores esperados são sempre os **corretos** (o requisito). Se o site errar, o teste falha e mostra o defeito.
- JSON não aceita comentários: a justificativa da escolha dos cenários fica neste README e nos comentários das classes de teste.

---

## Técnicas de teste aplicadas

Os cenários de `product-scenarios-users.json` foram escolhidos com técnicas da CTFL, em vez de combinar todos os usuários com todos os produtos (5 × 6 = 30):

- **Particionamento de Equivalência (usuários):** cada perfil com comportamento diferente é uma partição. O `standard_user` já tem os 6 produtos em `product-scenarios.json`.
- **Análise de Valor Limite (produtos):** os outros usuários testam só o **primeiro** (id 0) e o **último** (id 5) produto, os limites da faixa de ids que aparece na URL.

Resultado: 8 cenários em vez de 24, detectando os mesmos defeitos. Por exemplo, o `problem_user` no último produto abre o id 6, que não existe ("ITEM NOT FOUND"): um defeito clássico de limite.

**Risco aceito:** um defeito que aparecesse só num produto do meio da faixa, para um usuário especial, não seria detectado por esses cenários.

---

## Dynamic Catalog: como os testes lidam com páginas dinâmicas

As três páginas mudam sozinhas ou com tempo variável. Por isso os testes **esperam uma condição**, e nunca um tempo fixo:

| Página | Comportamento | Como o teste espera |
|---|---|---|
| Spinner | Mostra um spinner por um tempo **aleatório** (0,5 a 3 s) e depois os produtos | `waitInvisible` espera o spinner sumir. Um `Thread.sleep(2000)` falharia sempre que o carregamento passasse de 2 s |
| Lazy Load | Cada card mostra "Loading…" até aparecer na tela; rolar até o fim acrescenta mais 6 cards (lista infinita) | Espera personalizada: `wait.until(d -> quantidade de cards > antes)`. A quantidade inicial depende do tamanho da janela, então o teste não supõe um número fixo |
| Slider | Troca de produto sozinho a cada 2 s | **Leitura confirmada**: lê o ponto ativo antes e depois do nome e do preço; se mudou no meio, lê de novo |

Detalhes úteis:

- O Lazy Load usa uma lista própria de 22 itens, com tamanhos (`Sauce Labs Fleece Jacket (XS)`), diferente da página de produtos. Os cenários de conteúdo usam análise de valor limite: início, fim do primeiro lote, início do segundo lote, último item e o primeiro depois dele (a lista recomeça).
- O texto do placeholder usa o caractere de reticências (`…`, escrito `\u2026` no Java), e não três pontos. No DevTools (Console), `elemento.textContent.length` e `charCodeAt()` mostram a diferença.
- No Spinner e no Slider, a posição do card/ponto é o id do enum `Product`, que fornece os valores esperados.

---

## Recibo em PDF: como o teste funciona

1. Antes de cada cenário, `DownloadUtils.cleanDownloadDir()` esvazia a pasta `downloads/`. Assim o teste nunca confere um PDF antigo.
2. O `DriverFactory` configura o navegador para salvar os downloads nessa pasta, sem perguntar onde salvar (funciona também em headless).
3. O teste conclui o pedido e clica em **Generate PDF order**.
4. `DownloadUtils.waitForFile(...)` espera o arquivo aparecer e terminar de baixar, como uma espera explícita, mas para arquivos.
5. `PdfUtils.readText(...)` extrai o texto do PDF com o PDFBox, e o teste confere o conteúdo com `Assert`. O texto extraído também vai para o relatório, para facilitar a análise de falhas.

**Atenção às datas:** o **nome do arquivo** usa o horário **UTC** do pedido, e o **conteúdo** do PDF usa o horário **local** do navegador. Por isso o teste compara cada um com o fuso certo e aceita uma pequena diferença de tempo. Isso garante que o teste passe tanto no Jenkins (Windows, horário de Brasília) quanto no GitHub Actions (Linux, UTC).

---

## Integração contínua (CI)

| | GitHub Actions (`.github/workflows/ci.yml`) | Jenkins (`Jenkinsfile`) |
|---|---|---|
| Quando roda | a cada push e pull request na `master`, ou manualmente em *Actions* | verifica o repositório a cada ~5 minutos, ou manualmente em *Construir com parâmetros* |
| Suíte padrão | `regression` | escolhida no parâmetro `SUITE` |
| Execução | headless, no Linux | headless, no Windows |
| E-mail | se os segredos `EMAIL_*` estiverem cadastrados no repositório | se as credenciais `email-*` estiverem cadastradas no Jenkins |
| Resultados | relatório, prints e logs como *artifacts* da execução | aba *TestNG Results* e *Artefatos* do build |

O `config.properties` real não vai para o Git. Na CI, ele é criado a partir do `config.properties.example`, e as senhas chegam como segredos.

---

## Como adicionar um novo requisito (passo a passo)

1. **Página:** em `pages/`, adicione os localizadores e as ações, numa página existente ou numa nova classe que herde de `BasePage`. Inspecione os elementos no DevTools (Elements) e valide cada seletor com **Ctrl+F** antes de ir para o código.
2. **Dados:** monte os cenários com os valores esperados, numa aba nova do Excel do módulo ou num arquivo JSON em `testdata/`.
3. **Modelo (se for JSON):** crie um `record` em `src/main/java/.../data/` com os mesmos nomes das chaves do JSON.
4. **DataProvider:** em `dataproviders/`, adicione um método `@DataProvider` que lê o arquivo.
5. **Teste:** em `tests/<modulo>/`, crie a classe que herda de `BaseTest`, usa as páginas e faz os `Assert`.
6. **Suíte:** inclua a classe na suíte do módulo. Se ela deve passar sempre, inclua também na `regression.xml`.

---

## Cobertura atual

| Módulo | Cenários |
|---|---|
| Login | 12 cenários: 5 usuários válidos e 7 erros (bloqueado, campos vazios, senha errada, usuário inexistente, case sensitive) |
| Produtos: ordenação | 4 ordenações (padrão A–Z, Z–A, preço crescente e decrescente) × 5 usuários |
| Produtos: detalhes | 6 produtos com o usuário padrão + 8 cenários com os outros usuários: preço na lista, id na URL, nome, descrição, preço e imagem; adicionar e remover pela página de detalhes |
| Carrinho | Adicionar 1, 2 e todos os produtos (contador e itens no carrinho) |
| Carrinho: remover | Pela lista de produtos e pelo carrinho (contador e botão atualizados); remover todos deixa o carrinho vazio |
| Carrinho: persistência | Carrinho mantido ao navegar entre páginas, ao recarregar a página e após logout e novo login |
| Checkout | Compra concluída e validação dos 3 campos obrigatórios |
| Checkout: cancelar | Cancel nos dados do comprador volta ao carrinho; Cancel no resumo e Continue Shopping voltam aos produtos, sem perder o carrinho |
| Checkout: cálculo do resumo | 5 pedidos (1 item mais barato, 1 item mais caro, 2 itens, 3 itens e os 6 produtos): item total, taxa de 8% e total |
| Checkout: recibo em PDF | Os mesmos 5 pedidos: o PDF é baixado e confere nome do arquivo (data e hora), comprador, itens iguais aos da tela, item total, taxa, total e data do pedido |
| Menu lateral | Abrir e fechar; All Items; About abre o site da Sauce Labs na mesma aba; Logout e bloqueio de acesso sem login; Reset App State esvazia o carrinho |
| Rodapé | Links das redes sociais (endereço e abertura em nova aba), copyright com o ano atual, mesmo rodapé no carrinho |
| Dynamic Catalog: Spinner | O spinner aparece e some; os 6 produtos aparecem com nome e preço certos; o carregamento se repete ao recarregar |
| Dynamic Catalog: Lazy Load | Rolar até o fim carrega mais cards (lotes de 6); card fora da tela mostra "Loading…" e carrega ao aparecer; nome e preço nos limites da lista (posições 0, 5, 6, 21 e 22) |
| Dynamic Catalog: Slider | Um ponto por produto e só um ativo; troca sozinho para o próximo; cada ponto mostra o produto certo; depois do último volta ao primeiro |

## Backlog

### Concluído

- [x] **Login**: usuários válidos, usuário bloqueado e mensagens de erro (dados em Excel, fixos e no XML do TestNG).
- [x] **Carrinho**: adicionar 1, 2 e todos os produtos.
- [x] **Checkout**: compra concluída e validação dos campos obrigatórios.
- [x] **Ordenação de produtos**: Name (A to Z / Z to A) e Price (low to high / high to low), para cada usuário.
- [x] **Detalhes do produto**: nome, descrição, preço, imagem e id na URL; adicionar e remover a partir dali; cenários com os outros usuários.
- [x] **Cálculo do resumo do pedido**: Item total = soma dos preços; Tax = 8%; Total = item total + tax.
- [x] **Menu lateral**: abrir/fechar, All Items, About, Logout e Reset App State.
- [x] **Acesso sem login**: abrir `/inventory.html` direto volta para o login com mensagem de erro.
- [x] **Rodapé**: links das redes sociais e copyright.
- [x] **Remover produto do carrinho**: pela lista de produtos e pelo carrinho, com atualização do contador.
- [x] **Cancelar o checkout**: Cancel em cada passo e Continue Shopping no carrinho.
- [x] **Persistência do carrinho**: ao navegar, ao recarregar a página e após logout/login.
- [x] **Recibo em PDF**: baixar o PDF do pedido e validar itens, comprador, valores e data.
- [x] **Dynamic Catalog**: páginas Spinner, Lazy Load e Slider.
- [x] **Infraestrutura**: Page Object com componentes, relatório Extent com prints, logs, envio do relatório por e-mail, CI no GitHub Actions e no Jenkins.

### Pendente

- Nenhum requisito pendente. Próximos passos possíveis: testes que confirmem os defeitos encontrados (abaixo).

## Defeitos encontrados (ainda sem teste)

- **Reset App State não atualiza os botões da lista de produtos** (inclusive com o `standard_user`): o carrinho é esvaziado, mas os produtos continuam mostrando "Remove" até a página ser recarregada.
- **About com o `problem_user`** aponta para `https://saucelabs.com/error/404`, em vez do site da Sauce Labs.
- **Item repetido no Lazy Load**: as posições 13 e 14 da lista mostram o mesmo produto, `Test.allTheThings() T-Shirt (Red) (L)`. Os outros produtos aparecem uma vez em cada tamanho.

### Dúvida de requisito

- **O carrinho continua após o logout**, porque fica guardado no navegador e não na conta do usuário. Se outra pessoa entrar com **outro usuário** no mesmo navegador, verá o carrinho de quem saiu. O teste PER03 protege o comportamento atual, mas vale confirmar com o time se o carrinho deveria ser por usuário.
