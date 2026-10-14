// ============================================================
// Pipeline Jenkins - Selenium Automation Framework (Sauce Demo)
//
// "Pipeline as code": a configuração do job fica versionada junto com o projeto,
// em vez de existir só na tela do Jenkins.
//
// Pré-requisitos no Jenkins (Gerenciar Jenkins > Tools):
//   - Maven instalado com o nome  MAVEN-3.9.16
//   - JDK instalado com o nome    JDK-25
//   - Plugins: Pipeline, Git, TestNG Results, Timestamper
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
        choice(name: 'SUITE', choices: ['regression', 'login', 'checkout', 'login-parameters'],
               description: 'Suíte do TestNG (src/test/resources/suites)')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'],
               description: 'Navegador')
        booleanParam(name: 'HEADLESS', defaultValue: true,
                     description: 'Rodar sem abrir a janela do navegador')
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
                // -Dmaven.test.failure.ignore=true: um teste falhando não "quebra" o build;
                // o resultado (instável/sucesso) é decidido pelo plugin do TestNG no post.
                bat "mvn -B clean test -Dsuite=${params.SUITE} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS} -Demail.enabled=false -Dmaven.test.failure.ignore=true"
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
