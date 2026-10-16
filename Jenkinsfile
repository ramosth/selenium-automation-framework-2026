// ============================================================
// Pipeline Jenkins - Selenium Automation Framework (Sauce Demo)
//
// "Pipeline as code": a configuração do job fica versionada junto com o projeto,
// em vez de existir só na tela do Jenkins.
//
// Pré-requisitos no Jenkins (Gerenciar Jenkins > Tools):
//   - Maven instalado com o nome  MAVEN-3.9.16
//   - JDK instalado com o nome    JDK-25
//   - Plugins: Pipeline, Git, TestNG Results, Timestamper, Credentials Binding
//   - Para o envio do relatório por e-mail, cadastre em
//     Gerenciar Jenkins > Credentials > System > Global credentials > Add Credentials,
//     tipo "Secret text", com estes IDs:
//       email-sender         -> e-mail remetente (Gmail)
//       email-app-password   -> senha de app de 16 letras do Gmail
//       email-recipients     -> destinatários separados por vírgula
//
// Este Jenkins roda no Windows, por isso os comandos usam "bat" (no Linux seria "sh").
// ============================================================
pipeline {
    agent any

    tools {
        maven 'MAVEN-3.9.16'
        jdk 'JDK-25'
    }

    // Opções exibidas em "Construir com parâmetros"
    parameters {
        choice(name: 'SUITE', choices: ['regression', 'login', 'checkout', 'login-parameters', 'inventory', 'product-users'],
               description: 'Suíte do TestNG (src/test/resources/suites)')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'],
               description: 'Navegador')
        booleanParam(name: 'HEADLESS', defaultValue: true,
                     description: 'Rodar sem abrir a janela do navegador')
        booleanParam(name: 'ENVIAR_EMAIL', defaultValue: true,
                     description: 'Enviar o relatório por e-mail ao final (usa as credenciais email-*)')
    }

    // Verifica o GitHub a cada ~5 minutos e roda se houver commit novo
    triggers {
        pollSCM('H/5 * * * *')
    }

    options {
        timestamps()                                    // horário em cada linha do log
        timeout(time: 30, unit: 'MINUTES')              // evita build travado para sempre
        buildDiscarder(logRotator(numToKeepStr: '20'))  // guarda só os 20 últimos builds
        disableConcurrentBuilds()                       // um build por vez
    }

    stages {
        stage('Preparar configuração') {
            steps {
                // O config.properties real fica fora do Git (tem senha).
                // No servidor usamos o modelo, que já aponta para o Sauce Demo.
                bat 'if not exist src\\test\\resources\\config.properties copy src\\test\\resources\\config.properties.example src\\test\\resources\\config.properties'
            }
        }

        stage('Executar testes') {
            steps {
                script {
                    // -Dmaven.test.failure.ignore=true: um teste falhando não "quebra" o build;
                    // o resultado (instável/sucesso) é decidido pelo plugin do TestNG no post.
                    def mvn = "mvn -B clean test -Dsuite=${params.SUITE} -Dbrowser=${params.BROWSER} " +
                              "-Dheadless=${params.HEADLESS} -Dmaven.test.failure.ignore=true"

                    if (params.ENVIAR_EMAIL) {
                        // withCredentials injeta os segredos como variáveis de ambiente SÓ dentro
                        // deste bloco e mascara os valores no log (****).
                        // Aspas simples + %VAR%: quem expande é o Windows, não o Groovy,
                        // então a senha não fica gravada no script do pipeline.
                        withCredentials([
                            string(credentialsId: 'email-sender',       variable: 'EMAIL_SENDER'),
                            string(credentialsId: 'email-app-password', variable: 'EMAIL_APP_PASSWORD'),
                            string(credentialsId: 'email-recipients',   variable: 'EMAIL_RECIPIENTS')
                        ]) {
                            bat mvn + ' -Demail.enabled=true' +
                                ' "-Demail.sender=%EMAIL_SENDER%"' +
                                ' "-Demail.appPassword=%EMAIL_APP_PASSWORD%"' +
                                ' "-Demail.recipients=%EMAIL_RECIPIENTS%"'
                        }
                    } else {
                        bat mvn + ' -Demail.enabled=false'
                    }
                }
            }
        }
    }

    post {
        always {
            // Gráfico e detalhes dos testes na página do build
            testNG(reportFilenamePattern: 'target/surefire-reports/testng-results.xml')

            // Relatório Extent, prints e logs para download
            archiveArtifacts(artifacts: 'reports/**, screenshots/**, logs/**, target/surefire-reports/**',
                             allowEmptyArchive: true)
        }
        unstable {
            echo 'Há testes com falha: confira a aba "TestNG Results" e o relatório Extent em "Artefatos".'
        }
    }
}
