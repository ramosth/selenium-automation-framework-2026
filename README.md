# Selenium Automation Framework: Sauce Demo

Framework de automação de testes web em **Java + Selenium WebDriver + TestNG**, seguindo o padrão **Page Object Model (POM)**.
A aplicação testada é o [Sauce Demo](https://www.saucedemo.com/), uma loja virtual de demonstração.

**Recursos:** testes orientados a dados (Excel, dados fixos e parâmetros do XML), relatório HTML (Extent Reports) com prints, logs (Log4j2), envio do relatório por e-mail e escolha do navegador por configuração.

---

## Tecnologias

| Ferramenta | Para que serve |
|---|---|
| Java 17 + Maven | Linguagem e gerenciador de build/dependências |
| Selenium WebDriver 4 | Controla o navegador |
| TestNG | Organiza e executa os testes (anotações, DataProvider, suítes, listeners) |
| Apache POI | Lê os dados de teste das planilhas Excel |
| Extent Reports | Gera o relatório HTML da execução |
| Log4j2 | Gera os logs no console e em arquivo |
| Jakarta Mail | Envia o relatório por e-mail |

---

## Estrutura de pastas

```
selenium-framework/
├── pom.xml                          # Dependências e configuração do Maven/Surefire
├── README.md
├── src/
│   ├── main/java/com/test/automation/      # FRAMEWORK: código reutilizável, sem testes
│   │   ├── config/
│   │   │   └── ConfigReader.java           # Lê o config.properties
│   │   ├── driver/
│   │   │   └── DriverFactory.java          # Cria o navegador (chrome/firefox/edge, headless)
│   │   ├── pages/                          # PAGE OBJECTS: uma classe por página
│   │   │   ├── BasePage.java               # Base de todas as páginas: waits e ações comuns
│   │   │   ├── LoginPage.java
│   │   │   ├── InventoryPage.java          # Lista de produtos
│   │   │   ├── CartPage.java               # Carrinho
│   │   │   ├── CheckoutInformationPage.java# Checkout passo 1: dados do comprador
│   │   │   ├── CheckoutOverviewPage.java   # Checkout passo 2: resumo e valores
│   │   │   └── CheckoutCompletePage.java   # Confirmação do pedido
│   │   ├── reports/
│   │   │   └── ExtentReportManager.java    # Cria o relatório HTML
│   │   ├── listeners/
│   │   │   └── EmailReportListener.java    # Envia o relatório por e-mail ao fim da suíte
│   │   └── utils/                          # Utilitários genéricos
│   │       ├── ExcelUtils.java             # Leitura das planilhas
│   │       ├── EmailUtils.java             # Envio de e-mail (SMTP)
│   │       ├── ScreenshotUtils.java        # Captura de tela
│   │       └── Log.java                    # Atalho para o Log4j2
│   ├── main/resources/
│   │   └── log4j2.xml                      # Configuração dos logs
│   ├── test/java/com/test/automation/      # TESTES
│   │   ├── base/
│   │   │   └── BaseTest.java               # Abre/fecha navegador, relatório e prints
│   │   ├── dataproviders/                  # De onde vêm os dados de cada módulo
│   │   │   ├── LoginDataProvider.java
│   │   │   └── CheckoutDataProvider.java
│   │   └── tests/                          # Uma pasta por módulo/funcionalidade
│   │       ├── login/
│   │       │   ├── BaseLoginTest.java      # Validações comuns do login (if/else SUCCESS x ERROR)
│   │       │   ├── LoginTest.java          # Um método por cenário (aula 1)
│   │       │   ├── LoginTestData.java      # Data-driven: Excel ou dados fixos
│   │       │   └── LoginTestParameters.java# Dados vindos do XML do TestNG
│   │       ├── cart/
│   │       │   └── CartTest.java
│   │       └── checkout/
│   │           └── CheckoutTest.java
│   └── test/resources/
│       ├── config.properties.example       # Modelo de configuração (versionado)
│       ├── config.properties               # Configuração local com senha (NÃO versionado)
│       ├── testdata/                       # Massa de dados: um Excel por módulo
│       │   ├── LoginData.xlsx              # aba Login
│       │   └── CheckoutData.xlsx           # abas Cart e Checkout
│       └── suites/                         # Suítes do TestNG
│           ├── regression.xml              # Tudo (padrão do mvn test)
│           ├── login.xml
│           ├── checkout.xml
│           └── login-parameters.xml        # Login com parâmetros no próprio XML
├── docs/exemplos-aula/                     # Material de estudo, fora da compilação
│   └── LoginPageBasica.java                # Page Object com By (antes do PageFactory)
├── reports/      # (gerado) relatórios HTML           ┐
├── screenshots/  # (gerado) prints SUCCESS/FAILURE/SKIP ├─ ignorados pelo Git
├── logs/         # (gerado) selenium-framework.log     │
└── target/       # (gerado) build do Maven            ┘
```

### Por que `main` e `test`?

- **`src/main`** guarda o **framework**: o que é reutilizável e não é teste (páginas, navegador, utilitários, relatório).
- **`src/test`** guarda os **testes** e tudo o que só existe para eles (classe base de teste, dados, suítes, configuração).

### As camadas do Page Object

```
Teste (tests/)  ──usa──▶  Página (pages/)  ──usa──▶  Selenium WebDriver
   "o quê"                  "como"                     navegador
   Asserts                  locators + ações
```

- A **página** sabe *como* interagir: localizadores (`@FindBy`) e ações (`login`, `addProductToCart`). **Nunca faz Assert.**
- O **teste** diz *o que* validar: monta o cenário chamando as páginas e decide se passou com `Assert`.
- Se um botão mudar no site, só a página muda. Os testes continuam iguais.
- Ações que levam a outra página devolvem essa página (ex.: `cartPage.goToCheckout()` devolve `CheckoutInformationPage`), deixando o fluxo legível.

---

## Configuração

1. Copie `src/test/resources/config.properties.example` para `config.properties` na mesma pasta.
2. Preencha os dados de e-mail (ou deixe `email.enabled=false`).

Qualquer chave pode ser trocada numa execução, sem editar o arquivo:

```bash
mvn test -Dbrowser=firefox -Dheadless=true -Demail.enabled=false
```

---

## Como executar

| Comando | O que roda |
|---|---|
| `mvn test` | Suíte completa (`regression.xml`) |
| `mvn test -Dsuite=login` | Só login |
| `mvn test -Dsuite=checkout` | Carrinho e checkout |
| `mvn test -Dsuite=login-parameters` | Login com parâmetros do XML |
| `mvn clean test` | Limpa o build antes de rodar |

Pelo VS Code, o botão ▶ ao lado da classe ou do método também funciona, exceto em `LoginTestParameters`, que depende do XML. Ele fica como *SKIP* se for executado pelo botão.

### Saídas de cada execução

- `reports/ExtentReport_<data-hora>.html`: relatório com passos, resultado e prints das falhas.
- `screenshots/SUCCESS|FAILURE|SKIP/<teste>_<id>_<data-hora>.png`
- `logs/selenium-framework.log`
- E-mail com o resumo (passou/falhou) e o relatório anexo, se `email.enabled=true`.

---

## Dados de teste (Excel)

**Padrão adotado: um arquivo por módulo e uma aba por funcionalidade.**

- `LoginData.xlsx` → aba `Login`
- `CheckoutData.xlsx` → abas `Cart` e `Checkout`

Motivos: cada funcionalidade tem colunas diferentes, o nome do arquivo já diz o módulo (espelha `pages/` e `tests/`) e, em equipe, menos gente mexe no mesmo arquivo.

Regras das planilhas:

- A linha 1 é o cabeçalho e as colunas seguem a mesma ordem dos parâmetros do método de teste.
- A primeira coluna é sempre o `testId`: ele aparece no relatório e no nome do print.
- `expectedResult` = `SUCCESS` ou `ERROR`, com a mensagem esperada em `expectedMessage`.
- Célula vazia vira texto vazio, o que permite testar campos em branco.
- Para criar um cenário novo, basta adicionar uma linha, sem mexer no código.

---

## Como adicionar um novo requisito (passo a passo)

Exemplo: **ordenação de produtos**.

1. **Página**: em `pages/`, adicione os localizadores e ações. Pode ser numa página existente (`InventoryPage.sortBy(...)`) ou numa nova classe que herde de `BasePage`.
2. **Dados**: crie uma aba nova no Excel do módulo (ex.: aba `Sort` no arquivo de produtos) ou um arquivo novo se for outro módulo.
3. **DataProvider**: em `dataproviders/`, adicione um método `@DataProvider` que lê a aba.
4. **Teste**: em `tests/<modulo>/`, crie a classe que herda de `BaseTest`, usa a página e faz os `Assert`.
5. **Suíte**: inclua a classe na suíte do módulo e na `regression.xml`.

---

## Cobertura atual

| Módulo | Cenários |
|---|---|
| Login | 12 cenários: 5 usuários válidos e 7 erros (bloqueado, campos vazios, senha errada, usuário inexistente, case sensitive) |
| Carrinho | Adicionar 1, 2 e todos os produtos (contador e itens no carrinho) |
| Checkout | Compra concluída e validação dos 3 campos obrigatórios |

## Próximos requisitos (backlog)

- [ ] **Ordenação de produtos**: Name (A to Z / Z to A) e Price (low to high / high to low).
- [ ] **Remover produto do carrinho**: pela lista de produtos e pelo carrinho, com atualização do contador.
- [ ] **Detalhes do produto**: abrir o produto, conferir nome, descrição e preço, adicionar ao carrinho a partir dali e voltar.
- [ ] **Cálculo do resumo do pedido**: Item total = soma dos preços; Tax = 8%; Total = item total + tax.
- [ ] **Cancelar o checkout**: Cancel em cada passo e Continue Shopping no carrinho.
- [ ] **Menu lateral**: Logout, Reset App State (limpa o carrinho), All Items e About.
- [ ] **Acesso sem login**: abrir `/inventory.html` direto deve voltar para o login com mensagem de erro.
- [ ] **Persistência do carrinho**: itens continuam no carrinho ao navegar e após logout/login.
- [ ] **Usuários com defeitos conhecidos**: `problem_user`, `error_user`, `visual_user` e `performance_glitch_user` (testes que devem encontrar os bugs).
- [ ] **Rodapé**: links das redes sociais.
